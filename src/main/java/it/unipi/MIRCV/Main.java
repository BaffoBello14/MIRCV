package it.unipi.MIRCV;

import it.unipi.MIRCV.Utils.Indexing.DocIndexEntry;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) throws Exception {

        FileChannel fileChannelDI = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX + "/DocIndex.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        MappedByteBuffer mappedByteBuffer = fileChannelDI.map(FileChannel.MapMode.READ_WRITE, 0, 20);

        String doc_no = "30";
        doc_no = DocIndexEntry.padNumberWithZeros(doc_no, 8);

        mappedByteBuffer.putInt(4);
        mappedByteBuffer.put(doc_no.getBytes(StandardCharsets.UTF_8));
        mappedByteBuffer.putLong((long) 50);

        MappedByteBuffer mappedByteBuffer1 = fileChannelDI.map(FileChannel.MapMode.READ_ONLY, 0, 20);
        long did = mappedByteBuffer1.getInt();

        byte[] dno = new byte[8];
        mappedByteBuffer1.get(dno);

        String docn = new String(dno, StandardCharsets.UTF_8);
        long dsize = mappedByteBuffer1.getLong();

        System.out.println("docno " + docn + " doid" + did + " size" + dsize);

        fileChannelDI.close();
    }
}
