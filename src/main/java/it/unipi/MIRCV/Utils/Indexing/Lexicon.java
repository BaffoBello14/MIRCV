package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Lexicon {
    private HashMap<String, LexiconEntry> lexicon = new HashMap<>();
    protected static final int MAX_LEN_OF_TERM = 32;


    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public LexiconEntry retrieveEntry(String term) {
        if (lexicon.containsKey(term)) {
            return lexicon.get(term);
        }
        LexiconEntry lexiconEntry=find(term);
        if (lexiconEntry==null){
            return null;
        }
        add(term, lexiconEntry);
        return lexiconEntry;
    }

    public LexiconEntry find(String term) {
        try {
            long top = CollectionStatistics.getTerms()-1;
            long bot = 0;
            long mid;
            LexiconEntry entry=new LexiconEntry();
    
            try (
                    FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE)) {
    
                while (bot <= top) {
                    mid = (bot + top) / 2;
                    entry.readEntryFromDisk(mid * LexiconEntry.ENTRY_SIZE, fileChannel);
                    
                    if (entry == null) {
                        return null; 
                    }
    
                    String termFound = Lexicon.removePadding(entry.getTerm());
    
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
    
    public void add(String term,LexiconEntry lexiconEntry){
        lexicon.put(term,lexiconEntry);
    }

    public void add(String term) {
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
        int nullIndex = trimmed.indexOf(' ');
        return nullIndex >= 0 ? trimmed.substring(0, nullIndex) : trimmed;
    }
    public float getIDF(String term){
        return lexicon.get(term).getIdf();
    }

}
