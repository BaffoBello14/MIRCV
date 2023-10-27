package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DocIndex {
    // Data structure to hold the document index entries.
    private HashMap<Integer, DocIndexEntry> documentIndex;

    // Path to where the document index will be written on disk.
    private static String Path_To_DocIndex = PathAndFlags.PATH_TO_DOC_INDEX;

    // Constructor initializes the document index.
    public DocIndex() {
        documentIndex = new HashMap<>();
    }

    // Writes a document index entry to disk.
    public long write2Disk(int doc_id, long offset) {
        if (!documentIndex.containsKey(doc_id)) {
            return -1;
        }
        try {
            FileChannel fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            // Write the specific document index entry to disk.
            offset = documentIndex.get(doc_id).write2Disk(fileChannel, offset, doc_id);
            return offset;

        } catch (IOException e) {
            System.out.println("Problems writing document index to disk");
            return -1;
        }
    }

    // Getter for the document index.
    public HashMap<Integer, DocIndexEntry> getDocumentIndex() {
        return documentIndex;
    }

    // Setter for the document index.
    public void setDocumentIndex(HashMap<Integer, DocIndexEntry> documentIndex) {
        this.documentIndex = documentIndex;
    }

    // Adds a new document to the index.
    public void addDocument(int doc_id, String doc_no, long doc_size) {
        documentIndex.put(doc_id, new DocIndexEntry(doc_no, doc_size));
    }

    // Returns a sorted list of document IDs from the index.
    public ArrayList<Integer> sortDocIndex() {
        ArrayList<Integer> sortedDocIndex = new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
    }

    // Reads a document index entry from disk.
    public long readFromDisk(FileChannel fileChannel, long offset) {
        try {
            // Map a section of the file into memory for reading.
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);

            // Extract document information from the mapped byte buffer.
            int docId = mappedByteBuffer.getInt();
            byte[] doc = new byte[DocIndexEntry.DOC_NO_LENGTH];
            mappedByteBuffer.get(doc);
            String docNo = new String(doc, StandardCharsets.UTF_8);
            long docSize = mappedByteBuffer.getLong();

            System.out.println("docno" + docNo + "size" + docSize + "doc id " + docId);

            // Add the read document to the index.
            addDocument(docId, docNo, docSize);

            return offset + DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }
}
