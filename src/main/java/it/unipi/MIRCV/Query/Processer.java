package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.Indexing.PostingIndex;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.util.*;

/**
 * Class for processing queries.
 */
public class Processer {

    /**
     * Retrieves the posting lists for the terms in the query.
     *
     * @param query      List of query terms.
     * @param conjunctive Boolean flag indicating conjunctive (AND) or disjunctive (OR) operation.
     * @return ArrayList of PostingIndex objects representing the posting lists of the query terms.
     */
    public static ArrayList<PostingIndex> getQueryPostingLists(ArrayList<String> query, boolean conjunctive) {
        ArrayList<PostingIndex> postingOfQuery = new ArrayList<>();
        for (String term : query) {
            LexiconEntry lexiconEntry = Lexicon.getInstance().retrieveEntry(term);
            if (lexiconEntry == null) {
                if (conjunctive) {
                    return null;
                }
                continue;
            }
            postingOfQuery.add(new PostingIndex(lexiconEntry.getTerm()));
        }
        return postingOfQuery;
    }

    /**
     * Processes the query and returns a list of document IDs.
     *
     * @param query        Input query string.
     * @param k            Number of top results to retrieve.
     * @param conjunctive  Boolean flag indicating conjunctive (AND) or disjunctive (OR) operation.
     * @param scoringFun   The scoring function to be used.
     * @return ArrayList of document IDs matching the query.
     */
    public static ArrayList<Integer> processQuery(String query, int k, boolean conjunctive, String scoringFun) {
        // Clean and preprocess the query.
        String cleanedQuery = Preprocess.cleanText(query);
        List<String> cleaned = Preprocess.tokenize(cleanedQuery);

        // Apply stopword removal and stemming if enabled.
        if (PathAndFlags.STOPWORD_STEM_ENABLED) {
            cleaned = Preprocess.removeStopwords(cleaned);
            cleaned = Preprocess.applyStemming(cleaned);
        }

        // Check if the query is empty after preprocessing.
        if (cleaned.isEmpty()) {
            System.out.println("empty query");
            return null;
        }

        // Remove duplicates from the query terms.
        Set<String> queryDistinctWords = new HashSet<>(cleaned);

        // Retrieve posting lists for the query terms.
        ArrayList<PostingIndex> queryPostings = getQueryPostingLists(new ArrayList<>(queryDistinctWords), conjunctive);

        // Return null if no posting lists are retrieved.
        if (queryPostings == null || queryPostings.isEmpty()) {
            return null;
        }

        // Initialize a priority queue for the top-K results.
        TopKPriorityQueue<Pair<Float, Integer>> priorityQueue;

        // Choose between dynamic pruning and DAAT scoring based on the flag.
        if (PathAndFlags.DYNAMIC_PRUNING) {
            priorityQueue = new TopKPriorityQueue<>(k, Comparator.comparing(Pair::getKey));
        } else {
            priorityQueue = DAAT.scoreCollection(queryPostings, k, scoringFun, conjunctive);
        }

        // Return null if the priority queue is null.
        if (priorityQueue == null) {
            return null;
        }

        // Retrieve the document IDs from the priority queue.
        ArrayList<Integer> list = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            Pair<Float, Integer> pair = priorityQueue.poll();
            list.add(pair.getValue());
        }

        // Reverse the list to get the top-K results in descending order.
        Collections.reverse(list);
        return list;
    }
}
