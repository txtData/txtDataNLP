package de.txtdata.asl.util.files;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;

public class SimpleFileWriter {

    private BufferedWriter writer;

    public SimpleFileWriter(String fileName){
        try {
            this.writer = new BufferedWriter(new FileWriter(fileName));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public SimpleFileWriter(String fileName, String encoding){
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static SimpleFileWriter getAnsiWriter(String fileName){
        return new SimpleFileWriter(fileName, "Cp1252");
    }

    public SimpleFileWriter write(String str){
        try{
            this.writer.write(str);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public SimpleFileWriter write(List<String> lines, boolean appendNewline){
        for (String line : lines){
            if (appendNewline) line = line + "\n";
            this.write(line);
        }
        return this;
    }

    public void close(){
        try{
            this.writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
