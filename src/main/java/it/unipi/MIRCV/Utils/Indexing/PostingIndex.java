package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class PostingIndex {
    private String term;
    private ArrayList<Posting> postings = new ArrayList<>();
    private ArrayList<SkippingBlock> blocks;
    private SkippingBlock skippingBlockActual;
    private Posting postingActual;
    private Iterator<Posting> postingIterator;
    private Iterator<SkippingBlock> skippingBlockIterator;
    private boolean compression;
    

    public void closeLists() {
        postings.clear();
        blocks.clear();
    }

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public Posting getPostingActual() {
        return postingActual;
    }

    public String getTerm() {
        return term;
    }

    public ArrayList<Posting> getPostings() {
        return postings;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void addPostings(ArrayList<Posting> postings2Add) {
        postings.addAll(postings2Add);
    }

    public void openList() {
        try {
            MappedByteBuffer postingsBuffer = DiskIOManager.readFromDisk(
                PathConfig.getFreqDir() + term + ".dat", 
                0, 
                postings.size() * Posting.SIZE // Assuming you have a SIZE constant in Posting
            );

            MappedByteBuffer blocksBuffer = DiskIOManager.readFromDisk(
                PathConfig.getBlockFileDir() + term + ".dat",
                0,
                blocks.size() * SkippingBlock.SIZE // Assuming you have a SIZE constant in SkippingBlock
            );

            postings = extractPostingsFromBuffer(postingsBuffer);
            blocks = extractBlocksFromBuffer(blocksBuffer);

            skippingBlockIterator = blocks.iterator();
            postingIterator = postings.iterator();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Posting> extractPostingsFromBuffer(MappedByteBuffer buffer) {
        ArrayList<Posting> postingList = new ArrayList<>();

        // Assuming you can extract a posting from a buffer
        while (buffer.hasRemaining()) {
            int docId = buffer.getInt();
            int freq = buffer.getInt();
            postingList.add(new Posting(docId, freq));
        }

        return postingList;
    }

    private ArrayList<SkippingBlock> extractBlocksFromBuffer(MappedByteBuffer buffer) {
        ArrayList<SkippingBlock> blockList = new ArrayList<>();
    
        while (buffer.hasRemaining()) {
            SkippingBlock block = new SkippingBlock();
    
            block.setDocIdOffset(buffer.getLong());
            block.setDocIdSize(buffer.getInt());
            block.setFreqOffset(buffer.getLong());
            block.setFreqSize(buffer.getInt());
            block.setDocIdMax(buffer.getInt());
            block.setNumPostingOfBlock(buffer.getInt());
    
            blockList.add(block);
        }
    
        return blockList;
    }    

    public Posting next(){
        if(!postingIterator.hasNext()){
            if(!skippingBlockIterator.hasNext()){
                postingActual=null;
                return null;

            }
            skippingBlockActual=skippingBlockIterator.next();

            postings.clear();
            try {
                postings.addAll(skippingBlockActual.retrievePostingsFromDisk(compression));
            } catch (IOException e) {
                e.printStackTrace();
            }

            postingIterator=postings.iterator();
        }
        postingActual=postingIterator.next();
        return postingActual;
    }
}
