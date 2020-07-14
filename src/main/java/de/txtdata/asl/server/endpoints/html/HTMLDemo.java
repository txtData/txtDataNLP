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

@Path("demo")
public class HTMLDemo {

    public String defaultString = "He lives in Berlin and she in New York City, but she used to live in Berlin too.";

    public String htmlFile = "./src/main/html/view.html";
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
