/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.dataStructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * FST-inspired dictionary
 */
public class RecursiveDictionary<T>{

    public LinkedHashMap<String,RecursiveDictionary<T>> map = new LinkedHashMap<>();
    public List<T> meanings = new ArrayList<>();

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
            rd = oldRD.map.get(part);
            if (rd == null) {
                rd = new RecursiveDictionary<T>();
                oldRD.map.put(part, rd);
            }
            oldRD = rd;
        }
        if (rd!=null) {
            if (!rd.meanings.contains(object)) {
                rd.meanings.add(object);
            }
        }
    }

    public String toString(){
        return this.meanings+map.toString();
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
