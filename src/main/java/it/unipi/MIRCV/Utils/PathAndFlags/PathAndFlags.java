package it.unipi.MIRCV.Utils.PathAndFlags;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class PathAndFlags {
    // Directory paths
    public static String PATH_TO_DOC_ID = "./IndexData/Doc_ids/";
    public static String PATH_TO_FINAL = "./IndexData/Final";
    public static String PATH_TO_FREQ = "./IndexData/Freqs/";
    public static String PATH_TO_LEXICON = "./IndexData/Lexicons/";

    // File paths
    public static String PATH_TO_FINAL_DOC_ID = "./IndexData/Final/DocID.dat";
    public static String PATH_TO_FINAL_FREQ = "./IndexData/Final/Freq.dat";
    public static String PATH_TO_DOC_INDEX = "./IndexData/Doc_index/DocIndex.dat";
    public static String PATH_TO_FINAL_LEXICON = "./IndexData/Final/Lexicon.dat";
    public static String PATH_TO_COLLECTION_STAT = "./IndexData/CollectionStatistics/CollectionStat.dat";
    public static String PATH_TO_BLOCK_FILE = "./IndexData/BlockInfo/BlockInfo.dat";
    public static String PATH_TO_FLAGS = "./IndexData/Flags/Flags.dat";

    // Configuration flags
    public static boolean COMPRESSION_ENABLED = true;
    public static boolean STOPWORD_STEM_ENABLED = true;
    public static boolean DYNAMIC_PRUNING = false;
    public static final float BM25_k1 = 1.5F;
    public static final float BM25_b = 0.75F;
    public static final int POSTING_PER_BLOCK = 512;
    public static final int LEXICON_CACHE_SIZE = 1000;
    public static final int DOC_INDEX_CACHE_SIZE = 1000000;

    private PathAndFlags() {
        throw new AssertionError("PathAndFlags is a utility class and should not be instantiated.");
    }

    public static void writeFlagsToDisk() {
        try (FileChannel fileChannel = FileChannel.open(Paths.get(PATH_TO_FLAGS),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = ByteBuffer.allocate(3);
            buffer.put((byte) (COMPRESSION_ENABLED ? 1 : 0));
            buffer.put((byte) (STOPWORD_STEM_ENABLED ? 1 : 0));
            buffer.put((byte) (DYNAMIC_PRUNING ? 1 : 0));
            buffer.flip();
            fileChannel.write(buffer);
        } catch (IOException e) {
            System.err.println("Problems with writing flags to disk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void readFlagsFromDisk() {
        try (FileChannel fileChannel = FileChannel.open(Paths.get(PATH_TO_FLAGS), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(3);
            fileChannel.read(buffer);
            buffer.flip();
            COMPRESSION_ENABLED = buffer.get() == 1;
            STOPWORD_STEM_ENABLED = buffer.get() == 1;
            DYNAMIC_PRUNING = buffer.get() == 1;
        } catch (IOException e) {
            System.err.println("Problems with reading flags from disk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createDirectories() {
        createDirectory("./IndexData/BlockInfo/");
        createDirectory("./IndexData/CollectionStatistics/");
        createDirectory(PATH_TO_DOC_ID);
        createDirectory("./IndexData/Doc_index/");
        createDirectory(PATH_TO_FINAL);
        createDirectory("./IndexData/Flags/");
        createDirectory(PATH_TO_FREQ);
        createDirectory(PATH_TO_LEXICON);
    }

    private static void createDirectory(String path) {
        try {
            Path directoryPath = Paths.get(path);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            System.err.println("Problems with creating directories: " + e.getMessage());
        }
    }
}
