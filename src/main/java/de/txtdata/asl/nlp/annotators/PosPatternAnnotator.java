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

package de.txtdata.asl.nlp.annotators;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.util.dataStructures.RecursiveDictionary;
import de.txtdata.asl.util.misc.AslException;
import de.txtdata.asl.nlp.models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Extends a WhitelistAnnotator so that that matching of a string in a dictionary against a word in a text isn't
 * done via strict string equality, but with more complex POS rules.
 */
public class PosPatternAnnotator extends WhitelistAnnotator {

    private static String DIVIDER = "\\.";      // string is a regex
    private static String DIVIDER_ESCAPE = "§"; // not a regex, just a plain string.

    private int removeCharactersFromPattern = 1;
    private int allowAddingCharactersFromPattern = 3;

    protected PosPatternAnnotator(){}

    public PosPatternAnnotator(Language lang, String directory){
        super(lang, directory);
    }

    public PosPatternAnnotator(Language lang, String directory, boolean ignoreCase){
        super(lang, directory, ignoreCase);
    }

    /**
     * Allows to adjust how the matching of suffixes with the tilde operator works in detail.
     * @param removeCharactersFromPattern Number of characters that will be removed from the pattern before matching.
     * @param allowAddingCharactersFromPattern Number of characters that are allowed to be additionally present at the end of a word.
     */
    public void overrideMorphologyMatchingBehavior(int removeCharactersFromPattern, int allowAddingCharactersFromPattern){
        this.removeCharactersFromPattern = removeCharactersFromPattern;
        this.allowAddingCharactersFromPattern = allowAddingCharactersFromPattern;
    }

    @Override
    public void annotate(TextUnit sentence){
        super.annotate(sentence);
    }

    @Override
    protected List<Annotation> createAnnotations(RecursiveDictionaryMatch<DictionaryEntry> match, TextUnit textPiece, int start, int end){
        List<Annotation> results = new ArrayList<>();
        for (DictionaryEntry entry : match.dictionary.meanings){
            int startPos = textPiece.getWords().get(start).getStarts();
            int endPos = textPiece.getWords().get(end).getEnds();
            String surface = textPiece.getSurfaceText(startPos, endPos);
            Span span = new Span(surface,startPos,endPos);
            DictionaryMatch dm = new DictionaryMatch(entry);
            dm.matches = match.matches;
            Annotation anno = new Annotation(span, dm);
            results.add(anno);
        }
        return results;
    }

    @Override
    public List<RecursiveDictionaryMatch<DictionaryEntry>> getMatches(Word word, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionaryMatch<DictionaryEntry>> results = new ArrayList<>();
        Set<String> keys = dictionary.map.keySet();
        for (String posPattern : keys){
            if (this.matches(word, posPattern)){
                RecursiveDictionaryMatch dm = new RecursiveDictionaryMatch(dictionary.map.get(posPattern), word.getSurface(), posPattern);
                results.add(dm);
            }
            results.addAll(this.wildCardMatches(word, posPattern, dictionary));
        }
        return results;
    }

    protected boolean matches(Word word, String posPattern){
        if (posPattern.equals("+")) return true;
        if (posPattern.equals("")) return false;
        List<String> parts = Arrays.asList(posPattern.split(DIVIDER));
        if (parts.size()>=1){
            String surface = word.getSurface();
            if (this.ignoreCase)  surface = surface.toLowerCase();
            boolean b = partEquals(surface, parts.get(0));
            if (!b) return false;
        }
        if (parts.size()>=2 && !(word.getRoot()==null && parts.get(1).equals(""))){
            String part = parts.get(1);
            if (this.ignoreCase)  part = part.toLowerCase();
            boolean b = partEquals(word.getRoot(), part);
            if (!b) return false;
        }
        if (parts.size()>=3){
            boolean b = partEquals(word.getPOS().toLowerCase(), parts.get(2).toLowerCase());
            if (!b) return false;
        }
        if (parts.size()>=4){
            if (word.getMorph()==null) return false;
            List<String> ruleMorphs = Arrays.asList(parts.get(3).toLowerCase().split("\\|"));
            List<String> wordMorphs = Arrays.asList(word.getMorph().toLowerCase().split("\\|"));
            for (String ruleMorph : ruleMorphs){
                if (!wordMorphs.contains(ruleMorph)) return false;
            }
        }
        return true;
    }

    protected List<RecursiveDictionaryMatch<DictionaryEntry>> wildCardMatches(Word word, String posPattern, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionaryMatch<DictionaryEntry>> results = new ArrayList<>();
        if(posPattern.equals("*")){
            RecursiveDictionary<DictionaryEntry> rd = new RecursiveDictionary<>();
            rd.map.put("*",dictionary.map.get(posPattern));
            results.add(new RecursiveDictionaryMatch<DictionaryEntry>(rd, word.getSurface(), posPattern));
            results.add(new RecursiveDictionaryMatch<DictionaryEntry>(dictionary.map.get(posPattern), word.getSurface(), posPattern));
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
                        results.add(new RecursiveDictionaryMatch<DictionaryEntry>(dictionary.map.get(posPattern), word.getSurface(), posPattern));
                    }
                    if (i1>1){
                        i1--;
                    }
                    if (i2>1){
                        i2--;
                        String newPosPattern = "*{"+i1+","+i2+"}";
                        RecursiveDictionary<DictionaryEntry> rd = new RecursiveDictionary<>();
                        rd.map.put(newPosPattern,dictionary.map.get(posPattern));
                        results.add(new RecursiveDictionaryMatch<DictionaryEntry>(rd, word.getSurface(), posPattern));
                    }

                }
            }catch(Exception e){
                throw new AslException(e);
            }
        }
        return results;
    }

    /**
     * @param token The actual user text and its linguistic analysis
     * @param patternPart The dictionary entry
     * @return true if match, false otherwise
     */
    private boolean partEquals(String token, String patternPart){
        if (token==null) return false;
        token = token.replaceAll(DIVIDER, DIVIDER_ESCAPE);
        if (patternPart.length()==0){
            return true;
        }else if (token.equals(patternPart)) {
            return true;
        }else if (patternPart.startsWith("/")){
            token = token.toUpperCase();
            patternPart = patternPart.substring(1,patternPart.length()).toUpperCase();
            if (token.endsWith(patternPart)) return true;
        }else if (patternPart.endsWith("/")){
            token = token.toUpperCase();
            patternPart = patternPart.substring(0,patternPart.length()-1).toUpperCase();
            if (token.startsWith(patternPart)) return true;
        }else if (patternPart.endsWith("~")){
            if (this.getTildeMatch(patternPart, token)) return true;
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

    protected boolean getTildeMatch(String pattern, String text){
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();
        if (pattern.length()> this.removeCharactersFromPattern) {
            pattern = pattern.substring(0, pattern.length() - (1 + this.removeCharactersFromPattern));
            if (text.startsWith(pattern) && text.length() - pattern.length() < (
                    1 + this.removeCharactersFromPattern + this.allowAddingCharactersFromPattern))
                return true;
        }
        return false;
    }
}
