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

    public static String PUNCTUATIONS =  ",.:;-!?#/\"'’«»„“()[]+-•";

    public String surface;
    public String root;
    public String pos;
    public String morph;

    public int starts = 0;
    public int ends = 0;

    public List<String> types = new ArrayList<>();
    public Double idf = null;

    public Word(String surface){
        this.surface = surface;
    }

    public Word(String surface, int starts, int ends){
        this.surface = surface;
        this.starts = starts;
        this.ends   = ends;
    }

    public Word(Word toCopy){
        this.surface = toCopy.surface;
        this.root = toCopy.root;
        this.pos = toCopy.pos;
        this.morph = toCopy.morph;
        this.starts = toCopy.starts;
        this.ends = toCopy.ends;
        this.types= toCopy.types;
        this.idf = toCopy.idf;
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
