package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.io.*;
import java.nio.channels.FileChannel;



public class Lexicon {
    private static Lexicon instance=new Lexicon();
    protected static final int MAX_LEN_OF_TERM = 32;
    private final LFUCache<String,LexiconEntry> lruCache= new LFUCache<>(PathAndFlags.LEXICON_CACHE_SIZE);

    private Lexicon(){}
    public static Lexicon getInstance(){
        return instance;
    }

    public  LexiconEntry retrieveEntry(String term) {
        if (lruCache.containsKey(term)) {
            return lruCache.get(term);
        }
        LexiconEntry lexiconEntry=find(term);
        if (lexiconEntry==null){
            return null;
        }
        lruCache.put(term,lexiconEntry);
        return lexiconEntry;
    }

    public LexiconEntry find(String term) {
        try {
            long top = CollectionStatistics.getTerms()-1;
            long bot = 0;
            long mid;
            LexiconEntry entry=new LexiconEntry();
    
            try (
                    FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ)) {
    
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

    public LexiconEntry get(String term){
        if(lruCache.containsKey(term)){
            return lruCache.get(term);
        }
        LexiconEntry lexiconEntry=retrieveEntry(term);
        if(lexiconEntry==null){
            return null;
        }
        lruCache.put(term,lexiconEntry);
        return lruCache.get(term);
    }


}
