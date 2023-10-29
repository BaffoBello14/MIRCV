package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class SkippingBlock {

    // Attributes to describe the block.
    private long doc_id_offset;
    private int doc_id_size;
    private long freq_offset;
    private int freq_size;
    private int doc_id_max;
    private int num_posting_of_block;
    private long file_offset = 0;

    // Size of each element in the block.
    private static final int size_of_element = (8 + 4) * 2 + 4 + 4;

    // Write the block information to disk.
    public boolean writeOnDisk(FileChannel file_to_write) {
        if (file_to_write == null) {
            return false;
        }
        try {
            MappedByteBuffer mappedByteBuffer = file_to_write.map(FileChannel.MapMode.READ_WRITE, file_offset, size_of_element);
            if (mappedByteBuffer == null) {
                return false;
            }
            mappedByteBuffer.putLong(doc_id_offset);
            mappedByteBuffer.putInt(doc_id_size);
            mappedByteBuffer.putLong(freq_offset);
            mappedByteBuffer.putInt(freq_size);
            mappedByteBuffer.putInt(doc_id_max);
            mappedByteBuffer.putInt(num_posting_of_block);
            file_offset += size_of_element;
            return true;
        } catch (IOException e) {
            System.out.println("Problems with the write of block of posting");
            return false;
        }
    }

    // Several getter and setter methods for the class attributes.

    // Getter and Setter methods for doc_id_offset
    public long getDoc_id_offset() {
        return doc_id_offset;
    }

    public void setDoc_id_offset(long doc_id_offset) {
        this.doc_id_offset = doc_id_offset;
    }

    // Getter and Setter methods for doc_id_size
    public int getDoc_id_size() {
        return doc_id_size;
    }

    public void setDoc_id_size(int doc_id_size) {
        this.doc_id_size = doc_id_size;
    }

    // Getter and Setter methods for freq_offset
    public long getFreq_offset() {
        return freq_offset;
    }

    public void setFreq_offset(long freq_offset) {
        this.freq_offset = freq_offset;
    }

    // Getter and Setter methods for freq_size
    public int getFreq_size() {
        return freq_size;
    }

    public void setFreq_size(int freq_size) {
        this.freq_size = freq_size;
    }

    // Getter and Setter methods for doc_id_max
    public int getDoc_id_max() {
        return doc_id_max;
    }

    public void setDoc_id_max(int doc_id_max) {
        this.doc_id_max = doc_id_max;
    }

    // Getter and Setter methods for num_posting_of_block
    public int getNum_posting_of_block() {
        return num_posting_of_block;
    }

    public void setNum_posting_of_block(int num_posting_of_block) {
        this.num_posting_of_block = num_posting_of_block;
    }

    // Getter and Setter methods for file_offset
    public long getFile_offset() {
        return file_offset;
    }

    public void setFile_offset(long file_offset) {
        this.file_offset = file_offset;
    }

    /**
 * Retrieves the postings from a skipping block.
 * 
 * @return ArrayList of Posting objects representing the postings for a term.
 */
    public ArrayList<Posting> getSkippingBlockPostings() {
        try {
            // Open the channels for document IDs and frequencies.
            FileChannel fileChannelDocID = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_DOC_ID),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            FileChannel fileChannelFreqs = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_FREQ),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // Map the file content to ByteBuffers.
            MappedByteBuffer mappedByteBufferDocID = fileChannelDocID.map(FileChannel.MapMode.READ_ONLY, doc_id_offset, doc_id_size);
            MappedByteBuffer mappedByteBufferFreq = fileChannelFreqs.map(FileChannel.MapMode.READ_ONLY, freq_offset, freq_size);

            // Check if the buffers were successfully created.
            if (mappedByteBufferFreq == null || mappedByteBufferDocID == null) {
                return null;
            }

            ArrayList<Posting> postings = new ArrayList<>();

            // If compression is enabled, decompress the data.
            if (PathAndFlags.COMPRESSION_ENABLED) {
                byte[] doc_ids = new byte[doc_id_size];
                byte[] freqs = new byte[freq_size];
                
                mappedByteBufferDocID.get(doc_ids, 0, doc_id_size);
                mappedByteBufferFreq.get(freqs, 0, freq_size);

                int[] freqs_decompressed = UnaryConverter.convertFromUnary(freqs, num_posting_of_block);
                int[] doc_ids_decompressed = VariableByteEncoder.decodeArray(doc_ids);

                // Create and add Posting objects to the list.
                for (int i = 0; i < num_posting_of_block; i++) {
                    Posting posting = new Posting(doc_ids_decompressed[i], freqs_decompressed[i]);
                    postings.add(posting);
                }
            } else {
                // If compression is not enabled, directly retrieve the data.
                for (int i = 0; i < num_posting_of_block; i++) {
                    Posting posting = new Posting(mappedByteBufferDocID.getInt(), mappedByteBufferFreq.getInt());
                    postings.add(posting);
                }
            }

            return postings;
        } catch (IOException e) {
            System.out.println("Problems with reading from the file of the block descriptor.");
            e.printStackTrace();
            return null;
        }
    }
}
