/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.ml;

import de.txtData.asl.util.dataStructures.KeyValuePair;
import de.txtData.asl.util.misc.PrettyString;

public class ARFFStringCreator {

    private FeatureBundle featureBundle;

    public ARFFStringCreator(FeatureBundle featureBundle){
        this.featureBundle = featureBundle;
    }

    public String getARFFHeader(String relationName, boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION ").append(relationName).append("\n");
        if (sFeatures) {
            for (KeyValuePair<String, String> sf : this.featureBundle.stringFeatures) {
                String type = "String";
                if(sf.value.equalsIgnoreCase("true") || sf.value.equalsIgnoreCase("false")){
                    type = "{true,false}";
                }
                sb.append("@ATTRIBUTE ").append(sf.key).append(" ").append(type).append("\n");
            }
        }
        if (dFeatures) {
            for (KeyValuePair<String, Double> df : this.featureBundle.doubleFeatures) {
                sb.append("@ATTRIBUTE ").append(df.key).append(" ").append("Numeric").append("\n");
            }
        }
        sb.append("\n").append("@DATA").append("\n");
        return sb.toString();
    }

    public String getARFFLine(boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        if (sFeatures) {
            for (KeyValuePair<String, String> sf : this.featureBundle.stringFeatures) {
                sb.append(sf.value).append(", ");
            }
        }
        if (dFeatures) {
            for (KeyValuePair<String, Double> df : this.featureBundle.doubleFeatures) {
                sb.append(PrettyString.create(df.value, 1, 5)).append(", ");
            }
        }
        String result = sb.toString();
        if (result.length()>2){
            result = result.substring(0,result.length()-2);
        }
        return result;
    }
}
