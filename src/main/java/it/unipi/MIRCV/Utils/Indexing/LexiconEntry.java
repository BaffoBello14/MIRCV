package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class LexiconEntry {
    private static String BLOCK_DESC_PATH= PathAndFlags.PATH_TO_BLOCK_FILE+"/BlockInfo.dat";
    private long offset_doc_id=0;
    private int UpperTF=0;
    private int df=0;
    private double idf=0;
    private long offset_frequency=0;
    private long offset_skip_pointer=0;
    protected static final long ENTRY_SIZE=4*8+2*4+Lexicon.MAX_LEN_OF_TERM;



    public void updateTFMAX(PostingIndex index){
        for(Posting posting: index.getPostings()){
            if(posting.getFrequency()>this.UpperTF){
                this.UpperTF=posting.getFrequency();
            }
            this.df++;
        }
    }
    public double calculateIDF(){
        this.idf=Math.log(CollectionStatistics.getDocuments()/(double)this.df);
        return 0;
    }

    @Override
    public String toString() {
        return "LexiconEntry{" +
                "offset_doc_id=" + offset_doc_id +
                ", UpperTF=" + UpperTF +
                ", df=" + df +
                ", idf=" + idf +
                ", offset_frequency=" + offset_frequency +
                ", offset_skip_pointer=" + offset_skip_pointer +
                '}';
    }

    public long write2Disk(String term,long offset,FileChannel fileChannel){
        try {
            MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,offset,ENTRY_SIZE);
            if(mappedByteBuffer==null){
                return -1;
            }
            mappedByteBuffer.put(Lexicon.padStringToLength(term).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(offset_doc_id);
            mappedByteBuffer.putLong(offset_frequency);
            mappedByteBuffer.putInt(UpperTF);
            mappedByteBuffer.putInt(df);
            mappedByteBuffer.putDouble(idf);
            mappedByteBuffer.putLong(offset_skip_pointer);
            return offset+ENTRY_SIZE;
        }catch (IOException e){
            System.out.println("problems with the writing to disk of lexicon");
            return -1;
        }
    }
    public String readFromDisk(long offset, FileChannel fileChannel,String termtoRead) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, ENTRY_SIZE);
            if (mappedByteBuffer == null) {
                return "";
            }

            // Read the data from the MappedByteBuffer
            byte[] termBytes = new byte[Lexicon.MAX_LEN_OF_TERM];
            mappedByteBuffer.get(termBytes);
            String term = Lexicon.removePadding(new String(termBytes, StandardCharsets.UTF_8));
            if(!term.equals(termtoRead)){
                return "";
            }
            this.offset_doc_id = mappedByteBuffer.getLong();
            this.offset_frequency = mappedByteBuffer.getLong();
            this.UpperTF = mappedByteBuffer.getInt();
            this.df = mappedByteBuffer.getInt();
            this.idf = mappedByteBuffer.getDouble();
            this.offset_skip_pointer = mappedByteBuffer.getLong();

            return term;
        } catch (IOException e) {
            System.out.println("Problems with reading from the lexicon file.");
            return "";
        }
    }
    public int numBlocksnecessary(){
        if(df>512){
            return (int)Math.ceil(Math.sqrt(df));
        }
        return 1;
    }
    public int postingsPerBlock(int blocks){
        return (int)Math.ceil(df/(double)blocks);
    }

    public long getOffset_doc_id() {
        return offset_doc_id;
    }

    public void setOffset_doc_id(long offset_doc_id) {
        this.offset_doc_id = offset_doc_id;
    }

    public int getUpperTF() {
        return UpperTF;
    }

    public void setUpperTF(int upperTF) {
        UpperTF = upperTF;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public long getOffset_frequency() {
        return offset_frequency;
    }

    public void setOffset_frequency(long offset_frequency) {
        this.offset_frequency = offset_frequency;
    }

    public long getOffset_skip_pointer() {
        return offset_skip_pointer;
    }

    public void setOffset_skip_pointer(long offset_skip_pointer) {
        this.offset_skip_pointer = offset_skip_pointer;
    }


    //need a function to read the Skipping blocks
}
