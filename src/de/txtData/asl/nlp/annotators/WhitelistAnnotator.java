/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.annotators;

import de.txtData.asl.nlp.annotations.TextUnit;
import de.txtData.asl.nlp.models.DictionaryEntry;
import de.txtData.asl.nlp.models.Language;
import de.txtData.asl.nlp.annotations.Annotation;
import de.txtData.asl.nlp.models.Span;
import de.txtData.asl.nlp.models.Word;
import de.txtData.asl.util.dataStructures.RecursiveDictionary;
import de.txtData.asl.util.files.ResourceFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This annotator reads in dictionary files contained in a given directory, matches input texts against the
 * dictionary entries, and creates annotations for any text that matches these dictionary entries.
 * If getMatches() is not overridden equality between input text and dictionary entries are computed via strict string
 * equality.
 */
public class WhitelistAnnotator extends RecursiveDictionaryAnnotator<DictionaryEntry> {

    public String directory;
    public String typeIndicator = "#type:";
    public String tagIndicator ="#tag:";
    public String newTagIndicator = "#newtag:";
    public String clearTagsIndicator = "#cleartags";

    public WhitelistAnnotator(Language lang, String directory){
        super(lang);
        this.directory = directory;
        File dir = new File(this.directory);
        if (dir.isDirectory()){
            this.recursiveDirectoryReader("");
        }else{
            System.err.println(this.directory + " is not a directory!");
        }
    }

    public WhitelistAnnotator(Language lang){
        super(lang);
        File dir = new File(this.directory);
        if (dir.isDirectory()){
            this.recursiveDirectoryReader("");
        }else{
            System.err.println(this.directory + " is not a directory!");
        }
    }

    @Override
    protected List<Annotation> createAnnotations(RecursiveDictionary<DictionaryEntry> dictionary, TextUnit textPiece, int start, int end){
        List<Annotation> results = new ArrayList<>();
        for (DictionaryEntry entry : dictionary.meanings){
            int startPos = textPiece.getWords().get(start).starts;
            int endPos = textPiece.getWords().get(end).ends;
            String surface = textPiece.getTextSurface(startPos, endPos);
            Span span = new Span(surface,startPos,endPos);
            Annotation anno = new Annotation(span, entry);
            results.add(anno);
        }
        return results;
    }

    @Override
    public List<RecursiveDictionary<DictionaryEntry>> getMatches(Word word, RecursiveDictionary<DictionaryEntry> dictionary){
        List<RecursiveDictionary<DictionaryEntry>> results = new ArrayList<>();
        RecursiveDictionary<DictionaryEntry> result = dictionary.map.get(word.surface);
        if (result!=null) results.add(result);
        return results;
    }

    private void recursiveDirectoryReader(String subDir){
        File dir = new File(directory+"/"+subDir);
        if (dir.isDirectory()){
            String[] files = dir.list();
            for (String file : files){
                String fileName =  subDir + "/" + file;
                int i = file.indexOf(".");
                File subFile = new File(directory+"/"+fileName);
                if (!subFile.isDirectory()){
                    this.readFile(directory+"/"+fileName);
                }else{
                    this.recursiveDirectoryReader(subDir+"/"+file);
                }
            }
        }else{
            // should not happen
        }
    }

    private void readFile(String fileName){
        ResourceFile resourceFile = new ResourceFile(fileName, null, false, "//");
        String type = "noTypeDefined";
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
                tags.add(tag);
            }else if (line.startsWith(clearTagsIndicator)){
                tags.clear();
            }else if (line.startsWith(newTagIndicator)) {
                String tag = line.substring(newTagIndicator.length(), line.length()).trim();
                tags.clear();
                tags.add(tag);
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
