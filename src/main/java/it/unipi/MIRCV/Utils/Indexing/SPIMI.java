package it.unipi.MIRCV.Utils.Indexing;

public class SPIMI {
    private static String PATH_TO_DOC_ID="./IndexData/Doc_ids/";
    private static String PATH_TO_FREQ="./IndexData/Freqs/";
    private static String PATH_TO_FINAL_DOC_ID="./IndexData/Final";
    private static String PATH_TO_FINAL_FREQ="./IndexData/Final";
    private static String PATH_TO_DOC_INDEX="./IndexData/Doc_index";
    private static String PATH_TO_LEXICON="./IndexData/Lexicons";
    private static String PATH_TO_FINAL_LEXICON="./IndexData/Final";
    private static String PATH_TO_COLLECTION_STAT="./IndexData/CollectionStatistics";
    private static String PATH_TO_BLOCK_FILE="./IndexData/BlockInfo";
    public static boolean  execute(){
        return true;
    }

    public static String getPathToDocId() {
        return PATH_TO_DOC_ID;
    }

    public static String getPathToFreq() {
        return PATH_TO_FREQ;
    }

    public static String getPathToFinalDocId() {
        return PATH_TO_FINAL_DOC_ID;
    }

    public static String getPathToFinalFreq() {
        return PATH_TO_FINAL_FREQ;
    }

    public static String getPathToDocIndex() {
        return PATH_TO_DOC_INDEX;
    }

    public static String getPathToLexicon() {
        return PATH_TO_LEXICON;
    }

    public static String getPathToFinalLexicon() {
        return PATH_TO_FINAL_LEXICON;
    }

    public static String getPathToCollectionStat() {
        return PATH_TO_COLLECTION_STAT;
    }

    public static String getPathToBlockFile() {
        return PATH_TO_BLOCK_FILE;
    }
}
