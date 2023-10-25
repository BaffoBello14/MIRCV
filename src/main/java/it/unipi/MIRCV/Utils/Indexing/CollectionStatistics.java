package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.Paths.PathConfig;
import it.unipi.MIRCV.Utils.DiskIOManager;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class CollectionStatistics {
    private static int documents;
    private static double avgDocLen;
    private static long totalLenDoc;
    protected static long ENTRY_SIZE = 4 + 8 + 8 + 8;
    private static long terms;

    public static int getDocuments() {
        return documents;
    }

    public static void setDocuments(int documentCount) {
        documents = documentCount;
    }

    public static double getAvgDocLen() {
        return avgDocLen;
    }

    public static void setAvgDocLen(double averageDocLength) {
        avgDocLen = averageDocLength;
    }

    public static long getTerms() {
        return terms;
    }

    public static void setTerms(long termCount) {
        terms = termCount;
    }

    public static boolean write2Disk() {
        String path = PathConfig.getCollectionStatDir() + "/CollectionStatistics.dat";
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(path, 0, ENTRY_SIZE);
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            return DiskIOManager.writeToDisk(path, mappedByteBuffer);
        } catch (IOException e) {
            System.out.println("Problems with writing to collection statistics file: " + e.getMessage());
            return false;
        }
    }

    public static long getTotalLenDoc() {
        return totalLenDoc;
    }

    public static void setTotalLenDoc(long totalLenDoc) {
        CollectionStatistics.totalLenDoc = totalLenDoc;
    }

    public static boolean readFromDisk() {
        String path = PathConfig.getCollectionStatDir() + "/CollectionStatistics.dat";
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(path, 0, ENTRY_SIZE);
            documents = mappedByteBuffer.getInt();
            avgDocLen = mappedByteBuffer.getDouble();
            terms = mappedByteBuffer.getLong();
            totalLenDoc=mappedByteBuffer.getLong();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with reading from the collection statistics file: " + e.getMessage());
            return false;
        }
    }
}
