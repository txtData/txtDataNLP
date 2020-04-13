/***
 * Copyright 2013-2020 Michael Kaisser
 ***/

package de.txtdata.asl.ml;

import de.txtdata.asl.util.misc.PrettyString;
import de.txtdata.asl.util.dataStructures.Bag;

/**
 * Class to collect evaluation information.
 * Information will be stored relating to to the instance it concerns.
 * Resembles the training phase of a Naive Bayesian Classifier.
 * @param <T> The instance that's evaluated.
 */
public class InstanceBasedEvaluation<T> {

    public double dividend_modifier = 0;
    public double divisor_modifier = 0;

    private Bag<T> totals         = new Bag<>();
    private Bag<T> truePositives  = new Bag<>();
    private Bag<T> falsePositives = new Bag<>();
    private Bag<T> trueNegatives  = new Bag<>(); // note that TNs are neither used for precision nor recall.
    private Bag<T> falseNegatives = new Bag<>();


    public void addTruePositive(T element){
        this.totals.add(element);
        this.truePositives.add(element);
    }

    public void addFalsePositive(T element){
        this.totals.add(element);
        this.falsePositives.add(element);
    }

    public void addTrueNegatives(T element){
        this.totals.add(element);
        this.trueNegatives.add(element);
    }

    public void addFalseNegatives(T element){
        this.totals.add(element);
        this.falseNegatives.add(element);
    }

    public String prettyString(){
        StringBuilder sb = new StringBuilder();
        int elemWidth = this.getLongestElement()+2;
        boolean hasFalseNegatives = !falseNegatives.isEmpty();
        for (T element : totals.getAsSortedList()){
            double total = totals.getValue(element);
            double tp = truePositives.getValue(element);
            double fp = falsePositives.getValue(element);
            double fn = falseNegatives.getValue(element);
            double precision = tp / (tp + fp);
            double recall   = tp / (tp + fn);
            String pString = "P: "+ PrettyString.create(precision, 1, 3);
            String rString = "R: "+ PrettyString.create(recall,1,3);

            if ((tp+fp)==0) pString = "P:   ---";
            if ((tp+fn)==0) rString = "R:   ---";
            String result = PrettyString.create((int)total, 5,  element.toString(), elemWidth, pString, 10);
            if (hasFalseNegatives) {
                result = result + PrettyString.create(rString, 20);
            }
            if (divisor_modifier !=0 || dividend_modifier!=0){
                double precisionMod = (tp + dividend_modifier) / (tp + fp + divisor_modifier);
                String rpString = "RP: " + PrettyString.create(precisionMod, 1, 3);
                result = result + PrettyString.create(rpString,20);
            }
            result = result
                    + PrettyString.create("TP: "+(int)tp, 8)
                    + PrettyString.create("FP: "+(int)fp, 8)
                    + PrettyString.create("FN: "+(int)fn, 8);
            sb.append(result).append("\n");
        }
        return sb.toString();
    }

    public String toFeatureFileFormat(String name){
        return this.toFeatureFileFormat(name, 0, false);
    }

    public String toFeatureFileFormat(String name, int minValue, boolean addComments){
        StringBuilder sb = new StringBuilder();
        sb.append("name\t").append(name).append("\n");
        for (T element : totals.getAsSortedList()){
            double tp = truePositives.getValue(element);
            double fp = falsePositives.getValue(element);
            double precisionMod = (tp + dividend_modifier) / (tp + fp + divisor_modifier);
            if ((tp+fp)<minValue){
                if (addComments){
                    sb.append("//");
                }else{
                    continue;
                }
            }
            sb.append(element.toString()).append("\t");
            sb.append(PrettyString.create(precisionMod,1,5));
            if (addComments){
                sb.append("\t\t//")
                        .append("Total:").append((int)totals.getValue(element)).append(", ")
                        .append("TP:").append((int)truePositives.getValue(element)).append(", ")
                        .append("FP:").append((int)falsePositives.getValue(element));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getLongestElement(){
        int longest = 0;
        for (T element : totals.getAsSortedList()){
            int length = element.toString().length();
            if (length>longest) longest=length;
        }
        return longest;
    }
}
