package it.unipi.MIRCV.Utils.Paths;

public class PathConfig {
    
    private static final String BASE_DIR = "./IndexData/";

    private static final String DOC_ID_DIR = BASE_DIR + "Doc_ids/";
    private static final String FREQ_DIR = BASE_DIR + "Freqs/";
    private static final String FINAL_DIR = BASE_DIR + "Final";
    private static final String DOC_INDEX_DIR = BASE_DIR + "Doc_index";
    private static final String LEXICON_DIR = BASE_DIR + "Lexicons";
    private static final String COLLECTION_STAT_DIR = BASE_DIR + "CollectionStatistics";
    private static final String BLOCK_FILE_DIR = BASE_DIR + "BlockInfo";

    public static String getDocIdDir() {
        return DOC_ID_DIR;
    }

    public static String getFreqDir() {
        return FREQ_DIR;
    }

    public static String getFinalDir() {
        return FINAL_DIR;
    }

    public static String getDocIndexDir() {
        return DOC_INDEX_DIR;
    }

    public static String getLexiconDir() {
        return LEXICON_DIR;
    }

    public static String getCollectionStatDir() {
        return COLLECTION_STAT_DIR;
    }

    public static String getBlockFileDir() {
        return BLOCK_FILE_DIR;
    }   
}
