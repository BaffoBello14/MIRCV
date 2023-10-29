package it.unipi.MIRCV.Utils.Indexing;

import it.unipi.MIRCV.Converters.UnaryConverter;
import it.unipi.MIRCV.Converters.VariableByteEncoder;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;


import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class SPIMIMerger {
    private static long[] lexiconOffsets;
    private static long doc_id_offset=0;
    private static long freq_offset=0;
    private static long lexSize=0;
    private static long lex_offset=0;
    private static int SPIMI_Index=-1;
    private static LexiconEntry[] lexiconEntries;
    private static FileChannel[] doc_id_File_channels;
    private static FileChannel[] freq_file_channels;
    public static void setNumIndex(int num_index){
        SPIMI_Index=num_index;
    }
    private static PostingIndex loadList(LexiconEntry lexiconEntry,int index){
        PostingIndex postingIndex;
        try {
            MappedByteBuffer mappedByteBufferDOCID=doc_id_File_channels[index].map(FileChannel.MapMode.READ_ONLY,lexiconEntry.getOffset_doc_id(),lexiconEntry.getDocidByteSize());
            MappedByteBuffer mappedByteBufferFreq=freq_file_channels[index].map(FileChannel.MapMode.READ_ONLY,lexiconEntry.getOffset_frequency(),lexiconEntry.getFreqByteSize());
            postingIndex= new PostingIndex(lexiconEntry.getTerm());
            for(int i=0;i<lexiconEntry.getDf();i++){
                Posting posting=new Posting(mappedByteBufferDOCID.getInt(),mappedByteBufferFreq.getInt());
                postingIndex.getPostings().add(posting);

            }
            return postingIndex;
        }catch (IOException e){
            System.out.println("problems with opening the file of the doc_id or freq channel in load list of merger");
            e.printStackTrace();
            return null;
        }
    }
    public static boolean execute(){
        if(SPIMI_Index==-1){
            System.out.println("set the index produced by SPIMI Splitter");
            return false;
        }
        lexiconEntries=new LexiconEntry[SPIMI_Index];
        lexiconOffsets=new long[SPIMI_Index];
        doc_id_File_channels=new FileChannel[SPIMI_Index];
        freq_file_channels=new FileChannel[SPIMI_Index];
        long ifError;
        try{
            for(int i=0;i<SPIMI_Index;i++){
                lexiconEntries[i]=new LexiconEntry();
                lexiconOffsets[i]=0;
                FileChannel fileChannel=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON+i+".dat"), StandardOpenOption.READ);
                ifError=lexiconEntries[i].readEntryFromDisk(0, fileChannel);
                fileChannel.close();
                if(ifError==0||ifError==-1){
                    System.out.println("lexicon"+i+"not opened");
                    lexiconEntries[i]=null;
                }
                doc_id_File_channels[i]=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_DOC_ID+i+".dat"),StandardOpenOption.READ);
                freq_file_channels[i]=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FREQ+i+".dat"),StandardOpenOption.READ);
            }
        }catch (IOException e){
            System.out.println("error on the opening file on merger of spimi of doc id or freq file");
            e.printStackTrace();
            return false;
        }
        try{
            FileChannel fileChannelLexicon= FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_LEXICON),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelDocIdFinal=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_DOC_ID),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelFreqFinal=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_FINAL_FREQ),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            FileChannel fileChannelBlockInfo=FileChannel.open(Paths.get(PathAndFlags.PATH_TO_BLOCK_FILE),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            while(true){
                String termToProcess = getTermToProcess();
                if(termToProcess==null){
                    System.out.println("end of merging");
                    break;
                }
                if(termToProcess.isEmpty()){
                    continue;
                }
                System.out.print(termToProcess);
                LexiconEntry lexiconEntry=new LexiconEntry();
                lexiconEntry.setTerm(termToProcess);
                PostingIndex mergedPosting=processTerm(lexiconEntry,termToProcess);
                if(mergedPosting==null){
                    System.out.println("error with the merging of the list of "+termToProcess);
                    return false;
                }

                lexiconEntry.calculateBlockNeed();
                int numPostingPerBlock= (int) Math.ceil( lexiconEntry.getDf() /(double)lexiconEntry.getNumBlocks());

                int numBlocks=lexiconEntry.getNumBlocks();
                Iterator<Posting> postingIterator=mergedPosting.getPostings().iterator();
                for(int i=0;i<numBlocks;i++){
                    SkippingBlock skippingBlock=new SkippingBlock();
                    skippingBlock.setDoc_id_offset(doc_id_offset);
                    skippingBlock.setFreq_offset(freq_offset);
                    int postingInTheSkippingBlock=0;
                    int writtenPostings=i*numPostingPerBlock;

                    int numPostingTobeWriteToThisBlock=Math.min((mergedPosting.getPostings().size()-writtenPostings),numPostingPerBlock);

                    if(PathAndFlags.COMPRESSION_ENABLED){
                        int [] doc_id=new int[numPostingTobeWriteToThisBlock];
                        int [] freq=new int[numPostingTobeWriteToThisBlock];
                        while(true){
                            if(!postingIterator.hasNext()){
                                System.out.println(mergedPosting.getPostings().size());
                                System.out.println(writtenPostings);
                                System.out.println(numPostingTobeWriteToThisBlock);
                                return false;
                            }
                            Posting actualPosting=postingIterator.next();
                            doc_id[postingInTheSkippingBlock]=actualPosting.getDoc_id();
                            freq[postingInTheSkippingBlock]= actualPosting.getFrequency();
                            postingInTheSkippingBlock++;
                            if(numPostingTobeWriteToThisBlock==postingInTheSkippingBlock){
                                byte[] compressed_doc_ids= VariableByteEncoder.encodeArray(doc_id);
                                byte[] compressed_freqs= UnaryConverter.convertToUnary(freq);
                                try {
                                    MappedByteBuffer mappedByteBufferDOCID= fileChannelDocIdFinal.map(FileChannel.MapMode.READ_WRITE,doc_id_offset,compressed_doc_ids.length);
                                    MappedByteBuffer mappedByteBufferFREQS=fileChannelFreqFinal.map(FileChannel.MapMode.READ_WRITE,freq_offset,compressed_freqs.length);
                                    mappedByteBufferDOCID.put(compressed_doc_ids);
                                    mappedByteBufferFREQS.put(compressed_freqs);
                                    skippingBlock.setDoc_id_max(actualPosting.getDoc_id());
                                    skippingBlock.setNum_posting_of_block(postingInTheSkippingBlock);
                                    skippingBlock.writeOnDisk(fileChannelBlockInfo);
                                    doc_id_offset+=compressed_doc_ids.length;
                                    freq_offset+=compressed_freqs.length;
                                    break;
                                }catch (IOException e){
                                    System.out.println("problems with opening the file to write the compressed docids and freqs on the mergin algorithm of spimi");
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                        }
                    }else {
                        skippingBlock.setDoc_id_size(numPostingTobeWriteToThisBlock*4);
                        skippingBlock.setFreq_size(numPostingTobeWriteToThisBlock*4);
                        try {
                            MappedByteBuffer mappedByteBufferDOCID= fileChannelDocIdFinal.map(FileChannel.MapMode.READ_WRITE,doc_id_offset,numPostingTobeWriteToThisBlock*4L);
                            MappedByteBuffer mappedByteBufferFREQS=fileChannelFreqFinal.map(FileChannel.MapMode.READ_WRITE,freq_offset,numPostingTobeWriteToThisBlock*4L);
                            if(mappedByteBufferDOCID==null||mappedByteBufferFREQS==null){
                                System.out.println("channel for doc id or freq of no compressed version of merger is not open");
                                return false;
                            }
                            while(true){
                                if(!postingIterator.hasNext()){
                                    break;
                                }
                                Posting actualPosting=postingIterator.next();
                                mappedByteBufferDOCID.putInt(actualPosting.getDoc_id());
                                mappedByteBufferFREQS.putInt(actualPosting.getFrequency());
                                postingInTheSkippingBlock++;
                                if(numPostingTobeWriteToThisBlock==postingInTheSkippingBlock){
                                    skippingBlock.setDoc_id_max(actualPosting.getDoc_id());
                                    skippingBlock.setNum_posting_of_block(postingInTheSkippingBlock);
                                    skippingBlock.writeOnDisk(fileChannelBlockInfo);
                                    doc_id_offset+=numPostingTobeWriteToThisBlock*4L;
                                    freq_offset+=numPostingTobeWriteToThisBlock*4L;
                                    break;
                                }
                            }
                        }catch (IOException e){
                            System.out.println("problems with the file channels of the no compressed version of the merger");
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                lex_offset=lexiconEntry.writeEntryToDisk(termToProcess,lex_offset,fileChannelLexicon);
                System.out.println(lexiconEntry);
                lexSize++;
            }
            fileChannelBlockInfo.close();
            fileChannelLexicon.close();
            fileChannelDocIdFinal.close();
            fileChannelFreqFinal.close();
            for(int i=0;i<SPIMI_Index;i++){
                doc_id_File_channels[i].close();
                freq_file_channels[i].close();
            }
            CollectionStatistics.setTerms(lexSize);
            CollectionStatistics.write2Disk();
            return true;

        }catch (IOException e){
            System.out.println("problemswith opening of file channels of lexicon doc id freq partial or total");
            e.printStackTrace();
            return false;
        }
    }

    private static String getTermToProcess() {
        String termToProcess, nextTerm;
        termToProcess = null;
        for (int i = 0; i < SPIMI_Index; i++) {
            if (lexiconEntries[i] == null) {
                continue;
            }
            nextTerm = lexiconEntries[i].getTerm();
            if (termToProcess == null) {
                termToProcess = nextTerm;
                continue;
            }
            if (termToProcess.compareTo(nextTerm) > 0) {
                termToProcess = nextTerm;
            }
        }
        return termToProcess;
    }

    private static void moveToNextTermLexicon(String term){
        for(int i=0;i<SPIMI_Index;i++){
            if(lexiconEntries[i]!=null&&lexiconEntries[i].getTerm().equals(term)){
                try{
                    lexiconOffsets[i] += LexiconEntry.ENTRY_SIZE;
                    FileChannel fileChannel = FileChannel.open(Paths.get(PathAndFlags.PATH_TO_LEXICON + i + ".dat"), StandardOpenOption.READ);
                    if(lexiconOffsets[i]< fileChannel.size()){
                        long ifError = lexiconEntries[i].readEntryFromDisk(lexiconOffsets[i], fileChannel);
                        if (ifError == 0 || ifError == -1) {
                            lexiconEntries[i] = null;
                        }
                    }else{
                        lexiconEntries[i]=null;
                    }
                    fileChannel.close();
                }catch (IOException e){
                    System.out.println("problem with opening the file of lexicon partial of move to next term of Marge");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    private static PostingIndex processTerm(LexiconEntry lexiconEntry,String termToProcess){
        PostingIndex mergedPosting=new PostingIndex();
        mergedPosting.setTerm(termToProcess);
        for(int i=0;i<SPIMI_Index;i++){
            if(lexiconEntries[i]!=null&&lexiconEntries[i].getTerm().equals(termToProcess)){
                PostingIndex partialPosting=loadList(lexiconEntries[i],i);
                if(partialPosting==null){
                    System.out.println("partial index null of entry->"+i);
                    return null;
                }
                lexiconEntry.updateBM25Values(lexiconEntries[i].getTf(),lexiconEntries[i].getDoclen());
                lexiconEntry.updateTFMAX(partialPosting);
                mergedPosting.addPostings(partialPosting.getPostings());
            }
        }
        moveToNextTermLexicon(termToProcess);
        lexiconEntry.setOffset_doc_id(doc_id_offset);
        lexiconEntry.setOffset_frequency(freq_offset);
        lexiconEntry.calculateIDF();
        lexiconEntry.calculateUpperBounds();
        return mergedPosting;
    }

}
