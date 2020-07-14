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
