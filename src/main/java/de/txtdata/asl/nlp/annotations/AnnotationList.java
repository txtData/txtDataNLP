/*
 *  Copyright 2020 Michael Kaisser
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

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of helper methods for handling lists of annotations,
 * many of which deal with retrieving annotations that have specific properties.
 */
public class AnnotationList extends ArrayList<Annotation> {

    /**
     * Constructor. Creates a new, empty annotation list.
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

    public AnnotationList getAnnotationsBetween(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getStarts() >=from
                && annotation.getEnds() <=to){
                result.add(annotation);

            }
        }
        return result;
    }

    public AnnotationList getAnnotationsFromTo(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getStarts() ==from
                    && annotation.getEnds() ==to){
                result.add(annotation);

            }
        }
        return result;
    }

    public Annotation getAnnotationFrom(int index){
        for (Annotation annotation : this){
            if (annotation.getStarts() ==index){
                return annotation;
            }
        }
        return null;
    }

    public AnnotationList getAnnotationsFrom(int index){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.getStarts() ==index){
                result.add(annotation);
            }
        }
        return result;
    }

    public AnnotationList getAnnotationsFromOrAfter(int index){
        AnnotationList result = new AnnotationList();
        int bestDiff = -1;
        for (Annotation annotation : this){
            if (annotation.getStarts() >=index){
                int diff = annotation.getStarts() - index;
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

    public AnnotationList getOverlappingAnnotations(Annotation annotation){
        AnnotationList result = new AnnotationList();
        for (Annotation thisAnno : this){
            if (thisAnno.equals(annotation)) continue;
            boolean crosses = annotation.somehowOverlaps(thisAnno);
            if (crosses) result.add(thisAnno);
        }
        return result;
    }

    public AnnotationList getCrossingAnnotations(int index){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation : this){
            if (annotation.contains(index)) result.add(annotation);
        }
        return result;
    }

    public AnnotationList getLargerAnnotations(Annotation annotation){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation2 : this){
            if (annotation2.getStarts() <= annotation.getStarts()
                    && annotation2.getEnds() >= annotation.getEnds()) {
                result.add(annotation2);
            }
        }
        return result;
    }

    public AnnotationList getLargerAnnotations(int from, int to){
        AnnotationList result = new AnnotationList();
        for (Annotation annotation2 : this){
            if (annotation2.getStarts() <=from
                    && annotation2.getEnds() >=to) {
                result.add(annotation2);
            }
        }
        return result;
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
                if (compareWith.includes(annotation,false)){
                    isSubsumed = true;
                    break;
                }else if(annotation.includes(compareWith,false)){
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

    public boolean removeAnnotations(String type){
        return this.removeAll(this.getAnnotations(type));
    }

}
