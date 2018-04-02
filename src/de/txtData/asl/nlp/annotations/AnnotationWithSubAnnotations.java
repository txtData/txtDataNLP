/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

import de.txtData.asl.nlp.models.Span;

import java.util.ArrayList;
import java.util.List;

/**
 * Please note: This class should be used sparingly and only if there are good reasons for it, since things can get
 * messy fast.
 * Texts are often hierarchical: A text might contain sections, which contain paragraphs, which contain sentences, in
 * which Named Entities like persons are mentioned.
 * This class can be used to model such relations, e.g. it could be used to represent a sentence that contains
 * annotations on its own.
 * Note: It's up to the implementing class to choose how to deal with the indices of the annotations: Do they represent
 * positions in the whole text, or positions in the part at hand?
 */
public abstract class AnnotationWithSubAnnotations extends Annotation{

    protected AnnotationList annotations = new AnnotationList();

    public AnnotationWithSubAnnotations(Span span, IAnnotationObject annotationObject){
        super(span, annotationObject);
    }

    public AnnotationWithSubAnnotations(String surface, int starts, int ends, IAnnotationObject annotationObject){
        super(surface, starts, ends, annotationObject);
    }

    public AnnotationWithSubAnnotations(Span span, String type){
        super(span, type);
    }

    public AnnotationWithSubAnnotations(String surface, int starts, int ends, String type){
        super(surface, starts, ends, type);
    }

    public int getOffset(){
        return this.getSpan().starts;
    }

    public void addAnnotation(Annotation annotation){
        this.annotations.add(annotation);
    }

    public void addAnnotations(List<Annotation> annotations){
        this.annotations.addAll(annotations);
    }

    public void setAnnotations(List<Annotation> annotations){
        this.annotations = new AnnotationList(annotations);
    }

    public void setAnnotations(AnnotationList annotations){
        this.annotations =annotations;
    }

    public boolean removeAnnotation(Annotation annotation){
        return this.annotations.remove(annotation);
    }

    public boolean removeAnnotations(List<Annotation> toRemove){
        return this.annotations.removeAll(toRemove);
    }

    public AnnotationList getAnnotations() {
        return annotations;
    }

    public <T extends IAnnotationObject> AnnotationList getAnnotations(Class<T> classOfT){
        return this.annotations.getAnnotations(classOfT);
    }

    public <T extends IAnnotationObject> AnnotationList getAnnotations(Class<T> classOfT, String type){
        return this.annotations.getAnnotations(classOfT, type);
    }

    public AnnotationList getAnnotations(String type){
        return this.annotations.getAnnotations(type);
    }

    public <T extends IAnnotationObject> List<T> getAnnotationObjects(Class<T> classOfT){
        return this.annotations.getAnnotationObjects(classOfT);
    }



    public AnnotationList getAnnotationsStartingAt(int index){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : annotations){
            if (annotation.getSpan().starts==index){
                result.add(annotation);
            }
        }
        return result;
    }

    public Annotation getAnnotationFromTo(int from, int to){
        for (Annotation annotation : this.annotations){
            if (annotation.getSpan().starts==from && annotation.getSpan().ends==to){
                return annotation;
            }
        }
        return null;
    }

    public <T extends IAnnotationObject> AnnotationList getCrossingAnnotations(Annotation annotation, Class<T> classOfT){
        AnnotationList results = new AnnotationList();
        for (Annotation thisAnno : this.getAnnotations(classOfT)){
            if (thisAnno.equals(annotation)) continue;
            boolean crosses = annotation.getSpan().somehowOverlaps(thisAnno.getSpan());
            if (crosses) results.add(thisAnno);
        }
        return results;
    }

    // todo: Use faster sorting algorithm.
    public void sortAnnotations(){
        List<Annotation> sorted = new ArrayList<>();
        for (Annotation annotation : this.getAnnotations()){
            int i = 0;
            for (Annotation comparator : sorted){
                if (annotation.getStart()<comparator.getStart()){
                    break;
                }
                i++;
            }
            sorted.add(i,annotation);
        }
        this.setAnnotations(sorted);
    }

    /**
     * Takes all sub annotations of this instance's annotation and makes adds them to this instance's annotations.
     * @param recursive If true this operation will be recursively performed on the sub annotations.
     */
    public void raiseAnnotations(boolean recursive){
        List<Annotation> toAdd = new ArrayList<>();
        for (Annotation annotation : this.getAnnotations()){
            if (annotation instanceof AnnotationWithSubAnnotations){
                AnnotationWithSubAnnotations awsa = (AnnotationWithSubAnnotations)annotation;
                List<Annotation> toRemove = new ArrayList<>();
                for (Annotation annotation2 : awsa.getAnnotations()){
                    if (recursive && annotation2 instanceof AnnotationWithSubAnnotations) {
                        AnnotationWithSubAnnotations awsa2 = (AnnotationWithSubAnnotations) annotation2;
                        awsa2.raiseAnnotations(true);
                    }
                    toAdd.add(annotation2);
                    toRemove.add(annotation2);
                }
                awsa.removeAnnotations(toRemove);
            }
        }
        this.addAnnotations(toAdd);
    }


}
