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
 *  See also https://github.com/txtData/nlp
 */

package de.txtdata.asl.nlp.models;

import de.txtdata.asl.util.dataStructures.KeyValuePairList;
import de.txtdata.asl.util.misc.PrettyString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation for a word.
 * Note that this class has fields for start and end positions of the word in a text, however these are not used when
 * computing equality. The only requirement for two words to be equal is that their surfaces are equal.
 */
public class Word extends Span implements Serializable{

    public static final String UNSET       = "UNSET";
    public static final String STOPWORD    = "STOP";
    public static final String UPPERCASE   = "UPPER";
    public static final String LOWERCASE   = "LOWER";
    public static final String WHITESPACE  = "WHITE";
    public static final String PUNCTUATION = "PUNCT";
    public static final String NUMBER      = "NUMB";
    public static final String SEPARATOR   = "SEP"; // /t and /n

    private static String PUNCTUATIONS =  ",.:;-!?#/\"'’«»„“()[]+-•";

    private String root;
    private String pos;
    private String morph;

    private List<String> types = new ArrayList<>();
    private KeyValuePairList<String, Object> features = new KeyValuePairList<>();


    public Word(String surface){
        super(surface,0,0);
    }

    public Word(String surface, int starts, int ends){
        super(surface, starts, ends);
    }

    /**
     * Copy constructor.
     * @param toCopy The word object to copy.
     */
    public Word(Word toCopy){
        super(toCopy.getSurface(), toCopy.getStarts(), toCopy.getEnds());
        this.setRoot(toCopy.getRoot());
        this.setPOS(toCopy.getPOS());
        this.setMorph(toCopy.getMorph());
        this.setTypes(toCopy.getTypes());
    }


    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getPOS() {
        return pos;
    }

    public void setPOS(String pos) {
        this.pos = pos;
    }

    public String getMorph() {
        return morph;
    }

    public void setMorph(String morph) {
        this.morph = morph;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public boolean isType(String type){
        if (this.getTypes().contains(type)) return true;
        return false;
    }

    public String getTypeString(){
        StringBuilder sb = new StringBuilder();
        for (String type : this.getTypes()){
            sb = sb.append(type).append("/");
        }
        String result = sb.toString();
        if (result.length()>1) result = result.substring(0,result.length()-1);
        return result;
    }

    public void addType(String s){
        this.getTypes().add(s);
    }

    public KeyValuePairList<String, Object> getFeatures(){
        return this.features;
    }

    public void setFeatures(KeyValuePairList<String, Object> features){
        this.features = features;
    }


    public static String getPunctuations(){
        return PUNCTUATIONS;
    }

    public static void setPunctuations(String punctuations){
        PUNCTUATIONS = punctuations;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        if (getSurface() != null ? !getSurface().equals(word.getSurface()) : word.getSurface() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getSurface() != null ? getSurface().hashCode() : 0;
    }

    public String toString(){
        return this.getSurface();
    }

    public String toString(boolean extended){
        if (!extended) return this.getSurface();
        StringBuilder sb = new StringBuilder(this.getSurface());
        sb.append("/").append(this.getTypes());
        if (getRoot() !=null){
            sb.append("/").append(getRoot());
        }
        if (getPOS() !=null){
            sb.append("/").append(getPOS());
        }
        if (getMorph() !=null) {
            sb.append("/").append(getMorph());
        }
        if (getStarts() !=-1 && getEnds() !=-1){
            sb.append(" (").append(getStarts()).append("-").append(getEnds()).append(")");
        }
        return sb.toString();
    }

    public String prettyString(){
        StringBuilder sb = new StringBuilder(PrettyString.create(this.getSurface(),20));
        if (getRoot() !=null){
            sb.append(PrettyString.create(this.getRoot(),20));
        }
        if (getPOS() !=null){
            sb.append(PrettyString.create(this.getPOS(),10));
        }
        if (getMorph() !=null){
            sb.append(PrettyString.create(this.getMorph(),20));
        }

        sb.append(PrettyString.create(this.getTypeString(),12));
        if (getStarts() !=-1 && getEnds() !=-1){
            String fromTo ="("+ getStarts() +"-"+ getEnds() +")";
            sb.append(PrettyString.create(fromTo,12));
        }
        return sb.toString();
    }
}
