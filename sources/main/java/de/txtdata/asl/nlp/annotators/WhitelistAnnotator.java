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

package de.txtdata.asl.nlp.annotators;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.models.DictionaryEntry;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.nlp.models.Span;
import de.txtdata.asl.nlp.models.TextUnit;
import de.txtdata.asl.nlp.models.Word;
import de.txtdata.asl.util.dataStructures.RecursiveDictionary;
import de.txtdata.asl.util.files.ResourceFile;

/**
 * This annotators reads in dictionary files contained in a given directory, matches input texts against the
 * dictionary questions, and creates annotations for any text that matches these dictionary questions.
 * If getMatches() is not overridden equality between input text and dictionary questions are computed via strict string
 * equality.
 */
public class WhitelistAnnotator extends RecursiveDictionaryAnnotator<DictionaryEntry> {

    protected String dictionariesLocation;
    protected String typeIndicator      = "#type:";
    protected String tagIndicator       = "#tag:";
    protected String newTagIndicator    = "#newtag:";
    protected String clearTagsIndicator = "#cleartags";

    private   String  defaultType        = "NULL_TYPE";
    protected boolean ignoreCase         = true;



    protected WhitelistAnnotator(){}

    public WhitelistAnnotator(Language lang, String dictionariesLocation){
        this(lang, dictionariesLocation, true);
    }

    public WhitelistAnnotator(Language lang, String dictionariesLocation, boolean ignoreCase){
        super(lang);
        this.dictionariesLocation = dictionariesLocation;
        this.ignoreCase = ignoreCase;
        File dir = new File(this.dictionariesLocation);
        if (dir.isDirectory()){
            this.recursiveDirectoryReader("");
        }else{
            this.readFile(this.dictionariesLocation);
        }
    }

    public WhitelistAnnotator(Language lang){
        super(lang);
    }

    public WhitelistAnnotator(Language language, RecursiveDictionary<DictionaryEntry> completeDictionary, boolean ignoreCase){
        this.language = language;
        this.completeDictionary = completeDictionary;
        this.ignoreCase = ignoreCase;
    }

    public void addToDictionary(List<Word> words, String type, String tag){
        String surface = "";
        List<String> parts = new ArrayList<>();
        for (Word word : words){
            surface = surface + word.getSurface()+" ";
            parts.add(word.getSurface());
        }
        surface = surface.trim();
        String[] array = parts.stream().toArray(n -> new String[n]);
        DictionaryEntry entry = new DictionaryEntry(surface, type);
        entry.tags = new ArrayList<>();
        entry.tags.add(tag);
        this.completeDictionary.add(array, entry);
    }

    @Override
    protected List<Annotation> createAnnotations(RecursiveDictionaryMatch<DictionaryEntry> match, TextUnit textPiece, int start, int end){
        List<Annotation> results = new ArrayList<>();
        for (DictionaryEntry entry : match.dictionary.meanings){
            int startPos = textPiece.getWords().get(start).getStarts();
            int endPos = textPiece.getWords().get(end).getEnds();
            String surface = textPiece.getSurfaceText(startPos, endPos);
            Span span = new Span(surface,startPos,endPos);
            Annotation anno = new Annotation(span, entry);
            results.add(anno);
        }
        return results;
    }

    @Override
    public List<RecursiveDictionaryMatch<DictionaryEntry>> getMatches(Word word, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionaryMatch<DictionaryEntry>> results = new ArrayList<>();
        String surface = word.getSurface();
        if (this.ignoreCase)  surface = surface.toLowerCase();
        RecursiveDictionary<DictionaryEntry> result = dictionary.map.get(surface);
        RecursiveDictionaryMatch dm = new RecursiveDictionaryMatch(result, word.getSurface(), surface);
        if (result!=null) results.add(dm);
        return results;
    }

    protected void recursiveDirectoryReader(String directory){
        File location = new File(directory);
        if (location.isDirectory()){
            String[] items = location.list();
            for (String item : items){
                Path subItemPath = Paths.get(location.getPath(), item);
                
                if (!subItemPath.toFile().isDirectory()){
                    this.readFile(subItemPath.toString());
                }else{
                    this.recursiveDirectoryReader(subItemPath.toString());
                }
            }
        }else{
            // should not happen
        }
    }

    protected void readFile(String fileName){
        ResourceFile resourceFile = new ResourceFile(fileName, false, "//");
        String type = defaultType;
        List<String> tags = new ArrayList<>();
        for (String line : resourceFile.getList()) {
            line = line.trim();
            String[] parts = line.split("\t");
            String additionalTag = null;
            if (parts.length>1){
                additionalTag = parts[1].trim();
                line = parts[0].trim();
            }
            if (line.startsWith(typeIndicator)) {
                type = line.substring(typeIndicator.length(), line.length()).trim();
            }else if (line.startsWith(tagIndicator)) {
                String tag = line.substring(tagIndicator.length(), line.length()).trim();
                String[] tagParts = tag.split(",");
                for (String t : tagParts){
                    tags.add(t.trim());
                }
            }else if (line.startsWith(clearTagsIndicator)){
                tags.clear();
            }else if (line.startsWith(newTagIndicator)) {
                tags.clear();
                String tag = line.substring(newTagIndicator.length(), line.length()).trim();
                String[] tagParts = tag.split(",");
                for (String t : tagParts){
                    tags.add(t.trim());
                }
            }else if (line.length()>0){
                parts = line.split(" ");
                DictionaryEntry entry = new DictionaryEntry(line, type);
                entry.tags = new ArrayList<>();
                entry.tags.addAll(tags);
                if (additionalTag!=null){
                    entry.tags.add(additionalTag);
                }
                this.completeDictionary.add(parts, entry);
            }
        }
    }
}
