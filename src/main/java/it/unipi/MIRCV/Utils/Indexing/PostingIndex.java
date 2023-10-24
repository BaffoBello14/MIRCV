package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Iterator;

public class PostingIndex {
    private String term;
    private ArrayList<Posting>postings=new ArrayList<>();
    private ArrayList<SkippingBlock>blocks;
    private SkippingBlock skippingBlockActual;
    private Posting postingActual;
    private Iterator<Posting> postingIterator;
    private Iterator<SkippingBlock> skippingBlockIterator;
    private boolean compression;

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public String getTerm() {
        return term;
    }

    public ArrayList<Posting> getPostings() {
        return postings;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    public void addPostings(ArrayList<Posting> postings){

    }
    public void openList(){
        skippingBlockIterator= blocks.iterator();
        postingIterator=postings.iterator();
    }
    public Posting next(){
        if(!postingIterator.hasNext()){
            if(!skippingBlockIterator.hasNext()){
                postingActual=null;
                return null;

            }
            skippingBlockActual=skippingBlockIterator.next();

            postings.clear();
            postings.addAll(skippingBlockActual.getSkippingBlockPostings(compression));
            postingIterator=postings.iterator();
        }
        postingActual=postingIterator.next();
        return postingActual;
    }
}
