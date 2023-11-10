package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents an index of postings for a specific term in the context of information retrieval.
 */
public class PostingIndex {
    private String term;  // Term associated with the postings
    private ArrayList<Posting> postings = new ArrayList<>();  // List of postings for the term
    private ArrayList<SkippingBlock> blocks;  // List of skipping blocks for efficient iteration
    private SkippingBlock skippingBlockActual;  // Currently active skipping block
    private Posting postingActual;  // Currently active posting
    private Iterator<Posting> postingIterator;  // Iterator for postings
    private Iterator<SkippingBlock> skippingBlockIterator;  // Iterator for skipping blocks
    private float upperBound;
    private float idf;

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public float getIdf() {
        return idf;
    }

    public void setIdf(float idf) {
        this.idf = idf;
    }

    /**
     * Closes the posting and block lists associated with this term.
     */
    public void closeLists() {
        postings.clear();
        blocks.clear();
        // Lexicon.getInstance().remove(term);
    }

    /*
    public void updateBM25Values(int tf, int doc_len){
        float ratio=(float)this.tf_BM25/(float)(this.doc_len_BM25+this.tf_BM25);
        float newRatio=(float)tf/(float)(doc_len+tf);
        if(newRatio>ratio){
            this.tf_BM25=tf;
            this.doc_len_BM25=doc_len;
        }
    }

    public int getDoc_len_BM25() {
        return doc_len_BM25;
    }

    public int getTf_BM25() {
        return tf_BM25;
    }
    */

    /**
     * Initializes a new instance of the PostingIndex class.
     */
    public PostingIndex() {}

    /**
     * Initializes a new instance of the PostingIndex class with the specified term.
     *
     * @param term The term associated with the posting index.
     */
    public PostingIndex(String term) {
        this.term = term;
    }

    /**
     * Gets the currently active posting.
     *
     * @return The currently active posting.
     */
    public Posting getPostingActual() {
        return postingActual;
    }

    /**
     * Gets the term associated with this posting index.
     *
     * @return The term.
     */
    public String getTerm() {
        return term;
    }

    /**
     * Gets the list of postings for the term.
     *
     * @return The list of postings.
     */
    public ArrayList<Posting> getPostings() {
        return postings;
    }

    /**
     * Sets the term for this posting index.
     *
     * @param term The term to be set.
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * Adds a list of postings to the existing postings.
     *
     * @param postings2Add The list of postings to add.
     */
    public void addPostings(ArrayList<Posting> postings2Add) {
        postings.addAll(postings2Add);
    }

    /**
     * Opens the posting list by reading associated skipping blocks from the lexicon.
     */
    public void openList() {
        blocks = Lexicon.getInstance().get(term).readBlocks();
        if (blocks == null) {
            return;
        }
        skippingBlockIterator = blocks.iterator();
        postingIterator = postings.iterator();
    }

    /**
     * Moves to the next posting in the list.
     *
     * @return The next posting or null if the end is reached.
     */
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

    /**
     * Moves to the next posting with a document ID greater than or equal to the specified value.
     *
     * @param doc_id The document ID to compare.
     * @return The next posting with a document ID greater than or equal to doc_id, or null if not found.
     */
    public Posting nextGEQ(int doc_id) {
        boolean nextBlock = false;
        while (skippingBlockActual == null || skippingBlockActual.getDoc_id_max() < doc_id) {
            if (!skippingBlockIterator.hasNext()) {
                postingActual = null;
                return null;
            }
            skippingBlockActual = skippingBlockIterator.next();
            nextBlock = true;
        }
        if (nextBlock) {
            postings.clear();
            postings.addAll(skippingBlockActual.getSkippingBlockPostings());
            postingIterator = postings.iterator();
        }
        while (postingIterator.hasNext()) {
            postingActual = postingIterator.next();
            if (postingActual.getDoc_id() >= doc_id) {
                return postingActual;
            }
        }
        postingActual = null;
        return null;
    }
}
