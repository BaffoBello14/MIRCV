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
import it.unipi.MIRCV.Query.Pair;
import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Query.Scorer;
import it.unipi.MIRCV.Query.TopKPriorityQueue;
import it.unipi.MIRCV.Utils.Indexing.*;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {
    public static void main(String[] args) throws Exception {
        CollectionStatistics.readFromDisk();
        PathAndFlags.readFromDisk();
        /*
        ArrayList<PostingIndex> postingIndices=new ArrayList<>();
        String po="break";
        System.out.println(Lexicon.getInstance().find(po));
        LexiconEntry entry=Lexicon.getInstance().retrieveEntry(po);
        postingIndices.add(new PostingIndex(entry.getTerm()));
        postingIndices.get(0).openList();
        postingIndices.get(0).next();
        System.out.println(postingIndices.get(0).nextGEQ(1425508));
*/

        long start=System.currentTimeMillis();
        ArrayList<Integer>ret=Processer.processQuery("what is prescribed to treat thyroid storm",10,true,"bm25");
        long end=System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println(ret);
        start=System.currentTimeMillis();
        ret=Processer.processQuery("what is prescribed to treat thyroid storm",10,true,"bm25");

        end=System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println(ret);
    }
}

