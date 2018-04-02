/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.ml;

import de.txtData.asl.util.dataStructures.KeyValuePair;
import de.txtData.asl.util.dataStructures.KeyValuePairList;

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
}
