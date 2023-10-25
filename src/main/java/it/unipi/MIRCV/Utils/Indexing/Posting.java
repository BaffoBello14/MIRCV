package it.unipi.MIRCV.Utils.Indexing;

public class Posting {
    private int doc_id;
    private  int frequency;
    public static final int SIZE = Integer.BYTES + Integer.BYTES;
    public Posting(int doc_id,int frequency){
        this.doc_id=doc_id;
        this.frequency=frequency;
    }

    public int getDoc_id() {
        return doc_id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setDoc_id(int doc_id) {
        this.doc_id = doc_id;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    @Override
    public String toString(){
        return "[doc_id-> "+doc_id+" freq-> "+frequency+"]";
    }
}
