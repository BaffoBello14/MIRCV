package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
public class DocIndex {
    private static DocIndex instance=new DocIndex();
    private final LFUCache<Integer,DocIndexEntry>lruCache= new LFUCache<>(PathAndFlags.DOC_INDEX_CACHE_SIZE);

    private static String Path_To_DocIndex=PathAndFlags.PATH_TO_DOC_INDEX;
    private DocIndex(){

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

    public static DocIndex getInstance(){
        return instance;
    }
    public String getDoc_NO(int doc_id){
        if (lruCache.containsKey(doc_id)){
            return lruCache.get(doc_id).getDoc_no();
        }
        try{

            FileChannel fileChannel=FileChannel.open(Paths.get(Path_To_DocIndex),StandardOpenOption.READ);
            DocIndexEntry docIndexEntry= new DocIndexEntry();
            int ret=docIndexEntry.readFromDisk((long) (doc_id - 1) *DocIndexEntry.DOC_INDEX_ENTRY_SIZE,fileChannel);
            if(ret>0&&ret==doc_id){
                lruCache.put(doc_id,docIndexEntry);
                return docIndexEntry.getDoc_no();
            }
            return null;
        }catch (IOException e){
            System.out.println("problems with read form disk of the doc index ");
            e.printStackTrace();
            return null;
        }


    }

}
