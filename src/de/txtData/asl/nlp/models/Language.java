/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.models;

import java.io.Serializable;

/**
 * Representation of a language of a text.
 */
public class Language implements Serializable{

    public static final Language ENGLISH = new Language("EN");
    public static final Language GERMAN  = new Language("DE");
    public static final Language SPANISH = new Language("ES");
    public static final Language FRENCH  = new Language("FR");

    private String langCode;

    public Language(String langCode){
        this.langCode = langCode;
    }

    public boolean is(Language lang){
        if (this.langCode==null) return false;
        if (this.langCode.equalsIgnoreCase(lang.langCode)) return true;
        return false;
    }

    public String getCode(){
        return this.langCode;
    }

}
