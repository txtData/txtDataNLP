/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.tools;

import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.util.files.WordList;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for OpenNLP sentence splitter.
 */
public class OpenNLPSentenceSplitter{

    public Language language;
    public String modelDirectory;
    public String knownAbbreviationsFile = null;

    private SentenceDetectorME sentenceDetector;
    private WordList knownAbbreviations;

    public OpenNLPSentenceSplitter(Language language, String modelDirectory){
        this.language = language;
        this.modelDirectory = modelDirectory;
        this.initialize();
    }

    public OpenNLPSentenceSplitter(Language language, String modelDirectory, String knownAbbreviations){
        this.language = language;
        this.modelDirectory = modelDirectory;
        this.knownAbbreviationsFile = knownAbbreviations;
        this.initialize();
    }

   private void initialize(){
        try{
            SentenceModel sentenceModel = new SentenceModel(new FileInputStream(this.modelDirectory+"/"+this.language.getCode()+"-sent.bin"));
            sentenceDetector = new SentenceDetectorME(sentenceModel);
            if (this.knownAbbreviationsFile !=null){
                this.knownAbbreviations = new WordList(this.knownAbbreviationsFile, null, false, "//");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getSentences(String text){
        text = normalize(text);
        String[] sentences = sentenceDetector.sentDetect(text);
        return Arrays.asList(sentences);
    }

    public List<Span> getSentencesAsSpans(String text){
        List<Span> results = new ArrayList<>();
        text = normalize(text);
        opennlp.tools.util.Span[]   spans     = sentenceDetector.sentPosDetect(text);
        for (opennlp.tools.util.Span oSpan : spans){
            String surface = text.substring(oSpan.getStart(), oSpan.getEnd());
            Span span = new Span(surface, oSpan.getStart(), oSpan.getEnd());
            results.add(span);
        }
        results = this.postProcess(results, text);
        return results;
    }

    private List<Span> postProcess(List<Span> spans, String text){
        if (this.knownAbbreviations==null) return spans;
        for (int i=0; i<spans.size(); i++){
            String surface = spans.get(i).surface;
            if (surface==null) continue;
            int index = surface.lastIndexOf(" ");
            if (index!=-1){
                String lastWord = surface.substring(index+1,surface.length());
                boolean onList = this.knownAbbreviations.isOnList(lastWord);
                if (onList && spans.size()-1>i){
                    int starts = spans.get(i).starts;
                    int ends = spans.get(i+1).ends;
                    String sentence = text.substring(starts,ends);
                    spans.remove(i);
                    spans.remove(i);
                    spans.add(i,new Span(sentence,starts,ends));
                    i=i-1;
                }
            }
        }
        return spans;
    }

    private String normalize(String text){
        text = text.replaceAll("’","'");
        text = text.replaceAll("“"," ");
        return text;
    }

}
