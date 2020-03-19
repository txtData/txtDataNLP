package de.txtdata.asl.server.endpoints.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.util.dataStructures.Bag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity{
    public String lemma;
    public String surface;

    public Double score;
    public Integer count;

    public String type;
    public String primaryId;
    public List<String> ids;
    public String description;

    public String field;
    public int starts;
    public int ends;

    public static List<Entity> getEntities(List<Annotation> annotations, String fieldName){
        List<Entity> entityList = new ArrayList<>();
        for (Annotation annotation : annotations){
            entityList.add(fromAnnotation(annotation, fieldName));
        }
        return entityList;
    }

    public static List<Entity> getRankedEntities(List<Annotation> annotations, String fieldName){
        Bag<String> entities = new Bag<>();
        HashMap<String, Annotation> hash = new HashMap<>();
        for (Annotation annotation : annotations){
            entities.add(annotation.getSurface());
            if (!hash.containsKey(annotation.getSurface())){
                hash.put(annotation.getSurface(), annotation);
            }
        }
        List<Entity> entityList = new ArrayList<>();
        for (String surface : entities.getAsSortedList()){
            Annotation annotation = hash.get(surface);
            Entity entity = fromAnnotation(annotation, fieldName);
            entityList.add(entity);
            entity.score = entities.getValue(surface);
            entity.count = entity.score.intValue();
        }
        return entityList;
    }

    public static Entity fromAnnotation(Annotation annotation, String fieldName) {
        Entity entity = new Entity();
        entity.surface = annotation.getSurface();
        entity.type = annotation.getType();
        entity.field = fieldName;
        entity.starts = annotation.getStarts();
        entity.ends = annotation.getEnds();
        return entity;
    }
}
