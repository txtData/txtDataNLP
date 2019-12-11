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
        Word w2 = this.createWord(word.surface);
        if (w2.types!=null && !w2.types.isEmpty()){
            word.types = w2.types;
        }
        word.idf = w2.idf;
        word.root = w2.root;
    }

    public List<Word> createWords(List<Span> spans){
        List<Word> words = new ArrayList<>();
        for (Span span : spans){
            Word word = this.createWord(span.surface, span.starts, span.ends);
            words.add(word);
        }
        return words;
    }

    public Word createWord(String surface, int start, int end){
        Word word = this.createWord(surface);
        word.starts = start;
        word.ends = end;
        return word;
    }

    public Word createWord(String surface, String pos, int start, int end){
        Word word = this.createWord(surface);
        if (!word.isType(Word.WHITESPACE)) {
            word.pos = pos;
        }
        word.starts = start;
        word.ends = end;
        return word;
    }

    public Word createWord(String surface){
        Word word = new Word(surface);
        if (surface.length()==0){

        }else if (surface.length()==1) {
            Character c = surface.charAt(0);
            if (Character.isWhitespace(c)) {
                word.addType(Word.WHITESPACE);
            }else if (Word.PUNCTUATIONS.contains(surface)) {
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
            if (!word.types.contains(Word.PUNCTUATION)) {
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
            if (fw==null) word.idf = frequentWordList.getIDFApproximation(0);
            else word.idf = fw.getIDFApproximation();
        }
        if (stemmer!=null){
            word.root = stemmer.stem(surface);
        }
        return word;
    }

    private boolean isPunctuation(String s){
        if (Word.PUNCTUATIONS.contains(s)) return true;
        if (s.length()==1) return false;
        String[] parts = s.split("");
        for (String part : parts){
            if (!Word.PUNCTUATIONS.contains(part)){
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
