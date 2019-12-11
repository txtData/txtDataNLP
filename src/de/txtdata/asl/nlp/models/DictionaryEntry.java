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

package de.txtdata.asl.nlp.models;

import de.txtdata.asl.nlp.annotations.IAnnotationObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Representation of an entry in a dictionary.
 * Implements IAnnotationObject, so that text pieces can be marked with the dictionary questions that they match.
 */
public class DictionaryEntry implements IAnnotationObject {

    public String surface;
    public String type;
    public List<String> tags;

    public DictionaryEntry(String surface, String type){
        this.surface = surface;
        this.type = type;
    }

    public void addTag(String tag){
        if (this.tags==null) this.tags = new ArrayList<>();
        this.tags.add(tag);
    }

    public String getFirstTag(){
        if (this.tags==null || this.tags.isEmpty()) return null;
        return this.tags.get(0);
    }

    public static boolean hasTag(IAnnotationObject iAnnotationObject, String tag){
        if (!(iAnnotationObject instanceof DictionaryEntry)) return false;
        DictionaryEntry dictionaryEntry = (DictionaryEntry)iAnnotationObject;
        if (dictionaryEntry.tags.contains(tag)) return true;
        return false;
    }

    @Override
    public void setType(String type){
        this.type = type;
    }

    @Override
    public String getType(){
        return type;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        if(this.surface!=null) {
            sb.append(" '").append(this.surface).append("'");
        }
        if (this.tags!=null && !this.tags.isEmpty()){
            sb.append(" ").append(this.tags);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryEntry)) return false;

        DictionaryEntry entry = (DictionaryEntry) o;

        if (surface != null ? !surface.equals(entry.surface) : entry.surface != null) return false;
        if (tags != null ? !tags.equals(entry.tags) : entry.tags != null) return false;
        if (type != null ? !type.equals(entry.type) : entry.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = surface != null ? surface.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}
