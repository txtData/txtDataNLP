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
package de.txtdata.asl.nlp.tools;

import de.txtdata.asl.util.files.ResourceFile;

import java.util.HashMap;

/**
 * Reads a list of words from a file, together with their absolute frequencies derived from a corpus.
 * Allows querying this frequency information.
 */
public class FrequentWordList{

    private String separator = " ";

    private HashMap<String,FrequentWord> map = new HashMap<>();
    private long totalSize  =  0;
    private double value100 = -1;

    public FrequentWordList(String fileName){
        this.readFromFile(fileName);
    }

    public FrequentWordList(String fileName, String separator){
        this.separator = separator;
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
        ResourceFile resourceFile = new ResourceFile(fileName);
        int i = 0;
        for (String line : resourceFile.getList()){
            i++;
            FrequentWord fw = new FrequentWord();
            String parts[] = line.split(separator);
            fw.word = parts[0];
            fw.position = i;
            fw.count = Integer.parseInt(parts[1]);
            totalSize += fw.count;
            map.put(parts[0],fw);
            if (i==100) value100 = fw.count;
        }
        if (i>0){
            totalSize /= i;
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

    public long getTotalSize(){
        return totalSize;
    }

    public double getIDFApproximation(double count){
        if (count < 0.5) count = 0.5;
        double idf = value100 / count;
        idf = Math.log10(idf);
        if (idf<0.0) idf = 0.0;
        return idf;
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
}
