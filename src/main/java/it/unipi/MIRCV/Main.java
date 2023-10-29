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

        FileChannel fileChannelLEX=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON),StandardOpenOption.READ);
        LexiconEntry lexiconEntry=new LexiconEntry();
        long pos=0;
        while(lexiconEntry.readEntryFromDisk(pos,fileChannelLEX)!=-1
               // &&!lexiconEntry.getTerm().trim().isEmpty()
        ){
            System.out.println(lexiconEntry);
            pos+=100;
            System.out.println(pos);
            System.out.println(fileChannelLEX.size());
        }
        CollectionStatistics.readFromDisk();
        System.out.println(CollectionStatistics.getDocuments()+" "+CollectionStatistics.getAvgDocLen()+" "+CollectionStatistics.getTerms()+" "+CollectionStatistics.getTotalLenDoc());

    }
}
