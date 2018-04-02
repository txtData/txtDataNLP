/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.nlp.models.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a piece of text, its words and any annotations that belong to it.
 * Typically, this class is used to represent a sentence, although other use cases are possible.
 */
public class TextUnit extends AnnotationWithSubAnnotations {

    private List<Word> words = new ArrayList<>();

    public TextUnit(String surface, int starts, int ends, IAnnotationObject annotationObject){
        super(surface, starts, ends, annotationObject);
    }

    public TextUnit(Span span, IAnnotationObject annotationObject){
        super(span, annotationObject);
    }

    public void setWords(List<Word> words){
        this.words = words;
    }

    public List<Word> getWords(){
        return this.words;
    }

    public Word getTokenAtOrAfter(int index){
        Word result = null;
        int bestDiff = -1;
        for (Word s : words){
            if (s.starts >=index){
                int diff = s.starts -index;
                if (bestDiff==-1){
                    result = s;
                    bestDiff = diff;
                }else if (diff<bestDiff){
                    result = s;
                    bestDiff = diff;
                }else if (diff==bestDiff){
                    // should not happen
                    result = s;
                }
            }
        }
        return result;
    }

    public Word getTokenBefore(int index){
        Word result = null;
        int bestDiff = -1;
        for (Word s : words){
            if (s.starts <index){
                int diff = s.starts -index;
                if (bestDiff==-1){
                    result = s;
                    bestDiff = diff;
                }else if (diff>bestDiff){
                    result = s;
                    bestDiff = diff;
                }
            }
        }
        return result;
    }

    public List<Word> getTokensForAnnotation(Annotation annotation){
        return this.getTokensBetween(annotation.getSpan().starts, annotation.getSpan().ends);
    }

    public List<Word> getTokensBetween(int start, int end){
        List<Word> words = new ArrayList<>();
        for (Word word : this.getWords()){
            if (word.starts>=start && word.ends<=end){
                words.add(word);
            }
        }
        return words;
    }

    public List<Word> getTokensAfter(int start){
        List<Word> words = new ArrayList<>();
        for (Word word : this.getWords()){
            if (word.starts>=start){
                words.add(word);
            }
        }
        return words;
    }

    public List<String> getTokenSurfaces(){
        List<String> results = new ArrayList<>();
        for (Word word : this.getWords()){
            results.add(word.surface);
        }
        return results;
    }

    public String getTextSurface(int from, int to){
        if (this.getSpan().starts<=0) return this.getSurfaceText().substring(from, to);
        return this.getSurfaceText().substring(from - this.getSpan().starts, to - this.getSpan().starts);
    }

    public String toString(){
        return this.toString(true,true);
    }

    public String toString(boolean tokens, boolean annotations){
        StringBuilder sb = new StringBuilder();
        sb.append(this.getSurfaceText());
        if (tokens) {
            for (Word word : this.words) {
                sb.append("\n").append("\t" + word.prettyString());
            }
        }
        if (annotations) {
            for (Annotation anno : this.annotations) {
                sb.append("\n").append("\t").append(anno.toString());
            }
        }
        return sb.toString();
    }

}