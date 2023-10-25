package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

public class SkippingBlock {
    
    private long docIdOffset;
    private int docIdSize;
    private long freqOffset;
    private int freqSize;
    private int docIdMax;
    private int numPostingOfBlock;
    private long fileOffset;
    public static final int SIZE = (Long.BYTES + Integer.BYTES) * 2 + Integer.BYTES + Integer.BYTES;
    private final String docIdFile = PathConfig.getFinalDir() + "/Doc_ids.dat";
    private final String freqFile = PathConfig.getFinalDir() + "/Freqs.dat";

    public boolean writeToDisk() throws IOException {
        MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(docIdFile, fileOffset, sizeOfElement());
        mappedByteBuffer.putLong(docIdOffset);
        mappedByteBuffer.putInt(docIdSize);
        mappedByteBuffer.putLong(freqOffset);
        mappedByteBuffer.putInt(freqSize);
        mappedByteBuffer.putInt(docIdMax);
        mappedByteBuffer.putInt(numPostingOfBlock);
        
        DiskIOManager.writeToDisk(docIdFile, mappedByteBuffer);
        fileOffset += sizeOfElement();

        return true;
    }

    private int sizeOfElement() {
        return (Long.BYTES + Integer.BYTES) * 2 + Integer.BYTES + Integer.BYTES;
    }

    public long getDocIdOffset() {
        return docIdOffset;
    }
    
    public void setDocIdOffset(long docIdOffset) {
        this.docIdOffset = docIdOffset;
    }
    
    public int getDocIdSize() {
        return docIdSize;
    }
    
    public void setDocIdSize(int docIdSize) {
        this.docIdSize = docIdSize;
    }
    
    public long getFreqOffset() {
        return freqOffset;
    }
    
    public void setFreqOffset(long freqOffset) {
        this.freqOffset = freqOffset;
    }
    
    public int getFreqSize() {
        return freqSize;
    }
    
    public void setFreqSize(int freqSize) {
        this.freqSize = freqSize;
    }
    
    public int getDocIdMax() {
        return docIdMax;
    }
    
    public void setDocIdMax(int docIdMax) {
        this.docIdMax = docIdMax;
    }
    
    public int getNumPostingOfBlock() {
        return numPostingOfBlock;
    }
    
    public void setNumPostingOfBlock(int numPostingOfBlock) {
        this.numPostingOfBlock = numPostingOfBlock;
    }
    
    public long getFileOffset() {
        return fileOffset;
    }
    
    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }    

    public ArrayList<Posting> retrievePostingsFromDisk(boolean compression) throws IOException {
        MappedByteBuffer docIdBuffer = DiskIOManager.readFromDisk(docIdFile, docIdOffset, docIdSize);
        MappedByteBuffer freqBuffer = DiskIOManager.readFromDisk(freqFile, freqOffset, freqSize);
        
        return getPostingsFromBuffers(compression, docIdBuffer, freqBuffer);
    }

    private ArrayList<Posting> getPostingsFromBuffers(boolean compression, MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        return compression ? decodeCompressedPostings(docIdBuffer, freqBuffer) : decodeUncompressedPostings(docIdBuffer, freqBuffer);
    }

    private ArrayList<Posting> decodeCompressedPostings(MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        byte[] docIds = new byte[docIdSize];
        byte[] freqs = new byte[freqSize];

        docIdBuffer.get(docIds);
        freqBuffer.get(freqs);

        int[] freqsDecompressed = UnaryConverter.convertFromUnary(freqs, numPostingOfBlock);
        int[] docIdsDecompressed = VariableByteEncoder.decodeArray(docIds);

        ArrayList<Posting> postings = new ArrayList<>(numPostingOfBlock);
        for (int i = 0; i < numPostingOfBlock; i++) {
            postings.add(new Posting(docIdsDecompressed[i], freqsDecompressed[i]));
        }

        return postings;
    }

    private ArrayList<Posting> decodeUncompressedPostings(MappedByteBuffer docIdBuffer, MappedByteBuffer freqBuffer) {
        ArrayList<Posting> postings = new ArrayList<>(numPostingOfBlock);
        for (int i = 0; i < numPostingOfBlock; i++) {
            postings.add(new Posting(docIdBuffer.getInt(), freqBuffer.getInt()));
        }
        return postings;
    }
}
