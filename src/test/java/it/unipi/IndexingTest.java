package it.unipi;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

    private void createLexicon() throws Exception{
        FileChannel fc = FileChannel.open(Paths.get("./IndexDataTest/Final/LexiconGroundTrue.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        LexiconEntry le = new LexiconEntry();
        le.setTerm("beauti");
        le.setDf(2);
        le.setUpperTF(1);
        le.setOffset_doc_id(0);
        le.setUpperTFIDF(1.609438F);
        le.setIdf(1.609438F);
        le.setNumBlocks(1);
        le.setOffset_skip_pointer(0);
        le.setOffset_frequency(0);
        le.setUpperBM25(0.9058143F);

        long offset = 0;
        offset = le.writeEntryToDisk("beauti", offset, fc);
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
