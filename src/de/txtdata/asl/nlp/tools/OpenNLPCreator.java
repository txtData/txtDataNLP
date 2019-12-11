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

package de.txtdata.asl.nlp.tools;

import de.txtdata.asl.nlp.annotators.AbstractCreator;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.nlp.models.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator for combined OpenNLP tokenizer and tagger.
 */
public class OpenNLPCreator extends AbstractCreator {

    public String modelDirectory;
    public boolean postProcess = false;

    private OpenNLPTokenizer openNLPTokenizer;
    private OpenNLPTagger openNLPTagger;

    public OpenNLPCreator(Language language, String modelDirectory){
        super(language);
        this.modelDirectory = modelDirectory;
    }

    public void initialize(){
        this.initialize(false);
    }

    public void initialize(boolean runTagger){
        this.openNLPTokenizer = new OpenNLPTokenizer(this.language, this.modelDirectory, false);
        this.openNLPTokenizer.doPostProcess = this.postProcess;
        if (runTagger) {
            this.openNLPTagger = new OpenNLPTagger(this.language, this.modelDirectory);
        }
    }

    public void setTokenizer(OpenNLPTokenizer openNLPTokenizer){
        this.openNLPTokenizer = openNLPTokenizer;
    }

    public void setTagger(OpenNLPTagger openNLPTagger){
        this.openNLPTagger = openNLPTagger;
    }

    public OpenNLPTokenizer getTokenizer(){
        return this.openNLPTokenizer;
    }

    public OpenNLPTagger getTagger(){
        return this.openNLPTagger;
    }

    public TextUnit create(String sentence){
        return this.create(sentence, -1, -1);
    }

    public TextUnit create(String sentence, int from, int to){
        List<Span> spans = openNLPTokenizer.getTokensAsSpans(sentence);
        List<Word> words = new ArrayList<>();
        if (this.openNLPTagger!=null) {
            words = openNLPTagger.getTaggedWords(spans);
        }else{
            for (Span span : spans){
                Word word = new Word(span.surface);
                word.starts = span.starts;
                word.ends = span.ends;
                words.add(word);
            }
        }
        if (from>0) this.applyOffset(words, from);
        TextUnit textPiece = new TextUnit(sentence);
        textPiece.setWords(words);
        return textPiece;
    }


    private void applyOffset(List<Word> words, int offset){
        for (Word word : words){
            word.starts += offset;
            word.ends += offset;
        }
    }

    public void annotate(TextUnit textPiece){

    }

}
