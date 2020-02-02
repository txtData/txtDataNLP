package de.txtdata.asl.nlp.annotators;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.nlp.tools.OpenNLPSentenceSplitter;
import de.txtdata.asl.nlp.tools.OpenNLPTagger;
import de.txtdata.asl.nlp.tools.OpenNLPTokenizer;

import java.util.ArrayList;
import java.util.List;

public class ChunkerPipeline extends AbstractCreator {

    public String openNlpModelDirectory = ".\\data\\models\\";

    private OpenNLPSentenceSplitter openNLPSentenceSplitter;
    private OpenNLPTokenizer openNLPTokenizer;
    private OpenNLPTagger openNLPTagger;

    public static void main(String[] args) {
        ChunkerPipeline pipeline = new ChunkerPipeline(Language.ENGLISH);
        List<TextUnit> analyzedSentences = pipeline.createFromText("He lives in London and she in New York City.");
        for (TextUnit textUnit : analyzedSentences) {
            System.out.println(textUnit);
        }
    }

    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public ChunkerPipeline(Language language) {
        super(language);
        this.initialize();
    }

    public ChunkerPipeline(Language language, boolean useTagger) {
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
        this.chunk(sentence);

    }

    public void chunk(TextUnit sentence) {
        int annotationStart = -1;
        Word lastWord = null;
        for (Word word : sentence.getWords()){
            if (word.getPos()!=null && word.getPos().startsWith("N")){
                if (annotationStart==-1) annotationStart = word.getStarts();
            }else if (annotationStart!=-1){
                String surface = sentence.getSurfaceText(annotationStart, lastWord.getEnds());
                Annotation annotation = new Annotation(surface, annotationStart, lastWord.getEnds(), "NounChunk");
                sentence.addAnnotation(annotation);
                annotationStart = -1;
            }
            lastWord = word;
        }
        if (annotationStart!=-1) {
            String surface = sentence.getSurfaceText(annotationStart, lastWord.getEnds());
            Annotation annotation = new Annotation(surface, annotationStart, lastWord.getEnds(), "NounChunk");
            sentence.addAnnotation(annotation);
        }
    }
}
