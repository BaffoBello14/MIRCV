package it.unipi.MIRCV;

import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Utils.Indexing.*;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws Exception {
        CollectionStatistics.readFromDisk();
        PathAndFlags.readFlagsFromDisk();
        PathAndFlags.DYNAMIC_PRUNING=true;


        long start=System.currentTimeMillis();
        ArrayList<Integer>ret=Processer.processQuery("break",10,false,"bm25");
        long end=System.currentTimeMillis();
        System.out.println(end-start);
        System.out.println(ret);
        if(ret==null){
            System.exit(0);
        }
        for (int i:ret){
            System.out.print(DocIndex.getInstance().getDoc_NO(i)+" ");
        }
        System.out.println();
        start=System.currentTimeMillis();
        ret=Processer.processQuery("clown",10,true,"tfidf");

        end=System.currentTimeMillis();
        System.out.println(end-start);
        if(ret==null){
            System.exit(0);
        }
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

