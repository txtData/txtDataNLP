/*
 * Copyright 2013-2015 Michael Kaisser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.txtdata.asl.server.endpoints.opennlp;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.nlp.tools.OpenNLPSentenceSplitter;
import de.txtdata.asl.nlp.tools.OpenNLPTagger;
import de.txtdata.asl.nlp.tools.OpenNLPTokenizer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("opennlp")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceOpenNLP {

    public Language language = Language.ENGLISH;
    public String modelDirectory = ".\\data\\models";

    private static OpenNLPSentenceSplitter openNLPSentenceSplitter;
    private static OpenNLPTokenizer openNLPTokenizer;
    private static OpenNLPTagger openNLPTagger;

    public ServiceOpenNLP(){
        this.openNLPSentenceSplitter = new OpenNLPSentenceSplitter(language, modelDirectory);
        this.openNLPTokenizer = new OpenNLPTokenizer(language, modelDirectory);
        this.openNLPTagger = new OpenNLPTagger(language, modelDirectory);
    }

    @GET
    @Timed
    public List<Sentence> receiveGET(
            @QueryParam("split") Boolean split,
            @QueryParam("tokenize") Boolean tokenize,
            @QueryParam("tag") Boolean tag,
            @QueryParam("text") String text){

        List<Span> splitted = new ArrayList<>();

        if (split==null || split){
            splitted = this.openNLPSentenceSplitter.getSentencesAsSpans(text);
        }else{
            splitted.add(new Span(text,0,text.length()));
        }
        List<Sentence> result = new ArrayList<>();

        for (Span s : splitted){
            Sentence sentence = new Sentence();
            sentence.surface = s.getSurface();
            sentence.starts = s.getStarts();
            sentence.ends = s.getEnds();
            result.add(sentence);
            if (tokenize==null || tokenize){
                sentence.tokens = new ArrayList<>();
                List<Span> spans = openNLPTokenizer.getTokensAsSpans(sentence.surface);
                List<Word> words = new ArrayList<>();
                if (tag==null || tag){
                    words = openNLPTagger.getTaggedWords(spans);
                }
                int i=0;
                for (Span span : spans) {
                    Token token = new Token();
                    token.surface = span.getSurface();
                    token.starts = sentence.starts + span.getStarts();
                    token.ends = sentence.starts + span.getEnds();
                    sentence.tokens.add(token);
                    if (tag==null || tag){
                        token.tag = words.get(i).getPOS();
                    }
                    i++;
                }
            }
        }
        return result;
    }

    public class Sentence{
        public String surface;
        public Integer starts;
        public Integer ends;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Token> tokens;
    }

    public class Token{
        public String surface;
        public Integer starts;
        public Integer ends;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String tag;
    }
}
