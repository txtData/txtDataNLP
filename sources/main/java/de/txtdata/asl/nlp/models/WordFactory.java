/*
 *  Copyright 2013-2020 Michael Kaisser
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

import de.txtdata.asl.nlp.tools.FrequentWordList;
import de.txtdata.asl.nlp.tools.WordList;
import de.txtdata.asl.nlp.tools.AbstractStemmer;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory that creates <code>Word</code>s.
 */
public class WordFactory {

    public AbstractStemmer stemmer;
    public WordList stopWords;
    public FrequentWordList frequentWordList;

    private Language lang;

    public WordFactory(Language lang){
        this.lang = lang;
    }

    public WordFactory(Language lang, WordList stopWords){
        this(lang);
        this.stopWords = stopWords;
    }

    public WordFactory(Language lang, WordList stopWords, FrequentWordList fwl){
        this(lang);
        this.stopWords = stopWords;
        this.frequentWordList = fwl;
    }

    public WordFactory(Language lang, WordList stopWords, AbstractStemmer stemmer){
        this(lang);
        this.stopWords = stopWords;
        this.stemmer = stemmer;
    }

    public WordFactory(Language lang, WordList stopWords, FrequentWordList fwl, AbstractStemmer stemmer){
        this(lang);
        this.stopWords = stopWords;
        this.frequentWordList = fwl;
        this.stemmer = stemmer;
    }

    // overrides types, idf and root
    public void augment(Word word){
        Word w2 = this.createWord(word.getSurface());
        if (w2.getTypes()!=null && !w2.getTypes().isEmpty()){
            word.setTypes(w2.getTypes());
        }
        word.setIdf(w2.getIdf());
        word.setRoot(w2.getRoot());
    }

    public List<Word> createWords(List<Span> spans){
        List<Word> words = new ArrayList<>();
        for (Span span : spans){
            Word word = this.createWord(span.getSurface(), span.getStarts(), span.getEnds());
            words.add(word);
        }
        return words;
    }

    public Word createWord(String surface, int start, int end){
        Word word = this.createWord(surface);
        word.setStarts(start);
        word.setEnds(end);
        return word;
    }

    public Word createWord(String surface, String pos, int starts, int ends){
        Word word = this.createWord(surface, starts, ends);
        if (!word.isType(Word.WHITESPACE)) {
            word.setPOS(pos);
        }
        return word;
    }

    public Word createWord(String surface){
        Word word = new Word(surface);
        if (surface.length()==0){

        }else if (surface.length()==1) {
            Character c = surface.charAt(0);
            if (Character.isWhitespace(c)) {
                word.addType(Word.WHITESPACE);
            }else if (Word.getPunctuations().contains(surface)) {
                word.addType(Word.PUNCTUATION);
            }
        }
        if (stopWords!=null && stopWords.isOnList(surface)){
            word.addType(Word.STOPWORD);
        }
        if (firstLetterIsUpperCase(surface)) {
            word.addType(Word.UPPERCASE);
        }else if (firstLetterIsLowerCase(surface)) {
            word.addType(Word.LOWERCASE);
        }else if (this.isPunctuation(surface)) {
            if (!word.getTypes().contains(Word.PUNCTUATION)) {
                word.addType(Word.PUNCTUATION);
            }
        }
        if (surface.trim().isEmpty() && !word.isType(Word.WHITESPACE)){
            word.addType(Word.WHITESPACE);
        }
        if (surface.contains("\t") || surface.contains("\n")){
            word.addType(Word.SEPARATOR);
        }
        try {
            Double d = Double.parseDouble(surface);
            word.addType(Word.NUMBER);
        }catch(NumberFormatException nfe){
            // nothing to do
        }
        if (word.isType(Word.WHITESPACE) || word.isType(Word.PUNCTUATION)){
            return word;
        }

        if (frequentWordList!=null) {
            String lookUp = surface;
            if ("EN".equalsIgnoreCase(this.lang.getCode())){
                if (surface.endsWith("'s")){
                    lookUp = surface.substring(0,surface.length()-2);
                }else if (surface.endsWith("'")){
                    lookUp = surface.substring(0,surface.length()-1);
                }
            }
            FrequentWordList.FrequentWord fw = frequentWordList.lookUp(lookUp);
            if (fw==null) word.setIdf(frequentWordList.getIDFApproximation(0));
            else word.setIdf(fw.getIDFApproximation());
        }
        if (stemmer!=null){
            word.setRoot(stemmer.stem(surface));
        }
        return word;
    }

    private boolean isPunctuation(String s){
        if (Word.getPunctuations().contains(s)) return true;
        if (s.length()==1) return false;
        String[] parts = s.split("");
        for (String part : parts){
            if (!Word.getPunctuations().contains(part)){
                return false;
            }
        }
        return true;
    }

    private static boolean firstLetterIsUpperCase(String s){
        if (s.length()==0) return false;
        return Character.isUpperCase(s.charAt(0));
    }

    private static boolean firstLetterIsLowerCase(String s){
        if (s.length()==0) return false;
        return Character.isLowerCase(s.charAt(0));
    }
}
