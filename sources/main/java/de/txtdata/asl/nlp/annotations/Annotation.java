/*
 *  Copyright 2013-2020 Michael Kaisser
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
public class Annotation extends Span{

    private IAnnotationObject annotationObject;

    public Annotation(Span span, IAnnotationObject annotationObject){
        super(span.getSurface(), span.getStarts(), span.getEnds());
        this.annotationObject = annotationObject;
    }

    public Annotation(String surface, int starts, int ends, IAnnotationObject annotationObject){
        super(surface, starts, ends);
        this.annotationObject = annotationObject;
    }

    public Annotation(Span span, String type){
        super(span.getSurface(), span.getStarts(), span.getEnds());
        this.annotationObject = new SimpleAnnotationObject(type);
    }

    public Annotation(String surface, int starts, int ends, String type){
        super(surface, starts, ends);
        this.annotationObject = new SimpleAnnotationObject(type);
    }


    public void setAnnotationObject(IAnnotationObject annoObj){
        this.annotationObject = annoObj;
    }

    public <T extends IAnnotationObject> T getAnnotationObject(Class<T> classOfT) {
        if (!classOfT.isInstance(this.getAnnotationObject())) return null;
        return (T) this.getAnnotationObject();
    }

    public IAnnotationObject getAnnotationObject(){
        return this.annotationObject;
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
        if (this.getEnds() != toCompare.getEnds()) return false;
        if (this.getStarts() != toCompare.getStarts()) return false;
        if (this.getSurface() != null ? !this.getSurface().equals(toCompare.getSurface()) : toCompare.getSurface() != null) return false;
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.getStarts()).append("-").append(this.getEnds()).append("] ");
        String span = sb.toString();
        sb = new StringBuilder();
        sb.append(PrettyString.create(span,11));
        sb.append(" '").append(this.getSurface()).append("' ");
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
        if (this.getEnds() != that.getEnds()) return false;
        if (this.getStarts() != that.getStarts()) return false;
        if (this.getSurface() != null ? !getSurface().equals(that.getSurface()) : that.getSurface() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
