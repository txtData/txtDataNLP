/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.tools;

import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.util.files.WordList;
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
    public boolean postProcess = false;
    public String knownAbbreviationsFile = null;

    public String tokensToCutOff = "\"'«»-";

    private TokenizerME tokenizer;
    private WordList knownAbbreviations;

    public OpenNLPTokenizer(Language language, String modelDirectory){
        this(language, modelDirectory, false, null);
    }

    public OpenNLPTokenizer(Language language, String modelDirectory, boolean includeWhitespace){
        this(language, modelDirectory, includeWhitespace, null);
    }

    public OpenNLPTokenizer(Language language, String modelDirectory, boolean includeWhitespace, String knownAbbreviationsFile){
        this.language = language;
        this.modelDirectory = modelDirectory;
        this.includeWhitespace = includeWhitespace;
        this.knownAbbreviationsFile = knownAbbreviationsFile;
        this.initialize();
    }

    private void initialize(){
        try{
            TokenizerModel tokenizerModel = new TokenizerModelLoader().load(new File(this.modelDirectory+"/"+ this.language.getCode()+"-token.bin"));
            tokenizer = new TokenizerME(tokenizerModel);
            if (this.knownAbbreviationsFile !=null){
                this.knownAbbreviations = new WordList(this.knownAbbreviationsFile, null, false, "//");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getTokens(String sentence){
        sentence = normalize(sentence);
        String[] tokenized = tokenizer.tokenize(sentence);
        if (lowercaseAll) this.lowercaseTokens(tokenized);
        return new ArrayList<>(Arrays.asList(tokenized));
    }

    public List<Span> getTokensAsSpans(String sentence){
        List<Span> results = new ArrayList<>();
        sentence = normalize(sentence);
        opennlp.tools.util.Span[] spans = tokenizer.tokenizePos(sentence);
        for (opennlp.tools.util.Span oSpan : spans){
            String surface = sentence.substring(oSpan.getStart(), oSpan.getEnd());
            if (lowercaseAll) surface = surface.toLowerCase();
            Span span = new Span(surface, oSpan.getStart(), oSpan.getEnd());
            results.add(span);
        }
        if (postProcess){
            results = this.postProcess(results);
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
        results = this.postProcessAbbreviations(results);
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

    public List<Span> postProcessAbbreviations(List<Span> input){
        if (this.knownAbbreviations==null) return input;
        List<Span> results = new ArrayList<>();
        boolean addLast = true;
        for (int i=0; i<input.size()-1 ; i++){
            String s = input.get(i).surface +input.get(i+1).surface;
            if (!this.knownAbbreviations.containsLine(s)){
                results.add(input.get(i));
                addLast = true;
            }else{
                Span span = new Span(s, input.get(i).starts, input.get(i+1).ends);
                results.add(span);
                addLast = false;
                i++;
            }
        }
        if (addLast){
            results.add(input.get(input.size()-1));
        }
        return results;
    }

    private String normalize(String text){
        text = text.replaceAll("’","'");
        text = text.replaceAll("“","");
        return text;
    }
}
