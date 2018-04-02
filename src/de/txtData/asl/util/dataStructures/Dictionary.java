/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Basic implementation of a dictionary that can contain various entries for each key.
 */
public class Dictionary<K,V>{

    private HashMap<K,List<V>> map = new HashMap<>();

    public Dictionary(){
        super();
    }

    public void addToList(K key, V value){
        List<V> list = this.map.get(key);
        if (list!=null){
            list.add(value);
        }else{
            list = new ArrayList<>();
            list.add(value);
            this.map.put(key, list);
        }
    }

    public void addToList(K key, V value, boolean noDuplicates){
        List<V> list = this.map.get(key);
        if (list!=null){
            if (!list.contains(value)) {
                list.add(value);
            }
        }else{
            list = new ArrayList<>();
            list.add(value);
            this.map.put(key, list);
        }
    }

    public void addToList(K key, List<V> values, boolean noDuplicates){
        for (V value : values){
            this.addToList(key, value, noDuplicates);
        }
    }

    public List<V> getList(K key){
        return this.map.get(key);
    }

    public boolean containsKey(K key){
        return this.map.containsKey(key);
    }

    public void removeKey(K key){
        this.map.remove(key);
    }

    public String toString(){
        return map.toString();
    }

}
