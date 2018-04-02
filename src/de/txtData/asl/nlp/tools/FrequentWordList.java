/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.tools;

import de.txtData.asl.util.files.LineReader;
import java.util.HashMap;

/**
 * Reads in from a file a list of words, and their absolute frequencies derived from a corpus.
 * Allows querying for said frequency information.
 */
public class FrequentWordList{

    private HashMap<String,FrequentWord> map = new HashMap<>();
    private long   total       =  0;
    public  double value100    = -1;
    public  double value1000   = -1;
    public  double value10000  = -1;

    public FrequentWordList(String fileName){
        this.readFromFile(fileName);
    }

    public void override(String fileName){
        this.readFromFile(fileName);
    }

    public void override(String surface, int position, int count){
        FrequentWord fw = new FrequentWord();
        fw.word = surface;
        fw.position = position;
        fw.count = count;
        map.put(surface,fw);
    }

    private void readFromFile(String fileName){
        LineReader reader = new LineReader(fileName, "UTF8");
        int i = 0;
        String line;
        while ((line = reader.readLine())!=null){
            i++;
            FrequentWord fw = new FrequentWord();
            String parts[] = line.split(" ");
            fw.word = parts[0];
            fw.position = i;
            fw.count = Integer.parseInt(parts[1]);
            total += fw.count;
            map.put(parts[0],fw);
            if (i==100) value100 = fw.count;
            else if (i==1000) value1000 = fw.count;
            else if (i==10000) value10000 = fw.count;
        }
        if (i>0){
            total /= i;
        }
    }

    public FrequentWord lookUp(String word){
        word = word.toLowerCase();
        return map.get(word);
    }

    public int getCount(String word){
        FrequentWord fw = this.lookUp(word);
        if (fw!=null) return fw.count;
        return 0;
    }

    public class FrequentWord{
        public String word;
        public int position;
        public int count;

        public String toString(){
            return word+" ("+ position +":"+count+")";
        }

        public double getIDFApproximation(){
            double idf = value100 / count;
            idf = Math.log10(idf);
            if (idf<0.0) idf = 0.0;
            return idf;
        }
    }

    public double getIDFApproximation(double count){
        if (count < 0.5) count = 0.5;
        double idf = value100 / count;
        idf = Math.log10(idf);
        if (idf<0.0) idf = 0.0;
        return idf;
    }

}
