/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtdata.asl.ml;

import de.txtdata.asl.nlp.annotations.Annotation;

/**
 *
 */
public class AnnotationWithFeatures extends Annotation{

    public FeatureBundle featureBundle = new FeatureBundle();

    public AnnotationWithFeatures(Annotation annotation){
        super(annotation.getSpan(), annotation.getAnnotationObject());
    }
}
