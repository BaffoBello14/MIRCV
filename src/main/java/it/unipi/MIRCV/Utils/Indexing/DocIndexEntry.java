package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class DocIndexEntry {
    // Variables to hold document number and its size.
    private String doc_no;
    private long doc_size;

    // Constant to define the length of the document number.
    protected static final int DOC_NO_LENGTH = 8;
    
    // Constant to define the total entry size in the index.
    public static final int DOC_INDEX_ENTRY_SIZE = DOC_NO_LENGTH + 8 + 4;

    // Constructor initializes the document entry with provided number and size.
    public DocIndexEntry(String doc_no, long doc_size) {
        this.doc_no = padNumberWithZeros(doc_no, DOC_NO_LENGTH);
        this.doc_size = doc_size;
    }

    // Writes the document entry to disk.
    public long write2Disk(FileChannel fileChannel, long offset, int doc_id) {
        try {
            // Map a section of the file into memory for writing.
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, DOC_INDEX_ENTRY_SIZE);
            
            // Write the document ID, document number, and document size to the mapped byte buffer.
            mappedByteBuffer.putInt(doc_id);
            mappedByteBuffer.put(padNumberWithZeros(doc_no, DOC_NO_LENGTH).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(doc_size);
            
            return offset + DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("problems with the writing to disk of doc index");
            e.printStackTrace();
            return -1;
        }
    }

    // Getter for the document number.
    public String getDoc_no() {
        return doc_no;
    }

    // Getter for the document size.
    public long getDoc_size() {
        return doc_size;
    }

    // Custom string representation for the document entry.
    @Override
    public String toString() {
        return doc_no + " " + doc_size;
    }

    // Utility method to pad a number string with leading zeros until it reaches a specified width.
    public static String padNumberWithZeros(String numberString, int totalWidth) {
        return String.format("%0" + totalWidth + "d", Long.parseLong(numberString));
    }
}
