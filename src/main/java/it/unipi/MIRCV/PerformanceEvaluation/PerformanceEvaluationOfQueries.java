package it.unipi.MIRCV.PerformanceEvaluation;

import it.unipi.MIRCV.Query.Processer;
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
import java.util.List;
import java.util.Locale;
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

        try {
            InputStream file = Files.newInputStream(Paths.get(PATH_TO_ARCHIVE_COLLECTION));
            InputStream gzip = new GZIPInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
            String line;
            BufferedWriter bufferedWriterDAATTFIDF = new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DAATTFIDF.txt"));
            BufferedWriter bufferedWriterDAATBM25 = new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DAATBM25.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGTFIDF = new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DYNAMICPRUNINGTFIDF.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGBM25 = new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DYNAMICPRUNINGBM25.txt"));
            long start,end;
            ArrayList<Long> withCacheTFIDFDAAT=new ArrayList<>();
            ArrayList<Long> withCacheTFIDFDP=new ArrayList<>();
            ArrayList<Long> withoutCacheTFIDFDAAT=new ArrayList<>();
            ArrayList<Long> withoutCacheTFIDFDP=new ArrayList<>();
            ArrayList<Long> withCacheBM25DP=new ArrayList<>();
            ArrayList<Long> withCacheBM25DAAT=new ArrayList<>();
            ArrayList<Long> withoutCacheBM25DAAT=new ArrayList<>();
            ArrayList<Long> withoutCacheBM25DP=new ArrayList<>();

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
                start=System.currentTimeMillis();
                ArrayList<Integer> answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end=System.currentTimeMillis();
                withoutCacheTFIDFDAAT.add(end-start);
                write2File(bufferedWriterDAATTFIDF, answerOfSearchEngine, qno);

                start=System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end=System.currentTimeMillis();
                withCacheTFIDFDAAT.add(end-start);


                Lexicon.getInstance().clear();
                start=System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end=System.currentTimeMillis();
                withoutCacheBM25DAAT.add(end-start);
                write2File(bufferedWriterDAATBM25, answerOfSearchEngine, qno);

                start=System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end=System.currentTimeMillis();
                withCacheBM25DAAT.add(end-start);
                Lexicon.getInstance().clear();

                PathAndFlags.DYNAMIC_PRUNING = true;
                start=System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end=System.currentTimeMillis();
                withoutCacheTFIDFDP.add(end-start);
                write2File(bufferedWriterDYNAMICPRUNINGTFIDF, answerOfSearchEngine, qno);

                start=System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "tfidf");
                end=System.currentTimeMillis();
                withCacheTFIDFDP.add(end-start);

                Lexicon.getInstance().clear();

                start=System.currentTimeMillis();
                answerOfSearchEngine = Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end=System.currentTimeMillis();
                withoutCacheBM25DP.add(end-start);
                write2File(bufferedWriterDYNAMICPRUNINGBM25, answerOfSearchEngine, qno);


                start=System.currentTimeMillis();
                Processer.processQuery(lineofDoc[1], 10, false, "bm25");
                end=System.currentTimeMillis();
                withCacheBM25DP.add(end-start);

            }
            printAverage("withCacheTFIDFDAAT", averageOfTime(withCacheTFIDFDAAT));
            printAverage("withCacheTFIDFDP", averageOfTime(withCacheTFIDFDP));
            printAverage("withoutCacheTFIDFDAAT", averageOfTime(withoutCacheTFIDFDAAT));
            printAverage("withoutCacheTFIDFDP", averageOfTime(withoutCacheTFIDFDP));
            printAverage("withCacheBM25DP", averageOfTime(withCacheBM25DP));
            printAverage("withCacheBM25DAAT", averageOfTime(withCacheBM25DAAT));
            printAverage("withoutCacheBM25DAAT", averageOfTime(withoutCacheBM25DAAT));
            printAverage("withoutCacheBM25DP", averageOfTime(withoutCacheBM25DP));
            bufferedWriterDAATBM25.close();
            bufferedWriterDAATTFIDF.close();
            bufferedWriterDYNAMICPRUNINGBM25.close();
            bufferedWriterDYNAMICPRUNINGTFIDF.close();


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problems with opening the query file to perform a performance evaluation");

        }
    }
    private static void printAverage(String label, double average) {
        System.out.println(label + " -> " + average);
    }
    private static double averageOfTime(ArrayList<Long> list){
        long sum=0;
        for(long l:list){
            sum+=l;
        }
        return (double) sum /list.size();
    }

    private static void write2File(BufferedWriter bufferedWriter, ArrayList<Integer> answerOfSearchEngine, String qno) {
        try {
            if (answerOfSearchEngine == null) {
                bufferedWriter.write(qno + " Q0 null\n");
            } else {
                for (int i : answerOfSearchEngine) {
                    bufferedWriter.write(qno + " Q0 " + DocIndex.getInstance().getDoc_NO(i) + "\n");
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
