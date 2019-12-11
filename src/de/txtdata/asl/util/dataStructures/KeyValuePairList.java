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
package de.txtdata.asl.util.dataStructures;


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

}
