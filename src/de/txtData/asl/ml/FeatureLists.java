/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.ml;

import de.txtData.asl.util.files.DirectoryReader;

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
