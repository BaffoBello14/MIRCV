package it.unipi;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

public class PathAndFlagsTest {

    @BeforeEach
    public void setUp() {
        // Delete the FLAGS.dat file before each test
        PathAndFlags.PATH_TO_FLAGS="./IndexDataTest/Flags/Flags.dat";
        try {
            Files.deleteIfExists(Paths.get(PathAndFlags.PATH_TO_FLAGS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteFlagsToDisk() {
        PathAndFlags.COMPRESSION_ENABLED = true;
        PathAndFlags.STOPWORD_STEM_ENABLED = true;
        PathAndFlags.DYNAMIC_PRUNING = false;

        // Write flags to disk
        PathAndFlags.writeFlagsToDisk();

        // Read the flags from disk
        PathAndFlags.readFlagsFromDisk();

        assertTrue(PathAndFlags.COMPRESSION_ENABLED);
        assertTrue(PathAndFlags.STOPWORD_STEM_ENABLED);
        assertFalse(PathAndFlags.DYNAMIC_PRUNING);
    }

    @Test
    public void testReadFlagsFromDisk() {
        // Create a FLAGS.dat file with specific flag values
        try {
            Files.createFile(Paths.get(PathAndFlags.PATH_TO_FLAGS));
            Files.write(Paths.get(PathAndFlags.PATH_TO_FLAGS), new byte[]{1, 0, 1}, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the flags from disk
        PathAndFlags.readFlagsFromDisk();

        assertTrue(PathAndFlags.COMPRESSION_ENABLED);
        assertFalse(PathAndFlags.STOPWORD_STEM_ENABLED);
        assertTrue(PathAndFlags.DYNAMIC_PRUNING);
    }
}

