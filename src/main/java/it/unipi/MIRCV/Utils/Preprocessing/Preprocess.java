package it.unipi.MIRCV.Utils.Preprocessing;

import ca.rmen.porterstemmer.PorterStemmer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Preprocess {

    // Path to the stopwords file
    private static final String PATH_TO_STOPWORDS = "stopWord/stopword.txt";
    // Instance of the Porter Stemmer for stemming
    private static final PorterStemmer PORTER_STEMMER = new PorterStemmer();
    // List to store stopwords
    private static List<String> stopwords = new ArrayList<>();

    // Static block to load stopwords upon class initialization
    static {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(PATH_TO_STOPWORDS))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stopwords.add(line);
            }
        } catch (IOException e) {
            System.out.println("Problemi nell'apertura del file delle stopwords");
            e.printStackTrace();
        }
    }

    // Regular expressions for text cleaning
    private static final String URL_REGEX = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private static final String HTML_TAGS_REGEX = "<[^>]+>";
    private static final String NON_LETTER_REGEX = "[^a-zA-Z ]";
    private static final String MULTIPLE_SPACES_REGEX = " +";
    private static final String CONSECUTIVE_LETTERS_REGEX = "(.)\\1{2,}";
    private static final String CAMEL_CASE_REGEX = "(?<=[a-z])(?=[A-Z])";

    /**
     * Splits the TSV file lines into columns.
     */
    public static String[] parseLine(String line) {
        return line.split("\t");
    }

    /**
     * Cleans the text by removing URLs, HTML tags, non-letter characters, 
     * consecutive letters, and multiple spaces.
     */
    public static String cleanText(String text) {
        return text.replaceAll(URL_REGEX, " ")
                   .replaceAll(HTML_TAGS_REGEX, " ")
                   .replaceAll(NON_LETTER_REGEX, " ")
                   .replaceAll(MULTIPLE_SPACES_REGEX, " ")
                   .replaceAll(CONSECUTIVE_LETTERS_REGEX, "$1$1")
                   .trim();
    }

    /**
     * Tokenizes the text by splitting it into words and handles camelCase words.
     */
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String[] words = text.split(" ");
        for (String word : words) {
            String[] withoutCamelCase = word.split(CAMEL_CASE_REGEX);
            for (String token : withoutCamelCase) {
                tokens.add(token.toLowerCase(Locale.ROOT));
            }
        }
        return tokens;
    }

    /**
     * Removes stopwords from the list of tokens.
     */
    public static List<String> removeStopwords(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    /**
     * Applies stemming to tokens using the Porter algorithm.
     */
    public static List<String> applyStemming(List<String> tokens) {
        List<String> stemmedTokens = new ArrayList<>();
        for (String token : tokens) {
            stemmedTokens.add(PORTER_STEMMER.stemWord(token));
        }
        return stemmedTokens;
    }

    /**
     * Test function to process a TSV file and print the results.
     */
    public static void main(String[] args) {
        String tsvFilePath = "prova.tsv";
        int columnIndexToProcess = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(tsvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = parseLine(line);

                if (columns.length > columnIndexToProcess) {
                    String text = columns[columnIndexToProcess];
                    String cleanedText = cleanText(text);
                    List<String> tokens = tokenize(cleanedText);
                    List<String> tokensWithoutStopwords = removeStopwords(tokens);
                    List<String> stemmedTokens = applyStemming(tokensWithoutStopwords);

                    System.out.println("Testo originale: " + text);
                    System.out.println("Testo pulito: " + cleanedText);
                    System.out.println("Token: " + String.join(", ", tokens));
                    System.out.println("Token senza stopwords: " + String.join(", ", tokensWithoutStopwords));
                    System.out.println("Token con stemming: " + String.join(", ", stemmedTokens));
                    System.out.println("---------------------------------------------");
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file TSV: " + e.getMessage());
        }
    }
}
