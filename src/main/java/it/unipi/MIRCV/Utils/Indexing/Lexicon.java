package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.util.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Lexicon {
    private HashMap<String, LexiconEntry> lexicon = new HashMap<>();
    protected static final int MAX_LEN_OF_TERM = 32;

    private LexiconFileManager fileManager = new LexiconFileManager();

    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public LexiconEntry retrieveEntry(String term) {
        if (lexicon.containsKey(term)) {
            return lexicon.get(term);
        }
        return find(term);
    }

    public LexiconEntry find(String term) {
        try {
            long top = CollectionStatistics.getTerms();
            long bot = 0;
            long mid;
            LexiconEntry entry;
    
            try (FileInputStream fileInputStream = new FileInputStream(PathAndFlags.PATH_TO_FINAL_LEXICON + "/Lexicon.dat");
                 FileChannel fileChannel = fileInputStream.getChannel()) {
    
                while (bot <= top) {
                    mid = (bot + top) / 2;
                    entry = fileManager.readEntryFromDisk(mid * LexiconEntry.ENTRY_SIZE, fileChannel);
                    
                    if (entry == null) {
                        return null; 
                    }
    
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
       

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }

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

    public ArrayList<String> sortLexicon() {
        ArrayList<String> sorted = new ArrayList<>(lexicon.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    public static String padStringToLength(String input) {
        if (input.length() >= MAX_LEN_OF_TERM) {
            return input.substring(0, MAX_LEN_OF_TERM);
        } else {
            return String.format("%1$-" + MAX_LEN_OF_TERM + "s", input);
        }
    }

    public static String removePadding(String paddedString) {
        String trimmed = paddedString.trim();
        int nullIndex = trimmed.indexOf('\0');
        return nullIndex >= 0 ? trimmed.substring(0, nullIndex) : trimmed;
    }
}
