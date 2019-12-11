/*
 *  Copyright 2013-2019 Michael Kaisser
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
package de.txtData.asl.nlp.models;

import de.txtData.asl.nlp.annotations.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NGram {

    protected List<Word> words;
    private String surface = null;

    public NGram(){
        this.words = new ArrayList<>();
    }

    public NGram(List<Word> words){
        this.words = new ArrayList<>(words);
        this.generateSurface();
    }

    public NGram(NGram nGram){
        this.words = nGram.words;
        this.generateSurface();
    }

    public static List<NGram> fromText(TextUnit textUnit, int minLength, int maxLength, boolean lowercase){
        List<NGram> nGrams = new ArrayList<>();
        for (int i=0; i<textUnit.getWords().size(); i++){
            List<Word> nGram = new ArrayList<>();
            for (int j=i; j<textUnit.getWords().size() && j<i+minLength; j++){
                Word word = new Word(textUnit.getWords().get(j));
                if (lowercase) word.surface = word.surface.toLowerCase();
                nGram.add(word);
            }
            if (nGram.size()<minLength) continue;
            nGrams.add(new NGram(nGram));
            for (int j=i+minLength; j<textUnit.getWords().size() && j<i+maxLength; j++){
                Word word = new Word(textUnit.getWords().get(j));
                if (lowercase) word.surface = word.surface.toLowerCase();
                nGram.add(word);
                nGrams.add(new NGram(nGram));
            }
        }
        return nGrams;
    }

    public void annotateText(TextUnit textUnit){
        int pos = 0;
        Word start = textUnit.getWords().get(0);
        for (int i=0; i<textUnit.getWords().size(); i++){
            Word sentenceWord = textUnit.getWords().get(i);
            Word nGramWord = this.words.get(pos);
            if (sentenceWord.equals(nGramWord)){
                if (this.words.size()-1==pos){
                    Annotation annotation = new Annotation(this.surface, start.starts, sentenceWord.ends, "NGram");
                    textUnit.addAnnotation(annotation);
                    i = (i - pos)+1;
                    pos = 0;
                }
                pos++;
            }else if (i+1<textUnit.getWords().size()){
                start = textUnit.getWords().get(i+1);
                pos = 0;
            }
        }
    }

    public int getFromCharacter(){
        if (words==null || words.isEmpty()) return -1;
        return words.get(0).starts;
    }

    public int getToCharacter(){
        if (words==null || words.isEmpty()) return -1;
        return words.get(words.size()-1).ends;
    }

    public Word getFirstWord(){
        return words.get(0);
    }

    public Word getLastWord(){
        return words.get(words.size()-1);
    }

    @Override
    public String toString(){
        return this.toString(false, false);
    }

    public String toString(boolean pos, boolean tags){
        if (this.surface==null) {
            generateSurface();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Word word : words){
            stringBuilder.append(word.surface);
            if (pos)
                stringBuilder.append("/").append(word.pos);
            if (tags)
                stringBuilder.append("/").append(word.types);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    // Please note: Equality is computed strictly on the ngram's surface structure.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGram nGram = (NGram) o;
        if (this.getSurface() != null ? !this.getSurface().equals(nGram.getSurface()) : nGram.getSurface() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.getSurface() != null ? this.getSurface().hashCode() : 0;
    }

    public boolean sharesWordWith(NGram other){
        for (Word w1 : this.words){
            for (Word w2 : other.words){
                if (w1.equals(w2)) return true;
            }
        }
        return false;
    }

    public boolean contains(NGram other){
        return Collections.indexOfSubList(this.words , other.words)!=-1;
    }

    public List<Word> getWords(){
        List<Word> results = new ArrayList<>();
        for (Word word : words){
            results.add(word);
        }
        return results;
    }

    public String getSurface(){
        if (this.surface==null) generateSurface();
        return this.surface;
    }

    public List<String> getSurfaceAsList(){
        List<String> results = new ArrayList<>();
        for (Word word : words){
            results.add(word.surface);
        }
        return results;
    }

    public int getWordCount(){
        return this.getWords().size();
    }

    public boolean containsWordType(String type){
        for (Word word : this.words){
            if (word.isType(type)){
                return true;
            }
        }
        return false;
    }

    public boolean containsWord(String surface){
        for (Word word : this.words){
            if (word.surface.equals(surface)){
                return true;
            }
        }
        return false;
    }

    protected void generateSurface(){
        StringBuffer sb = new StringBuffer();
        for (Word word : words){
            sb.append(word).append(" ");
        }
        this.surface = sb.toString().trim();
    }

    private String getStringWithTags(){
        StringBuffer sb = new StringBuffer();
        for (Word word : words){
            sb.append(word);
            if (word.pos!=null){
                sb.append("/").append(word.pos);
            }else if (!word.isType(Word.WHITESPACE)){
                sb.append("/").append("-");
            }
        }
        return sb.toString();
    }
}
