package de.txtdata.asl.examples;

import de.txtdata.asl.nlp.annotators.AbstractCreator;
import de.txtdata.asl.nlp.annotators.WhitelistAnnotator;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.nlp.tools.OpenNLPSentenceSplitter;
import de.txtdata.asl.nlp.tools.OpenNLPTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class annotates names of German cities in German texts.
 * It demonstrates how annotations can be created by using resource files.
 * The resource file used here is found "in data/dictionaries/examples/germanCities_DE.txt".
 */
public class WhitelistPipeline extends AbstractCreator {

    private String exampleSentence = "In München und Köln leben mehr Menschen als in Schwerin.";

    private String openNlpModelDirectory = ".\\data\\models\\";
    private String dictionariesLocation = ".\\data\\dictionaries\\examples\\germanCities_DE.txt";

    private OpenNLPSentenceSplitter openNLPSentenceSplitter;
    private OpenNLPTokenizer openNLPTokenizer;
    private WhitelistAnnotator whitelistAnnotator;

    public static void main(String[] args) {
        WhitelistPipeline pipeline = new WhitelistPipeline(Language.GERMAN);
        List<TextUnit> analyzedSentences = pipeline.createFromText(pipeline.exampleSentence);
        for (TextUnit textUnit : analyzedSentences) {
            System.out.println(textUnit.toString(true,true));
        }
    }

    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public WhitelistPipeline(Language language) {
        super(language);
        this.initialize();
    }

    /**
     * Perform initialization: Load OpenNLP tokenizer etc.
     */
    public void initialize() {
        this.openNLPSentenceSplitter = new OpenNLPSentenceSplitter(this.language, this.openNlpModelDirectory);
        this.openNLPTokenizer        = new OpenNLPTokenizer(this.getLanguage(), this.openNlpModelDirectory);
        this.whitelistAnnotator      = new WhitelistAnnotator(this.language, this.dictionariesLocation, true);
    }

    public List<TextUnit> createFromText(String text) {
        List<String> sentences = this.openNLPSentenceSplitter.getSentences(text);
        List<TextUnit> results = new ArrayList<>();
        for (String sentence : sentences) {
            TextUnit tu = new TextUnit(sentence);
            this.annotate(tu);
            results.add(tu);
        }
        return results;
    }

    /**
     * Creates a <code>TextUnit</code> from a string.
     * @param sentence The text to analyse. Output of sentence splitter. Should be one sentence.
     * @return A TextUnit containing an analysed representation of the input text.
     */
    public TextUnit create(String sentence) {
        TextUnit tu = new TextUnit(sentence);
        this.annotate(tu);
        return tu;
    }


    public void annotate(TextUnit sentence) {
        List<Word> words = this.openNLPTokenizer.getTokensAsWords(sentence.getSurfaceText());
        sentence.setWords(words);
        this.whitelistAnnotator.annotate(sentence);
    }

}
