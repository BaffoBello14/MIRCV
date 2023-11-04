package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;


import java.util.Scanner;

public class ProcessCollection {
    public static void main(String[] args) throws Exception{
        Scanner scanner= new Scanner(System.in);
        String readed;
        System.out.print("Compressed doc_id and freqs (Y/n)?");
        readed=scanner.nextLine();
        switch (readed){
            case "Y":
            case "y":
            case "\r":{
                break;
            }
            case "N":
            case "n":{
                PathAndFlags.COMPRESSION_ENABLED=false;
                break;
            }
            default:{
                System.out.println("No recognized answer, so for default the compression is enabled");
            }
        }
        System.out.print("Stopword and Stemming (Y/n)?");
        readed=scanner.nextLine();
        switch (readed){
            case "Y":
            case "y":
            case "\r":{
                break;
            }
            case "N":
            case "n":{
                PathAndFlags.STOPWORD_STEM_ENABLED=false;
                break;
            }
            default:{
                System.out.println("No recognized answer, so for default the Stopword Removal and Stemming is enabled");
            }
        }
        PathAndFlags.write2Disk();
        long start=System.currentTimeMillis();
        int indexes= SPIMI.execute();
        long end=System.currentTimeMillis();
        System.out.println("SPIMI time->"+(end-start)/1000+"sec");
        long Spimi=end-start;
        //CollectionStatistics.readFromDisk();
        start=System.currentTimeMillis();
        SPIMIMerger.setNumIndex(indexes);
        SPIMIMerger.execute();
        end=System.currentTimeMillis();
        System.out.println("SPIMIMerger time->"+(end-start)/1000+"sec "+Spimi/1000+"sec");
    }
}
