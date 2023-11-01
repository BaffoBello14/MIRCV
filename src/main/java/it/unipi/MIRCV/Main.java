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

import java.util.HashMap;


public class Main {
    public static void main(String[] args) throws Exception {

        Lexicon lexicon=Lexicon.getInstance();
        CollectionStatistics.readFromDisk();
        long start=System.currentTimeMillis();
        DocIndex docIndex=DocIndex.getInstance();

        long len=docIndex.getDoc_len(5);
        LexiconEntry lexiconEntry=lexicon.retrieveEntry("ciao");
        long end=System.currentTimeMillis();
        System.out.println(end-start);
        start=System.currentTimeMillis();
        System.out.println(lexiconEntry);
        System.out.println(len);
        docIndex.getDoc_len(5);
        lexicon.retrieveEntry("ciao");
        end=System.currentTimeMillis();
        System.out.println(end-start);

    }
}

