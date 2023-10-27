package it.unipi.MIRCV.Utils.Paths;

public class PathConfig {
        
    private static final String BASE_DIR = "./IndexData/";

    // File names constants
    private static final String DOC_ID_FILE = "DocID.dat";
    private static final String FREQ_FILE = "Freq.dat";
    private static final String DOC_INDEX_FILE = "DocIndex.dat";
    private static final String LEXICON_FILE = "Lexicon.dat";
    private static final String COLLECTION_STAT_FILE = "CollectionStat.dat";
    private static final String BLOCK_FILE = "BlockInfo.dat";

    public static class Directories {
        public static final String DOC_ID_DIR = BASE_DIR + "Doc_ids/";
        public static final String FREQ_DIR = BASE_DIR + "Freqs/";
        public static final String FINAL_DIR = BASE_DIR + "Final/";
        public static final String DOC_INDEX_DIR = BASE_DIR + "Doc_index/";
        public static final String LEXICON_DIR = BASE_DIR + "Lexicons/";
        public static final String COLLECTION_STAT_DIR = BASE_DIR + "CollectionStatistics/";
        public static final String BLOCK_FILE_DIR = BASE_DIR + "BlockInfo/";
    }

    public static String getDocIdDir() {
        return Directories.DOC_ID_DIR;
    }

    public static String getFreqDir() {
        return Directories.FREQ_DIR;
    }

    public static String getFinalDocIdPath() {
        return Directories.FINAL_DIR + DOC_ID_FILE;
    }

    public static String getFinalFreqPath() {
        return Directories.FINAL_DIR + FREQ_FILE;
    }

    public static String getDocIndexPath() {
        return Directories.DOC_INDEX_DIR + DOC_INDEX_FILE;
    }

    public static String getLexiconDir() {
        return Directories.LEXICON_DIR;
    }

    public static String getFinalLexiconPath() {
        return Directories.FINAL_DIR + LEXICON_FILE;
    }

    public static String getCollectionStatPath() {
        return Directories.COLLECTION_STAT_DIR + COLLECTION_STAT_FILE;
    }

    public static String getBlockFilePath() {
        return Directories.BLOCK_FILE_DIR + BLOCK_FILE;
    }   
}
