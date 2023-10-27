package it.unipi.MIRCV.Utils.Indexing;

public class SPIMI {


    public static boolean  execute(){
        Runtime runtime= Runtime.getRuntime();
        long totalMemory= runtime.totalMemory();
        long freeMemory= runtime.freeMemory();
        long usedMemory=totalMemory-freeMemory;

    }


}
