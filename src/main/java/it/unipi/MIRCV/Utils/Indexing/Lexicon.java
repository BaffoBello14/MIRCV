package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Lexicon {

    // The main data structure to hold lexicon entries.
    private HashMap<String, LexiconEntry> lexicon = new HashMap<>();
    protected static final int MAX_LEN_OF_TERM = 32; // Max length of a term.

    // Getter method to retrieve the lexicon.
    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    // Retrieves an entry from the lexicon, or finds it on disk if not present.
    public LexiconEntry retrieveEntry(String term) {
        if (lexicon.containsKey(term)) {
            return lexicon.get(term);
        }
        LexiconEntry lexiconEntry = find(term);
        add(term, lexiconEntry);
        return lexiconEntry;
    }

    // Attempts to find a term in the lexicon file on disk using a binary search.
    public LexiconEntry find(String term) {
        try {
            long top = CollectionStatistics.getTerms();
            long bot = 0;
            long mid;
            LexiconEntry entry = new LexiconEntry();

            try (FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

                while (bot <= top) {
                    mid = (bot + top) / 2;
                    entry.readEntryFromDisk(mid * LexiconEntry.ENTRY_SIZE, fileChannel);

                    String termFound = Lexicon.removePadding(new String(entry.getTermBytes(), StandardCharsets.UTF_8));

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

    // Setter method to update the lexicon.
    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }

    // Adds an entry to the lexicon or updates it if it already exists.
    public void add(String term, LexiconEntry lexiconEntry) {
        term = padStringToLength(term);
        if (lexicon.containsKey(term)) {
            lexicon.get(term).setDf(lexicon.get(term).getDf() + 1);
            lexicon.get(term).calculateIDF();
        } else {
            lexicon.put(term, lexiconEntry);
        }
    }

    // Adds or updates a term in the lexicon.
    public void add(String term) {
        term = padStringToLength(term);
        lexicon.compute(term, (key, entry) -> {
            if (entry == null) {
                return new LexiconEntry();
            } else {
                entry.incrementDf();
                return entry;
            }
        });
    }

    // Returns a sorted list of terms in the lexicon.
    public ArrayList<String> sortLexicon() {
        ArrayList<String> sorted = new ArrayList<>(lexicon.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    // Pads a string to a specified length.
    public static String padStringToLength(String input) {
        if (input.length() >= MAX_LEN_OF_TERM) {
            return input.substring(0, MAX_LEN_OF_TERM);
        } else {
            return String.format("%1$-" + MAX_LEN_OF_TERM + "s", input);
        }
    }

    // Removes any padding from a string.
    public static String removePadding(String paddedString) {
        String trimmed = paddedString.trim();
        int nullIndex = trimmed.indexOf('\0');
        return nullIndex >= 0 ? trimmed.substring(0, nullIndex) : trimmed;
    }
}
