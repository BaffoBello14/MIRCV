package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;

import it.unipi.MIRCV.Utils.DiskIOManager;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

public class LexiconFileManager {

    private static final String LEXICON_FILE_PATH = PathConfig.getFinalDir() + "/Lexicon.dat";

    public LexiconEntry readEntryFromDisk(long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(LEXICON_FILE_PATH, offset, LexiconEntry.ENTRY_SIZE);

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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long writeEntryToDisk(LexiconEntry entry, String term, long offset) {
        try {
            MappedByteBuffer mappedByteBuffer = DiskIOManager.readFromDisk(LEXICON_FILE_PATH, offset, LexiconEntry.ENTRY_SIZE);
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

            DiskIOManager.writeToDisk(LEXICON_FILE_PATH, mappedByteBuffer);
            
            return offset + LexiconEntry.ENTRY_SIZE;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
