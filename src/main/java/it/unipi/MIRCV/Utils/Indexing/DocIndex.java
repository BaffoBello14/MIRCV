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
    private static DocIndex instance=new DocIndex();
    private HashMap<Integer,DocIndexEntry> documentIndex;
    private final LruCache<Integer,DocIndexEntry>lruCache= new LruCache<>(PathAndFlags.DOC_INDEX_CACHE_SIZE);

    private static String Path_To_DocIndex=PathAndFlags.PATH_TO_DOC_INDEX;
    private DocIndex(){
        documentIndex=new HashMap<>();
    }
    public long getDoc_len(int doc_id){
        if (lruCache.containsKey(doc_id)){
            return lruCache.get(doc_id).getDoc_size();
        }
        try{

            FileChannel fileChannel=FileChannel.open(Paths.get(Path_To_DocIndex),StandardOpenOption.READ);
            DocIndexEntry docIndexEntry= new DocIndexEntry();
            int ret=docIndexEntry.readFromDisk((long) (doc_id - 1) *DocIndexEntry.DOC_INDEX_ENTRY_SIZE,fileChannel);
            if(ret>0&&ret==doc_id){
                lruCache.put(doc_id,docIndexEntry);
                return docIndexEntry.getDoc_size();
            }
            return -1;
        }catch (IOException e){
            System.out.println("problems with read form disk of the doc index ");
            e.printStackTrace();
            return -1;
        }


    }
    public long write2Disk(int doc_id,long offset){
        if(!documentIndex.containsKey(doc_id)){
            return -1;
        }
        try{
            FileChannel fileChannel =FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            offset = documentIndex.get(doc_id).write2Disk(fileChannel, offset, doc_id);
            return offset;

        }catch(IOException e){
            System.out.println("Problems writing document index to disk");
            return -1;
        }
    }

    public void addDocument(int doc_id,String doc_no,long doc_size){
        documentIndex.put(doc_id,new DocIndexEntry(doc_no,doc_size));
    }

    public long readFromDisk(FileChannel fileChannel, long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);

            // Read data from the MappedByteBuffer
            int docId = mappedByteBuffer.getInt();
            byte[] doc=new byte[DocIndexEntry.DOC_NO_LENGTH];
            mappedByteBuffer.get(doc);
            String docNo = new String(doc, StandardCharsets.UTF_8);
            long docSize = mappedByteBuffer.getLong();
            System.out.println("docno"+docNo+"size"+docSize+"doc id "+docId);
            addDocument(docId,docNo,docSize);


            return offset+DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }
    public static DocIndex getInstance(){
        return instance;
    }


}
