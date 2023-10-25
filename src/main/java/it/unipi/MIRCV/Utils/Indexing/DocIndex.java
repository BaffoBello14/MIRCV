package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

public class DocIndex {

    private HashMap<Integer, DocIndexEntry> documentIndex = new HashMap<>();
    private static long offset = 0;
    private static final String PATH_TO_DOC_INDEX = PathConfig.getDocIndexDir() + "/DocIndex.dat";

    public long write2Disk(int doc_id) {
        DocIndexEntry entry = documentIndex.get(doc_id);
        if (entry == null) {
            return -1;
        }

        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PATH_TO_DOC_INDEX, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);
            offset = entry.write2Disk(mappedByteBuffer, doc_id);
            DiskIOManager.writeToDisk(PATH_TO_DOC_INDEX, mappedByteBuffer);
            return offset;
        } catch (IOException e) {
            System.out.println("Problems writing document index to disk");
            e.printStackTrace();
            return -1;
        }
    }

    public HashMap<Integer, DocIndexEntry> getDocumentIndex() {
        return new HashMap<>(documentIndex); // Return a shallow copy for encapsulation
    }

    public void setDocumentIndex(HashMap<Integer, DocIndexEntry> documentIndex) {
        this.documentIndex = new HashMap<>(documentIndex); // Use a shallow copy for encapsulation
    }

    public void addDocument(int doc_id, String doc_no, int doc_size) {
        documentIndex.put(doc_id, new DocIndexEntry(doc_no, doc_size));
    }

    public ArrayList<Integer> sortDocIndex() {
        ArrayList<Integer> sortedDocIndex = new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
    }

    public long readFromDisk(long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PATH_TO_DOC_INDEX, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);

            // Read data from the MappedByteBuffer
            int docId = mappedByteBuffer.getInt();
            byte[] doc = new byte[DocIndexEntry.DOC_NO_LENGTH];
            mappedByteBuffer.get(doc);
            String docNo = new String(doc, StandardCharsets.UTF_8);
            long docSize = mappedByteBuffer.getLong();

            System.out.println("docno" + docNo + "size" + docSize);
            addDocument(docId, docNo, (int)docSize);

            return offset + DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }
}
