/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.annotations.TextUnit;
import de.txtData.asl.nlp.models.Language;

/**
 * Abstract class for an 'Annotator'. An Annotator analyses a piece of text and detects parts of this text that have
 * a meaning, or are in any other way special. It marks these by creating annotations.
 * It needs text in that is preprocessed and comes in form of a TextUnit. It is however up to the
 * implementation of the annotator which functionality of TextUnit it uses.
 */
public abstract class AbstractAnnotator {

    protected Language language;

    /**
     * Standard constructor
     * @param language The language of this annotator.
     */
    public AbstractAnnotator(Language language){
        this.language = language;
    }

    /**
     * May be overridden by the subclass. Any initialization should happen here, rather than in the constructor, since
     * deserialization will run this method, but not this constructor.
     */
    public void initialize(){}

    /**
     * Processes input text and adds annotations to it.
     * @param sentence The text that should be processed.
     */
    public abstract void annotate(TextUnit sentence);

    public void setLanguage(Language language) { this.language = language; }

    public Language getLanguage(){
        return this.language;
    }

    public boolean isForLanguage(Language lang){
        return this.language.is(lang);
    }

}
