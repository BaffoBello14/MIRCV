package it.unipi.MIRCV.Utils;

public class DocIndex {
    private static long doc_id=0;
    private String doc_no;
    private String URL;
    private long len;


    public static void setDoc_id(long doc_id) {
        DocIndex.doc_id = doc_id;
    }

    public long getDoc_id() {
        return doc_id;
    }

    public String getDoc_no() {
        return doc_no;
    }

    public String getURL() {
        return URL;
    }

    public long getLen() {
        return len;
    }



    public void setDoc_no(String doc_no) {
        this.doc_no = doc_no;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setLen(long len) {
        this.len = len;
    }

}
