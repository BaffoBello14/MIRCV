package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.DocIndex;
import it.unipi.MIRCV.Utils.Indexing.Posting;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

/**
 * Class responsible for scoring based on different models (TFIDF, BM25).
 */
public class Scorer {

    /**
     * Calculates the score based on the specified scoring function (TFIDF or BM25).
     *
     * @param posting      The Posting object for a term in a document.
     * @param idf          The inverse document frequency of the term.
     * @param TFIDForBM25  The scoring function to be used (TFIDF or BM25).
     * @return The calculated score.
     */
    public static float score(Posting posting, float idf, String TFIDForBM25) {
        if (TFIDForBM25.equals("tfidf")) {
            return calculateTFIDF(posting.getFrequency(), idf);
        } else if (TFIDForBM25.equals("bm25")) {
            return calculateBM25(posting, idf);
        }
        System.out.println("Non-valid scoring function chosen");
        return -1F;
    }

    /**
     * Calculates the TFIDF score for a term in a document.
     *
     * @param tf  The term frequency in the document.
     * @param idf The inverse document frequency of the term.
     * @return The calculated TFIDF score.
     */
    public static float calculateTFIDF(int tf, float idf) {
        return (float) ((1 + Math.log(tf)) * idf);
    }

    /**
     * Calculates the BM25 score for a term in a document.
     *
     * @param posting The Posting object for a term in a document.
     * @param idf     The inverse document frequency of the term.
     * @return The calculated BM25 score.
     */
    public static float calculateBM25(Posting posting, float idf) {
        long doc_len = DocIndex.getInstance().getDoc_len(posting.getDoc_id());
        float tf = (float) (1 + Math.log(posting.getFrequency()));
        return (float) ((tf * idf) / (tf + PathAndFlags.BM25_k1 * (1 - PathAndFlags.BM25_b + PathAndFlags.BM25_b * (doc_len / CollectionStatistics.getAvgDocLen()))));
    }
}
