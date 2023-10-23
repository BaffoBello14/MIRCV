package it.unipi.MIRCV.Utils.Indexing;

import java.util.Collections;

public class CollectionStatistics {
    private int documents;
    private double avgDocLen;
    private long terms;
    private long posting;

    public CollectionStatistics(int documents, double avgDocLen, long terms, long posting) {
        this.documents = documents;
        this.avgDocLen = avgDocLen;
        this.terms = terms;
        this.posting = posting;
    }

    public int getDocuments() {
        return documents;
    }

    public void setDocuments(int documents) {
        this.documents = documents;
    }

    public double getAvgDocLen() {
        return avgDocLen;
    }

    public void setAvgDocLen(double avgDocLen) {
        this.avgDocLen = avgDocLen;
    }

    public long getTerms() {
        return terms;
    }

    public void setTerms(long terms) {
        this.terms = terms;
    }

    public long getPosting() {
        return posting;
    }

    public void setPosting(long posting) {
        this.posting = posting;
    }
}
