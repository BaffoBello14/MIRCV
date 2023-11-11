package it.unipi.MIRCV.Utils.Preprocessing;

import ca.rmen.porterstemmer.PorterStemmer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;

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
    // Compiled patterns for text cleaning
    private static final Pattern URL_PATTERN = Pattern.compile("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");
    private static final Pattern HTML_TAGS_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern NON_LETTER_PATTERN = Pattern.compile("[^a-zA-Z ]");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile(" +");
    private static final Pattern CONSECUTIVE_LETTERS_PATTERN = Pattern.compile("(.)\\1{2,}");
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");
    private static final Pattern NO_STANDARD_ASCII_PATTERN = Pattern.compile("[^\\x00-\\x7f]");


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
        return NO_STANDARD_ASCII_PATTERN.matcher(
                CONSECUTIVE_LETTERS_PATTERN.matcher(
                        MULTIPLE_SPACES_PATTERN.matcher(
                                NON_LETTER_PATTERN.matcher(
                                        HTML_TAGS_PATTERN.matcher(
                                                URL_PATTERN.matcher(text).replaceAll(" ")
                                        ).replaceAll(" ")
                                ).replaceAll(" ")
                        ).replaceAll(" ")
                ).replaceAll("$1$1")
        ).replaceAll(" ").trim();
    }

    /**
     * Tokenizes the text by splitting it into words and handles camelCase words.
     */
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        String[] words = text.split(" ");
        for (String word : words) {
            String[] withoutCamelCase = CAMEL_CASE_PATTERN.split(word);
            for (String token : withoutCamelCase) {
                if(token.trim().isEmpty()||token.isEmpty()){
                    continue;
                }
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
}
