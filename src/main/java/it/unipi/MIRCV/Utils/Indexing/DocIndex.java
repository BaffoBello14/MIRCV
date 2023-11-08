package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import org.junit.platform.commons.util.LruCache;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class DocIndex {
    // Singleton instance of the DocIndex class
    private static DocIndex instance = new DocIndex();

    // HashMap to store document index entries
    private HashMap<Integer, DocIndexEntry> documentIndex;

    // LRU cache to store recently accessed document index entries
    private final LruCache<Integer, DocIndexEntry> lruCache = new LruCache<>(PathAndFlags.DOC_INDEX_CACHE_SIZE);

    // Path to the document index file
    private static String Path_To_DocIndex = PathAndFlags.PATH_TO_DOC_INDEX;

    // Private constructor to prevent instantiation of the class from outside
    private DocIndex() {
        documentIndex = new HashMap<>();
    }

    // Method to get the length of a document given its ID
    public long getDoc_len(int doc_id) {
        // Check if the document index entry is present in the LRU cache
        if (lruCache.containsKey(doc_id)) {
            return lruCache.get(doc_id).getDoc_size();
        }

        try {
            // Open the document index file in read mode
            FileChannel fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ);

            // Create a new DocIndexEntry object to store the document index entry
            DocIndexEntry docIndexEntry = new DocIndexEntry();

            // Read the document index entry from the file
            int ret = docIndexEntry.readFromDisk((long) (doc_id - 1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE, fileChannel);

            // If the read was successful and the document ID matches, add the entry to the LRU cache and return the document length
            if (ret > 0 && ret == doc_id) {
                lruCache.put(doc_id, docIndexEntry);
                return docIndexEntry.getDoc_size();
            }

            // If the read was unsuccessful or the document ID does not match, return -1
            return -1;
        } catch (IOException e) {
            // If there was an exception while reading the document index file, print an error message and return -1
            System.out.println("problems with read form disk of the doc index ");
            e.printStackTrace();
            return -1;
        }
    }

    // Method to add a document index entry to the document index
    public void addDocument(int doc_id, String doc_no, long doc_size) {
        documentIndex.put(doc_id, new DocIndexEntry(doc_no, doc_size));
    }

    // Method to read a document index entry from the document index file given an offset
    public long readFromDisk(FileChannel fileChannel, long offset) {
        try {
            // Map a portion of the document index file to memory
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);

            // Read the document ID, document number, and document size from the MappedByteBuffer
            int docId = mappedByteBuffer.getInt();
            byte[] doc = new byte[DocIndexEntry.DOC_NO_LENGTH];
            mappedByteBuffer.get(doc);
            String docNo = new String(doc, StandardCharsets.UTF_8);
            long docSize = mappedByteBuffer.getLong();

            // Add the document index entry to the document index
            addDocument(docId, docNo, docSize);

            // Return the offset of the next document index entry
            return offset + DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            // If there was an exception while reading the document index file, print an error message and return -1
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }

    // Method to get the singleton instance of the DocIndex class
    public static DocIndex getInstance() {
        return instance;
    }

    // Method to get the document number given a document ID
    public String getDoc_NO(int doc_id) {
        // Check if the document index entry is present in the LRU cache
        if (lruCache.containsKey(doc_id)) {
            return lruCache.get(doc_id).getDoc_no();
        }

        try {
            // Open the document index file in read mode
            FileChannel fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ);

            // Create a new DocIndexEntry object to store the document index entry
            DocIndexEntry docIndexEntry = new DocIndexEntry();

            // Read the document index entry from the file
            int ret = docIndexEntry.readFromDisk((long) (doc_id - 1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE, fileChannel);

            // If the read was successful and the document ID matches, add the entry to the LRU cache and return the document number
            if (ret > 0 && ret == doc_id) {
                lruCache.put(doc_id, docIndexEntry);
                return docIndexEntry.getDoc_no();
            }

            // If the read was unsuccessful or the document ID does not match, return null
            return null;
        } catch (IOException e) {
            // If there was an exception while reading the document index file, print an error message and return null
            System.out.println("problems with read form disk of the doc index ");
            e.printStackTrace();
            return null;
        }
    }
}