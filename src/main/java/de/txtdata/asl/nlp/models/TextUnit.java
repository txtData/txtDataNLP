/*
 *  Copyright 2020 Michael Kaisser
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See also https://github.com/txtData/txtDataNLP
 */

package de.txtdata.asl.nlp.models;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.annotations.AnnotationList;
import de.txtdata.asl.nlp.annotations.IAnnotationObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a piece of text, its words and any annotations that belong to it.
 * Often, this class is used to represent a sentence, although other use cases are possible.
 */
public class TextUnit{

    private String surface;
    private List<Word> words = new ArrayList<>();
    private AnnotationList annotations = new AnnotationList();


    public TextUnit(String surface){
        this.surface = surface;
    }

    public TextUnit(String surface, List<Word> words){
        this.surface = surface;
        this.words = words;
    }

    public TextUnit(String surface, List<Word> words, AnnotationList annotations){
        this.surface = surface;
        this.words = words;
        this.annotations = annotations;
    }


    public String getSurfaceText(){
        return this.surface;
    }

    public String getSurfaceText(int from, int to){
        return this.getSurfaceText().substring(from, to);
    }


    public void setWords(List<Word> words){
        this.words = words;
    }

    public List<Word> getWords(){
        return this.words;
    }

    public Word getWordAtOrAfter(int index){
        Word result = null;
        int bestDiff = -1;
        for (Word s : words){
            if (s.getStarts() >=index){
                int diff = s.getStarts() -index;
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

    public Word getWordBefore(int index){
        Word result = null;
        int bestDiff = -1;
        for (Word s : words){
            if (s.getStarts() <index){
                int diff = s.getStarts() -index;
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

    public List<Word> getWordsForAnnotation(Annotation annotation){
        return this.getWordsBetween(annotation.getStarts(), annotation.getEnds());
    }

    public List<Word> getWordsBetween(int start, int end){
        List<Word> words = new ArrayList<>();
        for (Word word : this.getWords()){
            if (word.getStarts()>=start && word.getEnds()<=end){
                words.add(word);
            }
        }
        return words;
    }

    public List<Word> getWordsAfter(int start){
        List<Word> words = new ArrayList<>();
        for (Word word : this.getWords()){
            if (word.getStarts()>=start){
                words.add(word);
            }
        }
        return words;
    }

    public List<String> getWordSurfaces(){
        List<String> results = new ArrayList<>();
        for (Word word : this.getWords()){
            results.add(word.getSurface());
        }
        return results;
    }


    public AnnotationList getAnnotations() {
        return this.annotations;
    }

    public AnnotationList getAnnotations(String type){
        return this.annotations.getAnnotations(type);
    }

    public <T extends IAnnotationObject> AnnotationList getAnnotations(Class<T> classOfT){
        return this.getAnnotations().getAnnotations(classOfT);
    }

    public void setAnnotations(List<Annotation> annotations){
        this.annotations = new AnnotationList(annotations);
    }

    public void addAnnotation(Annotation annotation){
        this.annotations.add(annotation);
    }

    public void addAnnotations(List<Annotation> annotations){
        this.annotations.addAll(annotations);
    }

    public boolean removeAnnotation(Annotation annotation){
        return this.annotations.remove(annotation);
    }

    public boolean removeAnnotations(List<Annotation> toRemove){
        return this.annotations.removeAll(toRemove);
    }

    public boolean hasAnnotations(){
        return !this.annotations.isEmpty();
    }

    public boolean hasAnnotations(String type){
        return !this.getAnnotations(type).isEmpty();
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