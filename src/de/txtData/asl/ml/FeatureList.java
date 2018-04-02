/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.ml;

import de.txtData.asl.util.dataStructures.KeyValuePairList;
import de.txtData.asl.util.files.LineReader;

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

