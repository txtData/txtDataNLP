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

package de.txtdata.asl.nlp.models;

import de.txtdata.asl.util.dataStructures.KeyValuePairList;


/**
 * Representation of an entry in a dictionary, together with the parts that matched against the dictionary entry.
 * Implements IAnnotationObject, so that text pieces can be marked with the dictionary questions that they match.
 */
public class DictionaryMatch extends DictionaryEntry {

    public KeyValuePairList<String,String> matches = new KeyValuePairList<>();

    public DictionaryMatch(String surface, String type){
        super(surface, type);
    }

    public DictionaryMatch(DictionaryEntry dictionaryEntry){
        super(dictionaryEntry.surface, dictionaryEntry.type);
        this.tags = dictionaryEntry.tags;
    }

}
