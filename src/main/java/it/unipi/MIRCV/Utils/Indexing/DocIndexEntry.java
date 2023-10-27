package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class DocIndexEntry {
    private String doc_no;
    private long doc_size;
    protected static  final int DOC_NO_LENGTH=8;
    public static final int DOC_INDEX_ENTRY_SIZE=DOC_NO_LENGTH+8+4;
    public DocIndexEntry(String doc_no,long doc_size){
        this.doc_no=padNumberWithZeros(doc_no,DOC_NO_LENGTH);
        this.doc_size=doc_size;
    }
    public long write2Disk(FileChannel fileChannel,long offset,int doc_id){
        try {
            MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,offset,DOC_INDEX_ENTRY_SIZE);
            mappedByteBuffer.putInt(doc_id);
            mappedByteBuffer.put(padNumberWithZeros(doc_no,DOC_NO_LENGTH).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(doc_size);
            return offset+DOC_INDEX_ENTRY_SIZE;
        }catch (IOException e){
            System.out.println("problems with the writing to disk of doc index ");
            e.printStackTrace();
            return -1;
        }
    }
    public String getDoc_no() {
        return doc_no;
    }
    public long getDoc_size() {
        return doc_size;
    }

    @Override
    public String toString(){
        return doc_no+" "+doc_size;
    }
    public static String padNumberWithZeros(String numberString, int totalWidth) {
        return String.format("%0" + totalWidth + "d", Long.parseLong(numberString));
    }
}
