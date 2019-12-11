/*
 *  Copyright 2013-2019 Michael Kaisser
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
package de.txtData.asl.nlp.tools;

import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.util.misc.AslException;
import opennlp.tools.cmdline.tokenizer.TokenizerModelLoader;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for OpenNLP tokenizer.
 */
public class OpenNLPTokenizer{

    public Language language;
    public String modelDirectory;
    public boolean lowercaseAll = false;
    public boolean includeWhitespace = true;
    public boolean doPostProcess = false;

    public String tokensToCutOff = "\"'«»-";

    private TokenizerME tokenizer;

    public OpenNLPTokenizer(Language language, String modelDirectory){
        this(language, modelDirectory, false);
    }

    public OpenNLPTokenizer(Language language, String modelDirectory, boolean includeWhitespace){
        this.language = language;
        this.modelDirectory = modelDirectory;
        this.includeWhitespace = includeWhitespace;
        this.initialize();
    }

    private void initialize(){
        try{
            TokenizerModel tokenizerModel = new TokenizerModelLoader().load(new File(this.modelDirectory+"/"+ this.language.getCode()+"-token.bin"));
            tokenizer = new TokenizerME(tokenizerModel);
        }catch(Exception e){
            throw new AslException(e);
        }
    }

    public List<String> getTokens(String sentence){
        sentence = normalize(sentence);
        String[] tokenized;
        synchronized (this) {
            tokenized = tokenizer.tokenize(sentence);
        }
        if (lowercaseAll) this.lowercaseTokens(tokenized);
        return new ArrayList<>(Arrays.asList(tokenized));
    }

    public List<Span> getTokensAsSpans(String sentence){
        List<Span> results = new ArrayList<>();
        sentence = normalize(sentence);
        opennlp.tools.util.Span[] spans;
        synchronized (this) {
            spans = tokenizer.tokenizePos(sentence);
        }
        for (opennlp.tools.util.Span oSpan : spans){
            String surface = sentence.substring(oSpan.getStart(), oSpan.getEnd());
            if (lowercaseAll) surface = surface.toLowerCase();
            Span span = new Span(surface, oSpan.getStart(), oSpan.getEnd());
            results.add(span);
        }
        if (doPostProcess){
            results = this.postProcess(results);
        }
        return results;
    }

    public List<Word> getTokensAsWords(String sentence){
        List<Word> results = new ArrayList<>();
        for (Span span: this.getTokensAsSpans(sentence)){
            results.add(new Word(span.surface, span.starts, span.ends));
        }
        return results;
    }

    private void lowercaseTokens(String[] tokens){
        for (int i=0; tokens.length>i;i++){
            tokens[i] = tokens[i].toLowerCase();
        }
    }

    public List<Span> postProcess(List<Span> input){
        List<Span> results = this.postProcessApostrophes(input);
        return results;
    }

    public List<Span> postProcessApostrophes(List<Span> input){
        List<Span> results = new ArrayList<>();
        for (Span span : input){
            if (span.surface.length()<=1){
                results.add(span);
                continue;
            }
            String first = span.surface.substring(0,1);
            String last = span.surface.substring(span.surface.length()-1, span.surface.length());
            boolean inFirst = this.tokensToCutOff.contains(first);
            boolean inLast = this.tokensToCutOff.contains(last);
            if (inFirst && inLast){
                String middle = span.surface.substring(1,span.surface.length()-1);
                results.add(new Span(first, span.starts, span.starts+1));
                results.add(new Span(middle, span.starts+1, span.ends-1));
                results.add(new Span(last, span.ends-1, span.ends));
            }else if (inFirst){
                String remainder = span.surface.substring(1,span.surface.length());
                results.add(new Span(first, span.starts, span.starts+1));
                results.add(new Span(remainder, span.starts+1, span.ends));
            }else if (inLast){
                String remainder = span.surface.substring(0,span.surface.length()-1);
                results.add(new Span(remainder, span.starts, span.ends-1));
                results.add(new Span(last, span.ends-1, span.ends));
            }else{
                results.add(span);
            }
        }
        return results;
    }

    private String normalize(String text){
        text = text.replaceAll("’","'");
        text = text.replaceAll("“","");
        return text;
    }
}
