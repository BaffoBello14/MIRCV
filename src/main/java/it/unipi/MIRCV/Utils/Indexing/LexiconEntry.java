package it.unipi.MIRCV.Utils.Indexing;

public class LexiconEntry {
    private long offset_doc_id = 0;
    private int upperTF = 0;
    private int df = 0;
    private double idf = 0;
    private long offset_frequency = 0;
    private long offset_skip_pointer = 0;
    private byte[] termBytes;
    protected static final long ENTRY_SIZE = 4 * 8 + 2 * 4 + Lexicon.MAX_LEN_OF_TERM;

    public void updateTFMAX(PostingIndex index) {
        for (Posting posting : index.getPostings()) {
            if (posting.getFrequency() > this.upperTF) {
                this.upperTF = posting.getFrequency();
            }
            this.df++;
        }
    }

    public double calculateIDF() {
        this.idf = Math.log(CollectionStatistics.getDocuments() / (double) this.df);
        return this.idf;
    }

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

    public void incrementDf() {
        this.df++;
    }

    // Getter and Setter methods
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
}
