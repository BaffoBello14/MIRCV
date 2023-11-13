package it.unipi;

import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The QueryTest class contains unit tests for the query processing functionality.
 */
public class QueryTest {

    static boolean conjunctive = false;
    static String scoringFunction = "tfidf";

    /**
     * Initializes the necessary paths and flags for the test.
     */
    static void initialize() {
        PathAndFlags.PATH_TO_DOC_ID = "./IndexDataTest/Doc_ids/";
        PathAndFlags.PATH_TO_FINAL = "./IndexDataTest/Final";
        PathAndFlags.PATH_TO_FREQ = "./IndexDataTest/Freqs/";
        PathAndFlags.PATH_TO_LEXICON = "./IndexDataTest/Lexicons/";
        PathAndFlags.PATH_TO_FINAL_DOC_ID = "./IndexDataTest/Final/DocID.dat";
        PathAndFlags.PATH_TO_FINAL_FREQ = "./IndexDataTest/Final/Freq.dat";
        PathAndFlags.PATH_TO_DOC_INDEX = "./IndexDataTest/Doc_index/DocIndex.dat";
        PathAndFlags.PATH_TO_FINAL_LEXICON = "./IndexDataTest/Final/Lexicon.dat";
        PathAndFlags.PATH_TO_COLLECTION_STAT = "./IndexDataTest/CollectionStatistics/CollectionStat.dat";
        PathAndFlags.PATH_TO_BLOCK_FILE = "./IndexDataTest/BlockInfo/BlockInfo.dat";
        PathAndFlags.PATH_TO_FLAGS = "./IndexDataTest/Flags/Flags.dat";
        CollectionStatistics.readFromDisk();
    }

    /**
     * Tests the query processing functionality.
     */
    @Test
    public void testQuery() {
        initialize();
        ArrayList<String> queryList = new ArrayList<>(Arrays.asList(
                "Python programming language",
                "passion for coding",
                "quick brown fox lazy dog",
                "versatile programming",
                "enjoy reading books"
        ));

        ArrayList<ArrayList<Integer>> groundTruth = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(9)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(3)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4, 2)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(4)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8)),
                new ArrayList<>(Arrays.asList(8))
        ));
        ArrayList<ArrayList<Integer>> allResults = new ArrayList<>(); // Container for all results

        for (String query : queryList) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for(int k = 0; k < 2; k++) {
                        PathAndFlags.DYNAMIC_PRUNING = (k == 1);
                        conjunctive = (i == 1);
                        scoringFunction = (j == 1) ? "bm25" : "tfidf";

                        allResults.add(Processer.processQuery(query, 2, conjunctive, scoringFunction));
                    }
                }
            }
        }

        assertEquals(groundTruth, allResults);
    }
}
