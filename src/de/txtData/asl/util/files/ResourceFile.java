/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.files;

import de.txtData.asl.util.dataStructures.Dictionary;
import de.txtData.asl.util.dataStructures.KeyValuePairList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Helper class to read a resource file.
 */
public class ResourceFile{
	
	public String          fileName = null;
	public String          pathName = null;
	
	protected boolean      ignoreCase            = true;
	private String         commentString         = null;
	
	private List<String>   list = new ArrayList<>();

   	protected ResourceFile(){
   	}

	public ResourceFile(String fileName, String directoryName, boolean ignoreCase, String commentString){
		this.ignoreCase     = ignoreCase;
		this.commentString  = commentString;
        if (commentString.equals("")) this.commentString = null;
		
		File file=null;
		if (directoryName != null){
			file = new File(directoryName);
		}
		
		if (file!=null && file.isDirectory() && fileName != null){
			file = new File(file, fileName);
		}
		
		if (file==null && fileName!=null){
			file = new File(System.getProperty("user.dir"), fileName);
		}
		
		try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            this.fileName = file.getName();
            this.pathName = file.getParent();
            
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
                    list.add(line);
            	}
            }
            in.close();
        }catch(Exception e){
            System.out.println("Cannot find file '"+file+"'. ");
            e.printStackTrace();
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
