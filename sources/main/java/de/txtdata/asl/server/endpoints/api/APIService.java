package de.txtdata.asl.server.endpoints.api;

import com.codahale.metrics.annotation.Timed;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.server.ServiceApplication;
import de.txtdata.asl.server.endpoints.api.models.Entity;
import de.txtdata.asl.server.endpoints.api.models.Request;
import de.txtdata.asl.server.endpoints.api.models.Response;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class APIService {

    @GET
    @Timed
    public Response receiveGET(@QueryParam("text") @DefaultValue("") String text, @Context HttpServletResponse httpServletResponse) {
        Response response = new Response();
        response.text = text;
        TextUnit textUnit = ServiceApplication.chunker.create(text);
        response.rankedEntities = Entity.getRankedEntities(textUnit.getAnnotations(), null);
        response.entitiesInText = Entity.getEntities(textUnit.getAnnotations(), null);
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    @PUT
    @Timed
    public Response receivePUT(@Valid Request body, @Context HttpServletResponse httpServletResponse) {
        if (body==null || body.text==null){
            throw new WebApplicationException(javax.ws.rs.core.Response.status(400).entity("Cannot find text to analyse in JSON.").build());
        }
        Response response = new Response();
        response.text = body.text;
        response.title = body.title;
        response.abstractText = body.abstractText;

        String text = body.text;
        int textStarts = 0;
        int abstractStarts = 0;
        if (body.abstractText !=null){
            text = body.abstractText +"\n\n"+text;
            textStarts += (body.abstractText.length() + 2);
        }
        if (body.title!=null){
            text = body.title+"\n\n"+text;
            textStarts += (body.title.length() + 2);
            abstractStarts += abstractStarts + (body.title.length() + 2);
        }

        System.out.println("Received :"+text);
        TextUnit textUnit = ServiceApplication.chunker.create(text);
        response.rankedEntities = Entity.getRankedEntities(textUnit.getAnnotations(), null);
        response.entitiesInText = Entity.getEntities(textUnit.getAnnotations(), null);
        this.recalculateOffsets(response.rankedEntities, textStarts, abstractStarts);
        this.recalculateOffsets(response.entitiesInText, textStarts, abstractStarts);
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    private void recalculateOffsets(List<Entity> list, int textStarts, int slugStarts){
        for (Entity entity : list){
            if (entity.starts>=textStarts){
                entity.starts -= textStarts;
                entity.ends -= textStarts;
                entity.field = "text";
            }else if(entity.starts>=slugStarts){
                entity.starts -= slugStarts;
                entity.ends -= slugStarts;
                entity.field = "abstractText";
            }else{
                entity.field = "title";
            }
        }
    }
}
