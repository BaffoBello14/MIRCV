package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.Posting;
import it.unipi.MIRCV.Utils.Indexing.PostingIndex;

import java.util.ArrayList;

import java.util.Comparator;

public class MaxScoreDynamicPruning {
    public static void sortByUpperbound(ArrayList<PostingIndex> postingIndices){
        postingIndices.sort(Comparator.comparing(PostingIndex::getUpperBound));
    }
    public static TopKPriorityQueue<Pair<Float,Integer>>maxScore(ArrayList<PostingIndex>postings,int k, String TFIDFOrBM25, boolean conjunctive){
        sortByUpperbound(postings);
        for (PostingIndex index : postings) {
            index.openList();
            index.next();
        }
        TopKPriorityQueue<Pair<Float,Integer>>topKPriorityQueue= new TopKPriorityQueue<>(k,Comparator.comparing(Pair::getKey));
        float[]ub=new float[postings.size()];
        ub[0]=postings.get(0).getUpperBound();
        for(int i=1;i< postings.size();i++){
            ub[i]=ub[i-1]+postings.get(i).getUpperBound();
        }
        float threshold=0;
        int pivot=0;
        int current;
        boolean skip;
        while (pivot< postings.size()&&!isFinished(postings,pivot, postings.size())){
            float score=0;
            skip=false;
            current=getMinDocId(postings,pivot,postings.size());
            for(int i=pivot;i<postings.size();i++){
                if(postings.get(i).getPostingActual()!=null&&postings.get(i).getPostingActual().getDoc_id()==current){
                    score+=Scorer.score(postings.get(i).getPostingActual(),postings.get(i).getIdf(),TFIDFOrBM25);
                    postings.get(i).next();
                }
            }
            for(int i=pivot-1;i>=0;i--){
                if(score+ub[i]<threshold){
                    skip=true;
                    break;
                }
                Posting geq=postings.get(i).nextGEQ(current);
                if(geq!=null&&geq.getDoc_id()==current){
                    score+=Scorer.score(geq,postings.get(i).getIdf(),TFIDFOrBM25);
                }
            }
            if(skip){
                continue;
            }
            if(topKPriorityQueue.offer(new Pair<>(score,current))){
                threshold=topKPriorityQueue.peek().getKey();
                while (pivot<postings.size()&&ub[pivot]<threshold){
                    pivot++;
                }
            }
        }
        return topKPriorityQueue;
    }
    private static boolean isFinished(ArrayList<PostingIndex>postingIndices,int start,int end){
        for(int i=start;i<end;i++){
            if(postingIndices.get(i).getPostingActual()!=null){
                return false;
            }
        }
        return true;
    }
    public static int getMinDocId(ArrayList<PostingIndex> postings,int start,int end) {
        int min_doc = CollectionStatistics.getDocuments();
        for (int i=start;i<end;i++) {
            if (postings.get(i).getPostingActual() != null) {
                min_doc = Math.min(min_doc, postings.get(i).getPostingActual().getDoc_id());
            }
        }
        return min_doc;
    }
    public static int get_doc_id(ArrayList<PostingIndex>postings,int start,int end){
        return 0;
    }
    public static boolean areEquals(ArrayList<PostingIndex>postings,int start,int end){
        return true;
    }
    public static int get_max_doc_id(ArrayList<PostingIndex>postings,int start,int end){
        int doc_id=0;
        for(int i=start;i<end;i++){
            if(postings.get(i).getPostingActual()!=null){
                if (postings.get(i).getPostingActual().getDoc_id() > doc_id) {
                    doc_id = postings.get(i).getPostingActual().getDoc_id();
                }
            }else{
                return 0;
            }
        }
        return doc_id;
    }

}