package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Singleton class for managing document index information.
 */
public class DocIndex {
    private static DocIndex instance = new DocIndex();
    private final LFUCache<Integer, DocIndexEntry> lruCache = new LFUCache<>(PathAndFlags.DOC_INDEX_CACHE_SIZE);
    private static String Path_To_DocIndex = PathAndFlags.PATH_TO_DOC_INDEX;
    private static FileChannel fileChannel = null;

    static {
        try {
            File file= new File(Path_To_DocIndex);
            if(file.exists()){
                fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("problems with opening the file channel of doc index");
        }
    }

    private DocIndex() {
    }

    /**
     * Retrieves the document length for a given document ID.
     *
     * @param doc_id The document ID.
     * @return The document length, or -1 if an error occurs.
     */
    public long getDoc_len(int doc_id) {
        if (lruCache.containsKey(doc_id)) {
            return lruCache.get(doc_id).getDoc_size();
        }
        //try {
            //FileChannel fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ);
            DocIndexEntry docIndexEntry = new DocIndexEntry();
            int ret = docIndexEntry.readFromDisk((long) (doc_id - 1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE, fileChannel);
            if (ret > 0 && ret == doc_id) {
                lruCache.put(doc_id, docIndexEntry);
                return docIndexEntry.getDoc_size();
            }
            return -1;
        //} catch (IOException e) {
        //    System.out.println("Problems with reading from disk of the doc index ");
        //    e.printStackTrace();
        //    return -1;
        //}
    }

    /**
     * Retrieves the document number for a given document ID.
     *
     * @param doc_id The document ID.
     * @return The document number, or null if an error occurs.
     */
    public String getDoc_NO(int doc_id) {
        if (lruCache.containsKey(doc_id)) {
            return lruCache.get(doc_id).getDoc_no();
        }
        //try {
            //FileChannel fileChannel = FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ);
            DocIndexEntry docIndexEntry = new DocIndexEntry();
            int ret = docIndexEntry.readFromDisk((long) (doc_id - 1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE, fileChannel);
            if (ret > 0 && ret == doc_id) {
                lruCache.put(doc_id, docIndexEntry);
                return docIndexEntry.getDoc_no();
            }
            return null;
        //} catch (IOException e) {
        //    System.out.println("Problems with reading from disk of the doc index ");
        //    e.printStackTrace();
        //    return null;
        //}
    }

    /**
     * Retrieves the singleton instance of the DocIndex class.
     *
     * @return The DocIndex instance.
     */
    public static DocIndex getInstance() {
        return instance;
    }
}
