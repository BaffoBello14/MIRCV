package it.unipi.MIRCV.PerformanceEvaluation;

import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.Indexing.DocIndex;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
public class PerformanceEvaluationOfQueries {
    private static String PATH_TO_ARCHIVE_COLLECTION = "./Collection/msmarco-test2019-queries.tsv.gz";
    public static void execute(){
        CollectionStatistics.readFromDisk();
        PathAndFlags.readFlagsFromDisk();
        String[] lineofDoc;
        String qno;
        List<String> tokens;
        try {
            InputStream file = Files.newInputStream(Paths.get(PATH_TO_ARCHIVE_COLLECTION));
            InputStream gzip = new GZIPInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
            String line;
            BufferedWriter bufferedWriterDAATTFIDF=new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DAATTFIDF.txt"));
            BufferedWriter bufferedWriterDAATBM25=new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DAATBM25.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGTFIDF=new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DYNAMICPRUNINGTFIDF.txt"));
            BufferedWriter bufferedWriterDYNAMICPRUNINGBM25=new BufferedWriter(new FileWriter("./PerformanceEvaluatedFile/DYNAMICPRUNINGBM25.txt"));

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                lineofDoc = Preprocess.parseLine(line);
                if (lineofDoc[1].trim().isEmpty()) {
                    continue;
                }

                qno=lineofDoc[0];
                ArrayList<Integer>answerOfSearchEngine= Processer.processQuery(lineofDoc[1],10,false,"tfidf");
                write2File(bufferedWriterDAATTFIDF,answerOfSearchEngine,qno);
                answerOfSearchEngine=Processer.processQuery(lineofDoc[1],10,false,"bm25");
                write2File(bufferedWriterDAATBM25,answerOfSearchEngine,qno);
                PathAndFlags.DYNAMIC_PRUNING=true;
                answerOfSearchEngine= Processer.processQuery(lineofDoc[1],10,false,"tfidf");
                write2File(bufferedWriterDYNAMICPRUNINGTFIDF,answerOfSearchEngine,qno);
                answerOfSearchEngine=Processer.processQuery(lineofDoc[1],10,false,"bm25");
                write2File(bufferedWriterDYNAMICPRUNINGBM25,answerOfSearchEngine,qno);
            }
            bufferedWriterDAATBM25.close();
            bufferedWriterDAATTFIDF.close();
            bufferedWriterDYNAMICPRUNINGBM25.close();
            bufferedWriterDYNAMICPRUNINGTFIDF.close();

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Problems with opening the query file to perform a performance evaluation");

        }
    }
    private static void write2File(BufferedWriter bufferedWriter,ArrayList<Integer>answerOfSearchEngine,String qno){
        try{
            if (answerOfSearchEngine == null) {
                bufferedWriter.write(qno + " Q0 null\n");
            } else {
                for (int i : answerOfSearchEngine) {
                    bufferedWriter.write(qno + " Q0 " + DocIndex.getInstance().getDoc_NO(i) + "\n");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Problems with opening the query file to perform a performance evaluation in the write method");
        }
    }

    public static void main(String[] args) {
        execute();

    }
}
