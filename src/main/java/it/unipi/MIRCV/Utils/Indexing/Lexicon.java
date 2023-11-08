package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.*;
import java.nio.channels.FileChannel;
import org.junit.platform.commons.util.LruCache;

public class Lexicon {
    // Singleton instance of the Lexicon class
    private static Lexicon instance = new Lexicon();
    // Maximum length of a term in the lexicon
    protected static final int MAX_LEN_OF_TERM = 32;
    // LRU cache for storing recently accessed lexicon entries
    private final LruCache<String, LexiconEntry> lruCache = new LruCache<>(PathAndFlags.LEXICON_CACHE_SIZE);

    // Private constructor for the singleton instance
    private Lexicon() {
    }

    // Returns the singleton instance of the Lexicon class
    public static Lexicon getInstance() {
        return instance;
    }

    // Retrieves a lexicon entry for a given term
    public LexiconEntry retrieveEntry(String term) {
        // Check if the entry is in the LRU cache
        if (lruCache.containsKey(term)) {
            return lruCache.get(term);
        }
        // If not, search for the entry in the lexicon
        LexiconEntry lexiconEntry = find(term);
        // If the entry is not found, return null
        if (lexiconEntry == null) {
            return null;
        }
        // Add the entry to the LRU cache and return it
        lruCache.put(term, lexiconEntry);
        return lexiconEntry;
    }

    // Searches for a lexicon entry for a given term
    public LexiconEntry find(String term) {
        try {
            long top = CollectionStatistics.getTerms() - 1;
            long bot = 0;
            long mid;
            LexiconEntry entry = new LexiconEntry();

            try (FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON),
                    StandardOpenOption.READ)) {

                while (bot <= top) {
                    mid = (bot + top) / 2;
                    entry.readEntryFromDisk(mid * LexiconEntry.ENTRY_SIZE, fileChannel);

                    if (entry.getTerm().isEmpty()) {
                        return null;
                    }

                    String termFound = entry.getTerm();

                    int comparisonResult = term.compareTo(termFound);

                    if (comparisonResult == 0) {
                        return entry;
                    } else if (comparisonResult > 0) {
                        bot = mid + 1;
                    } else {
                        top = mid - 1;
                    }
                }
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Pads a string to the maximum length of a term in the lexicon
    public static String padStringToLength(String input) {
        if (input.length() >= MAX_LEN_OF_TERM) {
            return input.substring(0, MAX_LEN_OF_TERM);
        } else {
            return String.format("%1$-" + MAX_LEN_OF_TERM + "s", input);
        }
    }

    // Removes padding from a padded string
    public static String removePadding(String paddedString) {
        String trimmed = paddedString.trim();
        int nullIndex = trimmed.indexOf(' ');
        return nullIndex >= 0 ? trimmed.substring(0, nullIndex) : trimmed;
    }

    // Gets a lexicon entry for a given term
    public LexiconEntry get(String term) {
        // Check if the entry is in the LRU cache
        if (lruCache.containsKey(term)) {
            return lruCache.get(term);
        }
        // If not, retrieve the entry and add it to the LRU cache
        LexiconEntry lexiconEntry = retrieveEntry(term);
        // If the entry is not found, return null
        if (lexiconEntry == null) {
            return null;
        }
        // Add the entry to the LRU cache and return it
        lruCache.put(term, lexiconEntry);
        return lruCache.get(term);
    }

// This is the end of the original code selection
    
    public void remove(String term){
        if(lruCache.containsKey(term)){
            lruCache.remove(term);
        }
    }

}
