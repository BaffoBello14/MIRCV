package it.unipi;
import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class QueryTest {

    static boolean conjunctive = false;
    static String scoringFunction = "tfidf";
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

    public static void testQuery() {
        ArrayList<String> queryList = new ArrayList<>();
        queryList.add("Python programming language");
        queryList.add("passion for coding");
        queryList.add("quick brown fox lazy dog");
        queryList.add("versatile programming");
        queryList.add("enjoy reading books");

        for (String query : queryList) {
            testQueryWithOptions(query);
        }
    }

    private static void testQueryWithOptions(String query) {
        ArrayList<Integer> result;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    conjunctive = (i == 1);
                    scoringFunction = (j == 1) ? "bm25" : "tfidf";
                    PathAndFlags.DYNAMIC_PRUNING = (k == 1);

                    result=Processer.processQuery(query, 3, conjunctive, scoringFunction);

                    printQueryResult(query, conjunctive, scoringFunction, result);
                }
            }
        }
    }

    private static void printQueryResult(String query, boolean conjunctive, String scoringFunction, ArrayList<Integer> result) {
        System.out.println("Query: " + query + "\nParameters:\tconjunctive:" + conjunctive + "\tScoring function: " + scoringFunction+
                "\tApproach: "+(PathAndFlags.DYNAMIC_PRUNING ? "DAAT":"MaxScore"));
        System.out.println(result);
    }

    public static void main(String[] args) {
        QueryTest.initialize(); // Inizializzazione dei percorsi
        QueryTest.testQuery(); // Esecuzione dei test
    }

}
