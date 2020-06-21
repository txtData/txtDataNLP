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

import de.txtdata.asl.util.dataStructures.KeyValuePair;
import de.txtdata.asl.util.dataStructures.KeyValuePairList;

public class FeatureBundle{

    public KeyValuePairList<String,String> stringFeatures = new KeyValuePairList<>();
    public KeyValuePairList<String,Double> doubleFeatures = new KeyValuePairList<>();

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n");
        for (KeyValuePair<String,String> sf : stringFeatures){
            sb.append("\t\t").append(sf).append("\n");
        }
        for (KeyValuePair<String,Double> df : doubleFeatures){
            sb.append("\t\t").append(df).append("\n");
        }
        return sb.toString();
    }

    public void addStringFeature(String name, String value){
        this.stringFeatures.add(name, value);
    }

    public void addDoubleFeature(String name, Double value){
        this.doubleFeatures.add(name, value);
    }
}
