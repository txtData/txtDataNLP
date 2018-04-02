/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.files;

import java.util.List;

/**
 * Helper class that represents a simple list of words.
 */
public class WordList extends ResourceFile {

    public WordList(){
        super();
    }

	public WordList(String file){
		super(file, null, true, "//");
	}

    public WordList(String file, String dir, boolean ignoreCase, String commentString){
        super(file, dir, ignoreCase, commentString);
    }

	public boolean isOnList(String surface){
		return super.containsLine(surface);
	}

    public List<String> getWords(){
        return this.getList();
    }

    public String toString(){
        return this.getList().toString();
    }

}
