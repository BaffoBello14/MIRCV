package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.Indexing.PostingIndex;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.util.*;
import java.util.stream.Collectors;

public class Processer {

    public static ArrayList<PostingIndex>getQueryPostingLists(ArrayList<String>query,boolean conjunctive){
        ArrayList<PostingIndex> postingOfQuery=new ArrayList<>();
        for(String term:query){
            LexiconEntry lexiconEntry= Lexicon.getInstance().retrieveEntry(term);
            if(lexiconEntry==null){
                if(conjunctive){
                    return null;
                }
                continue;
            }
            postingOfQuery.add(new PostingIndex(lexiconEntry.getTerm()));
        }
        return postingOfQuery;
    }

    public static ArrayList<Integer> processQuery(String query,int k,boolean conjunctive,String scoringFun){
        String cleanedQuery=Preprocess.cleanText(query);
        List<String> cleaned=Preprocess.tokenize(cleanedQuery);
        if(PathAndFlags.STOPWORD_STEM_ENABLED){
            cleaned=Preprocess.removeStopwords(cleaned);
            cleaned=Preprocess.applyStemming(cleaned);
        }
        if(cleaned.isEmpty()){
            System.out.println("empty query");
            return null;
        }
        Set<String> queryDistinctWords = new HashSet<>(cleaned);
        ArrayList<PostingIndex> queryPostings=getQueryPostingLists(new ArrayList<>(queryDistinctWords),conjunctive);
        if(queryPostings==null||queryPostings.isEmpty()){
            return null;
        }
        TopKPriorityQueue<Pair<Float,Integer>>priorityQueue;
        if(PathAndFlags.DYNAMIC_PRUNING){
            priorityQueue= new TopKPriorityQueue<>(k,Comparator.comparing(Pair::getKey));
        }else{
            priorityQueue=DAAT.scoreCollection(queryPostings,k,scoringFun,conjunctive);
        }
        if (priorityQueue==null){
            return null;
        }
        ArrayList<Integer> list=new ArrayList<>();

        while(!priorityQueue.isEmpty()){
            Pair<Float,Integer> pair=priorityQueue.poll();
            list.add(pair.getValue());
        }
        Collections.reverse(list);
        return list;

    }
}
