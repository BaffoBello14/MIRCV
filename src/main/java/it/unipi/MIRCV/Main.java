//codifica caratteri
/*
package it.unipi.MIRCV;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        String dataToWrite = "ciao" + "\t" + 10;

        // Scrivi nel file come testo
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("data.dat"))) {
            dataOutputStream.write(dataToWrite.getBytes("UTF-8"));
        }

        // Leggi dal file come testo
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("data.dat", "r");
             DataInputStream dataInputStream = new DataInputStream(new FileInputStream(randomAccessFile.getFD()))) {

            byte[] readBytes = new byte[(int)randomAccessFile.length()];
            dataInputStream.read(readBytes);

            String readString = new String(readBytes, "UTF-8");
            System.out.print(readString);
        }
    }
}
*/

//codifica binaria

package it.unipi.MIRCV;

import it.unipi.MIRCV.Utils.Indexing.DocIndex;
import it.unipi.MIRCV.Utils.Indexing.DocIndexEntry;
import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX+"/DocIndex.dat"), StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        MappedByteBuffer mappedByteBuffer=fileChannelDI.map(FileChannel.MapMode.READ_WRITE,0,20);
        String doc_no="30";
        doc_no=DocIndexEntry.padNumberWithZeros(doc_no,8);
        mappedByteBuffer.putInt(4);
        mappedByteBuffer.put(doc_no.getBytes(StandardCharsets.UTF_8));
        mappedByteBuffer.putLong((long)50);
        MappedByteBuffer mappedByteBuffer1=fileChannelDI.map(FileChannel.MapMode.READ_ONLY,0,20);
        long did=mappedByteBuffer1.getInt();
        byte [] dno=new byte[8];
        mappedByteBuffer1.get(dno);
        String docn=new String(dno,StandardCharsets.UTF_8);
        long dsize=mappedByteBuffer1.getLong();
        System.out.println("docno "+docn+" doid"+did+" size"+dsize);
        fileChannelDI.close();

    }
}
