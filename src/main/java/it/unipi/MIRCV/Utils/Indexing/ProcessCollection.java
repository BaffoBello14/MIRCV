package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.util.Scanner;

/**
 * Class for processing a collection, including compression, stopword removal, stemming,
 * and executing the Single-pass in-memory indexing (SPIMI) algorithm.
 */
public class ProcessCollection {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;

        // Prompt for compressed doc_id and freqs
        System.out.print("Compressed doc_id and freqs (Y/n)? ");
        input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("n")) {
            PathAndFlags.COMPRESSION_ENABLED = false;
        }

        // Prompt for stopword and stemming
        System.out.print("Stopword and Stemming (Y/n)? ");
        input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("n")) {
            PathAndFlags.STOPWORD_STEM_ENABLED = false;
        }

        scanner.close();
        PathAndFlags.createDirectories();
        PathAndFlags.writeFlagsToDisk();

        // Record the start time for SPIMI execution
        long start = System.currentTimeMillis();
        int indexes = SPIMI.execute();
        long end = System.currentTimeMillis();
        
        // Print SPIMI execution time
        System.out.println("SPIMI time: " + (end - start) / 1000 + " seconds");
        long spimiTime = end - start;

        // Record the start time for SPIMI Merger execution
        start = System.currentTimeMillis();
        SPIMIMerger.setNumIndex(indexes);
        SPIMIMerger.execute();
        end = System.currentTimeMillis();
        
        // Print SPIMI Merger execution time along with SPIMI time
        System.out.println("SPIMIMerger time: " + (end - start) / 1000 + " seconds (SPIMI: " + spimiTime / 1000 + " seconds)");
    }
}
