/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.dataStructures;


/**
 * Basic implementation of a key value pair.
 */
public class KeyValuePair<K,V> {

    public K key;
    public V value;

    public KeyValuePair(){
    }

    public KeyValuePair(K key, V value){
        this.key = key;
        this.value = value;
    }

    public String toString(){
        return this.key+": "+value;
    }
}
