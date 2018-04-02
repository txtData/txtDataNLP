/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

import de.txtData.asl.nlp.models.Word;
import java.util.ArrayList;
import java.util.List;

/**
 * Most simple way to represent a text, e.g. a document.
 * Most use cases will extend this class.
 */
public class Text extends AnnotationWithSubAnnotations{

    public static final String TYPE = "TEXT";

    public Text(String surface){
        super(surface, 0, surface.length(), Text.TYPE);
    }

    public List<TextUnit> getTextUnits(){
        List<TextUnit> results = new ArrayList<>();
        for (Annotation annotation : this.getAnnotations()){
            if (annotation instanceof TextUnit){
                results.add((TextUnit)annotation);
            }
        }
        return results;
    }

    public List<Word> getWordsFromTo(int from, int to){
        List<Word> results = new ArrayList<>();
        List<TextUnit> textPieces = this.getTextUnits();
        for (TextUnit textPiece : textPieces){
            List<Word> words = textPiece.getTokensBetween(from, to);
            if (words!=null && !words.isEmpty()) results.addAll(words);
        }
        return results;
    }

    public List<Word> getWordsFrom(int from){
        List<TextUnit> textPieces = this.getTextUnits();
        for (TextUnit textPiece : textPieces){
            if (textPiece.getSpan().ends<from) continue;
            List<Word> words = textPiece.getTokensAfter(from);
            if (words!=null && !words.isEmpty()) return words;
        }
        return null;
    }

    public String toString(){
        return this.toString(true);
    }

    public String toString(boolean annotations){
        StringBuilder sb = new StringBuilder();
        sb.append(this.getSurfaceText());
        if (annotations) {
            for (Annotation anno : this.annotations) {
                sb.append("\n").append("\t").append(anno.toString());
            }
        }
        return sb.toString();
    }
}
