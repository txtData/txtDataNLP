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
package de.txtData.asl.util.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Basic implementation of a dictionary that can contain various values for each key.
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
