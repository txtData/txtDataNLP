/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.tools;

import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.nlp.models.Word;
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
            e.printStackTrace();
        }
    }

    public List<Word> getTaggedWords(List<Span> spans){
        List<Word> results = new ArrayList<>();
        String[] tokenized = new String[spans.size()];
        int i=0;
        for (Span span : spans){
            tokenized[i] = span.surface;
            i++;
        }
        String[] tagged = tagger.tag(tokenized);
        if (tokenized.length!=tagged.length){
            System.out.println("Warning! OpenNLPTagger: Tokens and tags don't match.");
        }
        int j=0;
        for (String token : tokenized){
            String tag = tagged[j];
            Span span = spans.get(j);
            Word word = new Word(token);
            word.pos = tag;
            word.starts = span.starts;
            word.ends = span.ends;
            results.add(word);
            j++;
        }
        return results;
    }
}
