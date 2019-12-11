/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtdata.asl.util.files;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Helper class to write to a file, one line at a time.
 */
public class LineWriter {

    private Writer writer = null;

    public LineWriter(String fileName){
        try{
            writer = new PrintWriter(fileName);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public LineWriter(String fileName, String encoding){
        try{
            writer = new OutputStreamWriter(new FileOutputStream(fileName),encoding);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public LineWriter(String fileName, boolean append){
        try{
            writer = new PrintWriter(new FileOutputStream(fileName, append));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public LineWriter(String fileName, String encoding, boolean append){
        try{
            writer = new OutputStreamWriter(new FileOutputStream(fileName, append),encoding);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeLine(String line){
        if (line==null) return;
        if (!line.endsWith("\n")) line = line+"\n";
        try {
            writer.write(line);
            writer.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
