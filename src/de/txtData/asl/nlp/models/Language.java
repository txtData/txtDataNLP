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

package de.txtData.asl.nlp.models;

import java.io.Serializable;

/**
 * Representation of a language of a text.
 */
public class Language implements Serializable{

    public static final Language ENGLISH   = new Language("en");
    public static final Language GERMAN    = new Language("de");
    public static final Language MANDARIN  = new Language("cmn");
    public static final Language SPANISH   = new Language("es");
    public static final Language FRENCH    = new Language("fr");

    private String langCode;

    public Language(String langCode){
        this.langCode = langCode;
    }

    public boolean is(Language lang){
        if (this.langCode==null) return false;
        if (this.langCode.equalsIgnoreCase(lang.langCode)) return true;
        return false;
    }

    public String getCode(){
        return this.langCode;
    }

}
