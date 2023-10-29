package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LexiconEntry {
    private String term;
    private long offset_doc_id = 0;
    private int upperTF = 0;
    private int df = 0;
    private float idf = 0;
    private float upperTFIDF=0;
    private int doclen=1;
    private int tf=0;
    private float upperBM25=0;
    private long offset_frequency = 0;
    private long offset_skip_pointer = 0;
    private int freqByteSize=0;
    private int numBlocks=1;
    private int docidByteSize=0;

    protected static final long ENTRY_SIZE = 68 + Lexicon.MAX_LEN_OF_TERM;

    public void updateTFMAX(PostingIndex index) {
        for (Posting posting : index.getPostings()) {
            if (posting.getFrequency() > this.upperTF) {
                this.upperTF = posting.getFrequency();
            }
            this.df++;
        }
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public float getUpperTFIDF() {
        return upperTFIDF;
    }

    public void setUpperTFIDF(float upperTFIDF) {
        this.upperTFIDF = upperTFIDF;
    }

    public int getDoclen() {
        return doclen;
    }

    public void setDoclen(int doclen) {
        this.doclen = doclen;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public float getUpperBM25() {
        return upperBM25;
    }

    public void setUpperBM25(float upperBM25) {
        this.upperBM25 = upperBM25;
    }

    public int getFreqByteSize() {
        return freqByteSize;
    }

    public void setFreqByteSize(int freqByteSize) {
        this.freqByteSize = freqByteSize;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }

    public int getDocidByteSize() {
        return docidByteSize;
    }

    public void setDocidByteSize(int docidByteSize) {
        this.docidByteSize = docidByteSize;
    }

    public void updateBM25Values(int tf, int doc_len){
        float ratio=(float)this.tf/(float)(this.doclen+this.tf);
        float newRatio=(float)tf/(float)(doclen+tf);
        if(newRatio>ratio){
            this.tf=tf;
            this.doclen=doc_len;
        }
    }

    public float calculateIDF() {
        this.idf = (float) Math.log(CollectionStatistics.getDocuments() / (float) this.df);
        return this.idf;
    }

    @Override
    public String toString() {
        return "Term: " + term + " " +
                "Offset Doc ID: " + offset_doc_id + " " +
                "Upper TF: " + upperTF + " " +
                "DF: " + df + " " +
                "IDF: " + idf + " " +
                "Upper TF-IDF: " + upperTFIDF + " " +
                "Doclen: " + doclen + " " +
                "TF: " + tf + " " +
                "Upper BM25: " + upperBM25 + " " +
                "Offset Frequency: " + offset_frequency + " " +
                "Offset Skip Pointer: " + offset_skip_pointer + " " +
                "Freq Byte Size: " + freqByteSize + " " +
                "Num Blocks: " + numBlocks + " " +
                "DocID Byte Size: " + docidByteSize;
    }


    public void incrementDf() {
        this.df++;
    }

    // Getter and Setter methods
    public long getOffset_doc_id() {
        return offset_doc_id;
    }

    public void setOffset_doc_id(long offset_doc_id) {
        this.offset_doc_id = offset_doc_id;
    }

    public int getUpperTF() {
        return upperTF;
    }

    public void setUpperTF(int upperTF) {
        this.upperTF = upperTF;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public float getIdf() {
        return idf;
    }

    public void setIdf(float idf) {
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
    public long readEntryFromDisk(long offset, FileChannel fileChannel) {
        try {
            if(offset>=fileChannel.size()){
                return -1;
            }
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, LexiconEntry.ENTRY_SIZE);

            if (mappedByteBuffer == null) {
                return -1;
            }

            byte[] termBytes = new byte[Lexicon.MAX_LEN_OF_TERM];
            mappedByteBuffer.get(termBytes);
            term=Lexicon.removePadding(new String(termBytes,StandardCharsets.UTF_8));
            df=(mappedByteBuffer.getInt());
            idf=(mappedByteBuffer.getFloat());
            upperTF=(mappedByteBuffer.getInt());
            doclen=(mappedByteBuffer.getInt());
            tf=(mappedByteBuffer.getInt());
            upperTFIDF=(mappedByteBuffer.getFloat());
            upperBM25=(mappedByteBuffer.getFloat());
            offset_doc_id=(mappedByteBuffer.getLong());
            offset_frequency=(mappedByteBuffer.getLong());
            docidByteSize=(mappedByteBuffer.getInt());
            freqByteSize=(mappedByteBuffer.getInt());
            numBlocks=(mappedByteBuffer.getInt());
            offset_skip_pointer=(mappedByteBuffer.getLong());

            return offset+ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long writeEntryToDisk(String term, long offset, FileChannel fileChannel) {
        try {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, LexiconEntry.ENTRY_SIZE);
            if (mappedByteBuffer == null) {
                return -1;
            }
            //System.out.println(term+"\t"+ this);
            mappedByteBuffer.put(Lexicon.padStringToLength(term).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putInt(df);
            mappedByteBuffer.putFloat(idf);
            mappedByteBuffer.putInt(upperTF);
            mappedByteBuffer.putInt(doclen);
            mappedByteBuffer.putInt(tf);
            mappedByteBuffer.putFloat(upperTFIDF);
            mappedByteBuffer.putFloat(upperBM25);
            mappedByteBuffer.putLong(offset_doc_id);
            mappedByteBuffer.putLong(offset_frequency);
            mappedByteBuffer.putInt(docidByteSize);
            mappedByteBuffer.putInt(freqByteSize);
            mappedByteBuffer.putInt(numBlocks);
            mappedByteBuffer.putLong(offset_skip_pointer);

            return offset + LexiconEntry.ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public void calculateUpperBounds(){
        this.upperTFIDF= (float) ((1+Math.log(this.upperTF))*this.idf);
        CollectionStatistics.computeAVGDOCLEN();
        this.upperBM25= (float) ((tf/(tf+ PathAndFlags.BM25_k1*(1-PathAndFlags.BM25_b+PathAndFlags.BM25_b*(doclen/CollectionStatistics.getAvgDocLen()))))*idf);
    }
    public void calculateBlockNeed(){
        this.offset_skip_pointer=SkippingBlock.getFile_offset();
        if(df> PathAndFlags.POSTING_PER_BLOCK){
            this.numBlocks= (int) Math.ceil(Math.sqrt(df));
        }
    }

}
