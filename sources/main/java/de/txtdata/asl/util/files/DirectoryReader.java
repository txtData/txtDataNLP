/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtdata.asl.util.files;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to get all files in a directory.
 */
public class DirectoryReader {

    private File directory = null;
    private List<String> files = new ArrayList<>();

    public DirectoryReader(String directory, String suffix){
        try{
            File file = new File(directory);
            if (file.isDirectory()){
                this.directory = file;
                String[] unfiltered = this.directory.list();
                for (String f : unfiltered){
                    if (f.endsWith(suffix)) files.add(f);
                }
            }else{
                System.err.println("'"+directory+"' is not a directory!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getFiles(){
        return this.files;
    }

}
