package it.unipi.MIRCV.Utils.Indexing;

import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

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
        LexiconEntry lexiconEntry = find(term);
        add(term, lexiconEntry);
        return lexiconEntry;
    }

    public LexiconEntry find(String term) {
        long top = CollectionStatistics.getTerms();
        long bot = 0;
        long mid;
        LexiconEntry entry;

        while (bot <= top) {
            mid = (bot + top) / 2;
            entry = fileManager.readEntryFromDisk(mid * LexiconEntry.ENTRY_SIZE);
            
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
        return null;
    }

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }
    
    public void add(String term, LexiconEntry lexiconEntry) {
        term = padStringToLength(term);
        lexicon.put(term, lexiconEntry);
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

class LexiconFileManager {
    public LexiconEntry readEntryFromDisk(long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PathConfig.getFinalDir() + "/Lexicon.dat", offset, LexiconEntry.ENTRY_SIZE);
            
            if (mappedByteBuffer == null) {
                return null;
            }

            byte[] termBytes = new byte[Lexicon.MAX_LEN_OF_TERM];
            mappedByteBuffer.get(termBytes);

            LexiconEntry entry = new LexiconEntry();
            entry.setTermBytes(termBytes);
            entry.setOffset_doc_id(mappedByteBuffer.getLong());
            entry.setOffset_frequency(mappedByteBuffer.getLong());
            entry.setUpperTF(mappedByteBuffer.getInt());
            entry.setDf(mappedByteBuffer.getInt());
            entry.setIdf(mappedByteBuffer.getDouble());
            entry.setOffset_skip_pointer(mappedByteBuffer.getLong());

            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long writeEntryToDisk(LexiconEntry entry, String term, long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(PathConfig.getFinalDir() + "/Lexicon.dat", offset, LexiconEntry.ENTRY_SIZE);
            
            if (mappedByteBuffer == null) {
                return -1;
            }

            mappedByteBuffer.put(Lexicon.padStringToLength(term).getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(entry.getOffset_doc_id());
            mappedByteBuffer.putLong(entry.getOffset_frequency());
            mappedByteBuffer.putInt(entry.getUpperTF());
            mappedByteBuffer.putInt(entry.getDf());
            mappedByteBuffer.putDouble(entry.getIdf());
            mappedByteBuffer.putLong(entry.getOffset_skip_pointer());

            DiskIOManager.writeToDisk(PathConfig.getFinalDir() + "/Lexicon.dat", mappedByteBuffer);

            return offset + LexiconEntry.ENTRY_SIZE;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
