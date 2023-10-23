package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Lexicon {
    private HashMap<String,LexiconEntry> lexicon=new HashMap<>();

    public HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }
    public void add(String term,long offset_doc_id, long offset_frequency, long offset_skip_pointer, float term_upper_bound, long offset_last_doc_id, long num_posting){
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
}
