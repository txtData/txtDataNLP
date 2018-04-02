/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotations;

import de.txtData.asl.util.dataStructures.Bag;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of helper methods, that help dealing with lists of annotations.
 */
public class AnnotationList extends ArrayList<Annotation> {

    /**
     * Constructor. Creates an empty annotation list.
     */
    public AnnotationList(){
        super();
    }

    public AnnotationList(ArrayList<Annotation> annotations){
        super(annotations);
    }

    public AnnotationList(List<Annotation> annotations){
        super(annotations);
    }

    public <T extends IAnnotationObject> AnnotationList getAnnotations(Class<T> classOfT){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (classOfT.isInstance(annotation.getAnnotationObject())){
                result.add(annotation);
            }
        }
        return result;
    }

    public <T extends IAnnotationObject> AnnotationList getAnnotations(Class<T> classOfT, String type){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (classOfT.isInstance(annotation.getAnnotationObject())){
                if (type.equals(annotation.getType())) {
                    result.add(annotation);
                }
            }
        }
        return result;
    }

    public AnnotationList getAnnotations(String type){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (type.equals(annotation.getType())) {
                result.add(annotation);
            }
        }
        return result;
    }

    public <T extends IAnnotationObject> List<T> getAnnotationObjects(Class<T> classOfT){
        List<T> result = new ArrayList<>();
        for (Annotation annotation : this){
            if (classOfT.isInstance(annotation.getAnnotationObject())){
                result.add((T)annotation.getAnnotationObject());
            }
        }
        return result;
    }


    public AnnotationList getAllAnnotationsBetween(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getSpan().starts>=from
                && annotation.getSpan().ends<=to){
                result.add(annotation);

            }
        }
        return result;
    }

    public AnnotationList getAnnotationsFromTo(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getSpan().starts==from
                    && annotation.getSpan().ends==to){
                result.add(annotation);

            }
        }
        return result;
    }

    public AnnotationList getAnnotationsFromOrAfter(int index){
        AnnotationList result = new AnnotationList();
        int bestDiff = -1;
        for (Annotation annotation : this){
            if (annotation.getSpan().starts>=index){
                int diff = annotation.getSpan().starts - index;
                if (bestDiff==-1){
                    result.add(annotation);
                    bestDiff = diff;
                }else if (diff<bestDiff){
                    result = new AnnotationList();
                    result.add(annotation);
                    bestDiff = diff;
                }else if (diff==bestDiff){
                    result.add(annotation);
                }
            }
        }
        return result;
    }

    public AnnotationList getSomehowOverlappingAnnotations(Annotation annotation){
        AnnotationList result = new AnnotationList();
        for (Annotation thisAnno : this){
            if (thisAnno.equals(annotation)) continue;
            boolean crosses = annotation.getSpan().somehowOverlaps(thisAnno.getSpan());
            if (crosses) result.add(thisAnno);
        }
        return result;
    }

    public AnnotationList getCrossingAnnotations(int index){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getSpan().contains(index)) result.add(annotation);
        }
        return result;
    }

    public AnnotationList getLargerAnnotations(Annotation annotation){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation2 : this){
            if (annotation2.getSpan().starts<=annotation.getSpan().starts
                    && annotation2.getSpan().ends>=annotation.getSpan().ends) {
                result.add(annotation2);
            }
        }
        return result;
    }

    public AnnotationList getLargerAnnotations(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation2 : this){
            if (annotation2.getSpan().starts<=from
                    && annotation2.getSpan().ends>=to) {
                result.add(annotation2);
            }
        }
        return result;
    }

    public AnnotationList getLongestAnnotations(){
        Bag<Annotation> sizes = new Bag<>();
        for (Annotation annotation : this){
            int length = annotation.getSpan().ends-annotation.getSpan().starts;
            sizes.add(annotation, length);
        }
        List<Annotation> longest = sizes.getKeysForHighestValue();
        return new AnnotationList(longest);
    }

    /**
     * Returns those annotations that are completely contained within another annotation.
     */
    public AnnotationList getSubsumedAnnotations(){
        AnnotationList subsumed = new AnnotationList();
        AnnotationList annotations = new AnnotationList(this);

        while(!annotations.isEmpty()){
            Annotation annotation = annotations.get(0);
            annotations.remove(0);
            boolean isSubsumed = false;
            for (Annotation compareWith : annotations){
                if (compareWith.getSpan().includes(annotation.getSpan(),false)){
                    isSubsumed = true;
                    break;
                }else if(annotation.getSpan().includes(compareWith.getSpan(),false)){
                    if (!subsumed.contains(compareWith)){
                        subsumed.add(compareWith);
                    }
                }
            }
            if (isSubsumed){
                subsumed.add(annotation);
            }
        }
        return subsumed;
    }

    public boolean removeAnnotation(Annotation annotation){
        return this.remove(annotation);
    }

    public boolean removeAnnotations(List<Annotation> toRemove){
        return this.removeAll(toRemove);
    }

}
