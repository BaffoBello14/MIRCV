package it.unipi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.Indexing.SPIMI;
import it.unipi.MIRCV.Utils.Indexing.SPIMIMerger;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

public class IndexingTest {

    public void indexTest() {
        initialize();
        // Altri compiti di indicizzazione da eseguire
    }

    private void initialize() {
        PathAndFlags.PATH_TO_DOC_ID = "./IndexDataTest/Doc_ids/";
        PathAndFlags.PATH_TO_FREQ = "./IndexDataTest/Freqs/";
        PathAndFlags.PATH_TO_FINAL_DOC_ID = "./IndexDataTest/Final/DocID.dat";
        PathAndFlags.PATH_TO_FINAL_FREQ = "./IndexDataTest/Final/Freq.dat";
        PathAndFlags.PATH_TO_DOC_INDEX = "./IndexDataTest/Doc_index/DocIndex.dat";
        PathAndFlags.PATH_TO_LEXICON = "./IndexDataTest/Lexicons/";
        PathAndFlags.PATH_TO_FINAL_LEXICON = "./IndexDataTest/Final/Lexicon.dat";
        PathAndFlags.PATH_TO_COLLECTION_STAT = "./IndexDataTest/CollectionStatistics/CollectionStat.dat";
        PathAndFlags.PATH_TO_BLOCK_FILE = "./IndexDataTest/BlockInfo/BlockInfo.dat";
        PathAndFlags.PATH_TO_FLAGS = "./IndexDataTest/Flags/Flags.dat";

        createDirectoryIfNotExists("./IndexDataTest/BlockInfo/");
        createDirectoryIfNotExists("./IndexDataTest/CollectionStatistics/");
        createDirectoryIfNotExists("./IndexDataTest/Doc_ids/");
        createDirectoryIfNotExists("./IndexDataTest/Doc_index/");
        createDirectoryIfNotExists("./IndexDataTest/Final/");
        createDirectoryIfNotExists("./IndexDataTest/Flags/");
        createDirectoryIfNotExists("./IndexDataTest/Freqs/");
        createDirectoryIfNotExists("./IndexDataTest/Lexicons/");
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        if (directoryPath != null && !directoryPath.isEmpty()) {
            try {
                if (!Files.exists(Paths.get(directoryPath))) {
                    Files.createDirectories(Paths.get(directoryPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testLexicon(String outputPath) {
        String FilePath = "indexing_test.txt";

        try {
            FileChannel fc = FileChannel.open(Paths.get(outputPath), 
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            BufferedReader reader = new BufferedReader(new FileReader(FilePath));
            String line;
            long offset = 0;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 10) {
                    String term = fields[0].trim();
                    int offsetDocId = Integer.parseInt(fields[1].trim());
                    int upperTF = Integer.parseInt(fields[2].trim());
                    int df = Integer.parseInt(fields[3].trim());
                    float idf = Float.parseFloat(fields[4].trim());
                    float upperTFIDF = Float.parseFloat(fields[5].trim());
                    float upperBM25 = Float.parseFloat(fields[6].trim());
                    int offsetFrequency = Integer.parseInt(fields[7].trim());
                    int offsetSkipPointer = Integer.parseInt(fields[8].trim());
                    int numBlocks = Integer.parseInt(fields[9].trim());

                    LexiconEntry le = new LexiconEntry();
                    le.setTerm(term);
                    le.setOffset_doc_id(offsetDocId);
                    le.setUpperTF(upperTF);
                    le.setDf(df);
                    le.setIdf(idf);
                    le.setUpperTFIDF(upperTFIDF);
                    le.setUpperBM25(upperBM25);
                    le.setOffset_frequency(offsetFrequency);
                    le.setOffset_skip_pointer(offsetSkipPointer);
                    le.setNumBlocks(numBlocks);

                    offset = le.writeEntryToDisk(term, offset, fc);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testLexiconComparison() throws Exception {
        String groundTruePath = "./IndexDataTest/Final/LexiconGroundTrue.dat";
        String lexiconPath = "./IndexDataTest/Final/Lexicon.dat";
        
        // Esegui il test per scrivere il file .dat
        testLexicon(groundTruePath);

        // Confronta il file creato con il file di riferimento
        assertTrue(compareFiles(groundTruePath, lexiconPath));
    }

    public static boolean compareFiles(String filePath1, String filePath2) throws IOException {
        try (BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(filePath1));
             BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(filePath2))) {
            int byte1, byte2;
 
            while ((byte1 = bis1.read()) != -1) {
                byte2 = bis2.read();
                if (byte1 != byte2) {
                    return false; // Files have different elements
                }
            }
 
            // Check if the second file has more elements
            return bis2.read() == -1;
        }
    }    

    public static void main(String[] args) throws Exception {
        IndexingTest indexingTest = new IndexingTest();
        indexingTest.indexTest();

        SPIMI.path_setter("./test_collection.tar.gz");
        SPIMI.threshold_setter(1);
        int spimi = SPIMI.execute();
        System.out.println(spimi);
        SPIMIMerger.setNumIndex(spimi);
        SPIMIMerger.execute();
        FileChannel fc = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON), StandardOpenOption.READ);
        long offset = 0;
        while (offset < fc.size()) {
            LexiconEntry le = new LexiconEntry();
            le.readEntryFromDisk(offset, fc);
            System.out.println("\n\t" + le.toString());
            offset += 84;
        }
    }
}
