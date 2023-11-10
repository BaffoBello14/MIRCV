package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.PostingIndex;

import java.util.ArrayList;

import java.util.Comparator;

public class MaxScoreDynamicPruning {
    public static void sortByUpperbound(ArrayList<PostingIndex> postingIndices){
        postingIndices.sort(Comparator.comparing(PostingIndex::getUpperBound));
    }

}
