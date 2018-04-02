/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

/**
 * Semantics container for an annotation.
 * Could represent a person, a date, a location or maybe a paragraph or a sentence.
 * Type is a descriptor for the semantic type of the annotation. Please note that different instances of one
 * the same AnnotationObject class can have different semantic types.
 * A NamedEntity class for example could return "PERSON", "DATE", "LOCATION" for type.
 * Any details are handled in the implementations.
 */
public interface IAnnotationObject{
    public String getType();
    public void setType(String type);
}
