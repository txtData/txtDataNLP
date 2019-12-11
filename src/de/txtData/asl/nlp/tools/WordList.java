/*
 *  Copyright 2013-2019 Michael Kaisser
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
package de.txtData.asl.nlp.tools;

import de.txtData.asl.util.files.ResourceFile;

import java.util.List;

/**
 * Helper class that represents a simple list of words.
 */
public class WordList extends ResourceFile {

	public WordList(String file){
		super(file, true, "//");
	}

    public WordList(String file, boolean ignoreCase, String commentString){
        super(file, ignoreCase, commentString);
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
