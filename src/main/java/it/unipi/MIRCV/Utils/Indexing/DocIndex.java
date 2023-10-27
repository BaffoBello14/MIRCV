package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
<<<<<<< HEAD
import java.nio.channels.FileChannel;
=======
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
>>>>>>> 1b303430e4448ef03e4920045b8c72e5174387d7
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocIndex {
<<<<<<< HEAD
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
=======
<<<<<<< HEAD
    private HashMap<Integer,DocIndexEntry> documentIndex;

    private static String Path_To_DocIndex=PathAndFlags.PATH_TO_DOC_INDEX;
    public DocIndex(){
        documentIndex=new HashMap<>();
    }
    public long write2Disk(int doc_id,long offset){
        if(!documentIndex.containsKey(doc_id)){
            return -1;
        }
        try{
            FileChannel fileChannel =FileChannel.open(Paths.get(Path_To_DocIndex), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            offset = documentIndex.get(doc_id).write2Disk(fileChannel, offset, doc_id);
            return offset;
=======

    private HashMap<Integer, DocIndexEntry> documentIndex = new HashMap<>();
    private static long offset = 0;
    private static final String PATH_TO_DOC_INDEX = PathConfig.getDocIndexDir() + "/DocIndex.dat";

    public long write2Disk(int doc_id) {
        DocIndexEntry entry = documentIndex.get(doc_id);
        if (entry == null) {
            return -1;
        }
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f

        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PATH_TO_DOC_INDEX, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);
            offset = entry.write2Disk(mappedByteBuffer, doc_id);
            DiskIOManager.writeToDisk(PATH_TO_DOC_INDEX, mappedByteBuffer);
>>>>>>> 1b303430e4448ef03e4920045b8c72e5174387d7
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
    public void addDocument(int doc_id,String doc_no,int doc_size){
        documentIndex.put(doc_id,new DocIndexEntry(doc_no,doc_size));
    }
    public ArrayList<Integer> sortDocIndex(){
        ArrayList<Integer>sortedDocIndex=new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
    }

<<<<<<< HEAD
=======
    public long readFromDisk(long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PATH_TO_DOC_INDEX, offset, DocIndexEntry.DOC_INDEX_ENTRY_SIZE);

            // Read data from the MappedByteBuffer
            int docId = mappedByteBuffer.getInt();
            byte[] doc = new byte[DocIndexEntry.DOC_NO_LENGTH];
            mappedByteBuffer.get(doc);
            String docNo = new String(doc, StandardCharsets.UTF_8);
            long docSize = mappedByteBuffer.getLong();
<<<<<<< HEAD
            System.out.println("docno"+docNo+"size"+docSize+"doc id "+docId);
            addDocument(docId,docNo,docSize);
=======
>>>>>>> e1cb0e5b4b73e809a29cbb83f693ae0f3047ab8f

            System.out.println("docno" + docNo + "size" + docSize);
            addDocument(docId, docNo, (int)docSize);

            return offset + DocIndexEntry.DOC_INDEX_ENTRY_SIZE;
        } catch (IOException e) {
            System.out.println("Problems with reading document index from disk");
            return -1;
        }
    }
>>>>>>> 1b303430e4448ef03e4920045b8c72e5174387d7
}
