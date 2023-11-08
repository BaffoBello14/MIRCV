package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.util.Scanner;

public class ProcessCollection {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.print("Compressed doc_id and freqs (Y/n)? ");
        input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("n")) {
            PathAndFlags.COMPRESSION_ENABLED = false;
        }

        System.out.print("Stopword and Stemming (Y/n)? ");
        input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("n")) {
            PathAndFlags.STOPWORD_STEM_ENABLED = false;
        }

        scanner.close();
        PathAndFlags.createDirectories();
        PathAndFlags.writeFlagsToDisk();


        long start = System.currentTimeMillis();
        int indexes = SPIMI.execute();
        long end = System.currentTimeMillis();
        System.out.println("SPIMI time: " + (end - start) / 1000 + " seconds");
        long spimiTime = end - start;

        start = System.currentTimeMillis();
        SPIMIMerger.setNumIndex(indexes);
        SPIMIMerger.execute();
        end = System.currentTimeMillis();
        System.out.println("SPIMIMerger time: " + (end - start) / 1000 + " seconds (SPIMI: " + spimiTime / 1000 + " seconds)");
    }
}
