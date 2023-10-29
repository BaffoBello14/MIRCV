package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Iterator;

public class PostingIndex {
    // Class variables
    private String term;
    private ArrayList<Posting> postings = new ArrayList<>();
    private ArrayList<SkippingBlock> blocks;
    private SkippingBlock skippingBlockActual;
    private Posting postingActual;

    // Iterators to iterate through postings and skipping blocks.
    private Iterator<Posting> postingIterator;
    private int doc_len_BM25 = 1;
    private int tf_BM25 = 0;
    private Iterator<SkippingBlock> skippingBlockIterator;

    // Method to clear posting lists
    public void closeLists() {
        postings.clear();
        blocks.clear();
    }

    // Method to update BM25 values
    public void updateBM25Values(int tf, int doc_len) {
        // Calculate ratios and update if the new ratio is greater
        float ratio = (float) this.tf_BM25 / (float) (this.doc_len_BM25 + this.tf_BM25);
        float newRatio = (float) tf / (float) (doc_len + tf);
        if (newRatio > ratio) {
            this.tf_BM25 = tf;
            this.doc_len_BM25 = doc_len;
        }
    }

    // Getter for BM25 document length
    public int getDoc_len_BM25() {
        return doc_len_BM25;
    }

    // Getter for BM25 term frequency
    public int getTf_BM25() {
        return tf_BM25;
    }

    // Constructors
    public PostingIndex() {
    }

    public PostingIndex(String term) {
        this.term = term;
    }

    // Getter for the currently pointed posting
    public Posting getPostingActual() {
        return postingActual;
    }

    // Getter for the term
    public String getTerm() {
        return term;
    }

    // Getter for the list of postings
    public ArrayList<Posting> getPostings() {
        return postings;
    }

    // Setter for the term
    public void setTerm(String term) {
        this.term = term;
    }

    // Method to add postings to the list
    public void addPostings(ArrayList<Posting> postings2Add) {
        postings.addAll(postings2Add);
    }

    // Method to open the posting list
    public void openList() {
        // Need to read blocks from lexicon
        // blocks = get blocks from lexicon
        skippingBlockIterator = blocks.iterator();
        postingIterator = postings.iterator();
    }

    // Method to get the next posting
    public Posting next() {
        if (!postingIterator.hasNext()) {
            if (!skippingBlockIterator.hasNext()) {
                postingActual = null;
                return null;
            }
            skippingBlockActual = skippingBlockIterator.next();
            postings.clear();
            postings.addAll(skippingBlockActual.getSkippingBlockPostings());
            postingIterator = postings.iterator();
        }
        postingActual = postingIterator.next();
        return postingActual;
    }
}
