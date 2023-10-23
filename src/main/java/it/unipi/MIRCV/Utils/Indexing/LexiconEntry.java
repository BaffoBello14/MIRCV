package it.unipi.MIRCV.Utils.Indexing;

public class LexiconEntry {
    private long offset_doc_id;
    private long offset_frequency;
    private long offset_skip_pointer;
    private float term_upper_bound;
    private long offset_last_doc_id;

    public LexiconEntry(long offset_doc_id, long offset_frequency, long offset_skip_pointer, float term_upper_bound, long offset_last_doc_id, long num_posting) {
        this.offset_doc_id = offset_doc_id;
        this.offset_frequency = offset_frequency;
        this.offset_skip_pointer = offset_skip_pointer;
        this.term_upper_bound = term_upper_bound;
        this.offset_last_doc_id = offset_last_doc_id;
        this.num_posting = num_posting;
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

    public float getTerm_upper_bound() {
        return term_upper_bound;
    }

    public void setTerm_upper_bound(float term_upper_bound) {
        this.term_upper_bound = term_upper_bound;
    }

    public long getOffset_last_doc_id() {
        return offset_last_doc_id;
    }

    public void setOffset_last_doc_id(long offset_last_doc_id) {
        this.offset_last_doc_id = offset_last_doc_id;
    }

    private long num_posting;

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
    public long getNum_posting() {
        return num_posting;
    }
    public void setNum_posting(long num_posting) {
        this.num_posting = num_posting;
    }
    @Override
    public String toString(){
        return "doc_id_offset "+offset_doc_id+" freq_offset "+offset_frequency+" offset_skip_pointer "+offset_skip_pointer+" term_upper_bound "+term_upper_bound+" num_posting "+num_posting+" offset_last_doc_id "+offset_last_doc_id;
    }
}
