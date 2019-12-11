/*
 *  Copyright 2013-2018 Michael Kaisser
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
package de.txtdata.asl.util.misc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Makes strings pretty.
 */
public class PrettyString {

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
        String beforeString = "";
        for (int i=1; i<=before; i++){
            beforeString=beforeString+"#";
        }
        String afterString = "";
        for (int i=1; i<=after; i++){
            afterString=afterString+"0";
        }
        DecimalFormat df = new DecimalFormat(beforeString+ "." +afterString, new DecimalFormatSymbols(Locale.ENGLISH));
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

