package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Lexicon {
    private static HashMap<String,LexiconEntry> lexicon=new HashMap<>();
    protected static final int MAX_LEN_OF_TERM=32;

    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }
    public LexiconEntry getEntry(String term){
        if(lexicon.containsKey(term)){
            return lexicon.get(term);
        }
        LexiconEntry lexiconEntry=find(term);
        add(term, lexiconEntry);
        return lexiconEntry;
    }

    public LexiconEntry find(String term){
        try{
            LexiconEntry entry = new LexiconEntry();
            long bot = CollectionStatistics.getTerms();
            long top = 0;
            long mid = 0;
            String termFound;
            FileInputStream fileInputStream = new FileInputStream(PathAndFlags.PATH_TO_FINAL_LEXICON + "/Lexicon.dat");
            FileChannel fileChannel = fileInputStream.getChannel();
            while (top <= bot) {
                mid = (long) (top + Math.ceil((top + bot) / 2.0));
                termFound = entry.readFromDisk(mid,fileChannel,term);
                if (termFound.equals(term)) {
                    return entry;
                }
                if (term.compareTo(termFound) > 0) {
                    top = mid;
                    continue;
                }
                bot = mid;

            }
            return null;
        }catch (IOException e){
            System.out.println("problems with the find term to open the file of lexicon final");
            e.printStackTrace();
            return null;
        }
    }

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }
    public void add(String term,LexiconEntry lexiconEntry){
        term=padStringToLength(term);
        if(lexicon.containsKey(term)){
            lexicon.get(term).setDf(lexicon.get(term).getDf()+1);
            lexicon.get(term).calculateIDF();
        }else{
            lexicon.put(term,lexiconEntry);

        }
    }
    public ArrayList<String>sortLexicon(){
        ArrayList<String>sorted=new ArrayList<>(lexicon.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    public static String padStringToLength(String input) {
        if (input.length() >= MAX_LEN_OF_TERM) {
            return input.substring(0, MAX_LEN_OF_TERM); // Truncate if too long
        } else {
            StringBuilder padded = new StringBuilder(input);
            while (padded.length() < MAX_LEN_OF_TERM) {
                padded.append('\0'); // Add \0 to pad
            }
            return padded.toString();
        }
    }
    public static String removePadding(String paddedString) {
        // Find the last non-space character in the padded string
        int lastIndex = paddedString.length() - 1;
        while (lastIndex >= 0 && paddedString.charAt(lastIndex) == ' ') {
            lastIndex--;
        }

        // If there is no padding, return the original string
        if (lastIndex < 0) {
            return paddedString;
        }

        // Remove the padding and return the original content
        return paddedString.substring(0, lastIndex + 1);
    }

}
