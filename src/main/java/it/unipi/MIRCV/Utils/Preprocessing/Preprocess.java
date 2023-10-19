package it.unipi.MIRCV.Utils.Preprocessing;
import ca.rmen.porterstemmer.PorterStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Preprocess {
    private static String Path_to_File="prova.tsv";
    private static final String Path_to_Stopwords="stopWord/stopword.txt";
    private static final PorterStemmer PStermmer=new PorterStemmer();
    private static final String URL_REGEX = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private static final String HTML_TAGS_REGEX = "<[^>]+>";
    private static final String NON_LETTER_REGEX = "[^a-zA-Z ]";
    private static final String MULTIPLE_SPACES_REGEX = " +";
    private static final String CONSECUTIVE_LETTERS_REGEX = "(.)\\1{2,}";
    private static final String CAMEL_CASE_REGEX = "(?<=[a-z])(?=[A-Z])";

    private static ArrayList<String> stopwords=new ArrayList<>();
    public static String [] parserLineCollection(String line){
        return line.split("\t");
    }
    public static String textCleaning(String text){
        // Remove URLs
        String textWithoutURLs = text.replaceAll(URL_REGEX, " ");

        // Remove HTML tags
        String textWithoutHTMLTags = textWithoutURLs.replaceAll(HTML_TAGS_REGEX, " ");

        // Remove non-letter characters
        String textWithoutNonLetters = textWithoutHTMLTags.replaceAll(NON_LETTER_REGEX, " ");

        // Remove sequential spaces
        String textWithoutMultipleSpaces = textWithoutNonLetters.replaceAll(MULTIPLE_SPACES_REGEX, " ");

        // Remove consecutive letters and spaces at begin and end
        return textWithoutMultipleSpaces.replaceAll(CONSECUTIVE_LETTERS_REGEX, "$1$1").trim();

    }

    public static String [] tokenizer(String text){
        ArrayList<String> finalTokens=new ArrayList<>();
        String[] tokens=text.split(" ");
        for(String word: tokens){
            String [] withoutCamelCase=word.split(CAMEL_CASE_REGEX);
            for(String subToken:withoutCamelCase){
                finalTokens.add(subToken.toLowerCase(Locale.ROOT));
            }
        }
        return finalTokens.toArray(new String[finalTokens.size()]);
    }

    public static String[] stemmer(String [] tokens){
        if(tokens.length==0)
            return null;
        ArrayList<String> stemmedTokens=new ArrayList<>();
        for(String word:tokens){
            stemmedTokens.add(PStermmer.stemWord(word));
        }
        return stemmedTokens.toArray(new String[stemmedTokens.size()]);
    }
    public static String[] stopwordRemoval(String [] tokens){
        ArrayList<String> tokensWithouStopwords=new ArrayList<>();
        if(stopwords.isEmpty()){
            try(BufferedReader bufferedReader=new BufferedReader(new FileReader(Path_to_Stopwords))){
                String line;
                while((line=bufferedReader.readLine())!=null){
                    stopwords.add(line);
                }

            }catch(IOException e){
                System.out.println("problems with opening the stopword file");
                e.printStackTrace();
            }
        }
        for(String word :tokens){
            if(!stopwords.contains(word)){
                tokensWithouStopwords.add(word);
            }
        }
        return tokensWithouStopwords.toArray(new String[tokensWithouStopwords.size()]);
    }
}
