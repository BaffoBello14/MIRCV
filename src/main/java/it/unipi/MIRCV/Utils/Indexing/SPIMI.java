package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;
import org.apache.commons.compress.archivers.*;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class SPIMI {
    private static final String PATH_TO_ARCHIVE_COLLECTION = "./Collection/collection.tar.gz";
    private static int numIndex = 0;
    private static long numPosting = 0;
    private static long offsetDocIndex = 0;

    // Main method to execute the SPIMI indexing process
    public static long execute() throws Exception {
        int doc_id = 1;
        String[] lineofDoc;
        String docno;
        
        // Open a FileChannel for the Doc Index
        FileChannel fileChannelDI = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        int total_length = 0;
        List<String> tokens;
        Runtime runtime = Runtime.getRuntime();
        
        try {
            InputStream file = Files.newInputStream(Paths.get(PATH_TO_ARCHIVE_COLLECTION));
            InputStream gzip = new GZIPInputStream(file);
            ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
            archiveStream.getNextEntry();
            BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream, StandardCharsets.UTF_8));
            String line;
            
            // Initialize the in-memory index
            HashMap<String, PostingIndex> index = new HashMap<>();

            // Read each line of the archived collection
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                lineofDoc = Preprocess.parseLine(line);
                
                if (lineofDoc[1].trim().isEmpty()) {
                    continue;
                }
                
                docno = lineofDoc[0];
                lineofDoc[1] = Preprocess.cleanText(lineofDoc[1]);
                tokens = Preprocess.tokenize(lineofDoc[1]);

                // If stopword removal and stemming is enabled, apply them
                if (PathAndFlags.STOPWORD_STEM_ENABLED) {
                    tokens = Preprocess.removeStopwords(tokens);
                    tokens = Preprocess.applyStemming(tokens);
                }
                
                if (tokens.isEmpty()) {
                    continue;
                }

                int documentLength = tokens.size();

                // Create a new DocIndexEntry for this document
                DocIndexEntry docIndexEntry = new DocIndexEntry(docno, documentLength);
                docIndexEntry.write2Disk(fileChannelDI, offsetDocIndex, doc_id);
                offsetDocIndex += DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
                total_length += documentLength;

                // Process each token of the document
                for (String term : tokens) {
                    PostingIndex posting;
                    if (index.containsKey(term)) {
                        posting = index.get(term);
                    } else {
                        posting = new PostingIndex(term);
                        index.put(term, posting);
                    }
                    addPosting(doc_id, posting);
                    posting.updateBM25Values(posting.getPostings().size(), documentLength);
                }

                if (doc_id % 100000 == 0) {
                    System.out.println(doc_id + "processed");
                }
                doc_id++;

                // If memory consumption exceeds 80%, write the in-memory index to disk
                if (((double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.totalMemory()) * 100 > 80) {
                    if (!write2Disk(index)) {
                        System.out.println("problems with writing to disk of SPIMI");
                        return -1;
                    }
                    index.clear();
                    System.gc();
                }
            }

            // Final write to disk for remaining in-memory index entries
            if (!write2Disk(index)) {
                System.out.println("problems with writing to disk spimi");
                return -1;
            }
            
            index.clear();
            System.gc();
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
            return -1;
        }

        CollectionStatistics.setTotalLenDoc(total_length);
        CollectionStatistics.setDocuments(doc_id-1);
        CollectionStatistics.computeAVGDOCLEN();
        CollectionStatistics.write2Disk();
        System.out.println(CollectionStatistics.getAvgDocLen());
        fileChannelDI.close();

        return numIndex;
    }

    /**
     * Writes the current in-memory index to disk.
     *
     * @param index The in-memory index to be written to disk.
     * @return true if writing is successful, false otherwise.
     */
    private static boolean write2Disk(HashMap<String,PostingIndex> index) {
        System.out.println("save index with size->" + index.size() + " the num ->" + numIndex);
        
        if (index.isEmpty()) {
            return true;
        }
        
        // Sort index by term
        index = index.entrySet().stream()
                     .sorted(Map.Entry.comparingByKey())
                     .collect(Collectors.toMap(Map.Entry::getKey, 
                                               Map.Entry::getValue, 
                                               (e1, e2) -> e1, 
                                               LinkedHashMap::new));

        try {
            // Opening channels for writing to disk
            FileChannel fileChannelLex = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON + numIndex + ".dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelDOCID = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID + numIndex + ".dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelFreq = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FREQ + numIndex + ".dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // Mapping byte buffers
            MappedByteBuffer mappedByteBufferDOCID = fileChannelDOCID.map(FileChannel.MapMode.READ_WRITE, 0, numPosting * 4L);
            MappedByteBuffer mappedByteBufferFreq = fileChannelFreq.map(FileChannel.MapMode.READ_WRITE, 0, numPosting * 4L);
            
            long lexOffset = 0;

            if (mappedByteBufferDOCID == null || mappedByteBufferFreq == null) {
                return false;
            }

            // Write each posting list to disk
            for (PostingIndex postingIndex : index.values()) {
                LexiconEntry lexiconEntry = new LexiconEntry();
                lexiconEntry.setOffset_doc_id(mappedByteBufferDOCID.position());
                lexiconEntry.setOffset_frequency(mappedByteBufferFreq.position());

                for (Posting posting : postingIndex.getPostings()) {
                    mappedByteBufferDOCID.putInt(posting.getDoc_id());
                    mappedByteBufferFreq.putInt(posting.getFrequency());
                }

                lexiconEntry.updateTFMAX(postingIndex);
                lexiconEntry.setDocidByteSize(postingIndex.getPostings().size() * 4);
                lexiconEntry.setFreqByteSize(postingIndex.getPostings().size() * 4);
                lexiconEntry.setTf(postingIndex.getTf_BM25());
                lexiconEntry.setDoclen(postingIndex.getDoc_len_BM25());
                lexOffset = lexiconEntry.writeEntryToDisk(postingIndex.getTerm(), lexOffset, fileChannelLex);
            }

            numIndex++;
            numPosting = 0;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a posting to a given PostingIndex.
     *
     * @param doc_id The document ID.
     * @param postingIndex The PostingIndex to which the posting is added.
     */
    protected static void addPosting(int doc_id, PostingIndex postingIndex) {
        if (!postingIndex.getPostings().isEmpty()) {
            Posting posting = postingIndex.getPostings().get(postingIndex.getPostings().size() - 1);

            if (doc_id == posting.getDoc_id()) {
                posting.setFrequency((posting.getFrequency() + 1));
                return;
            }
        }
        
        postingIndex.getPostings().add(new Posting(doc_id, 1));
        numPosting++;
    }

    public static void main(String[] args) throws Exception {
        // Open channels to read and write to the document index, frequency, and lexicon files
        FileChannel fileChannelDI = FileChannel.open(
            Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),
            StandardOpenOption.READ, 
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE
        );
        FileChannel fileChannelFRQ = FileChannel.open(
            Paths.get(PathAndFlags.PATH_TO_DOC_ID + "0.dat"),
            StandardOpenOption.READ, 
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE
        );
        FileChannel fileChannelLEX = FileChannel.open(
            Paths.get(PathAndFlags.PATH_TO_LEXICON + "0.dat"),
            StandardOpenOption.READ, 
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE
        );
    
        // Record the start time for the execution
        long start = System.currentTimeMillis();
        
        // Execute the SPIMI indexing process
        execute();
        
        // Record the end time for the execution
        long end = System.currentTimeMillis();
        
        // Print the time taken for the SPIMI indexing process
        System.out.println("SPIMI time->" + (end - start) / 1000 + "sec");
        
        // Read the document index from disk for the 200th document
        DocIndex docIndex = new DocIndex();
        docIndex.readFromDisk(fileChannelDI, 200);
        
        // Read a lexicon entry from the lexicon file
        LexiconEntry lexiconEntry = new LexiconEntry();
        lexiconEntry.readEntryFromDisk(LexiconEntry.ENTRY_SIZE, fileChannelLEX);
        
        // Map a byte buffer to read the frequency of terms from the frequency file
        byte[] term = new byte[Lexicon.MAX_LEN_OF_TERM];
        MappedByteBuffer mappedByteBuffer = fileChannelFRQ.map(FileChannel.MapMode.READ_ONLY, 8, 4);
        
        // Uncomment below if you want to print the term from the byte buffer
        // System.out.println(new String(term, StandardCharsets.UTF_8));
        
        // Print the lexicon entry, the term's frequency, and the sorted document index
        System.out.println(lexiconEntry);
        System.out.println(mappedByteBuffer.getInt());
        System.out.println(docIndex.sortDocIndex().get(0));
    }    
}
/*
leggo la riga
computo la riga
vedo se occupazione memoria >80
se si scrivi in disco
altrimenti nulla
 */