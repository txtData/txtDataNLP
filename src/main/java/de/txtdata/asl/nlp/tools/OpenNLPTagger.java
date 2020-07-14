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
package de.txtdata.asl.nlp.tools;

import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.util.misc.AslException;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for OpenNLP tagger.
 */
public class OpenNLPTagger {

    public static POSTaggerME tagger;

    public OpenNLPTagger(Language language, String modelDirectory){
        try{
            POSModel posModel = new POSModelLoader().load(new File(modelDirectory+"/"+language.getCode()+"-pos-maxent.bin"));
            tagger = new POSTaggerME(posModel);
        }catch(Exception e){
            throw new AslException(e);
        }
    }

    public List<Word> getTaggedWords(List<Span> spans){
        List<Word> results = new ArrayList<>();
        String[] tokenized = new String[spans.size()];
        int i=0;
        for (Span span : spans){
            tokenized[i] = span.getSurface();
            i++;
        }
        String[] tagged = tagger.tag(tokenized);
        if (tokenized.length!=tagged.length){
            System.out.println("Warning! OpenNLPTagger: Tokens and annotations don't match.");
        }
        int j=0;
        for (String token : tokenized){
            String tag = tagged[j];
            Span span = spans.get(j);
            Word word = new Word(token);
            word.setPOS(tag);
            word.setStarts(span.getStarts());
            word.setEnds(span.getEnds());
            results.add(word);
            j++;
        }
        return results;
    }
}
