/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.util.misc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Makes strings pretty.
 */
public class PrettyString{

    public static String create(Object o, int length){
        if (o==null) o="null";
        String s = o.toString();
        String result = s;
        for (int i=s.length(); i<length; i++){
            result=result+" ";
        }
        return result;
    }

    public static String create(Object s1, int length1, Object s2, int length2){
        String result = create(s1,length1);
        if (!result.substring(result.length()-1).equals(" ")){
            result=result+" ";
        }
        if (result.length()>length1){
            length2 = length2 - (result.length()-length1);
        }
        result = result+ create(s2,length2);
        return result;
    }

    public static String create(Object s1, int length1, Object s2, int length2, Object s3, int length3){
        return create(create(s1,length1,s2,length2),length1+length2,s3,length3);
    }

    public static String create(Object s1, int length1, Object s2, int length2, Object s3, int length3, Object s4, int length4){
        return create(create(s1,length1,s2,length2,s3,length3),length1+length2+length3,s4,length4);
    }

    public static String create(double d, int before, int after){
        String bS = "";
        for (int i=1; i<=before; i++){
            bS=bS+"#";
        }
        String aS = "";
        for (int i=1; i<=after; i++){
            aS=aS+"0";
        }
        DecimalFormat df = new DecimalFormat(bS+ "." +aS, new DecimalFormatSymbols(Locale.ENGLISH));
        String result = df.format(d);
        if (before>0 && result.startsWith(".")){
            result = "0"+result;
        }
        while (result.length()<before+after+1){
            result = " "+result;
        }
        return result;
    }

}

