/*
 *  Copyright 2013-2018 Michael Kaisser
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
 *  See also https://github.com/txtData/nlp
 */

package de.txtdata.asl.nlp.models;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.annotations.AnnotationList;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a piece of text, its words and any annotations that belong to it.
 * Typically, this class is used to represent a sentence, although other use cases are possible.
 */
public class TextUnit{

    protected String surface = null;
    protected AnnotationList annotations = new AnnotationList();
    private List<Word> words = new ArrayList<>();

    public TextUnit(String surface){
        this.surface = surface;
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

    public void addAnnotation(Annotation annotation){
        this.annotations.add(annotation);
    }

    public void addAnnotations(List<Annotation> annotations){
        this.annotations.addAll(annotations);
    }

    public AnnotationList getAnnotations() {
        return annotations;
    }

    public AnnotationList getAnnotations(String type){
        return this.annotations.getAnnotations(type);
    }

    public void setAnnotations(List<Annotation> annotations){
        this.annotations = new AnnotationList(annotations);
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

    public String getSurfaceText(){
        return this.surface;
    }

    public String getTextSurface(int from, int to){
        return this.getSurfaceText().substring(from, to);
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