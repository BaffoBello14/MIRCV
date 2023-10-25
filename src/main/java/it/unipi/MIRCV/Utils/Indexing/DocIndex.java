package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocIndex {
    private HashMap<Integer,DocIndexEntry> documentIndex;
    private static long offset=0;
    private static String Path_To_DocIndex=PathAndFlags.PATH_TO_DOC_INDEX+"/DocIndex.dat";
    public DocIndex(){
        documentIndex=new HashMap<>();
    }
    public long write2Disk(int doc_id){
        if(!documentIndex.containsKey(doc_id)){
            return -1;
        }
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(Path_To_DocIndex);
            FileChannel fileChannel = fileOutputStream.getChannel();
            offset = documentIndex.get(doc_id).write2Disk(fileChannel, offset, doc_id);
            return offset;

        }catch(IOException e){
            System.out.println("Problems writing document index to disk");
            return -1;
        }
    }

    public HashMap<Integer, DocIndexEntry> getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(HashMap<Integer, DocIndexEntry> documentIndex) {
        this.documentIndex = documentIndex;
    }
    public void addDocument(int doc_id,String doc_no,long doc_size){
        documentIndex.put(doc_id,new DocIndexEntry(doc_no,doc_size));
    }
    public ArrayList<Integer> sortDocIndex(){
        ArrayList<Integer>sortedDocIndex=new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
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
            System.out.println("docno"+docNo+"size"+docSize);
            addDocument(docId,docNo,docSize);


            return offset+DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }


}
