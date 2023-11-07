package it.unipi.MIRCV.Query;

import it.unipi.MIRCV.Utils.Indexing.Lexicon;
import it.unipi.MIRCV.Utils.Indexing.LexiconEntry;
import it.unipi.MIRCV.Utils.Indexing.Posting;
import it.unipi.MIRCV.Utils.Indexing.PostingIndex;

import java.util.ArrayList;

public class DynamicPrunerMaxScore {
    public static ArrayList<Integer> prune(ArrayList<String> query, int k) {
        // Ottenere l'elenco di posting per la query
        ArrayList<PostingIndex> postingOfQuery = Processer.getQueryPostingLists(new ArrayList<>(query), false);

        if (postingOfQuery == null || postingOfQuery.isEmpty()) {
            return null;
        }

        // Inizializzare l'elenco dei documenti risultanti
        ArrayList<Integer> resultDocIds = new ArrayList<>();

        // Inizializzare un vettore per tenere traccia dei punteggi massimi per ciascun posting
        float[] maxScores = new float[postingOfQuery.size()];

        // Inizializzare un vettore per tenere traccia delle posizioni correnti nei posting
        int[] currentPositions = new int[postingOfQuery.size()];

        for (int i = 0; i < postingOfQuery.size(); i++) {
            maxScores[i] = Float.NEGATIVE_INFINITY;
            currentPositions[i] = 0;
        }

        boolean allPostingsConsumed = false;

        while (!allPostingsConsumed) {
            allPostingsConsumed = true;

            for (int i = 0; i < postingOfQuery.size(); i++) {
                PostingIndex postingIndex = postingOfQuery.get(i);

                Posting posting = postingIndex.next();
                if (posting != null) {
                    allPostingsConsumed = false;

                    // Calcola il punteggio BM25 per il posting corrente
                    LexiconEntry lexiconEntry = getLexiconEntry(postingIndex.getTerm());
                    if (lexiconEntry != null) {
                        float score = Scorer.calculateBM25(posting, lexiconEntry.getIdf());
                        if (score > maxScores[i]) {
                            maxScores[i] = score;
                        }
                    }
                }
            }
        }

        // Pruning dinamico
        for (int i = 0; i < postingOfQuery.size(); i++) {
            PostingIndex postingIndex = postingOfQuery.get(i);
            LexiconEntry lexiconEntry = getLexiconEntry(postingIndex.getTerm());
            if (lexiconEntry != null && maxScores[i] < (k - resultDocIds.size()) / (float) (k + 1) * lexiconEntry.getUpperBM25()) {
                postingIndex.closeLists();
                postingOfQuery.remove(i);
                maxScores[i] = Float.NEGATIVE_INFINITY;
                currentPositions[i] = 0;
                i--;
            }
        }

        int currentDocId = Integer.MAX_VALUE;

        while (resultDocIds.size() < k && postingOfQuery.size() > 0) {
            currentDocId = Integer.MAX_VALUE;

            for (int i = 0; i < postingOfQuery.size(); i++) {
                PostingIndex postingIndex = postingOfQuery.get(i);

                while (currentPositions[i] < postingIndex.getPostings().size()) {
                    Posting posting = postingIndex.getPostings().get(currentPositions[i]);

                    if (posting.getDoc_id() < currentDocId) {
                        currentDocId = posting.getDoc_id();
                    }

                    if (posting.getDoc_id() == currentDocId) {
                        currentPositions[i]++;
                    } else {
                        break;
                    }
                }
            }

            if (currentDocId == Integer.MAX_VALUE) {
                break;
            }

            resultDocIds.add(currentDocId);
        }

        return resultDocIds;
    }

    private static LexiconEntry getLexiconEntry(String term) {
        // Implementa il metodo per ottenere l'entry del lessico corrispondente al termine
        return Lexicon.getInstance().find(term);
    }
}