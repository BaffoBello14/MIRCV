package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Class for managing collection statistics including document count, average document length, term count, and total document length.
 */
public class CollectionStatistics {
    private static int documents;
    private static double avgDocLen;
    private static long totalLenDoc;
    protected static long ENTRY_SIZE = 4 + 8 + 8 + 8;
    private static long terms;

    /**
     * Getter for the number of documents in the collection.
     *
     * @return The number of documents.
     */
    public static int getDocuments() {
        return documents;
    }

    /**
     * Computes and sets the average document length.
     */
    public static void computeAVGDOCLEN() {
        avgDocLen = (double) totalLenDoc / documents;
    }

    /**
     * Setter for the number of documents in the collection.
     *
     * @param documents1 The number of documents to set.
     */
    public static void setDocuments(int documents1) {
        documents = documents1;
    }

    /**
     * Getter for the average document length.
     *
     * @return The average document length.
     */
    public static double getAvgDocLen() {
        return avgDocLen;
    }

    /**
     * Setter for the average document length.
     *
     * @param avgDocLen1 The average document length to set.
     */
    public static void setAvgDocLen(double avgDocLen1) {
        avgDocLen = avgDocLen1;
    }

    /**
     * Getter for the total number of terms in the collection.
     *
     * @return The total number of terms.
     */
    public static long getTerms() {
        return terms;
    }

    /**
     * Setter for the total number of terms in the collection.
     *
     * @param terms1 The total number of terms to set.
     */
    public static void setTerms(long terms1) {
        terms = terms1;
    }

    /**
     * Getter for the total document length in the collection.
     *
     * @return The total document length.
     */
    public static long getTotalLenDoc() {
        return totalLenDoc;
    }

    /**
     * Setter for the total document length in the collection.
     *
     * @param totalLenDoc The total document length to set.
     */
    public static void setTotalLenDoc(long totalLenDoc) {
        CollectionStatistics.totalLenDoc = totalLenDoc;
    }

    /**
     * Writes collection statistics to disk.
     *
     * @return True if writing is successful, false otherwise.
     */
    public static boolean write2Disk() {
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, ENTRY_SIZE);

            // Write data to the MappedByteBuffer
            mappedByteBuffer.putInt(documents);
            mappedByteBuffer.putDouble(avgDocLen);
            mappedByteBuffer.putLong(terms);
            mappedByteBuffer.putLong(totalLenDoc);

            // Close the FileChannel
            fileChannel.close();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with writing to the collection statistics file");
            return false;
        }
    }

    /**
     * Reads collection statistics from disk.
     *
     * @return True if reading is successful, false otherwise.
     */
    public static boolean readFromDisk() {
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_COLLECTION_STAT), StandardOpenOption.READ);
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, ENTRY_SIZE);

            // Read data from the MappedByteBuffer
            documents = mappedByteBuffer.getInt();
            avgDocLen = mappedByteBuffer.getDouble();
            terms = mappedByteBuffer.getLong();
            totalLenDoc = mappedByteBuffer.getLong();

            // Close the FileChannel
            fileChannel.close();
            return true;
        } catch (IOException e) {
            System.out.println("Problems with reading from the collection statistics file");
            return false;
        }
    }
}
