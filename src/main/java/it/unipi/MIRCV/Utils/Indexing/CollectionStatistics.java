package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;

public class CollectionStatistics {
    private static int documents;
    private static double avgDocLen;
    protected static long ENTRY_SIZE = 4 + 8 + 8;
    private static long terms;
    public static int getDocuments() {
        return documents;
    }

    public static void setDocuments(int documents1) {
        documents = documents1;
    }

    public static double getAvgDocLen() {
        return avgDocLen;
    }

    public static void setAvgDocLen(double avgDocLen1) {
        avgDocLen = avgDocLen1;
    }

    public static long getTerms() {
        return terms;
    }

    public static void setTerms(long terms1) {
        terms = terms1;
    }


    public static boolean write2Disk(){
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(PathAndFlags.PATH_TO_COLLECTION_STAT+"/CollectionStatistics.dat");
            FileChannel fileChannel= fileOutputStream.getChannel();
            MappedByteBuffer mappedByteBuffer= fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, ENTRY_SIZE);
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            fileChannel.close();
            return true;
        }catch (IOException e){
            System.out.println("Problems with writing to collection statistics file");
            return false;
        }

    }
}
