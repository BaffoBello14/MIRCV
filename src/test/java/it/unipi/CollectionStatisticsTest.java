package it.unipi;

import it.unipi.MIRCV.Utils.Indexing.CollectionStatistics;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionStatisticsTest {

    @BeforeEach
    public void setUp() {
        // Reset the collection statistics before each test
        CollectionStatistics.setDocuments(0);
        CollectionStatistics.setAvgDocLen(0.0);
        CollectionStatistics.setTerms(0);
        CollectionStatistics.setTotalLenDoc(0);
        PathAndFlags.PATH_TO_COLLECTION_STAT="./IndexDataTest/CollectionStatistics/CollectionStatTest.dat";
    }

    @Test
    public void testInitialization() {
        assertEquals(0, CollectionStatistics.getDocuments());
        assertEquals(0.0, CollectionStatistics.getAvgDocLen(), 0.001);
        assertEquals(0, CollectionStatistics.getTerms());
        assertEquals(0, CollectionStatistics.getTotalLenDoc());
    }

    @Test
    public void testComputeAvgDocLen() {
        CollectionStatistics.setDocuments(3);
        CollectionStatistics.setTotalLenDoc(15);

        CollectionStatistics.computeAVGDOCLEN();

        assertEquals(5.0, CollectionStatistics.getAvgDocLen(), 0.001);
    }

    @Test
    public void testWriteAndReadToDisk() {
        // Set some values for testing
        CollectionStatistics.setDocuments(10);
        CollectionStatistics.setAvgDocLen(7.5);
        CollectionStatistics.setTerms(500);
        CollectionStatistics.setTotalLenDoc(75);

        // Write to disk
        assertTrue(CollectionStatistics.write2Disk());

        // Reset values to test reading from disk
        CollectionStatistics.setDocuments(0);
        CollectionStatistics.setAvgDocLen(0.0);
        CollectionStatistics.setTerms(0);
        CollectionStatistics.setTotalLenDoc(0);

        // Read from disk
        assertTrue(CollectionStatistics.readFromDisk());

        // Verify if values were read correctly
        assertEquals(10, CollectionStatistics.getDocuments());
        assertEquals(7.5, CollectionStatistics.getAvgDocLen(), 0.001);
        assertEquals(500, CollectionStatistics.getTerms());
        assertEquals(75, CollectionStatistics.getTotalLenDoc());
    }
}

