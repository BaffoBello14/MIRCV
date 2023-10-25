package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.Paths.PathConfig;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;
import org.apache.commons.compress.archivers.*;

import java.io.*;
<<<<<<< HEAD
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
=======
import java.nio.file.*;
import java.util.*;
import java.nio.channels.FileChannel;
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f
import java.util.zip.GZIPInputStream;


public class SPIMI {
<<<<<<< HEAD
    private static final String PATH_TO_ARCHIVE_COLLECTION="./Collection/reducedCollection100.tar.gz";
    private static int numIndex=0;
    private static long numPosting=0;
    private static boolean finished=false;
    private static long offsetDocIndex=0;
=======
    private static final String PATH_TO_ARCHIVE_COLLECTION = "./Collection/reducedCollection100.tar.gz";
    private static boolean finished = false;
    private static long offsetDocIndex = 0;
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f

    public static boolean execute() throws Exception {
        int doc_id = 1;
        String[] lineofDoc;
        String docno;
<<<<<<< HEAD
        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
=======
        FileChannel fileChannelDI = FileChannel.open(Paths.get(PathConfig.getDocIndexDir() + "/DocIndex.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f

        int total_length = 0;
        List<String> tokens;
<<<<<<< HEAD
        HashMap<String,PostingIndex> index=new HashMap<>();
        while(!finished){
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            while ((usedMemory / totalMemory) * 100 < 80) {
                try {
                    InputStream file=new FileInputStream(PATH_TO_ARCHIVE_COLLECTION);
                    InputStream gzip=new GZIPInputStream(file);
                    ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
                    ArchiveEntry entry;

                    while ((entry = archiveStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lineofDoc= Preprocess.parseLine(line);
                                docno=lineofDoc[0];
                                lineofDoc[1]=Preprocess.cleanText(lineofDoc[1]);
                                tokens=Preprocess.tokenize(lineofDoc[1]);
                                tokens=Preprocess.removeStopwords(tokens);
                                tokens=Preprocess.applyStemming(tokens);
                                int documentLength=tokens.size();
                                DocIndexEntry docIndexEntry= new DocIndexEntry(docno,documentLength);
                                docIndexEntry.write2Disk(fileChannelDI,offsetDocIndex,doc_id);
                                offsetDocIndex+=DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
                                total_length+=documentLength;
                                for(String term:tokens){
                                    PostingIndex posting;
                                    if(index.containsKey(term)){
                                        posting=index.get(term);
                                    }else{
                                        posting=new PostingIndex();
                                        index.put(term,posting);
                                    }
=======
        HashMap<String, PostingIndex> index = new HashMap<>();
        
        // Logic to read from tar.gz file and process it
        while (!finished) {
            try {
                InputStream file = new FileInputStream(PATH_TO_ARCHIVE_COLLECTION);
                InputStream gzip = new GZIPInputStream(file);
                ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
                ArchiveEntry entry;
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f

                while ((entry = archiveStream.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            lineofDoc = Preprocess.parseLine(line);
                            docno = lineofDoc[0];
                            lineofDoc[1] = Preprocess.cleanText(lineofDoc[1]);
                            tokens = Preprocess.tokenize(lineofDoc[1]);
                            tokens = Preprocess.removeStopwords(tokens);
                            tokens = Preprocess.applyStemming(tokens);
                            int documentLength = tokens.size();
                            DocIndexEntry docIndexEntry = new DocIndexEntry(docno, documentLength);
                            docIndexEntry.write2Disk(fileChannelDI, offsetDocIndex, doc_id);
                            total_length += documentLength;
                            for (String term : tokens) {
                                PostingIndex posting;
                                if (index.containsKey(term)) {
                                    posting = index.get(term);
                                } else {
                                    posting = new PostingIndex();
                                    index.put(term, posting);
                                }
                            }
                            doc_id++;
                        }
                        finished = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Update statistics
        CollectionStatistics.setTotalLenDoc(CollectionStatistics.getTotalLenDoc() + total_length);
        CollectionStatistics.setDocuments(doc_id - 1);
        fileChannelDI.close();
        return true;
    }
    private static boolean write2Disk(HashMap<String,PostingIndex> index){
        System.out.println("save index with size->"+index.size()+" the num ->"+numIndex);
        if(index.isEmpty()){
            return true;
        }
        index=index.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1, LinkedHashMap::new));
        try {
            FileChannel fileChannelLEX=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelDOCID=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelFreq=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FREQ+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            MappedByteBuffer mappedByteBufferDOCID=fileChannelDOCID.map(FileChannel.MapMode.READ_WRITE,0,numPosting*4L);
            MappedByteBuffer mappedByteBufferFreq=fileChannelFreq.map(FileChannel.MapMode.READ_WRITE,0,numPosting*4L);
            long lexOffset=0;
            if(mappedByteBufferDOCID==null||mappedByteBufferFreq==null){
                return false;
            }
            for(PostingIndex postingIndex:index.values()){
                LexiconEntry lexiconEntry=new LexiconEntry();
                lexiconEntry.setOffset_doc_id(mappedByteBufferDOCID.position());
                lexiconEntry.setOffset_frequency(mappedByteBufferFreq.position());
                for(Posting posting: postingIndex.getPostings()){
                    mappedByteBufferDOCID.putInt(posting.getDoc_id());
                    mappedByteBufferFreq.putInt(posting.getFrequency());
                }
                lexiconEntry.updateTFMAX(postingIndex);
                lexiconEntry.setDf((int) (numPosting*4L));
                lexOffset=lexiconEntry.write2Disk(lexOffset,fileChannelLEX, postingIndex.getTerm());
            }
            numIndex++;
            numPosting=0;
            return true;
        }catch (IOException e){
            System.out.println("problems with write to disk of SPIMI");
            e.printStackTrace();
            return false;
        }
    }
    protected static void addPosting(int doc_id,PostingIndex postingIndex){
        if(postingIndex.getPostings().size()!=0){

        }
    }

<<<<<<< HEAD
    public static void main(String[] args) throws  Exception{

        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        execute();
        DocIndex docIndex=new DocIndex();
        docIndex.readFromDisk(fileChannelDI,40);
        System.out.println(docIndex.sortDocIndex().get(0));
=======
    public static void main(String[] args) throws Exception {
        int doc_id = 1;
        String[] lineofDoc;
        String docno;
        FileChannel fileChannelDI = FileChannel.open(Paths.get(PathConfig.getDocIndexDir() + "/DocIndex.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        List<String> tokens;
        HashMap<String, PostingIndex> index = new HashMap<>();

        // Logic similar to the one in execute method
        InputStream file = new FileInputStream(PATH_TO_ARCHIVE_COLLECTION);
        InputStream gzip = new GZIPInputStream(file);
        ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
        ArchiveEntry entry;

        while ((entry = archiveStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    lineofDoc = Preprocess.parseLine(line);
                    docno = lineofDoc[0];
                    lineofDoc[1] = Preprocess.cleanText(lineofDoc[1]);
                    tokens = Preprocess.tokenize(lineofDoc[1]);
                    tokens = Preprocess.removeStopwords(tokens);
                    tokens = Preprocess.applyStemming(tokens);
                    int documentLength = tokens.size();
                    DocIndexEntry docIndexEntry = new DocIndexEntry(docno, documentLength);
                    docIndexEntry.write2Disk(fileChannelDI, offsetDocIndex, doc_id);
                    for (String term : tokens) {
                        PostingIndex posting;
                        if (index.containsKey(term)) {
                            posting = index.get(term);
                        } else {
                            posting = new PostingIndex();
                            index.put(term, posting);
                        }
                    }
                    doc_id++;
                }
                finished = true;
                break;
            }
        }
        
        // Reading from DocIndex
        DocIndex docIndex = new DocIndex();
        docIndex.readFromDisk(0);
        System.out.println(docIndex.sortDocIndex());
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f
    }
}
