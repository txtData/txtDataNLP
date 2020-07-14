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
import de.txtdata.asl.util.misc.PrettyString;

public class CSVStringCreator {

    private FeatureBundle featureBundle;

    public CSVStringCreator(FeatureBundle featureBundle){
        this.featureBundle = featureBundle;
    }

    public String getCSVHeader(String firstColumn, boolean sFeatures, boolean dFeatures){
        return this.getCSVHeader(firstColumn, null, sFeatures, dFeatures);
    }

    public String getCSVHeader(String firstColumn, String surface, boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        if (firstColumn!=null) {
            sb.append(firstColumn);
        }
        sb.append(", ");
        if (surface!=null){
            sb.append(surface).append(", ");
        }
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
        return this.getCSVLine(id, null, sFeatures, dFeatures);
    }

    public String getCSVLine(String id, String surface, boolean sFeatures, boolean dFeatures){
        StringBuilder sb = new StringBuilder();
        if (id!=null) {
            sb.append(id);
        }
        sb.append(", ");
        if (surface!=null){
            sb.append("\"").append(surface).append("\", ");
        }
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
