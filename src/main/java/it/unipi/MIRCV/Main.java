package it.unipi.MIRCV;

import it.unipi.MIRCV.Utils.Indexing.*;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) throws Exception {

        // Open the file channel to read the final lexicon
        FileChannel fileChannelLEX = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ);

        // Create a new lexicon entry object
        LexiconEntry lexiconEntry = new LexiconEntry();

        long pos = 0;
        
        // Iterate through and read lexicon entries from the file
        // Stop when there's no entry left or the entry is empty
        while (lexiconEntry.readEntryFromDisk(pos, fileChannelLEX) != -1) {
            System.out.println(lexiconEntry);

            pos += 100;
            System.out.println(pos);
            System.out.println(fileChannelLEX.size());
        }

        // Read collection statistics from disk
        CollectionStatistics.readFromDisk();

        // Print out the collection statistics
        System.out.println(CollectionStatistics.getDocuments() + " " + 
                           CollectionStatistics.getAvgDocLen() + " " + 
                           CollectionStatistics.getTerms() + " " + 
                           CollectionStatistics.getTotalLenDoc());
    }
}
