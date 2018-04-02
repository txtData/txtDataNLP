/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.annotations.Text;
import de.txtData.asl.nlp.annotations.TextUnit;
import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.annotations.Annotation;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.util.dataStructures.RecursiveDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a fairly fast algorithm to match (many) dictionary entries against input text,
 * somewhat inspired by a Finite State Transducer.
 * All dictionaries are kept in memory, so if the dictionaries become really large, this isn't the right approach.
 * @param <T>
 */
public abstract class RecursiveDictionaryAnnotator<T> extends AbstractAnnotator {

    // All of the dictionary.
    // (During the matching process many partial dictionaries are create and passed around.)
    protected RecursiveDictionary<T> completeDictionary = new RecursiveDictionary<>();

    public RecursiveDictionaryAnnotator(Language lang){
        super(lang);
    }

    protected abstract List<Annotation> createAnnotations(RecursiveDictionary<T> dictionary, TextUnit atp, int start, int end);
    protected abstract List<RecursiveDictionary<T>> getMatches(Word word, RecursiveDictionary<T> dictionary);

    public void annotate(Text text){
        for (TextUnit textPiece : text.getTextUnits()){
            this.annotate(textPiece);
        }
    }

    public void annotate(TextUnit sentence){
        List<Annotation> results = new ArrayList<>();
        List<RecursiveDictionary<T>> dictionaries = new ArrayList<>();
        List<Integer> startPos = new ArrayList<>();
        for (int i=0; sentence.getWords().size()>i ;i++){
            Word word = sentence.getWords().get(i);
            if (word.surface.trim().equals("")) continue; // ignore whitespace, if present
            List<RecursiveDictionary<T>> newDictionaries = new ArrayList<>();
            List<Integer> newStartPos = new ArrayList<>();
            int j=-1;
            for (RecursiveDictionary<T> dic : dictionaries){
                j++;
                List<RecursiveDictionary<T>> newDics = this.getMatches(word, dic);
                if (newDics == null) continue;
                for (RecursiveDictionary<T> newDic : newDics){
                    if (!newDic.meanings.isEmpty()) {
                        List<Annotation> toAdd = this.createAnnotations(newDic, sentence, startPos.get(j), i);
                        results.addAll(toAdd);
                    }
                    newDictionaries.add(newDic);
                    newStartPos.add(startPos.get(j));
                }
            }
            dictionaries = newDictionaries;
            startPos = newStartPos;

            List<RecursiveDictionary<T>> rds = this.getMatches(word, this.completeDictionary);
            if (rds!=null){
                for(RecursiveDictionary<T> rd : rds) {
                    dictionaries.add(rd);
                    startPos.add(i);
                    if (!rd.meanings.isEmpty()) {
                        List<Annotation> toAdd = this.createAnnotations(rd, sentence, i, i);
                        results.addAll(toAdd);
                    }
                }
            }
        }
        sentence.addAnnotations(results);
    }


    public RecursiveDictionary<T> query(RecursiveDictionary<T> dictionary, String key){
        RecursiveDictionary<T> dic = dictionary.map.get(key);
        return dic;
    }

}
