package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Singleton class for managing the lexicon, which maps terms to their corresponding LexiconEntry.
 */
public class Lexicon {
    private static Lexicon instance = new Lexicon();
    protected static final int MAX_LEN_OF_TERM = 32;
    private final LFUCache<String, LexiconEntry> lfuCache = new LFUCache<>(PathAndFlags.LEXICON_CACHE_SIZE);
    private static FileChannel fileChannel = null;

    static {
        try {
            File file = new File(PathAndFlags.PATH_TO_FINAL_LEXICON);
            if(file.exists()){
                fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("problems with opening the file channel of lexicon");
        }
    }

    private Lexicon() {
    }

    /**
     * Retrieves the singleton instance of the Lexicon class.
     *
     * @return The Lexicon instance.
     */
    public static Lexicon getInstance() {
        return instance;
    }

    /**
     * Retrieves the LexiconEntry for a given term.
     *
     * @param term The term to retrieve.
     * @return The LexiconEntry for the term, or null if not found.
     */
    public LexiconEntry retrieveEntry(String term) {
        if (lfuCache.containsKey(term)) {
            return lfuCache.get(term);
        }
        LexiconEntry lexiconEntry = find(term);
        if (lexiconEntry == null) {
            return null;
        }
        lfuCache.put(term, lexiconEntry);
        return lexiconEntry;
    }

    /**
     * Finds the LexiconEntry for a given term using binary search in the lexicon file.
     *
     * @param term The term to find.
     * @return The LexiconEntry for the term, or null if not found.
     */
    public LexiconEntry find(String term) {
        long top = CollectionStatistics.getTerms() - 1;
        long bot = 0;
        long mid;
        LexiconEntry entry = new LexiconEntry();


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
        return null;
    }

    /**
     * Pads a string to a specified length.
     *
     * @param input The input string.
     * @return The padded string.
     */
    public static String padStringToLength(String input) {
        if (input.length() >= MAX_LEN_OF_TERM) {
            return input.substring(0, MAX_LEN_OF_TERM);
        } else {
            return String.format("%1$-" + MAX_LEN_OF_TERM + "s", input);
        }
    }

    /**
     * Removes padding from a padded string.
     *
     * @param paddedString The padded string.
     * @return The string without padding.
     */
    public static String removePadding(String paddedString) {
        String trimmed = paddedString.trim();
        int nullIndex = trimmed.indexOf(' ');
        return nullIndex >= 0 ? trimmed.substring(0, nullIndex) : trimmed;
    }

    /**
     * Gets the LexiconEntry for a given term, either from the cache or by retrieving it.
     *
     * @param term The term to get.
     * @return The LexiconEntry for the term, or null if not found.
     */
    public LexiconEntry get(String term) {
        if (lfuCache.containsKey(term)) {
            return lfuCache.get(term);
        }
        LexiconEntry lexiconEntry = retrieveEntry(term);
        if (lexiconEntry == null) {
            return null;
        }
        lfuCache.put(term, lexiconEntry);
        return lfuCache.get(term);
    }

    /**
     * Clears the cache.
     */
    public void clear(){
        lfuCache.clear();
    }
}
