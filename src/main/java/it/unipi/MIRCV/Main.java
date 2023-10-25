package it.unipi.MIRCV;

import it.unipi.MIRCV.Utils.Indexing.DocIndexEntry;
import it.unipi.MIRCV.Utils.Paths.PathConfig;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {
        try {
            // Open a FileChannel to the DocIndex.dat file
            FileChannel fileChannelDI = FileChannel.open(Paths.get(PathConfig.getDocIndexDir() + "/DocIndex.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // Map a portion of the file for writing
            MappedByteBuffer mappedByteBuffer = fileChannelDI.map(FileChannel.MapMode.READ_WRITE, 0, 20);

            String doc_no = "30";
            doc_no = DocIndexEntry.padNumberWithZeros(doc_no, 8);

            // Write to the mapped buffer
            mappedByteBuffer.putInt(4);
            mappedByteBuffer.put(doc_no.getBytes(StandardCharsets.UTF_8));
            mappedByteBuffer.putLong(50L);

            // Map a portion of the file for reading
            MappedByteBuffer mappedByteBuffer1 = fileChannelDI.map(FileChannel.MapMode.READ_ONLY, 0, 20);

            // Read from the mapped buffer
            long did = mappedByteBuffer1.getInt();
            byte[] dno = new byte[8];
            mappedByteBuffer1.get(dno);
            String docn = new String(dno, StandardCharsets.UTF_8);
            long dsize = mappedByteBuffer1.getLong();

            System.out.println("docno " + docn + " docid " + did + " size " + dsize);

            fileChannelDI.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
