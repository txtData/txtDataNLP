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

import de.txtdata.asl.util.dataStructures.Dictionary;
import de.txtdata.asl.util.dataStructures.KeyValuePairList;
import de.txtdata.asl.util.misc.AslException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to read a resource file.
 */
public class ResourceFile{
	
	private String  fileName = null;
	private String  commentString = null;
    private boolean ignoreCase = true;

	private List<String>   list = new ArrayList<>();

    public ResourceFile(String fileName){
        this(fileName, false, null);
    }

    public ResourceFile(String fileName, boolean ignoreCase){
        this(fileName, ignoreCase, null);
    }

	public ResourceFile(String fileName, boolean ignoreCase, String commentString){
        this.fileName = fileName;
		this.ignoreCase     = ignoreCase;
		this.commentString  = commentString;
        if (commentString!=null && commentString.equals("")) this.commentString = null;

		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), StandardCharsets.UTF_8))) {
            String line;
            while((line = in.readLine()) != null){
            	line=line.trim();
            	if (this.ignoreCase) line=line.toLowerCase();
            	if (this.commentString==null || (!line.equals("") && !line.startsWith(commentString))){
                    if (line.startsWith("\"") && line.endsWith("\"")){
                        line = line.substring(1,line.length()-1);
                    }
                    if (this.commentString!=null && line.contains(this.commentString)){
                        line=line.substring(0,line.indexOf(this.commentString)).trim();
                    }
                    this.list.add(line);
            	}
            }
        }catch(Exception e){
            throw new AslException(e);
        }
	}

	public boolean containsLine(String line){
		if (line==null) return false;
		if (this.ignoreCase) line=line.toLowerCase();
		return list.contains(line);
	}

    public void addToList(List<String> other){
        for (String s : other){
            if (this.ignoreCase) s=s.toLowerCase();
            if (!this.list.contains(s)) this.list.add(s);
        }
    }

    public Dictionary<String,String> asDictionary(){
        Dictionary<String,String> dictionary = new Dictionary<>();
        for (String line : list){
            String[] parts = line.split("\t");
            if (parts.length>=2){
                dictionary.addToList(parts[0], parts[1]);
            }
        }
        return dictionary;
    }

    public KeyValuePairList<String,String> asKeyValuePairList(){
        KeyValuePairList<String,String> keyValuePairs = new KeyValuePairList<>();
        for (String line : list){
            String[] parts = line.split("\t");
            if (parts.length>=2){
                keyValuePairs.add(parts[0], parts[1]);
            }
        }
        return keyValuePairs;
    }

	public List<String> getList(){
		return this.list;
	}

}
