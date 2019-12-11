package de.txtdata.asl.util.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;

public class FileIO {

    public static void writeToFile(String fileName, String content){
        SimpleFileWriter fileWriter = new SimpleFileWriter(fileName, "UTF-8");
        fileWriter.write(content);
        fileWriter.close();
    }

    public static String readFromFile(String fileName){
        return readFromFile(fileName, "UTF-8");
    }

    public static String readFromFile(String fileName, String charsetName){
        try {
            File file = new File(fileName);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            inputStream.close();
            return new String(data, charsetName);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void writeAsJson(String fileName, Object o){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(o);
        writeToFile(fileName,json);
    }

    public static void readJson(String fileName){

    }

}
