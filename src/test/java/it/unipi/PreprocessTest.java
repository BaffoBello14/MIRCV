package it.unipi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.unipi.MIRCV.Utils.Preprocessing.*;

import java.util.List;

public class PreprocessTest {
    
    @Test
    public void testPreprocess() {
        String[] inputLines = {
            "This is a <b>sample</b> content with a URL: http://www.example.com and some repeated letters: aaaa and CamelCaseWord.",
            "The benefits of sustainable technologies are becoming evident. Read more on GreenTech's official website: http://greentech.com."
        };

        String[] expectedCleanedTexts = {
            "This is a sample content with a URL and some repeated letters aa and CamelCaseWord",
            "The benefits of sustainable technologies are becoming evident Read more on GreenTech s official website"
        };

        String[][] expectedTokens = {
            {"this", "is", "a", "sample", "content", "with", "a", "url", "and", "some", "repeated", "letters", "aa", "and", "camel", "case", "word"},
            {"the", "benefits", "of", "sustainable", "technologies", "are", "becoming", "evident", "read", "more", "on", "green", "tech", "s", "official", "website"}
        };

        String[][] expectedTokensWithoutStopwords = {
            {"sample", "content", "url", "repeated", "letters", "aa", "camel", "word"},
            {"benefits", "sustainable", "technologies", "evident", "read", "green", "tech", "official"}
        };

        String[][] expectedStemmedTokens = {
            {"sampl", "content", "url", "repeat", "letter", "aa", "camel", "word"},
            {"benefit", "sustain", "technolog", "evid", "read", "green", "tech", "offici"}
        };

        for (int i = 0; i < inputLines.length; i++) {
            String text = inputLines[i];

            // Testing cleanText method
            String cleanedText = Preprocess.cleanText(text);
            assertEquals(expectedCleanedTexts[i], cleanedText);

            // Testing tokenize method
            List<String> tokens = Preprocess.tokenize(cleanedText);
            assertArrayEquals(expectedTokens[i], tokens.toArray(new String[0]));

            // Testing removeStopwords method
            List<String> tokensWithoutStopwords = Preprocess.removeStopwords(tokens);
            assertArrayEquals(expectedTokensWithoutStopwords[i], tokensWithoutStopwords.toArray(new String[0]));

            // Testing applyStemming method
            List<String> stemmedTokens = Preprocess.applyStemming(tokensWithoutStopwords);
            assertArrayEquals(expectedStemmedTokens[i], stemmedTokens.toArray(new String[0]));
        }
    }
}
