package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * Represents an entry in the lexicon, containing information about a term.
 */
public class LexiconEntry {
    private String term;
    private long offset_doc_id = 0;
    private int upperTF = 0;
    private int df = 0;
    private float idf = 0;
    private float upperTFIDF = 0;
    private float upperBM25 = 0;
    private long offset_frequency = 0;
    private long offset_skip_pointer = 0;
    private int numBlocks = 1;

    protected static final long ENTRY_SIZE = 68 + Lexicon.MAX_LEN_OF_TERM - 16;
    private static FileChannel fileChannel =null;

    static{
        try {
            File file=new File(PathAndFlags.PATH_TO_BLOCK_FILE);
            if(file.exists()) {
                fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_BLOCK_FILE), StandardOpenOption.READ);
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("problems with opening file with lexicon entry with block file");
        }
    }

    /**
     * Updates the maximum term frequency (TF) based on the postings in the given PostingIndex.
     *
     * @param index The PostingIndex containing postings for the term.
     */
    public void updateTFMAX(PostingIndex index) {
        for (Posting posting : index.getPostings()) {
            if (posting.getFrequency() > this.upperTF) {
                this.upperTF = posting.getFrequency();
            }
            this.df++;
        }
    }

    // Getter and Setter methods

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public float getUpperTFIDF() {
        return upperTFIDF;
    }

    public void setUpperTFIDF(float upperTFIDF) {
        this.upperTFIDF = upperTFIDF;
    }

    public float getUpperBM25() {
        return upperBM25;
    }

    public void setUpperBM25(float upperBM25) {
        this.upperBM25 = upperBM25;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }

    public long getOffset_doc_id() {
        return offset_doc_id;
    }

    public void setOffset_doc_id(long offset_doc_id) {
        this.offset_doc_id = offset_doc_id;
    }


    public void setUpperTF(int upperTF) {
        this.upperTF = upperTF;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public float getIdf() {
        return idf;
    }

    public void setIdf(float idf) {
        this.idf = idf;
    }

    public long getOffset_frequency() {
        return offset_frequency;
    }

    public void setOffset_frequency(long offset_frequency) {
        this.offset_frequency = offset_frequency;
    }


    public void setOffset_skip_pointer(long offset_skip_pointer) {
        this.offset_skip_pointer = offset_skip_pointer;
    }

    /**
     * Reads the LexiconEntry from the specified offset in the given FileChannel.
     *
     * @param offset      The offset in the FileChannel.
     * @param fileChannel The FileChannel from which to read.
     * @return The updated offset after reading the entry.
     */
    public long readEntryFromDisk(long offset, FileChannel fileChannel) {
        try {
            if (offset >= fileChannel.size()) {
                return -1;
            }
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, LexiconEntry.ENTRY_SIZE);

            if (mappedByteBuffer == null) {
                return -1;
            }

            byte[] termBytes = new byte[Lexicon.MAX_LEN_OF_TERM];
            mappedByteBuffer.get(termBytes);
            term = Lexicon.removePadding(new String(termBytes, StandardCharsets.UTF_8));
            df = (mappedByteBuffer.getInt());
            idf = (mappedByteBuffer.getFloat());
            upperTF = (mappedByteBuffer.getInt());
            upperTFIDF = (mappedByteBuffer.getFloat());
            upperBM25 = (mappedByteBuffer.getFloat());
            offset_doc_id = (mappedByteBuffer.getLong());
            offset_frequency = (mappedByteBuffer.getLong());
            numBlocks = (mappedByteBuffer.getInt());
            offset_skip_pointer = (mappedByteBuffer.getLong());

            return offset + ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Reads the skipping blocks associated with the LexiconEntry from the block file.
     *
     * @return The list of SkippingBlocks.
     */
    public ArrayList<SkippingBlock> readBlocks() {
        try {
            //FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_BLOCK_FILE), StandardOpenOption.READ);
            ArrayList<SkippingBlock> blocks = new ArrayList<>();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset_skip_pointer, (long) numBlocks * SkippingBlock.size_of_element);
            if (mappedByteBuffer == null) {
                return null;
            }

            for (int i = 0; i < numBlocks; i++) {
                SkippingBlock skippingBlock = new SkippingBlock();
                skippingBlock.setDoc_id_offset(mappedByteBuffer.getLong());
                skippingBlock.setDoc_id_size(mappedByteBuffer.getInt());
                skippingBlock.setFreq_offset(mappedByteBuffer.getLong());
                skippingBlock.setFreq_size(mappedByteBuffer.getInt());
                skippingBlock.setDoc_id_max(mappedByteBuffer.getInt());
                skippingBlock.setNum_posting_of_block(mappedByteBuffer.getInt());
                blocks.add(skippingBlock);
            }
            return blocks;
        } catch (IOException e) {
            System.out.println("Problems with reading blocks in the lexicon entry");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes the LexiconEntry to the specified offset in the given FileChannel.
     *
     * @param term        The term associated with the LexiconEntry.
     * @param offset      The offset in the FileChannel.
     * @param fileChannel The FileChannel to which to write.
     * @return The updated offset after writing the entry.
     */
    public long writeEntryToDisk(String term, long offset, FileChannel fileChannel) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, LexiconEntry.ENTRY_SIZE);
            if (mappedByteBuffer == null) {
                return -1;
            }

            mappedByteBuffer.put(Lexicon.padStringToLength(term).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putInt(df);
            mappedByteBuffer.putFloat(idf);
            mappedByteBuffer.putInt(upperTF);
            mappedByteBuffer.putFloat(upperTFIDF);
            mappedByteBuffer.putFloat(upperBM25);
            mappedByteBuffer.putLong(offset_doc_id);
            mappedByteBuffer.putLong(offset_frequency);
            mappedByteBuffer.putInt(numBlocks);
            mappedByteBuffer.putLong(offset_skip_pointer);

            return offset + LexiconEntry.ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Calculates the number of blocks needed for the LexiconEntry based on the term frequency.
     */
    public void calculateBlockNeed() {
        this.offset_skip_pointer = SkippingBlock.getFile_offset();
        if (df > PathAndFlags.POSTING_PER_BLOCK) {
            this.numBlocks = (int) Math.ceil(Math.sqrt(df));
        }
    }

    /**
     * Returns a string representation of the LexiconEntry.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "Term: " + term + " " +
                "Offset Doc ID: " + offset_doc_id + " " +
                "Upper TF: " + upperTF + " " +
                "DF: " + df + " " +
                "IDF: " + idf + " " +
                "Upper TF-IDF: " + upperTFIDF + " " +
                "Upper BM25: " + upperBM25 + " " +
                "Offset Frequency: " + offset_frequency + " " +
                "Offset Skip Pointer: " + offset_skip_pointer + " " +
                "Num Blocks: " + numBlocks + " ";
    }
}
