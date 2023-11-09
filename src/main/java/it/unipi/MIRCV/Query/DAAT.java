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
    public static TopKPriorityQueue<Pair<Float, Integer>> scoreCollection(ArrayList<PostingIndex>postings, int k,String TFIDFOrBM25,boolean conjunctive){

        for (PostingIndex index : postings) {
            index.openList();
            index.next();
        }
        TopKPriorityQueue<Pair<Float,Integer>> topK=new TopKPriorityQueue<>(k,Comparator.comparing(Pair::getKey));
        int doc_id=conjunctive?get_doc_id(postings): getMinDocId(postings);
        if(doc_id==0){
            return null;
        }

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
            doc_id=conjunctive?get_doc_id(postings): getMinDocId(postings);
            if(doc_id==0){
                break;
            }
        }
        return topK;

    }
    public static int get_max_doc_id(ArrayList<PostingIndex>postingIndices){
        int max_doc=0;
        for(PostingIndex postingIndex:postingIndices){
            if(postingIndex.getPostingActual()!=null){
                max_doc=Math.max(max_doc,postingIndex.getPostingActual().getDoc_id());
            }else{
                return 0;
            }
        }
        return max_doc;
    }
    public static boolean areEquals(ArrayList<PostingIndex>postingIndices){
        if(postingIndices.get(0).getPostingActual()==null){
            return false;
        }
        int doc_id=postingIndices.get(0).getPostingActual().getDoc_id();
        for(int i=1;i< postingIndices.size();i++){
            if(postingIndices.get(i).getPostingActual()==null){
                return false;
            }
            if(doc_id!=postingIndices.get(i).getPostingActual().getDoc_id()){
                return false;
            }
        }
        return true;
    }
    public static int get_doc_id(ArrayList<PostingIndex>postingIndices){
        int doc_id=get_max_doc_id(postingIndices);
        if(doc_id==0){
            return 0;
        }

        for(int i=0;i< postingIndices.size();i++){
            if(areEquals(postingIndices)){
                return doc_id;
            }
            if(postingIndices.get(i).getPostingActual()==null){
                return 0;
            }
            if(postingIndices.get(i).getPostingActual().getDoc_id()>doc_id){
                doc_id=postingIndices.get(i).getPostingActual().getDoc_id();
                i=-1;
                continue;
            }
            if(postingIndices.get(i).getPostingActual().getDoc_id()<doc_id){
                Posting geq=postingIndices.get(i).nextGEQ(doc_id);
                if(geq==null){
                    return 0;
                }
                if(geq.getDoc_id()>doc_id){
                    doc_id=geq.getDoc_id();
                    i=-1;
                }
            }

        }
        return 0;
    }

}
