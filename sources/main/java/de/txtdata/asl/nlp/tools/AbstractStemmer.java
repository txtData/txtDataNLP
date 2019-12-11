/***
 * Copyright 2013-2019 Michael Kaisser
 ***/

package de.txtdata.asl.nlp.tools;

/**
 * Simple, abstract class modelling stemmer behavior.
 */
public abstract class AbstractStemmer {

    public abstract String stem(String input);
    public abstract boolean equals(String a, String b);

}
    

