package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SkippingBlock {
    private static String Freq_file= PathAndFlags.PATH_TO_FINAL_FREQ;
    private static String Doc_ID_file=PathAndFlags.PATH_TO_FINAL_DOC_ID;
    private long doc_id_offset;
    private int doc_id_size;
    private long freq_offset;
    private int freq_size;
    private int doc_id_max;
    private int num_posting_of_block;
    private long file_offset=0;
    private static final int size_of_element=(8+4)*2+4+4;

    public boolean writeOnDisk(FileChannel file_to_write){
        if(file_to_write == null){
            return false;
        }
        try{
            MappedByteBuffer mappedByteBuffer=file_to_write.map(FileChannel.MapMode.READ_WRITE,file_offset,size_of_element);
            if(mappedByteBuffer==null){
                return false;
            }
            mappedByteBuffer.putLong(doc_id_offset);
            mappedByteBuffer.putInt(doc_id_size);
            mappedByteBuffer.putLong(freq_offset);
            mappedByteBuffer.putInt(freq_size);
            mappedByteBuffer.putInt(doc_id_max);
            mappedByteBuffer.putInt(num_posting_of_block);
            file_offset+=size_of_element;
            return true;
        }catch (IOException e){
            System.out.println("problems with the write of block of posting");
            return false;
        }
    }

    public long getDoc_id_offset() {
        return doc_id_offset;
    }

    public void setDoc_id_offset(long doc_id_offset) {
        this.doc_id_offset = doc_id_offset;
    }

    public long getDoc_id_size() {
        return doc_id_size;
    }

    public void setDoc_id_size(int doc_id_size) {
        this.doc_id_size = doc_id_size;
    }

    public long getFreq_offset() {
        return freq_offset;
    }

    public void setFreq_offset(long freq_offset) {
        this.freq_offset = freq_offset;
    }

    public long getFreq_size() {
        return freq_size;
    }

    public void setFreq_size(int freq_size) {
        this.freq_size = freq_size;
    }

    public int getDoc_id_max() {
        return doc_id_max;
    }

    public void setDoc_id_max(int doc_id_max) {
        this.doc_id_max = doc_id_max;
    }

    public int getNum_posting_of_block() {
        return num_posting_of_block;
    }

    public void setNum_posting_of_block(int num_posting_of_block) {
        this.num_posting_of_block = num_posting_of_block;
    }

    public long getFile_offset() {
        return file_offset;
    }

    public void setFile_offset(long file_offset) {
        this.file_offset = file_offset;
    }
    public ArrayList<Posting> getSkippingBlockPostings(boolean compression){
        try{
            FileChannel fileChannelDocID= (FileChannel) Files.newByteChannel(Paths.get(Doc_ID_file));
            FileChannel fileChannelFreqs= (FileChannel) Files.newByteChannel(Paths.get(Freq_file));
            MappedByteBuffer mappedByteBufferDocID=fileChannelDocID.map(FileChannel.MapMode.READ_ONLY,doc_id_offset,doc_id_size);
            MappedByteBuffer mappedByteBufferFreq=fileChannelFreqs.map(FileChannel.MapMode.READ_ONLY,freq_offset,freq_size);
            if(mappedByteBufferFreq==null||mappedByteBufferDocID==null){
                return null;
            }
            ArrayList<Posting> postings=new ArrayList<>();
            if(compression){
                byte [] doc_ids=new byte[doc_id_size];
                byte [] freqs=new byte[freq_size];
                mappedByteBufferDocID.get(doc_ids,0,doc_id_size);
                mappedByteBufferFreq.get(freqs,0,freq_size);
                int [] freqs_decompressed= UnaryConverter.convertFromUnary(freqs,num_posting_of_block);
                int [] doc_ids_decompressed= VariableByteEncoder.decodeArray(doc_ids);
                for(int i=0;i<num_posting_of_block;i++){
                    Posting posting=new Posting(doc_ids_decompressed[i],freqs_decompressed[i]);
                    postings.add(posting);
                }
            }else {
                for(int i=0;i<num_posting_of_block;i++){
                    Posting posting=new Posting(mappedByteBufferDocID.getInt(),mappedByteBufferFreq.getInt());
                    postings.add(posting);
                }
            }
            return postings;
        }catch (IOException e){
            System.out.println("problems with the reading form the file of the block desciptor");
            e.printStackTrace();
            return null;
        }
    }
}
