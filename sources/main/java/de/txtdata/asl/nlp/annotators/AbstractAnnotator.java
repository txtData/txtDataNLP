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

package de.txtdata.asl.nlp.annotators;

import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.TextUnit;

/**
 * Abstract class for an 'Annotator'. An Annotator analyses a piece of text and detects parts of this text that have
 * a meaning, or are in any other way special. It marks these by creating annotations.
 * It needs text that is preprocessed and comes in form of a TextUnit. It is however up to the
 * implementation of the annotators which functionality of TextUnit it uses.
 */
public abstract class AbstractAnnotator {

    protected Language language;

    protected AbstractAnnotator(){}

    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public AbstractAnnotator(Language language){
        this.language = language;
    }

    /**
     * May be overridden by the subclass. Any initialization should happen here, rather than in the constructor, since
     * deserialization will run this method, but not the constructor.
     */
    public void initialize(){}

    /**
     * Processes input text and adds annotations to it.
     * @param sentence The text that should be processed.
     */
    public abstract void annotate(TextUnit sentence);

    public void setLanguage(Language language) { this.language = language; }

    public Language getLanguage(){
        return this.language;
    }

    public boolean isForLanguage(Language lang){
        return this.language.is(lang);
    }

}
