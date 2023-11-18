package it.unipi.MIRCV;

import it.unipi.MIRCV.Query.Processer;
import it.unipi.MIRCV.Utils.Indexing.*;
import it.unipi.MIRCV.Utils.PathAndFlags.PathAndFlags;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CollectionStatistics.readFromDisk();
        PathAndFlags.readFlagsFromDisk();
        Lexicon.getInstance();
        DocIndex.getInstance();
        
        Scanner scanner = new Scanner(System.in);
        long timerStart, timerEnd;
        ArrayList<Integer> queryResult;
        String query, scoreFun;

        do {
            System.out.print("Query-> ");
            query = scanner.nextLine();
            if (query.trim().isEmpty()) {
                System.out.println("empty query");
                continue;
            }

            System.out.print("Daat(1) or Dynamic Pruning(2) or exit(3)?");
            int chose = Integer.parseInt(scanner.nextLine());
            if (chose != 1 && chose != 2 && chose != 3) {
                System.out.println("no good choice, please repeat");
                continue;
            } else if (chose == 3) {
                break;
            }

            System.out.print("Conjunctive(1) or Disjunctive(2)?");
            int chose1 = Integer.parseInt(scanner.nextLine());
            if (chose1 != 1 && chose1 != 2) {
                System.out.println("no conjunctive nor disjunctive selected, please restart");
                continue;
            }

            System.out.print("Score function bm25 or tfidf->");
            scoreFun = scanner.nextLine();
            if (!scoreFun.equals("bm25") && !scoreFun.equals("tfidf")) {
                System.out.println("no tfidf or bm25");
                continue;
            }

            PathAndFlags.DYNAMIC_PRUNING = chose != 1;
            timerStart = System.currentTimeMillis();
            queryResult = Processer.processQuery(query, 10, chose1 == 1, scoreFun);
            timerEnd = System.currentTimeMillis();

            if (queryResult == null) {
                System.out.println("no docs for the query");
            } else {
                System.out.print("Results of DocNos-> ");
                for (int i : queryResult) {
                    System.out.print(DocIndex.getInstance().getDoc_NO(i) + " ");
                }
                System.out.println("with time->" + (timerEnd - timerStart) + "ms");
            }

        } while (true);

        // Close the scanner to prevent resource leak
        scanner.close();
    }
}
