package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;


import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProcessCollection {
    public static void main(String[] args) throws Exception{
        FileChannel fileChannelDI= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_INDEX), StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        FileChannel fileChannelFRQ= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID+"0.dat"),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        //FileChannel fileChannelLEX=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON),StandardOpenOption.READ, StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        long start=System.currentTimeMillis();
        int indexes= (int) SPIMI.execute();
        long end=System.currentTimeMillis();
        System.out.println("SPIMI time->"+(end-start)/1000+"sec");
        long Spimi=end-start;
        CollectionStatistics.readFromDisk();
        start=System.currentTimeMillis();
        SPIMIMerger.setNumIndex(indexes);
        SPIMIMerger.execute();
        end=System.currentTimeMillis();
        System.out.println("SPIMIMerger time->"+(end-start)/1000+"sec "+Spimi/1000+"sec");
        System.exit(0);
        FileChannel [] fileChannelLEX = new FileChannel[60];
        LexiconEntry []lexiconEntries=new LexiconEntry[60];
        for(int i=0;i<60;i++){
            fileChannelLEX[i]=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON+i+".dat"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            lexiconEntries[i]=new LexiconEntry();
            lexiconEntries[i].readEntryFromDisk(fileChannelLEX[i].size()-LexiconEntry.ENTRY_SIZE,fileChannelLEX[i]);
            System.out.println(i+""+lexiconEntries[i]);
        }

        DocIndex docIndex=new DocIndex();
        docIndex.readFromDisk(fileChannelDI,200);
        LexiconEntry lexiconEntry=new LexiconEntry();
        //lexiconEntry.readEntryFromDisk(fileChannelLEX.size()-2*LexiconEntry.ENTRY_SIZE,fileChannelLEX);
        byte [] term=new byte[Lexicon.MAX_LEN_OF_TERM];
        MappedByteBuffer mappedByteBuffer=fileChannelFRQ.map(FileChannel.MapMode.READ_ONLY,8,4);
        //mappedByteBuffer1.get(term);
        //System.out.println(new String(term, StandardCharsets.UTF_8));
        System.out.println(lexiconEntry);
        System.out.println(mappedByteBuffer.getInt());
        System.out.println(docIndex.sortDocIndex().get(0));
    }
}
