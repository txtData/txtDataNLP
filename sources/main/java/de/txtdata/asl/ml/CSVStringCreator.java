package de.txtdata.asl.ml;

import de.txtdata.asl.util.dataStructures.KeyValuePair;
import de.txtdata.asl.util.misc.PrettyString;

public class CSVStringCreator {

    private FeatureBundle featureBundle;

    public CSVStringCreator(FeatureBundle featureBundle){
        this.featureBundle = featureBundle;
    }

    public String getCSVHeader(String firstColumn, boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        sb.append(firstColumn).append(", ");
        if (sFeatures) {
            for (KeyValuePair<String, String> sf : this.featureBundle.stringFeatures) {
                sb.append(sf.key).append(", ");
            }
        }
        if (dFeatures) {
            for (KeyValuePair<String, Double> df : this.featureBundle.doubleFeatures) {
                sb.append(df.key).append(", ");
            }
        }
        String result = sb.toString();
        if (result.length()>2){
            result = result.substring(0,result.length()-2);
        }
        return result;
    }

    public String getCSVLine(String id, boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(", ");
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
