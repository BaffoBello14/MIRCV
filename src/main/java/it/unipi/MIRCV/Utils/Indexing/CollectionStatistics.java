package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.FileInputStream;
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
            fileOutputStream.close();
            fileChannel.close();
            return true;
        }catch (IOException e){
            System.out.println("Problems with writing to collection statistics file");
            return false;
        }

    }
<<<<<<< HEAD
=======

    public static void setTotalLenDoc(long totalLenDoc) {
        CollectionStatistics.totalLenDoc = totalLenDoc;
    }

<<<<<<< HEAD
    public static boolean write2Disk(){
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(PathAndFlags.PATH_TO_COLLECTION_STAT);
            FileChannel fileChannel= fileOutputStream.getChannel();
            MappedByteBuffer mappedByteBuffer= fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, ENTRY_SIZE);
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            mappedByteBuffer.putLong(totalLenDoc);
            fileOutputStream.close();
            fileChannel.close();
            return true;
        }catch (IOException e){
            System.out.println("Problems with writing to collection statistics file");
            return false;
        }

    }
=======
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f
>>>>>>> 1b303430e4448ef03e4920045b8c72e5174387d7
    public static boolean readFromDisk() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PathAndFlags.PATH_TO_COLLECTION_STAT + "/CollectionStatistics.dat");
            FileChannel fileChannel = fileInputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, ENTRY_SIZE);

            // Read data from the MappedByteBuffer
            documents = mappedByteBuffer.getInt();
            avgDocLen = mappedByteBuffer.getDouble();
            terms = mappedByteBuffer.getLong();

            // Close the FileChannel and the FileInputStream
            fileChannel.close();
            fileInputStream.close();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with reading from the collection statistics file");
            return false;
        }
    }

}
