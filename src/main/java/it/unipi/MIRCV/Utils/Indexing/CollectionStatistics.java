package it.unipi.MIRCV.Utils.Indexing;

import java.util.Collections;

public class CollectionStatistics {
    private static int documents;
    private static double avgDocLen;
    private static long terms;
    private static long posting;
    public static int getDocuments() {
        return documents;
    }

    public static void setDocuments(int documents1) {
        documents = documents1;
    }

    public static double getAvgDocLen() {
        return avgDocLen;
    }

    public static void setAvgDocLen(double avgDocLen1) {
        avgDocLen = avgDocLen1;
    }

    public static long getTerms() {
        return terms;
    }

    public static void setTerms(long terms1) {
        terms = terms1;
    }

    public static long getPosting() {
        return posting;
    }

    public static void setPosting(long posting1) {
        posting = posting1;
    }
}
