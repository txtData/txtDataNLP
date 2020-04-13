package de.txtdata.asl.examples;

import de.txtdata.asl.nlp.annotators.AbstractCreator;
import de.txtdata.asl.nlp.annotators.PosPatternAnnotator;
import de.txtdata.asl.nlp.annotators.SubsumedAnnotationsRemover;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.nlp.tools.OpenNLPSentenceSplitter;
import de.txtdata.asl.nlp.tools.OpenNLPTagger;
import de.txtdata.asl.nlp.tools.OpenNLPTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class uses POS patterns to annotate a few selected German words in their morphological variants.
 * It demonstrates how annotations can be created by using resource files.
 * The resource file used here is found "in data/dictionaries/examples/germanRules_DE.txt".
 */
public class POSPatternPipeline extends AbstractCreator {

    private String exampleSentence = "Im Dorf, in größeren Dörfern und auf den Städten wird von Trollen, die sich trollen am Städtetag viel getrunken.";

    private String openNlpModelDirectory = ".\\data\\models\\";
    private String dictionariesLocation = ".\\data\\dictionaries\\examples\\germanRules_DE.txt";

    private OpenNLPSentenceSplitter openNLPSentenceSplitter;
    private OpenNLPTokenizer openNLPTokenizer;
    private OpenNLPTagger openNLPTagger;
    private PosPatternAnnotator posPatternAnnotator;
    private SubsumedAnnotationsRemover subsumedAnnotationsRemover;

    public static void main(String[] args) {
        POSPatternPipeline pipeline = new POSPatternPipeline(Language.GERMAN);
        List<TextUnit> analyzedSentences = pipeline.createFromText(pipeline.exampleSentence);
        for (TextUnit textUnit : analyzedSentences) {
            System.out.println(textUnit);
        }
    }

    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public POSPatternPipeline(Language language) {
        super(language);
        this.initialize();
    }

    /**
     * Perform initialization: Load OpenNLP tokenizer etc.
     */
    public void initialize() {
        this.openNLPSentenceSplitter = new OpenNLPSentenceSplitter(this.language, this.openNlpModelDirectory);
        this.openNLPTokenizer = new OpenNLPTokenizer(this.getLanguage(), this.openNlpModelDirectory);
        this.openNLPTagger = new OpenNLPTagger(this.getLanguage(), this.openNlpModelDirectory);
        this.posPatternAnnotator = new PosPatternAnnotator(this.language, this.dictionariesLocation, true);
        this.subsumedAnnotationsRemover = new SubsumedAnnotationsRemover(this.language, "germanWordTypes");
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
        List<Span> tokens = this.openNLPTokenizer.getTokensAsSpans(sentence.getSurfaceText());
        List<Word> words = this.openNLPTagger.getTaggedWords(tokens);
        sentence.setWords(words);
        this.posPatternAnnotator.annotate(sentence);
        this.subsumedAnnotationsRemover.annotate(sentence);
    }

}
