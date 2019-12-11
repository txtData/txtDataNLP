package de.txtdata.asl.util.dataStructures;

import de.txtdata.asl.util.misc.PrettyString;

/**
 * Class to hold features as used in Machine Learning tasks.
 * Each feature has a name and a double value.
 */
public class FeatureList {

  public KeyValuePairList<String, Double> featureValues = new KeyValuePairList<>();

  public FeatureList() {
  }

  public void add(String featureName, Double featureValue) {
    this.featureValues.add(featureName, featureValue);
  }

  public void set(String featureName, Double featureValue) {
    for (KeyValuePair kvp : this.featureValues) {
      if (kvp.key.equals(featureName)) {
        kvp.value = featureValue;
        return;
      }
    }
    this.featureValues.add(featureName, featureValue);
  }

  public Double get(String key) {
    return featureValues.get(key);
  }

  public void remove(String key) {
    this.featureValues.removeKey(key);
  }

  public double getNonNull(String key) {
    Double value = featureValues.get(key);
    if (value == null)
      return -1.0;
    return value;
  }

  public String toString() {
    return this.toString("");
  }

  public String toString(String indent) {
    StringBuilder sb = new StringBuilder();
    for (KeyValuePair kvp : this.featureValues) {
      sb.append(indent).append(PrettyString.create(kvp.key, 16)).append("\t").append(kvp.value).append("\n");
    }
    return sb.toString();
  }

  public String createHeader(String separator) {
    StringBuilder sb = new StringBuilder();
    for (KeyValuePair kvp : this.featureValues) {
      sb.append(kvp.key).append(separator);
    }
    String s = sb.toString();
    if (s.length() > separator.length())
      s = s.substring(0, s.length() - separator.length());
    return s;
  }

  public String toLine(String separator) {
    StringBuilder sb = new StringBuilder();
    for (KeyValuePair kvp : this.featureValues) {
      sb.append(PrettyString.create((double) kvp.value, 1, 5)).append(separator);
    }
    String s = sb.toString();
    if (s.length() > separator.length())
      s = s.substring(0, s.length() - separator.length());
    return s;
  }

}

