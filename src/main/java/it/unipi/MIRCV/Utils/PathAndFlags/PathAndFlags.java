package it.unipi.MIRCV.Utils.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PathAndFlags {
    public static String PATH_TO_DOC_ID="./IndexData/Doc_ids/";
    public static String PATH_TO_FREQ="./IndexData/Freqs/";
    public static String PATH_TO_FINAL_DOC_ID="./IndexData/Final/DocID.dat";
    public static String PATH_TO_FINAL_FREQ="./IndexData/Final/Freq.dat";
    public static String PATH_TO_DOC_INDEX="./IndexData/Doc_index/DocIndex.dat";
    public static String PATH_TO_LEXICON="./IndexData/Lexicons/";
    public static String PATH_TO_FINAL_LEXICON="./IndexData/Final/Lexicon.dat";
    public static String PATH_TO_COLLECTION_STAT="./IndexData/CollectionStatistics/CollectionStat.dat";
    public static String PATH_TO_BLOCK_FILE="./IndexData/BlockInfo/BlockInfo.dat";
    public static String PATH_TO_FLAGS="./IndexData/Flags/Flags.dat";
    public static boolean COMPRESSION_ENABLED=true;
    public static boolean STOPWORD_STEM_ENABLED=true;
    public static boolean DYNAMIC_PRUNING=false;
    public static final float BM25_k1= 1.5F;
    public static final float BM25_b=0.75F;
    public static final int POSTING_PER_BLOCK=512;
    public static final int LEXICON_CACHE_SIZE=1000;
    public static final int DOC_INDEX_CACHE_SIZE=1000000;

    public static void write2Disk(){
        try{
            FileChannel fileChannel= FileChannel.open(Paths.get(PATH_TO_FLAGS), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            MappedByteBuffer mappedByteBuffer= fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,3L );
            mappedByteBuffer.put((byte)(COMPRESSION_ENABLED?1:0));
            mappedByteBuffer.put((byte)(STOPWORD_STEM_ENABLED?1:0));
            mappedByteBuffer.put((byte)(DYNAMIC_PRUNING?1:0));
            fileChannel.close();
        }catch (IOException e){
            System.out.println("Problems with writing to collection statistics file");
            e.printStackTrace();
        }
    }
    public static void readFromDisk(){
        try{
            FileChannel fileChannel= FileChannel.open(Paths.get(PATH_TO_FLAGS), StandardOpenOption.READ);
            MappedByteBuffer mappedByteBuffer= fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,3L );
            COMPRESSION_ENABLED=mappedByteBuffer.get()==1;
            STOPWORD_STEM_ENABLED=mappedByteBuffer.get()==1;
            DYNAMIC_PRUNING=mappedByteBuffer.get()==1;
            fileChannel.close();
        }catch (IOException e){
            System.out.println("Problems with writing to collection statistics file");
            e.printStackTrace();
        }
    }

}
