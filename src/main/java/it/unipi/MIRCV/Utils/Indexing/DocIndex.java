package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocIndex {
    private HashMap<Integer,DocIndexEntry> documentIndex;
    private static int offset=0;
    private static final int ENTRY_SIZE=4+4+8;
    public DocIndex(){
        documentIndex=new HashMap<>();
    }

    public HashMap<Integer, DocIndexEntry> getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(HashMap<Integer, DocIndexEntry> documentIndex) {
        this.documentIndex = documentIndex;
    }
    public void addDocument(int doc_id,int doc_no,int doc_size){
        documentIndex.put(doc_id,new DocIndexEntry(doc_no,doc_size));
    }
    public ArrayList<Integer> sortDocIndex(){
        ArrayList<Integer>sortedDocIndex=new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
    }
    public boolean write2Disk(FileChannel fileChannel){
        if(fileChannel==null){
            return false;
        }
        try{
            MappedByteBuffer mappedByteBuffer= fileChannel.map(FileChannel.MapMode.READ_WRITE,offset, (long) ENTRY_SIZE * documentIndex.size());
            if(mappedByteBuffer==null){
                return false;
            }
            List<Integer> doc_ids=sortDocIndex();
            for(int i:doc_ids){
                mappedByteBuffer.putInt(i);
                mappedByteBuffer.putInt(documentIndex.get(i).getDoc_no());
                mappedByteBuffer.putLong(documentIndex.get(i).getDoc_size());
            }
            return true;

        }catch(IOException e){
            System.out.println("problem with file channel at writing doc index to file");
            e.printStackTrace();
            return false;
        }
    }
}
