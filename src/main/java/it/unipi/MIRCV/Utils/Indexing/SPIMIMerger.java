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

/**
 * SPIMIMerger class for merging inverted indexes created by SPIMI.
 */
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
    private static FileChannel fileChannelDOCID = null;

    static {
        try {
            fileChannelDOCID = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX), StandardOpenOption.READ);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Problems with opening the docindex file in the spimi merger");
        }
    }

    /**
     * Sets the number of indexes produced by SPIMI.
     *
     * @param num_index The number of indexes produced by SPIMI.
     */
    public static void setNumIndex(int num_index) {
        SPIMI_Index = num_index;
    }

    /**
     * Loads the inverted list from a specific lexicon entry.
     *
     * @param lexiconEntry The lexicon entry.
     * @param index        The index of the lexicon entry.
     * @return The loaded PostingIndex.
     */
    private static PostingIndex loadList(LexiconEntry lexiconEntry, int index) {
        PostingIndex postingIndex;
        try {
            MappedByteBuffer mappedByteBufferDOCID = doc_id_File_channels[index].map(FileChannel.MapMode.READ_ONLY, lexiconEntry.getOffset_doc_id(), lexiconEntry.getDf() * 4L);
            MappedByteBuffer mappedByteBufferFreq = freq_file_channels[index].map(FileChannel.MapMode.READ_ONLY, lexiconEntry.getOffset_frequency(), lexiconEntry.getDf() * 4L);
            postingIndex = new PostingIndex(lexiconEntry.getTerm());

            for (int i = 0; i < lexiconEntry.getDf(); i++) {
                Posting posting = new Posting(mappedByteBufferDOCID.getInt(), mappedByteBufferFreq.getInt());
                postingIndex.getPostings().add(posting);
            }

            return postingIndex;
        } catch (IOException e) {
            System.out.println("Problems with opening the file of the doc_id or freq channel in load list of merger");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes the SPIMI merging algorithm.
     *
     * @return True if the merging is successful, false otherwise.
     */
    public static boolean execute() {
        if (SPIMI_Index == -1) {
            System.out.println("Set the index produced by SPIMI Splitter");
            return false;
        }

        lexiconEntries = new LexiconEntry[SPIMI_Index];
        lexiconOffsets = new long[SPIMI_Index];
        doc_id_File_channels = new FileChannel[SPIMI_Index];
        freq_file_channels = new FileChannel[SPIMI_Index];
        long ifError;

        try {
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
            System.out.println("Error opening the file on merger of SPIMI for doc_id or freq file");
            e.printStackTrace();
            return false;
        }

        try {
            FileChannel fileChannelLexicon = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelDocIdFinal = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_DOC_ID), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelFreqFinal = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_FREQ), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelBlockInfo = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_BLOCK_FILE), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            while (true) {
                String termToProcess = getTermToProcess();

                if (termToProcess == null) {
                    System.out.println("End of merging");
                    break;
                }

                if (termToProcess.isEmpty()) {
                    continue;
                }

                LexiconEntry lexiconEntry = new LexiconEntry();
                lexiconEntry.setTerm(termToProcess);
                PostingIndex mergedPosting = processTerm(lexiconEntry, termToProcess);

                if (mergedPosting == null) {
                    System.out.println("Error with the merging of the list of " + termToProcess);
                    return false;
                }

                lexiconEntry.calculateBlockNeed();
                int numPostingPerBlock = (int) Math.ceil(lexiconEntry.getDf() / (double) lexiconEntry.getNumBlocks());
                int numBlocks = lexiconEntry.getNumBlocks();
                Iterator<Posting> postingIterator = mergedPosting.getPostings().iterator();

                for (int i = 0; i < numBlocks; i++) {
                    SkippingBlock skippingBlock = new SkippingBlock();
                    skippingBlock.setDoc_id_offset(doc_id_offset);
                    skippingBlock.setFreq_offset(freq_offset);
                    int postingInTheSkippingBlock = 0;
                    int writtenPostings = i * numPostingPerBlock;
                    int numPostingTobeWriteToThisBlock = Math.min((mergedPosting.getPostings().size() - writtenPostings), numPostingPerBlock);

                    if (PathAndFlags.COMPRESSION_ENABLED) {
                        int[] doc_id = new int[numPostingTobeWriteToThisBlock];
                        int[] freq = new int[numPostingTobeWriteToThisBlock];

                        while (true) {
                            if (!postingIterator.hasNext()) {
                                System.out.println(mergedPosting.getPostings().size());
                                System.out.println(writtenPostings);
                                System.out.println(numPostingTobeWriteToThisBlock);
                                return false;
                            }

                            Posting actualPosting = postingIterator.next();
                            doc_id[postingInTheSkippingBlock] = actualPosting.getDoc_id();
                            freq[postingInTheSkippingBlock] = actualPosting.getFrequency();
                            postingInTheSkippingBlock++;

                            if (numPostingTobeWriteToThisBlock == postingInTheSkippingBlock) {
                                byte[] compressed_doc_ids = VariableByteEncoder.encodeArray(doc_id);
                                byte[] compressed_freqs = UnaryConverter.convertToUnary(freq);

                                skippingBlock.setDoc_id_size(compressed_doc_ids.length);
                                skippingBlock.setFreq_size(compressed_freqs.length);

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
                                    System.out.println("Problems with opening the file to write the compressed doc_ids and freqs in the merging algorithm of SPIMI");
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                        }
                    } else {
                        skippingBlock.setDoc_id_size(numPostingTobeWriteToThisBlock * 4);
                        skippingBlock.setFreq_size(numPostingTobeWriteToThisBlock * 4);

                        try {
                            MappedByteBuffer mappedByteBufferDOCID = fileChannelDocIdFinal.map(FileChannel.MapMode.READ_WRITE, doc_id_offset, numPostingTobeWriteToThisBlock * 4L);
                            MappedByteBuffer mappedByteBufferFREQS = fileChannelFreqFinal.map(FileChannel.MapMode.READ_WRITE, freq_offset, numPostingTobeWriteToThisBlock * 4L);

                            if (mappedByteBufferDOCID == null || mappedByteBufferFREQS == null) {
                                System.out.println("Channel for doc_id or freq of no compressed version of merger is not open");
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

                                if (numPostingTobeWriteToThisBlock == postingInTheSkippingBlock) {
                                    skippingBlock.setDoc_id_max(actualPosting.getDoc_id());
                                    skippingBlock.setNum_posting_of_block(postingInTheSkippingBlock);
                                    skippingBlock.writeOnDisk(fileChannelBlockInfo);

                                    doc_id_offset += numPostingTobeWriteToThisBlock * 4L;
                                    freq_offset += numPostingTobeWriteToThisBlock * 4L;

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
                lexSize++;
            }

            fileChannelBlockInfo.close();
            fileChannelLexicon.close();
            fileChannelDocIdFinal.close();
            fileChannelFreqFinal.close();

            for (int i = 0; i < SPIMI_Index; i++) {
                doc_id_File_channels[i].close();
                freq_file_channels[i].close();
            }

            CollectionStatistics.setTerms(lexSize);
            CollectionStatistics.write2Disk();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with opening file channels of lexicon, doc_id, freq, partial, or total");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the term to process in the merging algorithm.
     *
     * @return The term to process.
     */
    private static String getTermToProcess() {
        String termToProcess, nextTerm;
        termToProcess = null;

        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] == null) {
                continue;
            }

            nextTerm = lexiconEntries[i].getTerm();

            if (termToProcess == null) {
                termToProcess = nextTerm;
                continue;
            }

            if (termToProcess.compareTo(nextTerm) > 0) {
                termToProcess = nextTerm;
            }
        }

        return termToProcess;
    }

    /**
     * Moves to the next term in the lexicon entries.
     *
     * @param term The current term.
     */
    private static void moveToNextTermLexicon(String term) {
        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] != null && lexiconEntries[i].getTerm().equals(term)) {
                try {
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
                    System.out.println("Problems with opening the file of lexicon partial when moving to the next term during merging");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    
    /**
     * Processes a term during the merging algorithm.
     *
     * @param lexiconEntry     The lexicon entry for the term.
     * @param termToProcess    The term to process.
     * @return The merged PostingIndex.
     */
    private static PostingIndex processTerm(LexiconEntry lexiconEntry, String termToProcess) {
        PostingIndex mergedPosting = new PostingIndex();
        mergedPosting.setTerm(termToProcess);

        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] != null && lexiconEntries[i].getTerm().equals(termToProcess)) {
                PostingIndex partialPosting = loadList(lexiconEntries[i], i);

                if (partialPosting == null) {
                    System.out.println("Partial index null for entry -> " + i);
                    return null;
                }

                lexiconEntry.updateTFMAX(partialPosting);
                mergedPosting.addPostings(partialPosting.getPostings());
            }
        }

        float BM25Upper = 0F;
        float actualBM25, idf;
        int N = CollectionStatistics.getDocuments();
        idf = (float) ((Math.log((double) N / mergedPosting.getPostings().size())));
        int tf = 0;

        for (Posting posting : mergedPosting.getPostings()) {
            actualBM25 = calculateBM25WithoutIDF((float) (1 + Math.log(posting.getFrequency())), posting.getDoc_id());

            if (actualBM25 != -1F && actualBM25 > BM25Upper) {
                BM25Upper = actualBM25;
            }

            if (tf < posting.getFrequency()) {
                tf = posting.getFrequency();
            }
        }

        lexiconEntry.setIdf(idf);
        lexiconEntry.setDf(mergedPosting.getPostings().size());
        lexiconEntry.setUpperBM25(BM25Upper * idf);
        moveToNextTermLexicon(termToProcess);
        lexiconEntry.setOffset_doc_id(doc_id_offset);
        lexiconEntry.setOffset_frequency(freq_offset);
        lexiconEntry.setUpperTFIDF((float) ((1 + Math.log(tf)) * idf));
        return mergedPosting;
    }

    /**
     * Calculates BM25 without IDF for a given term frequency and document ID.
     *
     * @param tf    The term frequency.
     * @param doc_id The document ID.
     * @return The calculated BM25 score without IDF.
     */
    private static float calculateBM25WithoutIDF(float tf, long doc_id) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannelDOCID.map(FileChannel.MapMode.READ_ONLY, (doc_id - 1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE + DocIndexEntry.DOC_NO_LENGTH + 4, 8);
            long doclen = mappedByteBuffer.getLong();
            return (float) ((tf / (tf + PathAndFlags.BM25_k1 * (1 - PathAndFlags.BM25_b + PathAndFlags.BM25_b * (doclen / CollectionStatistics.getAvgDocLen())))));
        } catch (IOException e) {
            System.out.println("Problems with opening the file channel of doc id in calculate BM25 in merger");
            e.printStackTrace();
            return -1F;
        }
    }
}
