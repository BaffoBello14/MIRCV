package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.Indexing.Posting;
import it.unipi.MIRCV.Utils.Indexing.PostingIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DAAT2 {
    public static void main(String[] args) throws Exception {
        // Definisci i termini di ricerca
        String[] query = {"computer", "science"};

        // Definisci il numero di risultati da restituire
        int k = 3;

        // Esegui l'algoritmo DAAT
        PriorityQueue<Result> results = retrieveResultsForQuery(query, k);

        // Restituire i primi k risultati
        printTopKResults(results, k);
    }

    static PriorityQueue<Result> retrieveResultsForQuery(String[] query, int k) {
        PriorityQueue<Result> results = new PriorityQueue<>();

        // Scorre i termini della query
        for (String term : query) {
            LexiconEntry lexiconEntry = findLexiconEntry(term);

            // Se l'entry del lessico non è trovata per il termine, passa al successivo
            if (lexiconEntry != null) {
                List<Posting> postings = retrievePostingsForTerm(lexiconEntry);

                // Scorre i posting associati al termine
                for (Posting posting : postings) {
                    // Calcola il punteggio BM25 per ciascun posting
                    float score = calculateBM25Score(posting, lexiconEntry.getIdf());
                    // Aggiunge il risultato alla coda di priorità dei risultati
                    results.add(new Result(score, posting.getDoc_id()));
                }
            }
        }
        return results;
    }

    static LexiconEntry findLexiconEntry(String term) {
        // Cerca nel lessico l'entry corrispondente al termine
        return Lexicon.getInstance().find(term);
    }

    static List<Posting> retrievePostingsForTerm(LexiconEntry lexiconEntry) {
        List<Posting> postings = new ArrayList<>();

        // Se l'entry del lessico è valida, recupera i posting associati al termine
        if (lexiconEntry != null) {
            String term = lexiconEntry.getTerm();
            PostingIndex postingIndex = new PostingIndex(term);
            postingIndex.openList();

            // Scorre i posting associati al termine
            Posting posting;
            while ((posting = postingIndex.next()) != null) {
                postings.add(posting);
            }

            // Chiude le liste di posting
            postingIndex.closeLists();
        }
        return postings;
    }

    static float calculateBM25Score(Posting posting, float idf) {
        // Implementa il calcolo del punteggio BM25 per un posting specifico
        return Scorer.calculateBM25(posting, idf);
    }

    static void printTopKResults(PriorityQueue<Result> results, int k) {
        for (int i = 0; i < k; i++) {
            Result result = results.poll();
            // Stampa i primi k risultati
            System.out.println("Documento " + result.docId + " con punteggio " + result.score);
        }
    }

    static class Result implements Comparable<Result> {
        float score;
        int docId;

        public Result(float score, int docId) {
            this.score = score;
            this.docId = docId;
        }

        @Override
        public int compareTo(Result other) {
            return Float.compare(other.score, this.score);
        }
    }
}
