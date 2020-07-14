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
