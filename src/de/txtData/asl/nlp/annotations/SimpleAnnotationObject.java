/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

/**
 * Most simple implementation of IAnnotationObject.
 */
public class SimpleAnnotationObject implements IAnnotationObject {

    private String type = null;

    public SimpleAnnotationObject(String type){
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
        return this.type;
    }
}
