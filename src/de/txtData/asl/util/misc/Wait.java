/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Wastes processor cycles.
 */
public class Wait{

    public static void forKey(){
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String forUserInput(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void forSeconds(int s){
        try{
            Thread.sleep(s*1000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
