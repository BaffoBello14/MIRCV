package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CollectionStatistics {
    // class variables
    private static int documents;
    private static double avgDocLen;
    private static long totalLenDoc;
    protected static long ENTRY_SIZE = 4 + 8 + 8 + 8;
    private static long terms;

    // empty constructor
    public CollectionStatistics() {}

    // getter and setter for documents
    public static int getDocuments() {
        return documents;
    }

    public static void setDocuments(int documents1) {
        documents = documents1;
    }

    // computes the average length of documents
    public static void computeAVGDOCLEN() {
        avgDocLen = (double) totalLenDoc / documents;
    }

    // getter and setter for avgDocLen
    public static double getAvgDocLen() {
        return avgDocLen;
    }

    public static void setAvgDocLen(double avgDocLen1) {
        avgDocLen = avgDocLen1;
    }

    // getter and setter for terms
    public static long getTerms() {
        return terms;
    }

    public static void setTerms(long terms1) {
        terms = terms1;
    }

    // getter and setter for totalLenDoc
    public static long getTotalLenDoc() {
        return totalLenDoc;
    }

    public static void setTotalLenDoc(long totalLenDoc) {
        CollectionStatistics.totalLenDoc = totalLenDoc;
    }

    // writes data to disk
    public static boolean write2Disk() {
        try {
            // opens the file for writing
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            // maps the file to memory
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, ENTRY_SIZE);
            // writes the data to the buffer
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            mappedByteBuffer.putLong(totalLenDoc);
            // closes the file
            fileChannel.close();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with writing to collection statistics file");
            return false;
        }
    }

    // reads data from disk
    public static boolean readFromDisk() {
        try {
            // opens the file for reading
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT), StandardOpenOption.READ);
            // maps the file to memory
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, ENTRY_SIZE);
            // reads the data from the buffer
            documents = mappedByteBuffer.getInt();
            avgDocLen = mappedByteBuffer.getDouble();
            terms = mappedByteBuffer.getLong();
            totalLenDoc = mappedByteBuffer.getLong();
            // closes the file
            fileChannel.close();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with reading from the collection statistics file");
            return false;
        }
    }
}
