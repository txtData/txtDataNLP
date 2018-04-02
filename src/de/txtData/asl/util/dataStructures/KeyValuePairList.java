/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.dataStructures;


import java.util.ArrayList;

/**
 * Basic implementation of a list of key value pairs.
 */
public class KeyValuePairList<K,V> extends ArrayList<KeyValuePair<K,V>>{

    public KeyValuePairList(){
        super();
    }

    public void add(K key, V value){
        int found = -1;
        for(int i = 0; this.size()>i ;i++){
            if (this.get(i).key.equals(key)){
                found =i;
                break;
            }
        }
        if (found!=-1){
            this.set(found,new KeyValuePair<K, V>(key,value));
        }else{
            this.add(new KeyValuePair<K, V>(key, value));
        }
    }

    public V get(K key){
        for(KeyValuePair<K,V> kvp : this){
            if (kvp.key.equals(key)){
                return kvp.value;
            }
        }
        return null;
    }

    public void removeKey(K key){
        KeyValuePair<K,V> found = null;
        for(KeyValuePair<K,V> kvp : this){
            if (kvp.key.equals(key)){
                found = kvp;
                break;
            }
        }
        if (found!=null) this.remove(found);
    }

    public String toStringHTML(){
        String result = "";
        for(KeyValuePair<K,V> kvp : this){
            result += kvp.key+"     "+kvp.value+"<br>";
        }
        return result;
    }
}
