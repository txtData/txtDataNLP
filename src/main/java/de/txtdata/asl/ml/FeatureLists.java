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

import de.txtdata.asl.util.files.DirectoryReader;

import java.util.HashMap;

public class FeatureLists {

    public HashMap<String,FeatureList> featureLists = new HashMap<>();

    public FeatureLists(String directory){
        initialize(directory);
    }

    public void initialize(String directory){
        DirectoryReader directoryReader = new DirectoryReader(directory,".txt");
        for (String fileName : directoryReader.getFiles()){
            FeatureList featureList = new FeatureList(directory+"//"+fileName);
            featureLists.put(featureList.attributeName,featureList);
        }
    }

    public FeatureList get(String attributeName){
        return featureLists.get(attributeName);
    }

    public Double get(String attributeName, String key){
        FeatureList featureList = featureLists.get(attributeName);
        if (featureList==null) return null;
        return featureList.get(key);
    }
}
