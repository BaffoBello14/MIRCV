package it.unipi.MIRCV.Utils.Indexing;

public class SPIMI {
    private static String PATH_TO_DOC_ID;
    private static String PATH_TO_FREQ;
    private static String PATH_TO_FINAL_DOC_ID;
    private static String PATH_TO_FINAL_FREQ;
    private static String PATH_TO_DOC_INDEX;
    private static String PATH_TO_LEXICON;
    private static String PATH_TO_FINAL_LEXICON;
    private static String PATH_TO_COLLECTION_STAT;
    private static String PATH_TO_BLOCK_FILE;
    public static boolean  execute(){
        return true;
    }

    public static void setPathToDocId(String pathToDocId) {
        PATH_TO_DOC_ID = pathToDocId;
    }

    public static void setPathToFreq(String pathToFreq) {
        PATH_TO_FREQ = pathToFreq;
    }

    public static void setPathToFinalDocId(String pathToFinalDocId) {
        PATH_TO_FINAL_DOC_ID = pathToFinalDocId;
    }

    public static void setPathToFinalFreq(String pathToFinalFreq) {
        PATH_TO_FINAL_FREQ = pathToFinalFreq;
    }

    public static void setPathToDocIndex(String pathToDocIndex) {
        PATH_TO_DOC_INDEX = pathToDocIndex;
    }

    public static void setPathToLexicon(String pathToLexicon) {
        PATH_TO_LEXICON = pathToLexicon;
    }

    public static void setPathToFinalLexicon(String pathToFinalLexicon) {
        PATH_TO_FINAL_LEXICON = pathToFinalLexicon;
    }

    public static void setPathToCollectionStat(String pathToCollectionStat) {
        PATH_TO_COLLECTION_STAT = pathToCollectionStat;
    }

    public static void setPathToBlockFile(String pathToBlockFile) {
        PATH_TO_BLOCK_FILE = pathToBlockFile;
    }
}
