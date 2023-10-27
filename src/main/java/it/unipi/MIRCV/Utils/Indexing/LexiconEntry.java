package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class LexiconEntry {
    // Various attributes of a LexiconEntry.
    private long offset_doc_id = 0;
    private int upperTF = 0; // Upper term frequency.
    private int df = 0; // Document frequency.
    private double idf = 0; // Inverse document frequency.
    private long offset_frequency = 0;
    private long offset_skip_pointer = 0;
    private byte[] termBytes;
    protected static final long ENTRY_SIZE = 4 * 8 + 2 * 4 + Lexicon.MAX_LEN_OF_TERM; // Size of a LexiconEntry when stored.

    // Updates the maximum term frequency using the provided PostingIndex.
    public void updateTFMAX(PostingIndex index) {
        for (Posting posting : index.getPostings()) {
            if (posting.getFrequency() > this.upperTF) {
                this.upperTF = posting.getFrequency();
            }
            this.df++;
        }
    }

    // Calculates and returns the inverse document frequency.
    public double calculateIDF() {
        this.idf = Math.log(CollectionStatistics.getDocuments() / (double) this.df);
        return this.idf;
    }

    // Custom string representation for a LexiconEntry.
    @Override
    public String toString() {
        return "LexiconEntry{" +
                "offset_doc_id=" + offset_doc_id +
                ", upperTF=" + upperTF +
                ", df=" + df +
                ", idf=" + idf +
                ", offset_frequency=" + offset_frequency +
                ", offset_skip_pointer=" + offset_skip_pointer +
                '}';
    }

    // Increments the document frequency by 1.
    public void incrementDf() {
        this.df++;
    }

    // Various getter and setter methods for the attributes.

    public long getOffset_doc_id() {
        return offset_doc_id;
    }

    public void setOffset_doc_id(long offset_doc_id) {
        this.offset_doc_id = offset_doc_id;
    }

    public int getUpperTF() {
        return upperTF;
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

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public byte[] getTermBytes() {
        return termBytes;
    }
    
    public void setTermBytes(byte[] termBytes) {
        this.termBytes = termBytes;
    }    

    public long getOffset_frequency() {
        return offset_frequency;
    }

    public void setOffset_frequency(long offset_frequency) {
        this.offset_frequency = offset_frequency;
    }

    public long getOffset_skip_pointer() {
        return offset_skip_pointer;
    }

    public void setOffset_skip_pointer(long offset_skip_pointer) {
        this.offset_skip_pointer = offset_skip_pointer;
    }

    // Reads a LexiconEntry from disk.
    public long readEntryFromDisk(long offset, FileChannel fileChannel) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, LexiconEntry.ENTRY_SIZE);
            if (mappedByteBuffer == null) {
                return -1;
            }

            byte[] termBytes = new byte[Lexicon.MAX_LEN_OF_TERM];
            mappedByteBuffer.get(termBytes);
            offset_doc_id = mappedByteBuffer.getLong();
            offset_frequency = mappedByteBuffer.getLong();
            upperTF = mappedByteBuffer.getInt();
            df = mappedByteBuffer.getInt();
            idf = mappedByteBuffer.getDouble();
            offset_skip_pointer = mappedByteBuffer.getLong();

            return offset + ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Writes a LexiconEntry to disk.
    public long writeEntryToDisk(String term, long offset, FileChannel fileChannel) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, LexiconEntry.ENTRY_SIZE);
            if (mappedByteBuffer == null) {
                return -1;
            }

            System.out.println(term + toString());
            mappedByteBuffer.put(Lexicon.padStringToLength(term).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(offset_doc_id);
            mappedByteBuffer.putLong(offset_frequency);
            mappedByteBuffer.putInt(upperTF);
            mappedByteBuffer.putInt(df);
            mappedByteBuffer.putDouble(idf);
            mappedByteBuffer.putLong(offset_skip_pointer);

            return offset + LexiconEntry.ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }  
}
