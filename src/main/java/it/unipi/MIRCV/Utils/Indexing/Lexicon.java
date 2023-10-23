package it.unipi.MIRCV.Utils.Indexing;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Lexicon {
    private HashMap<String,LexiconEntry> lexicon=new HashMap<>();
    private static final int MAX_LEN_OF_TERM=32;
    private static final int ENTRY_SIZE=MAX_LEN_OF_TERM+5*8+4;

    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }
    public void add(String term,long offset_doc_id, long offset_frequency, long offset_skip_pointer, float term_upper_bound, long offset_last_doc_id, long num_posting){
        if (term.length()>MAX_LEN_OF_TERM){
            term=term.substring(0,MAX_LEN_OF_TERM);
        }
        if(lexicon.containsKey(term)){
            lexicon.get(term).setTerm_upper_bound(Math.max(term_upper_bound,lexicon.get(term).getTerm_upper_bound()));

        }else{
            lexicon.put(term,new LexiconEntry(offset_doc_id,offset_frequency,offset_skip_pointer, term_upper_bound,offset_last_doc_id,num_posting));

        }
    }
    public ArrayList<String>sortLexicon(){
        ArrayList<String>sorted=new ArrayList<>(lexicon.keySet());
        Collections.sort(sorted);
        return sorted;
    }
    public long writeToDisk(long position, FileChannel fileChannel){
        try{
            MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,position, (long) lexicon.size() *ENTRY_SIZE);
            if(mappedByteBuffer==null){
                return -1;
            }
            List<String> terms=sortLexicon();
            for(String term : terms){
                mappedByteBuffer.put(term.getBytes(StandardCharsets.UTF_8));
                mappedByteBuffer.putLong(lexicon.get(term).getOffset_doc_id());
                mappedByteBuffer.putLong(lexicon.get(term).getOffset_frequency());
                mappedByteBuffer.putLong(lexicon.get(term).getOffset_skip_pointer());
                mappedByteBuffer.putFloat(lexicon.get(term).getTerm_upper_bound());
                mappedByteBuffer.putLong(lexicon.get(term).getOffset_last_doc_id());
                mappedByteBuffer.putLong(lexicon.get(term).getNum_posting());

            }
            return position+ (long) lexicon.size() *ENTRY_SIZE;
        }catch (IOException e){
            System.out.println("problems in write to disk of lexicon");
            return -1;
        }
    }
}
