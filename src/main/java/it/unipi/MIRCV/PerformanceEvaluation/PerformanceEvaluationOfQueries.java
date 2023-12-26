package it.unipi.MIRCV.PerformanceEvaluation;

import it.unipi.MIRCV.Query.Pair;
import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Query.TopKPriorityQueue;
import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.DocIndex;
import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

/**
 * The PerformanceEvaluationOfQueries class performs the performance evaluation of search engine queries.
 */
public class PerformanceEvaluationOfQueries {
    private static String PATH_TO_ARCHIVE_COLLECTION = "./Collection/msmarco-test2019-queries.tsv.gz";

    /**
     * Executes the performance evaluation of search engine queries.
     */
    public static void execute() {
        CollectionStatistics.readFromDisk();
        PathAndFlags.readFlagsFromDisk();
        String[] lineofDoc;
        String qno;
        String directoryPath = "./PerformanceEvaluatedFile";

        try {
            // Create the directory if it doesn't exist
            if (!Files.exists(Paths.get(directoryPath))) {
                Files.createDirectories(Paths.get(directoryPath));
            }

            InputStream file = Files.newInputStream(Paths.get(PATH_TO_ARCHIVE_COLLECTION));
            InputStream gzip = new GZIPInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
            String line;
            BufferedWriter bufferedWriterDAATTFIDF = new BufferedWriter(new FileWriter(directoryPath + "/DAATTFIDF.txt"));
            BufferedWriter bufferedWriterDAATBM25 = new BufferedWriter(new FileWriter(directoryPath + "/DAATBM25.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGTFIDF = new BufferedWriter(new FileWriter(directoryPath + "/DYNAMICPRUNINGTFIDF.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGBM25 = new BufferedWriter(new FileWriter(directoryPath + "/DYNAMICPRUNINGBM25.txt"));
            long start, end;
            ArrayList<Long> withCacheTFIDFDAAT = new ArrayList<>();
            ArrayList<Long> withCacheTFIDFDP = new ArrayList<>();
            ArrayList<Long> withoutCacheTFIDFDAAT = new ArrayList<>();
            ArrayList<Long> withoutCacheTFIDFDP = new ArrayList<>();
            ArrayList<Long> withCacheBM25DP = new ArrayList<>();
            ArrayList<Long> withCacheBM25DAAT = new ArrayList<>();
            ArrayList<Long> withoutCacheBM25DAAT = new ArrayList<>();
            ArrayList<Long> withoutCacheBM25DP = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                lineofDoc = Preprocess.parseLine(line);
                if (lineofDoc[1].trim().isEmpty()) {
                    continue;
                }

                qno = lineofDoc[0];
                Lexicon.getInstance().clear();
                start = System.currentTimeMillis();
                TopKPriorityQueue<Pair<Float, Integer>> answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end = System.currentTimeMillis();
                withoutCacheTFIDFDAAT.add(end - start);
                write2File(bufferedWriterDAATTFIDF, answerOfSearchEngine, qno);

                start = System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end = System.currentTimeMillis();
                withCacheTFIDFDAAT.add(end - start);

                Lexicon.getInstance().clear();
                start = System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end = System.currentTimeMillis();
                withoutCacheBM25DAAT.add(end - start);
                write2File(bufferedWriterDAATBM25, answerOfSearchEngine, qno);

                start = System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end = System.currentTimeMillis();
                withCacheBM25DAAT.add(end - start);
                Lexicon.getInstance().clear();

                PathAndFlags.DYNAMIC_PRUNING = true;
                start = System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end = System.currentTimeMillis();
                withoutCacheTFIDFDP.add(end - start);
                write2File(bufferedWriterDYNAMICPRUNINGTFIDF, answerOfSearchEngine, qno);

                start = System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end = System.currentTimeMillis();
                withCacheTFIDFDP.add(end - start);

                Lexicon.getInstance().clear();

                start = System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end = System.currentTimeMillis();
                withoutCacheBM25DP.add(end - start);
                write2File(bufferedWriterDYNAMICPRUNINGBM25, answerOfSearchEngine, qno);


                start = System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end = System.currentTimeMillis();
                withCacheBM25DP.add(end - start);

            }
            printStats("withoutCacheTFIDFDAAT", withoutCacheTFIDFDAAT);
            printStats("withCacheTFIDFDAAT", withCacheTFIDFDAAT);
            printStats("withoutCacheBM25DAAT", withoutCacheBM25DAAT);
            printStats("withCacheBM25DAAT", withCacheBM25DAAT);
            printStats("withoutCacheTFIDFDP", withoutCacheTFIDFDP);
            printStats("withCacheTFIDFDP", withCacheTFIDFDP);
            printStats("withoutCacheBM25DP", withoutCacheBM25DP);
            printStats("withCacheBM25DP", withCacheBM25DP);
            bufferedWriterDAATBM25.close();
            bufferedWriterDAATTFIDF.close();
            bufferedWriterDYNAMICPRUNINGBM25.close();
            bufferedWriterDYNAMICPRUNINGTFIDF.close();


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problems with opening the query file to perform a performance evaluation");

        }
    }

    private static void printStats(String label, ArrayList<Long> list) {
        if (list.isEmpty()) {
            System.out.println(label + " -> No data available");
            return;
        }

        long min = minimumOfTime(list);
        long max = maximumOfTime(list);
        double mean = averageOfTime(list);
        double stdDev = standardDeviationOfTime(list, mean);

        System.out.println(label + " -> Min: " + min + ", Max: " + max + ", Mean: " + mean + ", StdDev: " + stdDev);
    }

    private static long minimumOfTime(ArrayList<Long> list) {
        return Collections.min(list);
    }

    private static long maximumOfTime(ArrayList<Long> list) {
        return Collections.max(list);
    }

    private static double averageOfTime(ArrayList<Long> list) {
        long sum = 0;
        for (long l : list) {
            sum += l;
        }
        return (double) sum / list.size();
    }

    private static double standardDeviationOfTime(ArrayList<Long> list, double mean) {
        double sumSquaredDiff = 0;
        for (long value : list) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        double variance = sumSquaredDiff / list.size();
        return Math.sqrt(variance);
    }

    private static void write2File(BufferedWriter bufferedWriter, TopKPriorityQueue<Pair<Float, Integer>> answerOfSearchEngine, String qno) {
        ArrayList<Pair<Float, Integer>> pair = new ArrayList<>();
        try {
            if (answerOfSearchEngine != null) {
                while (!answerOfSearchEngine.isEmpty()) {
                    pair.add(answerOfSearchEngine.poll());
                }
                Collections.reverse(pair);
                for (int i = 0; i < pair.size(); i++) {
                    bufferedWriter.write(qno + " Q0 " + Integer.parseInt(DocIndex.getInstance().getDoc_NO(pair.get(i).getValue())) + " " + (i + 1) + " " + pair.get(i).getKey() + " CHANG0" + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problems with opening the query file to perform a performance evaluation in the write method");
        }
    }

    public static void main(String[] args) {
        execute();
    }
}
