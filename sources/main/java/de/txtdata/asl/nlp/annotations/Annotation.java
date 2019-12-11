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

package de.txtdata.asl.nlp.annotations;

import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.util.misc.PrettyString;

/**
 * Model class for an annotation in a text.
 */
public class Annotation{

    private Span span;
    private IAnnotationObject annotationObject;

    public Annotation(Span span, IAnnotationObject annotationObject){
        this.span = span;
        this.annotationObject = annotationObject;
    }

    public Annotation(String surface, int starts, int ends, IAnnotationObject annotationObject){
        this.span = new Span(surface,starts,ends);
        this.annotationObject = annotationObject;
    }

    public Annotation(Span span, String type){
        this.span = span;
        this.annotationObject = new SimpleAnnotationObject(type);
    }

    public Annotation(String surface, int starts, int ends, String type){
        this.span = new Span(surface,starts,ends);
        this.annotationObject = new SimpleAnnotationObject(type);
    }

    public Span getSpan(){
        return this.span;
    }

    public void setSpan(Span span){
        this.span = span;
    }

    public int getStart(){
        return this.span.starts;
    }

    public int getEnd(){
        return this.span.ends;
    }

    public String getSurfaceText(){
        return this.span.surface;
    }

    public void setSurfaceText(String surface){
        this.span.surface = surface;
    }

    public void setAnnotationObject(IAnnotationObject annoObj){
        this.annotationObject = annoObj;
    }

    public IAnnotationObject getAnnotationObject(){
        return this.annotationObject;
    }

    public <T extends IAnnotationObject> T getAnnotationObject(Class<T> classOfT) {
        if (!classOfT.isInstance(this.getAnnotationObject())) return null;
        return (T) this.getAnnotationObject();
    }

    public String getType(){
        if (this.annotationObject!=null){
            return this.annotationObject.getType();
        }else {
            return null;
        }
    }

    public void setType(String type){
        if (this.annotationObject!=null){
            this.annotationObject.setType(type);
        }
    }

    public boolean equalsSpan(Span toCompare){
        if (this.span.ends != toCompare.ends) return false;
        if (this.span.starts != toCompare.starts) return false;
        if (this.span.surface != null ? !this.span.surface.equals(toCompare.surface) : toCompare.surface != null) return false;
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.span.starts).append("-").append(this.span.ends).append("] ");
        String span = sb.toString();
        sb = new StringBuilder();
        sb.append(PrettyString.create(span,11));
        sb.append(" '").append(this.span.surface).append("' ");
        if (annotationObject!=null) {
            sb.append(annotationObject.toString());
        }else{
            sb.append("[NO ANNOTATION OBJECT]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        if (annotationObject != null ? !annotationObject.equals(that.annotationObject) : that.annotationObject != null) return false;
        if (span != null ? !span.equals(that.span) : that.span != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + (annotationObject != null ? annotationObject.hashCode() : 0);
        return result;
    }
}
