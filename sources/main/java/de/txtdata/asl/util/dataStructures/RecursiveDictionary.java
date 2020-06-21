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
package de.txtdata.asl.util.dataStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * FST-inspired dictionary.
 */
public class RecursiveDictionary<T>{

    public LinkedHashMap<String, RecursiveDictionary<T>> map = new LinkedHashMap<>();
    public List<T> meanings = new ArrayList<>();
    public RecursiveDictionary before = null;

    public RecursiveDictionary(){
    }

    public void add(String key, T object){
        this.add(key.split(" "), object);
    }

    public void add(String[] keys, T object){
        RecursiveDictionary<T> rd = null;
        RecursiveDictionary<T> oldRD = this;
        for (int i=0; i<keys.length ;i++){
            String part = keys[i];
            if (part.length()==0) continue;
            if ((part.startsWith("?") || part.endsWith("?")) && part.length()>1){
                if (part.startsWith("?")) part = part.substring(1,part.length());
                if (part.endsWith("?")) part = part.substring(0,part.length()-1);
                String[] newKeys = Arrays.copyOfRange(keys, i+1, keys.length);
                oldRD.add(newKeys, object);
                if(newKeys.length>0){
                    oldRD.add(newKeys, object);
                }else{
                    oldRD.meanings.add(object);
                }
            }
            rd = oldRD.map.get(part);
            if (rd == null) {
                rd = new RecursiveDictionary<T>();
                oldRD.map.put(part, rd);
            }
            rd.before = oldRD;
            oldRD = rd;
        }
        if (rd!=null) {
            if (!rd.meanings.contains(object)) {
                rd.meanings.add(object);
            }
        }
    }

    public String toString(){
        return this.meanings+this.map.toString();
    }

    public String toString(boolean pretty){
        if (!pretty) return this.toString();
        return this.toString("");
    }

    public String toString(String indent){
        String s = "";
        if (this.meanings.size()>0){
            s = s + " " + this.meanings;
        }
        s = s + "\n";
        for (String key : this.map.keySet()){
            s=s+indent+key;
            s=s+this.map.get(key).toString(indent+"  ");
        }
        return s;
    }

}
