/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.files;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to read in a file, line by line or all at once.
 */
public class LineReader {

    private BufferedReader reader = null;

    public LineReader(String fileName){
        try{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "ISO-8859-1"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public LineReader(String fileName, String encoding){
        try{
           reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String readLine(){
        try{
            return reader.readLine();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String readAll(){
        try{
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = this.readLine())!=null){
                sb.append(line).append("\n");
            }
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<String> readAllLines(){
        try{
            List<String> results = new ArrayList<>();
            String line;
            while ((line = this.readLine())!=null){
                results.add(line);
            }
            return results;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
