package de.txtdata.asl.server.endpoints.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    public String title;
    public String abstractText;
    public String text;
    public List<Entity> rankedEntities;
    public List<Entity> entitiesInText;
}
