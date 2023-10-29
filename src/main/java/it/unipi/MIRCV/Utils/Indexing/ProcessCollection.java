package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProcessCollection {
    public static void main(String[] args) throws Exception {
        // Open file channels for document index and document frequency
        FileChannel fileChannelDI = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        FileChannel fileChannelFRQ = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID + "0.dat"),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        // Record the starting time
        long start = System.currentTimeMillis();

        // Execute SPIMI
        // int indexes = (int) SPIMI.execute(); // Commented out, possibly an incomplete code snippet

        // Record the ending time and calculate execution time
        long end = System.currentTimeMillis();
        System.out.println("SPIMI time -> " + (end - start) / 1000 + " sec");

        // Read collection statistics from disk
        CollectionStatistics.readFromDisk();

        // Set the number of indexes and execute SPIMIMerger
        start = System.currentTimeMillis();
        SPIMIMerger.setNumIndex(60);
        SPIMIMerger.execute();
        end = System.currentTimeMillis();
        System.out.println("SPIMIMerger time -> " + (end - start) / 1000 + " sec");

        // Open file channels for lexicon and create arrays for LexiconEntry objects
        FileChannel[] fileChannelLEX = new FileChannel[60];
        LexiconEntry[] lexiconEntries = new LexiconEntry[60];
        for (int i = 0; i < 60; i++) {
            fileChannelLEX[i] = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON + i + ".dat"),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            lexiconEntries[i] = new LexiconEntry();

            // Read LexiconEntry from disk and display it
            lexiconEntries[i].readEntryFromDisk(fileChannelLEX[i].size() - LexiconEntry.ENTRY_SIZE, fileChannelLEX[i]);
            System.out.println(i + " " + lexiconEntries[i]);
        }

        // Create a DocIndex object and read it from disk
        DocIndex docIndex = new DocIndex();
        docIndex.readFromDisk(fileChannelDI, 200);

        // Create a LexiconEntry object (commented out, possibly incomplete)
        LexiconEntry lexiconEntry = new LexiconEntry();
        // lexiconEntry.readEntryFrom Disk(fileChannelLEX.size() - 2 * LexiconEntry.ENTRY_SIZE, fileChannelLEX);

        // Create a byte array for the term and read from MappedByteBuffer
        byte[] term = new byte[Lexicon.MAX_LEN_OF_TERM];
        MappedByteBuffer mappedByteBuffer = fileChannelFRQ.map(FileChannel.MapMode.READ_ONLY, 8, 4);

        // Display the lexicon entry, an integer from MappedByteBuffer, and the first entry from docIndex
        System.out.println(lexiconEntry);
        System.out.println(mappedByteBuffer.getInt());
        System.out.println(docIndex.sortDocIndex().get(0));
    }
}
