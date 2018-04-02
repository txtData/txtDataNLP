/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.models.DictionaryEntry;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.util.dataStructures.RecursiveDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Extends a WhitelistAnnotator, so that that matching of a string in a dictionary against a word in a text, isn't
 * done via strict string equality anymore, but with more complex POS rules.
 */
public class PosPatternAnnotator extends WhitelistAnnotator {

    public final String DIVIDER = "\\.";      // string is a regex
    public final String DIVIDER_ESCAPE = "#"; // not a regex, just a plain string.

    public PosPatternAnnotator(Language lang, String directory){
        super(lang,directory);
    }

    @Override
    public List<RecursiveDictionary<DictionaryEntry>> getMatches(Word word, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionary<DictionaryEntry>> results = new ArrayList<>();
        Set<String> keys = dictionary.map.keySet();
        for (String posPattern : keys){
            if (this.matches(word, posPattern)){
                results.add(dictionary.map.get(posPattern));
            }
            results.addAll(this.wildCardMatches(posPattern, dictionary));
        }
        return results;
    }

    protected boolean matches(Word word, String posPattern){
        if (posPattern.equals("+")) return true;
        if (posPattern.equals("")) return false;
        List<String> parts = Arrays.asList(posPattern.split(DIVIDER));
        if (parts.size()>=1){
            boolean b = partEquals(word.surface, parts.get(0));
            if (!b) return false;
        }
        if (parts.size()>=2){
            boolean b = partEquals(word.root, parts.get(1));
            if (!b) return false;
        }
        if (parts.size()>=3){
            boolean b = partEquals(word.pos, parts.get(2));
            if (!b) return false;
        }
        if (parts.size()>=4){
            if (word.morph==null) return false;
            List<String> ruleMorphs = Arrays.asList(parts.get(3).toLowerCase().split("\\|"));
            List<String> wordMorphs = Arrays.asList(word.morph.toLowerCase().split("\\|"));
            for (String ruleMorph : ruleMorphs){
                if (!wordMorphs.contains(ruleMorph)) return false;
            }
        }
        return true;
    }

    protected List<RecursiveDictionary<DictionaryEntry>> wildCardMatches(String posPattern, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionary<DictionaryEntry>> results = new ArrayList<>();
        if(posPattern.equals("*")){
            RecursiveDictionary<DictionaryEntry> rd = new RecursiveDictionary<>();
            rd.map.put("*",dictionary.map.get(posPattern));
            results.add(rd);
            results.add(dictionary.map.get(posPattern));
        }else if (posPattern.startsWith("*")){
            try {
                int b1 = posPattern.indexOf("{");
                int c = posPattern.indexOf(",");
                int b2 = posPattern.indexOf("}");
                if (b2 > 0 && b2 > c && c > b1) {
                    String s1 = posPattern.substring(b1 + 1, c);
                    String s2 = posPattern.substring(c + 1, b2);
                    Integer i1 = Integer.parseInt(s1);
                    Integer i2 = Integer.parseInt(s2);

                    if (i1==1){
                        results.add(dictionary.map.get(posPattern));
                    }
                    if (i1>1){
                        i1--;
                    }
                    if (i2>1){
                        i2--;
                        String newPosPattern = "*{"+i1+","+i2+"}";
                        RecursiveDictionary<DictionaryEntry> rd = new RecursiveDictionary<>();
                        rd.map.put(newPosPattern,dictionary.map.get(posPattern));
                        results.add(rd);
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * @param token The actual user text and its linguistic analysis
     * @param patternPart What's found in the dictionary file
     * @return true if match, false otherwise
     */
    private boolean partEquals(String token, String patternPart){
        if (token==null) return false;
        token = token.replaceAll(DIVIDER, DIVIDER_ESCAPE);
        if (patternPart.length()==0){
            return true;
        }else if (token.equalsIgnoreCase(patternPart)) {
            return true;
        }else if (patternPart.startsWith("/")){
            token = token.toUpperCase();
            patternPart = patternPart.substring(1,patternPart.length()).toUpperCase();
            if (token.endsWith(patternPart)) return true;
        }else if (patternPart.endsWith("/")){
            token = token.toUpperCase();
            patternPart = patternPart.substring(0,patternPart.length()-1).toUpperCase();
            if (token.startsWith(patternPart)) return true;
        }else if (patternPart.startsWith("(") && patternPart.endsWith(")")){
            patternPart = patternPart.substring(1,patternPart.length()-1);
            List<String> parts = Arrays.asList(patternPart.split("\\|"));
            for (String part : parts){
                boolean b = partEquals(token, part);
                if (b) return true;
            }
        }
        return false;
    }
}
