package it.unipi.MIRCV.Utils.Indexing;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;
import org.apache.commons.compress.archivers.*;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SPIMI {
    private static final String PATH_TO_ARCHIVE_COLLECTION="./Collection/reducedCollection100.tar.gz";
    private static int numBlock=0;
    private static boolean finished=false;
    private static long offsetDocIndex=0;

    public static boolean  execute() throws Exception{
        int doc_id=1;
        String [] lineofDoc;
        String docno;
        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX+"/DocIndex.dat"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        int total_length=0;
        List<String> tokens;
        HashMap<String,PostingIndex> index=new HashMap<>();
        while(!finished){
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            while ((usedMemory / totalMemory) * 100 < 80) {
                try {
                    InputStream file=new FileInputStream(PATH_TO_ARCHIVE_COLLECTION);
                    InputStream gzip=new GZIPInputStream(file);
                    ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
                    ArchiveEntry entry;

                    while ((entry = archiveStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lineofDoc= Preprocess.parseLine(line);
                                docno=lineofDoc[0];
                                lineofDoc[1]=Preprocess.cleanText(lineofDoc[1]);
                                tokens=Preprocess.tokenize(lineofDoc[1]);
                                tokens=Preprocess.removeStopwords(tokens);
                                tokens=Preprocess.applyStemming(tokens);
                                int documentLength=tokens.size();
                                DocIndexEntry docIndexEntry= new DocIndexEntry(docno,documentLength);
                                docIndexEntry.write2Disk(fileChannelDI,offsetDocIndex,doc_id);
                                total_length+=documentLength;
                                for(String term:tokens){
                                    PostingIndex posting;
                                    if(index.containsKey(term)){
                                        posting=index.get(term);
                                    }else{
                                        posting=new PostingIndex();
                                        index.put(term,posting);
                                    }

                                }
                                doc_id++;

                            }
                            finished=true;
                            break;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(finished) break;
            }
        }
        CollectionStatistics.setTotalLenDoc(CollectionStatistics.getTotalLenDoc()+total_length);
        CollectionStatistics.setDocuments(doc_id-1);
        fileChannelDI.close();
        return true;
    }

    public static void main(String[] args) throws  Exception{
        int doc_id=1;
        String [] lineofDoc;
        String docno;
        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX+"/DocIndex.dat"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        int total_length=0;
        List<String> tokens;
        HashMap<String,PostingIndex> index=new HashMap<>();
        InputStream file=new FileInputStream(PATH_TO_ARCHIVE_COLLECTION);
        InputStream gzip=new GZIPInputStream(file);
        ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
        ArchiveEntry entry;
        while ((entry = archiveStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    lineofDoc= Preprocess.parseLine(line);
                    docno=lineofDoc[0];
                    lineofDoc[1]=Preprocess.cleanText(lineofDoc[1]);
                    tokens=Preprocess.tokenize(lineofDoc[1]);
                    tokens=Preprocess.removeStopwords(tokens);
                    tokens=Preprocess.applyStemming(tokens);
                    int documentLength=tokens.size();
                    DocIndexEntry docIndexEntry= new DocIndexEntry(docno,documentLength);
                    docIndexEntry.write2Disk(fileChannelDI,offsetDocIndex,doc_id);
                    total_length+=documentLength;
                    for(String term:tokens){
                        PostingIndex posting;
                        if(index.containsKey(term)){
                            posting=index.get(term);
                        }else{
                            posting=new PostingIndex();
                            index.put(term,posting);
                        }

                    }
                    doc_id++;

                }
                finished=true;
                break;
            }
            break;
        }
        DocIndex docIndex=new DocIndex();
        docIndex.readFromDisk(fileChannelDI,0);
        System.out.println(docIndex.sortDocIndex());
    }



}
