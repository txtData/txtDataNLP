/*
 *  Copyright 2013-2018 Michael Kaisser
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

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.annotations.Annotation;
import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.TextUnit;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.util.dataStructures.KeyValuePairList;
import de.txtData.asl.util.dataStructures.RecursiveDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a fairly fast algorithm to match (many) dictionary questions against input text,
 * somewhat inspired by a Finite State Transducer.
 * All dictionaries are kept in memory, so if the dictionaries become very large, this isn't the right approach.
 * @param <T>
 */
public abstract class RecursiveDictionaryAnnotator<T> extends AbstractAnnotator {

    // All of the dictionaries.
    protected RecursiveDictionary<T> completeDictionary = new RecursiveDictionary<>();

    protected RecursiveDictionaryAnnotator(){}

    public RecursiveDictionaryAnnotator(Language lang){
        super(lang);
    }

    protected abstract List<Annotation> createAnnotations(RecursiveDictionaryMatch<T> match, TextUnit textUnit, int start, int end);
    protected abstract List<RecursiveDictionaryMatch<T>> getMatches(Word word, RecursiveDictionary<T> dictionary);

    public void annotate(TextUnit sentence){
        List<Annotation> results = new ArrayList<>();
        List<RecursiveDictionaryMatch<T>> dictionaries = new ArrayList<>();
        List<Integer> startPos = new ArrayList<>();
        for (int i=0; sentence.getWords().size()>i ;i++){
            Word word = sentence.getWords().get(i);
            if (word.surface.trim().equals("")) continue; // ignore whitespace, if present
            List<RecursiveDictionaryMatch<T>> newDictionaries = new ArrayList<>();
            List<Integer> newStartPos = new ArrayList<>();
            int j=-1;
            for (RecursiveDictionaryMatch<T> dic : dictionaries){
                j++;
                List<RecursiveDictionaryMatch<T>> newDics = this.getMatches(word, dic.dictionary);
                if (newDics == null) continue;
                for (RecursiveDictionaryMatch<T> newDic : newDics){
                    if (!newDic.dictionary.meanings.isEmpty()) {
                        List<Annotation> toAdd = this.createAnnotations(newDic, sentence, startPos.get(j), i);
                        results.addAll(toAdd);
                    }
                    newDictionaries.add(newDic);
                    newDic.matches.addAll(0, dic.matches);
                    newStartPos.add(startPos.get(j));
                }
            }
            dictionaries = newDictionaries;
            startPos = newStartPos;

            List<RecursiveDictionaryMatch<T>> rds = this.getMatches(word, this.completeDictionary);
            if (rds!=null){
                for(RecursiveDictionaryMatch<T> dm : rds) {
                    dictionaries.add(dm);
                    startPos.add(i);
                    if (!dm.dictionary.meanings.isEmpty()) {
                        List<Annotation> toAdd = this.createAnnotations(dm, sentence, i, i);
                        results.addAll(toAdd);
                    }
                }
            }
        }
        sentence.addAnnotations(results);
    }


    public RecursiveDictionary<T> query(RecursiveDictionary<T> dictionary, String key){
        RecursiveDictionary<T> dic = dictionary.map.get(key);
        return dic;
    }

    public RecursiveDictionary<T> getDictionary(){
        return this.completeDictionary;
    }

    protected class RecursiveDictionaryMatch<T>{
        public RecursiveDictionary<T> dictionary;
        public KeyValuePairList<String,String> matches = new KeyValuePairList<>();

        public RecursiveDictionaryMatch(RecursiveDictionary dictionary, String surface, String pattern){
            this.dictionary = dictionary;
            this.matches.add(surface,pattern);
        }


    }

}
