package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.*;

import java.util.*;

public class DAAT {
    public static int getMinDocId(ArrayList<PostingIndex>postings){
        int min_doc=CollectionStatistics.getDocuments();
        for(PostingIndex postingIndex:postings){
            if(postingIndex.getPostingActual()!=null){
                min_doc=Math.min(min_doc,postingIndex.getPostingActual().getDoc_id());
            }
        }
        return min_doc;
    }
    public static TopKPriorityQueue<Pair<Float, Integer>> scoreCollection(ArrayList<PostingIndex>postings, ArrayList<String>query, int k,String TFIDFOrBM25){
        Map<String, Integer> queryFrequencies = new HashMap<>();

        for (String term : query) {
            queryFrequencies.put(term, queryFrequencies.getOrDefault(term, 0) + 1);
        }

        for(PostingIndex postingIndex:postings){
            postingIndex.openList();
            postingIndex.next();
        }
        int doc_id=getMinDocId(postings);
        TopKPriorityQueue<Pair<Float,Integer>> topK=new TopKPriorityQueue<>(k,Comparator.comparing(Pair::getKey));
        int doc_len=CollectionStatistics.getDocuments();
        while ((doc_id!=doc_len)){
            float score=0.0F;
            for(PostingIndex postingIndex:postings){
                Posting posting=postingIndex.getPostingActual();
                if(posting!=null){
                    if(posting.getDoc_id()==doc_id){
                        score+=Scorer.score(posting, Lexicon.getInstance().retrieveEntry(postingIndex.getTerm()).getIdf(),TFIDFOrBM25);
                        postingIndex.next();
                    }
                }
            }
            topK.offer(new Pair<>(score,doc_id));
            doc_id=getMinDocId(postings);
        }
        return topK;

    }
}
