package it.unipi.MIRCV.Utils.Indexing;

public class Posting {
    // Attributes to hold the document ID and its frequency.
    private int doc_id;
    private int frequency = 0;

    // Constructor initializes the posting with a document ID and its frequency.
    public Posting(int doc_id, int frequency) {
        this.doc_id = doc_id;
        this.frequency = frequency;
    }

    // Getter method to retrieve the document ID.
    public int getDoc_id() {
        return doc_id;
    }

    // Getter method to retrieve the frequency of the term in the document.
    public int getFrequency() {
        return frequency;
    }

    // Setter method to update the document ID.
    public void setDoc_id(int doc_id) {
        this.doc_id = doc_id;
    }

    // Setter method to update the frequency of the term in the document.
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    // Custom string representation for a posting.
    @Override
    public String toString() {
        return "[doc_id-> " + doc_id + " freq-> " + frequency + "]";
    }
}
