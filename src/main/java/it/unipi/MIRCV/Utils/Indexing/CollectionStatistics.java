package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CollectionStatistics {

    // Class-level static variables to hold various collection statistics.
    private static int documents;
    private static double avgDocLen;
    private static long totalLenDoc;
    private static long terms;
    protected static long ENTRY_SIZE = 4 + 8 + 8 + 8;  // Size of entry for writing to disk

    public static int getDocuments() {
        return documents;
    }

    // Computes the average document length.
    public static void computeAVGDOCLEN() {
        avgDocLen = (double) totalLenDoc / documents;
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

    public static long getTotalLenDoc() {
        return totalLenDoc;
    }

    public static void setTotalLenDoc(long totalLenDoc1) {
        CollectionStatistics.totalLenDoc = totalLenDoc1;
    }

    // Writes the collection statistics to disk.
    public static boolean write2Disk() {
        try {
            // Open a file channel for both reading and writing.
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // Map a portion of the file directly into memory for writing.
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, ENTRY_SIZE);

            // Write statistics to the mapped byte buffer.
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            mappedByteBuffer.putLong(totalLenDoc);

            // Close the file channel.
            fileChannel.close();

            return true;  // Return true indicating success.
        } catch (IOException e) {
            System.out.println("Problems with writing to collection statistics file");
            return false;  // Return false indicating failure.
        }
    }

    // Reads the collection statistics from disk.
    public static boolean readFromDisk() {
        try {
            // Open a file channel for reading.
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT), StandardOpenOption.READ);

            // Map a portion of the file directly into memory for reading.
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, ENTRY_SIZE);

            // Read statistics from the mapped byte buffer.
            documents = mappedByteBuffer.getInt();
            avgDocLen = mappedByteBuffer.getDouble();
            terms = mappedByteBuffer.getLong();
            totalLenDoc = mappedByteBuffer.getLong();

            // Close the file channel.
            fileChannel.close();

            return true;  // Return true indicating success.
        } catch (IOException e) {
            System.out.println("Problems with reading from the collection statistics file");
            return false;  // Return false indicating failure.
        }
    }
}
