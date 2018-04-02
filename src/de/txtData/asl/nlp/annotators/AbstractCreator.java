/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.annotations.TextUnit;
import de.txtData.asl.nlp.models.Language;

/**
 * A Creator is an Annotator that, other than an Annotator, has the ability to start an annotation process directly
 * from unprocessed text.
 */
public abstract class AbstractCreator extends AbstractAnnotator{

    public AbstractCreator(Language lang){
        super(lang);
    }

    public abstract TextUnit create(String surface);
}
