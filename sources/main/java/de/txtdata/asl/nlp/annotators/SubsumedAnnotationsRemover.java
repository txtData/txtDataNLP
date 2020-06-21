/***
 * Copyright 2020 Michael Kaisser
 ***/

package de.txtdata.asl.nlp.annotators;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.annotations.AnnotationList;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.TextUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes annotations that are entirely contained within another annotation.
 */
public class SubsumedAnnotationsRemover extends AbstractAnnotator {

    public String type;

    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public SubsumedAnnotationsRemover(Language language){
        super(language);
        this.type = null;
    }

    /**
     * Standard constructor
     * @param language The language of this annotators.
     * @param type Only annotations of this type will be removed.
     */
    public SubsumedAnnotationsRemover(Language language, String type){
        super(language);
        this.type = type;
    }

    /**
     * Processes input text and adds annotations to it.
     * @param textPiece The text that should be processed.
     */
    public void annotate(TextUnit textPiece){
        List<Annotation> toRemove = new ArrayList<>();
        List<Annotation> annotations;
        if (type!=null)
            annotations = textPiece.getAnnotations(type);
        else
            annotations = new AnnotationList(textPiece.getAnnotations());

        while(!annotations.isEmpty()){
            Annotation annotation = annotations.get(0);
            annotations.remove(0);
            boolean isSubsumed = false;
            for (Annotation compareWith : annotations){
                if (compareWith.includes(annotation,false)){
                    isSubsumed = true;
                    break;
                }else if(annotation.includes(compareWith,false)){
                    if (!toRemove.contains(compareWith)){
                        toRemove.add(compareWith);
                    }
                }
            }
            if (isSubsumed){
                toRemove.add(annotation);
            }
        }
        textPiece.removeAnnotations(toRemove);
    }

}
