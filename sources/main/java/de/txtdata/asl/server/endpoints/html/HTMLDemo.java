package de.txtdata.asl.server.endpoints.html;

import com.codahale.metrics.annotation.Timed;
import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.server.ServiceApplication;
import de.txtdata.asl.server.endpoints.api.models.Entity;
import de.txtdata.asl.util.files.FileIO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

//import de.mk.com.enfi.annotators.pipelines.KeywordIndexingPipeline;
//import de.mk.com.enfi.models.annotations.EntityResult;

@Path("demo")
public class HTMLDemo {

    public String defaultString = "He lives in Berlin and she in New York City, but she used to live in Berlin too.";

    public String htmlFile = "./sources/main/html/view.html";
    public String replaceTable = "<!--TABLE-->";
    public String replaceText = "<!--TEXT-->";

    private String html;

    public HTMLDemo() {
        this.html = FileIO.readFromFile(htmlFile, "UTF-8");
    }

    @GET
    @Timed
    public String receiveGET(@QueryParam("text") String input, @QueryParam("button") String button) {
        System.out.println("Received text: "+input);
        if (input == null) input = "";
        if ("default".equals(button)) input = defaultString;
        System.out.println(input + " " + button);
        TextUnit textUnit = ServiceApplication.chunker.create(input);
        List<Annotation> annotations = textUnit.getAnnotations();
        List<Entity> rankedEntities = Entity.getRankedEntities(textUnit.getAnnotations(), null);

        String htmlText = AnnotationsToHTML.getHTMLText(input, annotations);
        String htmlTable = AnnotationsToHTML.getHTMLTable(rankedEntities);

        String result = this.html;
        if (input!=null && input.trim().length()>0){
            int i = result.indexOf(this.replaceTable);
            if (i!=-1) {
                result = result.substring(0, i)
                        + htmlTable
                        + result.substring(i + replaceTable.length(), result.length());
            }
            i = result.indexOf(this.replaceText);
            if (i!=-1) {
                result = result.substring(0, i)
                        + htmlText
                        + result.substring(i + replaceText.length(), result.length());
            }
        }else{
            result = result+"<br><b>No input. Please enter some text.<b>";
        }
        return result;
    }



}