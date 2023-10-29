package it.unipi.MIRCV.Utils.PathAndFlags;

public class PathAndFlags {
    public static String PATH_TO_DOC_ID="./IndexData/Doc_ids/";
    public static String PATH_TO_FREQ="./IndexData/Freqs/";
    public static String PATH_TO_FINAL_DOC_ID="./IndexData/Final/DocID.dat";
    public static String PATH_TO_FINAL_FREQ="./IndexData/Final/Freq.dat";
    public static String PATH_TO_DOC_INDEX="./IndexData/Doc_index/DocIndex.dat";
    public static String PATH_TO_LEXICON="./IndexData/Lexicons/";
    public static String PATH_TO_FINAL_LEXICON="./IndexData/Final/Lexicon.dat";
    public static String PATH_TO_COLLECTION_STAT="./IndexData/CollectionStatistics/CollectionStat.dat";
    public static String PATH_TO_BLOCK_FILE="./IndexData/BlockInfo/BlockInfo.dat";
    public static boolean COMPRESSION_ENABLED=true;
    public static boolean STOPWORD_STEM_ENABLED=true;
    public static boolean BM25=false;
    public static final float BM25_k1= 1.5F;
    public static final float BM25_b=0.75F;
    public static final int POSTING_PER_BLOCK=512;



}
