package it.unipi.MIRCV.Utils.Indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DocIndex {
    private HashMap<Integer,DocIndexEntry> documentIndex;
    public DocIndex(){
        documentIndex=new HashMap<>();
    }

    public HashMap<Integer, DocIndexEntry> getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(HashMap<Integer, DocIndexEntry> documentIndex) {
        this.documentIndex = documentIndex;
    }
    public void addDocument(int doc_id,int doc_no,int doc_size){
        documentIndex.put(doc_id,new DocIndexEntry(doc_no,doc_size));
    }
    public ArrayList<Integer> sortDocIndex(){
        ArrayList<Integer>sortedDocIndex=new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIndex);
        return sortedDocIndex;
    }
}
