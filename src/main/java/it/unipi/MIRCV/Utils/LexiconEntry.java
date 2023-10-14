package it.unipi.MIRCV.Utils;

public class LexiconEntry {
    private String term;
    private long term_id;
    private long offset_doc_id;
    private long offset_frequency;
    private int df;
    private long num_posting;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public long getTerm_id() {
        return term_id;
    }

    public void setTerm_id(long term_id) {
        this.term_id = term_id;
    }

    public long getOffset_doc_id() {
        return offset_doc_id;
    }

    public void setOffset_doc_id(long offset_doc_id) {
        this.offset_doc_id = offset_doc_id;
    }

    public long getoffset_frequency() {
        return offset_frequency;
    }

    public void setoffset_frequency(long getOffset_frequency) {
        this.offset_frequency = getOffset_frequency;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public long getNum_posting() {
        return num_posting;
    }

    public void setNum_posting(long num_posting) {
        this.num_posting = num_posting;
    }
}
