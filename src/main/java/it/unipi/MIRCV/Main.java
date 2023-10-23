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
import it.unipi.MIRCV.Utils.Preprocessing.Preprocess;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        DocIndex docIndex=new DocIndex();
        Lexicon lexicon=new Lexicon();
        lexicon.setLexicon(new HashMap<>());
        BufferedReader reader=new BufferedReader(new FileReader("prova.tsv"));
        String line;
        String [] docno;
        while((line= reader.readLine())!=null){
            docno= Preprocess.parseLine(line);
            System.out.println(docno[0]);
            line=Preprocess.cleanText(docno[1]);
            List<String>tokens=Preprocess.tokenize(line);
            tokens=Preprocess.removeStopwords(tokens);
            List<String>tokenized=Preprocess.applyStemming(tokens);
            for(String word :tokenized){
                lexicon.add(word,0,0,0,12,0,3);
                System.out.print(word+" ");
            }
            System.out.println();
            docIndex.addDocument(Integer.parseInt(docno[0]),Integer.parseInt(docno[0]),tokenized.size());
        }
        for (Map.Entry<Integer, DocIndexEntry> entry : docIndex.getDocumentIndex().entrySet() ){
            System.out.println("doc_id"+entry.getKey()+" doc_no|len "+entry.getValue());
        }
        lexicon.sortLexicon();
        for (Map.Entry<String, LexiconEntry> entry : lexicon.getLexicon().entrySet() ){
            System.out.println("term:"+entry.getKey()+"\t"+entry.getValue());
        }
    }
}
