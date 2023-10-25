package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

public class SkippingBlock {
    private long doc_id_offset;
    private int doc_id_size;
    private long freq_offset;
    private int freq_size;
    private int doc_id_max;
    private int num_posting_of_block;
    private long file_offset = 0;
    private static final int size_of_element = (8 + 4) * 2 + 4 + 4;
    private static final String Doc_ID_file = PathConfig.getFinalDir() + "/Doc_ids.dat";
    private static final String Freq_file = PathConfig.getFinalDir() + "/Freqs.dat";
    public static final int SIZE = Integer.BYTES + Integer.BYTES;

    public boolean writeOnDisk() {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(Doc_ID_file, file_offset, size_of_element);
            mappedByteBuffer.putLong(doc_id_offset);
            mappedByteBuffer.putInt(doc_id_size);
            mappedByteBuffer.putLong(freq_offset);
            mappedByteBuffer.putInt(freq_size);
            mappedByteBuffer.putInt(doc_id_max);
            mappedByteBuffer.putInt(num_posting_of_block);
            
            DiskIOManager.writeToDisk(Doc_ID_file, mappedByteBuffer);
            file_offset += size_of_element;
            return true;
        } catch (IOException e) {
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
    
    public ArrayList<Posting> getSkippingBlockPostings(boolean compression) {
        try {
            MappedByteBuffer mappedByteBufferDocID = DiskIOManager.readFromDisk(Doc_ID_file, doc_id_offset, doc_id_size);
            MappedByteBuffer mappedByteBufferFreq = DiskIOManager.readFromDisk(Freq_file, freq_offset, freq_size);

            return getSkippingBlockPostings(compression, mappedByteBufferDocID, mappedByteBufferFreq);
        } catch (IOException e) {
            System.out.println("problems with the reading form the file of the block descriptor");
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Posting> getSkippingBlockPostings(boolean compression, MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        return compression ? getCompressedPostings(docIdBuffer, freqBuffer) : getUncompressedPostings(docIdBuffer, freqBuffer);
    }

    private ArrayList<Posting> getCompressedPostings(MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        byte[] docIds = new byte[doc_id_size];
        byte[] freqs = new byte[freq_size];

        docIdBuffer.get(docIds);
        freqBuffer.get(freqs);

        int[] freqsDecompressed = UnaryConverter.convertFromUnary(freqs, num_posting_of_block);
        int[] docIdsDecompressed = VariableByteEncoder.decodeArray(docIds);

        ArrayList<Posting> postings = new ArrayList<>(num_posting_of_block);
        for (int i = 0; i < num_posting_of_block; i++) {
            postings.add(new Posting(docIdsDecompressed[i], freqsDecompressed[i]));
        }

        return postings;
    }

    private ArrayList<Posting> getUncompressedPostings(MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        ArrayList<Posting> postings = new ArrayList<>(num_posting_of_block);

        for (int i = 0; i < num_posting_of_block; i++) {
            postings.add(new Posting(docIdBuffer.getInt(), freqBuffer.getInt()));
        }

        return postings;
    }
}
