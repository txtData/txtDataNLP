/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.tools;

import de.txtData.asl.nlp.annotations.SimpleAnnotationObject;
import de.txtData.asl.nlp.annotations.Text;
import de.txtData.asl.nlp.annotations.TextUnit;
import de.txtData.asl.nlp.annotators.AbstractCreator;
import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.nlp.models.WordFactory;

import java.util.List;

/**
 * Creator for combined OpenNLP sentence splitter, tokenizer, tagger.
 */
public class OpenNLPCreator extends AbstractCreator{

    public String modelDirectory;
    public boolean postProcess = false;
    public String knownAbbreviations;

    private OpenNLPSentenceSplitter openNLPSentenceSplitter;
    private OpenNLPTokenizer openNLPTokenizer;
    private OpenNLPTagger openNLPTagger;
    private WordFactory wordFactory;

    public OpenNLPCreator(Language language, String modelDirectory){
        super(language);
        this.modelDirectory = modelDirectory;
    }

    public OpenNLPCreator(Language language, String modelDirectory, WordFactory wordFactory){
        super(language);
        this.modelDirectory = modelDirectory;
        this.wordFactory = wordFactory;
    }

    public void initialize(){
        this.openNLPSentenceSplitter = new OpenNLPSentenceSplitter(this.language, this.modelDirectory, this.knownAbbreviations);
        this.openNLPTokenizer = new OpenNLPTokenizer(this.language, this.modelDirectory, false, this.knownAbbreviations);
        this.openNLPTokenizer.postProcess = this.postProcess;
        this.openNLPTagger = new OpenNLPTagger(this.language, this.modelDirectory);
    }

    public void setSentenceSplitter(OpenNLPSentenceSplitter openNLPSentenceSplitter){
        this.openNLPSentenceSplitter = openNLPSentenceSplitter;
    }

    public void setTokenizer(OpenNLPTokenizer openNLPTokenizer){
        this.openNLPTokenizer = openNLPTokenizer;
    }

    public void setTagger(OpenNLPTagger openNLPTagger){
        this.openNLPTagger = openNLPTagger;
    }

    public OpenNLPSentenceSplitter getSentenceSplitter(){
        return this.openNLPSentenceSplitter;
    }

    public OpenNLPTokenizer getTokenizer(){
        return this.openNLPTokenizer;
    }

    public OpenNLPTagger getTagger(){
        return this.openNLPTagger;
    }

    public Text createText(String surface){
        Text result = new Text(surface);
        List<Span> sentences = this.openNLPSentenceSplitter.getSentencesAsSpans(surface);
        for (Span sentence : sentences){
            TextUnit textPiece = this.create(sentence.surface, sentence.starts, sentence.ends);
            result.addAnnotation(textPiece);
        }
        return result;
    }


    public TextUnit create(String sentence){
        return this.create(sentence, -1, -1);
    }

    public TextUnit create(String sentence, int from, int to){
        List<Span> spans = openNLPTokenizer.getTokensAsSpans(sentence);
        List<Word> words = openNLPTagger.getTaggedWords(spans);
        if (from>0) this.applyOffset(words, from);
        TextUnit textPiece = new TextUnit(sentence, from, to, new SimpleAnnotationObject("SENTENCE"));
        textPiece.setWords(words);
        this.extendWordInfo(words);
        return textPiece;
    }

    private void extendWordInfo(List<Word> words){
        if (wordFactory==null) return;
        for (Word word : words) {
            Word tempWord = wordFactory.createWord(word.surface);
            if (word.pos == null && tempWord.pos != null) {
                word.pos = tempWord.pos;
            }
            if (word.idf == null && tempWord.idf != null) {
                word.idf = tempWord.idf;
            }
            if ((word.types == null || word.types.isEmpty()) && tempWord.types != null) {
                word.types = tempWord.types;
            }
            if (word.root == null && tempWord.root != null) {
                word.root = tempWord.root;
            }
        }
    }

    private void applyOffset(List<Word> words, int offset){
        for (Word word : words){
            word.starts += offset;
            word.ends += offset;
        }
    }

    public void annotate(TextUnit textPiece){

    }

}
