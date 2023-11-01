package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.DocIndex;
import it.unipi.MIRCV.Utils.Indexing.Posting;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

public class Scorer {
    public static float score(Posting posting, float idf, String TFIDForBM25){
        if(TFIDForBM25.equals("tfidf")){

        } else if (TFIDForBM25.equals("bm25")) {

        }
        System.out.println("non valid scoring fun choose");
        return -1F;
    }
    public static float calculateTFIDF(int tf,float idf){
        return (float) ((1+Math.log(tf))*idf);
    }
    public static float calculateBM25(Posting posting,float idf){
        long doc_len= DocIndex.getInstance().getDoc_len(posting.getDoc_id());
        CollectionStatistics.readFromDisk();
        float tf= (float) (1+Math.log(posting.getFrequency()));
        return (tf*idf)/(tf+ PathAndFlags.BM25_k1*(1-PathAndFlags.BM25_b+PathAndFlags.BM25_b*doc_len/CollectionStatistics.getTotalLenDoc()));
    }
}
