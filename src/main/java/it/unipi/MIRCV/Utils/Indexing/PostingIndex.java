package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Iterator;

public class PostingIndex {
    // Term associated with the index.
    private String term;

    // List of postings for the term.
    private ArrayList<Posting> postings = new ArrayList<>();

    // List of skipping blocks for the term.
    private ArrayList<SkippingBlock> blocks;

    // Reference to the current skipping block and posting during iteration.
    private SkippingBlock skippingBlockActual;
    private Posting postingActual;

    // Iterators to iterate through postings and skipping blocks.
    private Iterator<Posting> postingIterator;
    private Iterator<SkippingBlock> skippingBlockIterator;

    // Flag to determine if compression is enabled.
    private boolean compression;

    // Clear lists of postings and blocks.
    public void closeLists() {
        postings.clear();
        blocks.clear();
    }

    // Default constructor.
    public PostingIndex() {}

    // Constructor to initialize with a term.
    public PostingIndex(String term) {
        this.term = Lexicon.padStringToLength(term);
    }

    // Setter for the compression flag.
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    // Getter for the current posting.
    public Posting getPostingActual() {
        return postingActual;
    }

    // Getter for the term.
    public String getTerm() {
        return term;
    }

    // Getter for the list of postings.
    public ArrayList<Posting> getPostings() {
        return postings;
    }

    // Setter for the term.
    public void setTerm(String term) {
        this.term = term;
    }

    // Add a list of postings to the current list.
    public void addPostings(ArrayList<Posting> postings2Add) {
        postings.addAll(postings2Add);
    }

    // Prepare to iterate through the lists of postings and skipping blocks.
    public void openList() {
        // Need to read blocks from the lexicon.
        // blocks = get blocks from the lexicon (Note: this line is commented out and may need to be implemented)
        skippingBlockIterator = blocks.iterator();
        postingIterator = postings.iterator();
    }

    // Iterate to the next posting.
    public Posting next() {
        if (!postingIterator.hasNext()) {
            if (!skippingBlockIterator.hasNext()) {
                postingActual = null;
                return null;
            }
            skippingBlockActual = skippingBlockIterator.next();
            postings.clear();
            postings.addAll(skippingBlockActual.getSkippingBlockPostings(compression));
            postingIterator = postings.iterator();
        }
        postingActual = postingIterator.next();
        return postingActual;
    }
}
