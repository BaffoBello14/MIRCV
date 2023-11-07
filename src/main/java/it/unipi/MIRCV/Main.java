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
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
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
        ArrayList<Integer>ret=Processer.processQuery("top 10 bidet",10,true,"tfidf");
        long end=System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println(ret);
        start=System.currentTimeMillis();
        ret=Processer.processQuery("top 10 bidet",10,true,"tfidf");

        end=System.currentTimeMillis();
        System.out.println(end-start);
        for (int i:ret){
            System.out.print(DocIndex.getInstance().getDoc_NO(i)+" ");
        }

/*

Hello, how are you, bautiful?
2	I love programming.                                     love, program
3	The quick brown fox jumps over the lazy dog.             quick, brown, fox, jump, lazi, dog
4	Python is a versatile programming language.         python, versatil, program, languag
5	Today is a beautiful day.                           beauti, dai
6	Learning a new language is fun.                 learn, languag, fun
7	The sky is blue.                     sky blue
8	I enjoy reading books.              enjoi, read, book
9	Coding is my passion.               code, passion
10	Coffee is my favorite drink.        coffe, favorit, drink


beauti,1-1 5-1
blue 7-1
book 8-1
brown, 3 1
code, 9-1
coffe, 10-1
dai, 5-1
dog, 3-1
drink 10-1
enjoi,8-1
favorit,10-1
fox,3-1
fun,6-1
jump,3-1
languag,4-1 6-1
lazi, 3-1
learn, 6-1
love, 2-1
passion 9-1
program, 2-1 4-1
python, 4-1
quick, 3-1
read, 8-1
sky, 7-1
versatil 4-1
Lexicon                                                                         docIds                      freqs                   blockinfo
term    |docid  |uppertf    |df|idf|uppertfidf|upperbm25|offsetfreq|offsetblock|#blocks
beauti   0       1           2    1.609438     0.56281406 0         0            1
blue     1       1           1   2.3025851      0.45901638 1        32           1
book     2       1           1   //             0.38754326 2        64           1
brown    3       1           1   //          0.26415095    3        96           1
code     4       1           1  //            0.45901638   4        128           1        2
cofee    5       1           // //              0.38754326 5        160           1      3
dai     6       1           //  //                 2        6       +32             1
dog     7       1           //      //        0.26415095    7       +32           1     6
drink
enjoi
favorit
fox
fun
jump
 */
        /*
        float tf= (float) (1+Math.log(1)); //tf
        float idf= (float) Math.log((double) 10 /1);// df
        float bm25= (float) (tf/(tf+1.5*(0.25+0.75*(2/2.8))));//dl
       System.out.println(idf);

       System.out.println(bm25*idf);
*/


    }
}

