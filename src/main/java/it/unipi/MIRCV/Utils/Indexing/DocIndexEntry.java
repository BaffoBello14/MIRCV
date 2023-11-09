package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Class representing an entry in the document index.
 */
public class DocIndexEntry {
    private String doc_no;
    private long doc_size;

    public static final int DOC_NO_LENGTH = 8;
    public static final int DOC_INDEX_ENTRY_SIZE = DOC_NO_LENGTH + 8 + 4;

    /**
     * Default constructor for DocIndexEntry.
     */
    public DocIndexEntry() {
    }

    /**
     * Parameterized constructor for DocIndexEntry.
     *
     * @param doc_no   The document number.
     * @param doc_size The document size.
     */
    public DocIndexEntry(String doc_no, long doc_size) {
        this.doc_no = padNumberWithZeros(doc_no, DOC_NO_LENGTH);
        this.doc_size = doc_size;
    }

    /**
     * Writes the DocIndexEntry to disk at the specified offset.
     *
     * @param fileChannel The FileChannel to write to.
     * @param offset      The offset in the file.
     * @param doc_id      The document ID.
     * @return The new offset after writing the entry.
     */
    public long write2Disk(FileChannel fileChannel, long offset, int doc_id) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, DOC_INDEX_ENTRY_SIZE);
            mappedByteBuffer.putInt(doc_id);
            mappedByteBuffer.put(padNumberWithZeros(doc_no, DOC_NO_LENGTH).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(doc_size);
            return offset + DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with writing to disk of doc index");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Getter for the document number.
     *
     * @return The document number.
     */
    public String getDoc_no() {
        return doc_no;
    }

    /**
     * Getter for the document size.
     *
     * @return The document size.
     */
    public long getDoc_size() {
        return doc_size;
    }

    /**
     * Converts the DocIndexEntry to a string representation.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return doc_no + " " + doc_size;
    }

    /**
     * Pads a number with zeros to achieve the specified width.
     *
     * @param numberString The number string to pad.
     * @param totalWidth   The total width of the padded number.
     * @return The padded number.
     */
    public static String padNumberWithZeros(String numberString, int totalWidth) {
        return String.format("%0" + totalWidth + "d", Long.parseLong(numberString));
    }

    /**
     * Reads the DocIndexEntry from disk at the specified offset.
     *
     * @param offset      The offset in the file.
     * @param fileChannel The FileChannel to read from.
     * @return The document ID.
     */
    public int readFromDisk(long offset, FileChannel fileChannel) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, DOC_INDEX_ENTRY_SIZE);
            int doc_id = mappedByteBuffer.getInt();
            byte[] docno = new byte[DOC_NO_LENGTH];
            mappedByteBuffer.get(docno);
            doc_no = new String(docno, StandardCharsets.UTF_8);
            doc_size = mappedByteBuffer.getLong();
            return doc_id;
        } catch (IOException e) {
            System.out.println("Problems with reading from disk of doc index");
            e.printStackTrace();
            return -1;
        }
    }
}
