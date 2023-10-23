package it.unipi.MIRCV.Utils.Indexing;

public class DocIndexEntry {
    private int doc_no;
    private long doc_size;
    public DocIndexEntry(int doc_no,long doc_size){
        this.doc_no=doc_no;
        this.doc_size=doc_size;
    }

    public int getDoc_no() {
        return doc_no;
    }

    public void setDoc_no(int doc_no) {
        this.doc_no = doc_no;
    }

    public long getDoc_size() {
        return doc_size;
    }

    public void setDoc_size(long doc_size) {
        this.doc_size = doc_size;
    }
    @Override
    public String toString(){
        return doc_no+" "+doc_size;
    }

}
