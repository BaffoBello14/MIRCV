package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class SPIMIMerger {
    private static long[] lexiconOffsets;
    private static long doc_id_offset = 0;
    private static long freq_offset = 0;
    private static long lexSize = 0;
    private static long lex_offset = 0;
    private static int SPIMI_Index = -1;
    private static LexiconEntry[] lexiconEntries;
    private static FileChannel[] doc_id_File_channels;
    private static FileChannel[] freq_file_channels;

    // Set the number of indexes for the SPIMI Merger.
    public static void setNumIndex(int num_index) {
        SPIMI_Index = num_index;
    }

    // Load a posting list from the index file.
    private static PostingIndex loadList(LexiconEntry lexiconEntry, int index) {
        PostingIndex postingIndex;
        try {
            // Map the doc_id data into memory.
            MappedByteBuffer mappedByteBufferDOCID = doc_id_File_channels[index].map(FileChannel.MapMode.READ_ONLY, lexiconEntry.getOffset_doc_id(), lexiconEntry.getDocidByteSize);
            // Map the frequency data into memory.
            MappedByteBuffer mappedByteBufferFreq = freq_file_channels[index].map(FileChannel.MapMode.READ_ONLY, lexiconEntry.getOffset_frequency(), lexiconEntry.getFreqByteSize);
            // Create a new PostingIndex for the term.
            postingIndex = new PostingIndex(lexiconEntry.getTerm());
            for (int i = 0; i < lexiconEntry.getDf(); i++) {
                // Read doc_id and frequency and create a Posting.
                Posting posting = new Posting(mappedByteBufferDOCID.getInt(), mappedByteBufferFreq.getInt());
                // Add the Posting to the PostingIndex.
                postingIndex.getPostings().add(posting);
            }
            return postingIndex;
        } catch (IOException e) {
            System.out.println("Problems with opening the file of the doc_id or freq channel in load list of merger.");
            e.printStackTrace();
            return null;
        }
    }


    public static boolean execute() {
        // Check if the SPIMI index is set.
        if (SPIMI_Index == -1) {
            System.out.println("Set the index produced by SPIMI Splitter");
            return false;
        }

        // Initialize arrays and variables.
        lexiconEntries = new LexiconEntry[SPIMI_Index];
        lexiconOffsets = new long[SPIMI_Index];
        doc_id_File_channels = new FileChannel[SPIMI_Index];
        freq_file_channels = new FileChannel[SPIMI_Index];
        long ifError;

        try {
            // Open and read lexicon entries from individual SPIMI Splitter output files.
            for (int i = 0; i < SPIMI_Index; i++) {
                lexiconEntries[i] = new LexiconEntry();
                lexiconOffsets[i] = 0;
                FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON + i + ".dat"), StandardOpenOption.READ);
                ifError = lexiconEntries[i].readEntryFromDisk(0, fileChannel);
                fileChannel.close();

                if (ifError == 0 || ifError == -1) {
                    System.out.println("Lexicon " + i + " not opened");
                    lexiconEntries[i] = null;
                }

                doc_id_File_channels[i] = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID + i + ".dat"), StandardOpenOption.READ);
                freq_file_channels[i] = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FREQ + i + ".dat"), StandardOpenOption.READ);
            }
        } catch (IOException e) {
            System.out.println("Error on opening the file on merger of SPIMI for doc id or freq file");
            e.printStackTrace();
            return false;
        }

        try {
            // Open final output files for lexicon, doc_id, freq, and block info.
            FileChannel fileChannelLexicon = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelDocIdFinal = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_DOC_ID), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelFreqFinal = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_FREQ), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelBlockInfo = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_BLOCK_FILE), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            while (true) {
                // Get the next term to process.
                String termToProcess = getTermToProcess();

                if (termToProcess == null) {
                    System.out.println("End of merging");
                    break;
                }

                if (termToProcess.isEmpty()) {
                    continue;
                }

                System.out.print(termToProcess);

                LexiconEntry lexiconEntry = new LexiconEntry();
                lexiconEntry.setTerm(termToProcess);

                PostingIndex mergedPosting = processTerm(lexiconEntry, termToProcess);

                if (mergedPosting == null) {
                    System.out.println("Error with the merging of the list of " + termToProcess);
                    return false;
                }

                lexiconEntry.calculateBlockNeed();
                int numPostingPerBlock = (int) Math.ceil(lexiconEntry.getDf() / (double) lexiconEntry.getNumBlocks);
                int numBlocks = lexiconEntry.getNumBlocks();
                Iterator<Posting> postingIterator = mergedPosting.getPostings().iterator();

                for (int i = 0; i < numBlocks; i++) {
                    SkippingBlock skippingBlock = new SkippingBlock();
                    skippingBlock.setDoc_id_offset(doc_id_offset);
                    skippingBlock.setFreq_offset(freq_offset);
                    int postingInTheSkippingBlock = 0;
                    int writtenPostings = i * numPostingPerBlock;
                    int numPostingToBeWriteToThisBlock = Math.min((mergedPosting.getPostings().size() - writtenPostings), numPostingPerBlock);

                    if (PathAndFlags.COMPRESSION_ENABLED) {
                        int[] doc_id = new int[numPostingToBeWriteToThisBlock];
                        int[] freq = new int[numPostingToBeWriteToThisBlock];

                        while (true) {
                            if (!postingIterator.hasNext()) {
                                System.out.println(mergedPosting.getPostings().size());
                                System.out.println(writtenPostings);
                                System.out.println(numPostingToBeWriteToThisBlock);
                                return false;
                            }
                            Posting actualPosting = postingIterator.next();
                            doc_id[postingInTheSkippingBlock] = actualPosting.getDoc_id();
                            freq[postingInTheSkippingBlock] = actualPosting.getFrequency();
                            postingInTheSkippingBlock++;

                            if (numPostingToBeWriteToThisBlock == postingInTheSkippingBlock) {
                                byte[] compressed_doc_ids = VariableByteEncoder.encodeArray(doc_id);
                                byte[] compressed_freqs = UnaryConverter.convertToUnary(freq);

                                try {
                                    MappedByteBuffer mappedByteBufferDOCID = fileChannelDocIdFinal.map(FileChannel.MapMode.READ_WRITE, doc_id_offset, compressed_doc_ids.length);
                                    MappedByteBuffer mappedByteBufferFREQS = fileChannelFreqFinal.map(FileChannel.MapMode.READ_WRITE, freq_offset, compressed_freqs.length);

                                    mappedByteBufferDOCID.put(compressed_doc_ids);
                                    mappedByteBufferFREQS.put(compressed_freqs);

                                    skippingBlock.setDoc_id_max(actualPosting.getDoc_id());
                                    skippingBlock.setNum_posting_of_block(postingInTheSkippingBlock);
                                    skippingBlock.writeOnDisk(fileChannelBlockInfo);

                                    doc_id_offset += compressed_doc_ids.length;
                                    freq_offset += compressed_freqs.length;
                                    break;
                                } catch (IOException e) {
                                    System.out.println("Problems with opening the file to write the compressed docids and freqs on the merging algorithm of SPIMI");
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                        }
                    } else {
                        skippingBlock.setDoc_id_size(numPostingToBeWriteToThisBlock * 4);
                        skippingBlock.setFreq_size(numPostingToBeWriteToThisBlock * 4);

                        try {
                            MappedByteBuffer mappedByteBufferDOCID = fileChannelDocIdFinal.map(FileChannel.MapMode.READ_WRITE, doc_id_offset, numPostingToBeWriteToThisBlock * 4L);
                            MappedByteBuffer mappedByteBufferFREQS = fileChannelFreqFinal.map(FileChannel.MapMode.READ_WRITE, freq_offset, numPostingToBeWriteToThisBlock * 4L);

                            if (mappedByteBufferDOCID == null || mappedByteBufferFREQS == null) {
                                System.out.println("Channel for doc id or freq of non-compressed version of merger is not open");
                                return false;
                            }

                            while (true) {
                                if (!postingIterator.hasNext()) {
                                    break;
                                }
                                Posting actualPosting = postingIterator.next();
                                mappedByteBufferDOCID.putInt(actualPosting.getDoc_id());
                                mappedByteBufferFREQS.putInt(actualPosting.getFrequency());
                                postingInTheSkippingBlock++;

                                if (numPostingToBeWriteToThisBlock == postingInTheSkippingBlock) {
                                    skippingBlock.setDoc_id_max(actualPosting.getDoc_id());
                                    skippingBlock.setNum_posting_of_block(postingInTheSkippingBlock);
                                    skippingBlock.writeOnDisk(fileChannelBlockInfo);

                                    doc_id_offset += numPostingToBeWriteToThisBlock * 4L;
                                    freq_offset += numPostingToBeWriteToThisBlock * 4L;
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Problems with the file channels of the non-compressed version of the merger");
                            e.printStackTrace();
                            return false;
                        }
                    }
                }

                lex_offset = lexiconEntry.writeEntryToDisk(termToProcess, lex_offset, fileChannelLexicon);
                System.out.println(lexiconEntry);
                lexSize++;
            }

            // Close file channels.
            fileChannelBlockInfo.close();
            fileChannelLexicon.close();
            fileChannelDocIdFinal.close();
            fileChannelFreqFinal.close();

            for (int i = 0; i < SPIMI_Index; i++) {
                doc_id_File_channels[i].close();
                freq_file_channels[i].close();
            }

            // Update collection statistics.
            CollectionStatistics.setTerms(lexSize);
            CollectionStatistics.write2Disk();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with opening file channels of lexicon, doc id, freq, partial or total");
            e.printStackTrace();
            return false;
        }
    }


    private static String getTermToProcess() {
        String termToProcess, nextTerm;
        termToProcess = null;

        // Iterate through the lexicon entries to find the next term to process.
        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] == null) {
                continue;
            }
            nextTerm = lexiconEntries[i].getTerm;

            // If termToProcess is null, set it to nextTerm.
            if (termToProcess == null) {
                termToProcess = nextTerm;
                continue;
            }

            // Compare terms and update termToProcess if needed.
            if (termToProcess.compareTo(nextTerm) > 0) {
                termToProcess = nextTerm;
            }
        }
        return termToProcess;
    }


    private static void moveToNextTermLexicon(String term) {
        // Iterate through the lexicon entries to move to the next term.
        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] != null && lexiconEntries[i].getTerm().equals(term)) {
                try {
                    // Update the lexicon offset and read the next lexicon entry.
                    lexiconOffsets[i] += LexiconEntry.ENTRY_SIZE;
                    FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON + i + ".dat"), StandardOpenOption.READ);

                    if (lexiconOffsets[i] < fileChannel.size()) {
                        long ifError = lexiconEntries[i].readEntryFromDisk(lexiconOffsets[i], fileChannel);
                        if (ifError == 0 || ifError == -1) {
                            lexiconEntries[i] = null;
                        }
                    } else {
                        lexiconEntries[i] = null;
                    }

                    fileChannel.close();
                } catch (IOException e) {
                    System.out.println("Problem with opening the file of lexicon partial when moving to the next term in merge");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static PostingIndex processTerm(LexiconEntry lexiconEntry, String termToProcess) {
        // Create a new PostingIndex for the term to store merged postings.
        PostingIndex mergedPosting = new PostingIndex();
        mergedPosting.setTerm(termToProcess);

        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] != null && lexiconEntries[i].getTerm().equals(termToProcess)) {
                // Load the posting list for the term from the current SPIMI index.
                PostingIndex partialPosting = loadList(lexiconEntries[i], i);

                if (partialPosting == null) {
                    System.out.println("Partial index is null for entry -> " + i);
                    return null;
                }

                // Update BM25 values, TFMAX, and add postings to the mergedPosting.
                lexiconEntry.updateBM25Values(lexiconEntries[i].getTf(), lexiconEntries[i].getDoclen());
                lexiconEntry.updateTFMAX(partialPosting);
                mergedPosting.addPostings(partialPosting.getPostings());
            }
        }

        // Move to the next term in the lexicon.
        moveToNextTermLexicon(termToProcess);

        // Set doc_id and frequency offsets, calculate IDF, and upper bounds.
        lexiconEntry.setOffset_doc_id(doc_id_offset);
        lexiconEntry.setOffset_frequency(freq_offset);
        lexiconEntry.calculateIDF();
        lexiconEntry.calculateUpperBounds();

        return mergedPosting;
    }
}