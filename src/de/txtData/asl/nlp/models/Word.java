/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.models;

import de.txtData.asl.util.misc.PrettyString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation for a word.
 * Note that this class has fields for start and end positions of the word in a text, however these are not used when
 * computing equality. Two words are equals if their surfaces are equal, there are no other requirements.
 */
public class Word implements Serializable{

    public static String UNSET       = "UNSET";
    public static String STOPWORD    = "STOP";
    public static String UPPERCASE   = "UPPER";
    public static String LOWERCASE   = "LOWER";
    public static String WHITESPACE  = "WHITE";
    public static String PUNCTUATION = "PUNCT";
    public static String NUMBER      = "NUMB";
    public static String SEPARATOR   = "SEP"; // /t and /n

    public static String PUNCTUATIONS =  ",.:;-!?#/\"'’«»„“()[]+-•–\"*+«´//«»%…";

    public String surface;
    public String root;
    public String pos;
    public String morph;

    public int starts = -1;
    public int ends = -1;

    public List<String> types = new ArrayList<>();
    public Double idf = null;

    public Word(String surface){
        this.surface = surface;
    }

    public String toString(){
        return this.surface;
    }

    public String toString(boolean extended){
        if (!extended) return this.surface;
        StringBuilder sb = new StringBuilder(this.surface);
        sb.append("/").append(this.types);
        if (root !=null){
            sb.append("/").append(root);
        }
        if (pos!=null){
            sb.append("/").append(pos);
        }
        if (morph!=null) {
            sb.append("/").append(morph);
        }
        if (idf!=null && idf!=-1.0) {
            sb.append("/").append(idf);
        }
        if (starts !=-1 && ends !=-1){
            sb.append(" (").append(starts).append("-").append(ends).append(")");
        }
        return sb.toString();
    }

    public String prettyString(){
        StringBuilder sb = new StringBuilder(PrettyString.create(this.surface,20));
        if (root !=null){
            sb.append(PrettyString.create(this.root,20));
        }
        if (pos!=null){
            sb.append(PrettyString.create(this.pos,10));
        }
        if (morph!=null){
            sb.append(PrettyString.create(this.morph,20));
        }

        sb.append(PrettyString.create(this.typeString(),12));
        if (starts !=-1 && ends !=-1){
            String fromTo ="("+ starts +"-"+ ends +")";
            sb.append(PrettyString.create(fromTo,12));
        }
        if (idf!=null && idf!=-1.0) {
            sb.append(PrettyString.create(this.idf,1,7)).append("  ");
        }
        return sb.toString();
    }

    public boolean isType(String type){
        if (this.types.contains(type)) return true;
        return false;
    }

    protected String typeString(){
        StringBuilder sb = new StringBuilder();
        for (String type : this.types){
            sb = sb.append(type).append("/");
        }
        String result = sb.toString();
        if (result.length()>1) result = result.substring(0,result.length()-1);
        return result;
    }

    public void addType(String s){
        this.types.add(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        if (surface != null ? !surface.equals(word.surface) : word.surface != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return surface != null ? surface.hashCode() : 0;
    }
}
