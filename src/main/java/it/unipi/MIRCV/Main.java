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

import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.Indexing.*;
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
/*
        FileChannel fileChannelDOC=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX),StandardOpenOption.READ);
        MappedByteBuffer mappedByteBuffer = fileChannelDOC.map(FileChannel.MapMode.READ_ONLY, (1-1) * DocIndexEntry.DOC_INDEX_ENTRY_SIZE , DocIndexEntry.DOC_INDEX_ENTRY_SIZE);
        System.out.println(mappedByteBuffer.getInt());
        byte [] docno=new byte[DocIndexEntry.DOC_NO_LENGTH];
        mappedByteBuffer.get(docno);
        System.out.println(new String(docno));
        System.out.println(mappedByteBuffer.getLong());
*/
        Lexicon lexicon=new Lexicon();
        CollectionStatistics.readFromDisk();
        LexiconEntry lexiconEntry=lexicon.find("clown");
        System.out.println(lexiconEntry);

    }
}

