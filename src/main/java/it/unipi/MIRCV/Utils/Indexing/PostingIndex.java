package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Iterator;

public class PostingIndex {
<<<<<<< Updated upstream
    // Term associated with the index.
    private String term;

    // List of postings for the term.
    private ArrayList<Posting> postings = new ArrayList<>();

    // List of skipping blocks for the term.
    private ArrayList<SkippingBlock> blocks;

    // Reference to the current skipping block and posting during iteration.
=======
    // Class variables
    private String term;
    private ArrayList<Posting> postings = new ArrayList<>();
    private ArrayList<SkippingBlock> blocks;
>>>>>>> Stashed changes
    private SkippingBlock skippingBlockActual;
    private Posting postingActual;

    // Iterators to iterate through postings and skipping blocks.
    private Iterator<Posting> postingIterator;
<<<<<<< Updated upstream
    private Iterator<SkippingBlock> skippingBlockIterator;

    // Flag to determine if compression is enabled.
    private boolean compression;

    // Clear lists of postings and blocks.
=======
    private int doc_len_BM25 = 1;
    private int tf_BM25 = 0;
    private Iterator<SkippingBlock> skippingBlockIterator;

    // Method to clear posting lists
>>>>>>> Stashed changes
    public void closeLists() {
        postings.clear();
        blocks.clear();
    }

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
    public Posting getPostingActual() {
        return postingActual;
    }

<<<<<<< Updated upstream
    // Getter for the term.
=======
    // Getter for the term
>>>>>>> Stashed changes
    public String getTerm() {
        return term;
    }

<<<<<<< Updated upstream
    // Getter for the list of postings.
=======
    // Getter for the list of postings
>>>>>>> Stashed changes
    public ArrayList<Posting> getPostings() {
        return postings;
    }

<<<<<<< Updated upstream
    // Setter for the term.
=======
    // Setter for the term
>>>>>>> Stashed changes
    public void setTerm(String term) {
        this.term = term;
    }

<<<<<<< Updated upstream
    // Add a list of postings to the current list.
=======
    // Method to add postings to the list
>>>>>>> Stashed changes
    public void addPostings(ArrayList<Posting> postings2Add) {
        postings.addAll(postings2Add);
    }

<<<<<<< Updated upstream
    // Prepare to iterate through the lists of postings and skipping blocks.
    public void openList() {
        // Need to read blocks from the lexicon.
        // blocks = get blocks from the lexicon (Note: this line is commented out and may need to be implemented)
=======
    // Method to open the posting list
    public void openList() {
        // Need to read blocks from lexicon
        // blocks = get blocks from lexicon
>>>>>>> Stashed changes
        skippingBlockIterator = blocks.iterator();
        postingIterator = postings.iterator();
    }

<<<<<<< Updated upstream
    // Iterate to the next posting.
=======
    // Method to get the next posting
>>>>>>> Stashed changes
    public Posting next() {
        if (!postingIterator.hasNext()) {
            if (!skippingBlockIterator.hasNext()) {
                postingActual = null;
                return null;
            }
            skippingBlockActual = skippingBlockIterator.next();
            postings.clear();
<<<<<<< Updated upstream
            postings.addAll(skippingBlockActual.getSkippingBlockPostings(compression));
=======
            postings.addAll(skippingBlockActual.getSkippingBlockPostings());
>>>>>>> Stashed changes
            postingIterator = postings.iterator();
        }
        postingActual = postingIterator.next();
        return postingActual;
    }
}
