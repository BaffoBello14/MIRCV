package it.unipi.MIRCV.Utils.Indexing;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;
import org.apache.commons.compress.archivers.*;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class SPIMI {
    private static final String PATH_TO_ARCHIVE_COLLECTION="./Collection/reducedCollection1000.tar.gz";
    private static int numIndex=0;
    private static long numPosting=0;
    private static boolean finished=false;
    private static long offsetDocIndex=0;

    public static long  execute() throws Exception{
        int doc_id=1;
        String [] lineofDoc;
        String docno;
        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);

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
                    InputStream file= Files.newInputStream(Paths.get(PATH_TO_ARCHIVE_COLLECTION));
                    InputStream gzip=new GZIPInputStream(file);
                    ArchiveInputStream archiveStream = new ArchiveStreamFactory().createArchiveInputStream("tar", gzip);
                    ArchiveEntry entry;

                    while ((entry = archiveStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(archiveStream,StandardCharsets.UTF_8));
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
                                offsetDocIndex+=DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
                                total_length+=documentLength;
                                for(String term:tokens){
                                    if(term.isEmpty()||term.trim().isEmpty()){
                                        continue;
                                    }
                                    PostingIndex posting;
                                    if(index.containsKey(term)){
                                        posting=index.get(term);
                                    }else{
                                        posting=new PostingIndex(term);
                                        index.put(term,posting);
                                    }
                                    addPosting(doc_id,posting);

                                }

                                doc_id++;
                                usedMemory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

                            }
                            write2Disk(index);
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

        CollectionStatistics.computeAVGDOCLEN();
        CollectionStatistics.write2Disk();
        CollectionStatistics.readFromDisk();
        System.out.println(CollectionStatistics.getAvgDocLen());
        fileChannelDI.close();

        return numIndex;
    }
    private static boolean write2Disk(HashMap<String,PostingIndex> index){
        System.out.println("save index with size->"+index.size()+" the num ->"+numIndex);
        if(index.isEmpty()){
            return true;
        }
        index=index.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1, LinkedHashMap::new));
        try {
            FileChannel fileChannelLex=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelDOCID=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelFreq=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FREQ+numIndex+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            MappedByteBuffer mappedByteBufferDOCID=fileChannelDOCID.map(FileChannel.MapMode.READ_WRITE,0,numPosting*4L);
            MappedByteBuffer mappedByteBufferFreq=fileChannelFreq.map(FileChannel.MapMode.READ_WRITE,0,numPosting*4L);
            long lexOffset=0;
            if(mappedByteBufferDOCID==null||mappedByteBufferFreq==null){
                return false;
            }
            for(PostingIndex postingIndex:index.values()){
                LexiconEntry lexiconEntry=new LexiconEntry();
                lexiconEntry.setOffset_doc_id(mappedByteBufferDOCID.position());
                lexiconEntry.setOffset_frequency(mappedByteBufferFreq.position());
                for(Posting posting: postingIndex.getPostings()){
                    System.out.println(posting.getDoc_id()+" "+posting.getFrequency());
                    mappedByteBufferDOCID.putInt(posting.getDoc_id());
                    mappedByteBufferFreq.putInt(posting.getFrequency());
                }
                lexiconEntry.updateTFMAX(postingIndex);
                lexiconEntry.setDf(postingIndex.getPostings().size());
                lexOffset=lexiconEntry.writeEntryToDisk(postingIndex.getTerm(), lexOffset,fileChannelLex);


            }

            numIndex++;
            numPosting=0;
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    protected static void addPosting(int doc_id,PostingIndex postingIndex){
        if(!postingIndex.getPostings().isEmpty()){
            Posting posting=postingIndex.getPostings().get(postingIndex.getPostings().size()-1);
            if(doc_id==posting.getDoc_id()){
                posting.setFrequency((posting.getFrequency()+1));
                return;
            }

        }
        postingIndex.getPostings().add(new Posting(doc_id,1));
        numPosting++;
    }
    public static void main(String[] args) throws  Exception{

        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        FileChannel fileChannelFRQ= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID+"0.dat"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        FileChannel fileChannelLEX=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON+"0.dat"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        execute();
        DocIndex docIndex=new DocIndex();
        docIndex.readFromDisk(fileChannelDI,200);
        byte [] term=new byte[Lexicon.MAX_LEN_OF_TERM];
        MappedByteBuffer mappedByteBuffer=fileChannelFRQ.map(FileChannel.MapMode.READ_ONLY,8,4);
        MappedByteBuffer mappedByteBuffer1=fileChannelLEX.map(FileChannel.MapMode.READ_ONLY,48,4);
        //mappedByteBuffer1.get(term);
        //System.out.println(new String(term, StandardCharsets.UTF_8));
        System.out.println(mappedByteBuffer1.getInt());
        System.out.println(mappedByteBuffer.getInt());
        System.out.println(docIndex.sortDocIndex().get(0));
    }



}
