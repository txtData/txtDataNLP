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

package de.txtdata.asl.ml;

import de.txtdata.asl.util.dataStructures.KeyValuePairList;
import de.txtdata.asl.util.files.LineReader;

public class FeatureList{

    public String attributeName;
    public KeyValuePairList<String,Double> featureValues = new KeyValuePairList<>();

    public FeatureList(String fileName){
        this.loadFromFile(fileName);
    }

    public void loadFromFile(String fileName){
        LineReader lineReader = new LineReader(fileName,"UTF-8");
        for (String line : lineReader.readAllLines()){
            if (line.startsWith("//")) continue;
            String[] parts = line.split("//");
            if (parts.length>=2) {
                String comment = parts[1];
            }
            parts = parts[0].split("\t");
            if (parts.length!=2) continue;
            if (parts[0].equalsIgnoreCase("name")){
                this.attributeName = parts[1];
            }else{
                featureValues.add(parts[0],Double.parseDouble(parts[1]));
            }
        }
    }

    public Double get(String key){
        return featureValues.get(key);
    }

    public double getNonNull(String key){
        Double value = featureValues.get(key);
        if (value==null) return -1.0;
        return value;
    }

    public String toString(){
        return "FeatureList:"+ attributeName;
    }
}

