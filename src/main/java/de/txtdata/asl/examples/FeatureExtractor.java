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

package de.txtdata.asl.examples;

import de.txtdata.asl.ml.ARFFStringCreator;
import de.txtdata.asl.ml.AnnotationWithFeatures;
import de.txtdata.asl.ml.CSVStringCreator;
import de.txtdata.asl.nlp.annotations.Annotation;
import de.txtdata.asl.nlp.annotations.AnnotationList;
import de.txtdata.asl.nlp.annotators.AbstractCreator;
import de.txtdata.asl.nlp.annotators.PosPatternAnnotator;
import de.txtdata.asl.nlp.annotators.SubsumedAnnotationsRemover;
import de.txtdata.asl.nlp.models.*;
import de.txtdata.asl.nlp.tools.OpenNLPTagger;
import de.txtdata.asl.nlp.tools.OpenNLPTokenizer;

import java.util.List;

/**
 * This class demonstrates how to extract features for a machine learning model from detected entities in a text.
 * It will first use POS rules to detect noun chunks in text and then compute a few features for each noun chunk.
 * These features are written to an ARFF file and could then be used to learn which candidates indeed are English names.
 */
public class FeatureExtractor extends AbstractCreator {

    public String openNlpModelDirectory = ".\\data\\models\\";
    public String dictionariesLocation  = ".\\data\\dictionaries\\examples\\englishNames_EN.txt";

    private OpenNLPTokenizer openNLPTokenizer;
    private OpenNLPTagger openNLPTagger;
    private PosPatternAnnotator posPatternAnnotator;
    private SubsumedAnnotationsRemover subsumedAnnotationsRemover;

    public static void main(String[] args) {
        FeatureExtractor pipeline = new FeatureExtractor(Language.ENGLISH);
        TextUnit analyzedSentence = pipeline.create("Dr. John Miller and Peter S. Johnson met in Birmingham.");
        createAnnotationsWithFeatures(analyzedSentence);
        collectFeatures(analyzedSentence);
        String arff = createARRFFileContent(analyzedSentence);
        String csv = createCSVFileContent(analyzedSentence);

        System.out.println("Sentence analysis:");
        System.out.println(analyzedSentence);
        System.out.println("\n\n");
        System.out.println("Training data in CSV format:");
        System.out.println(csv);
        System.out.println("\n\n");
        System.out.println("Training data in ARFF format:");
        System.out.println(arff);

    }


    /**
     * Standard constructor
     * @param language The language of this annotators.
     */
    public FeatureExtractor(Language language) {
        super(language);
        this.initialize();
    }


    private static void createAnnotationsWithFeatures(TextUnit analyzedSentence){
        AnnotationList annotationsToAdd = new AnnotationList();
        for (Annotation annotation : analyzedSentence.getAnnotations("nameSignatures")){
            annotationsToAdd.add(new AnnotationWithFeatures(annotation));
        }
        analyzedSentence.getAnnotations().removeAnnotations(analyzedSentence.getAnnotations("nameSignatures"));
        analyzedSentence.addAnnotations(annotationsToAdd);
    }

    private static void collectFeatures(TextUnit analyzedSentence){
        for (Annotation annotation : analyzedSentence.getAnnotations("nameSignatures")){
            double hasFirstName = 0.0;
            double hasLastName = 0.0;
            double hasTitle = 0.0;
            AnnotationList nameAnnotations = analyzedSentence.getAnnotations().getAnnotationsBetween(annotation.getStarts(), annotation.getEnds()).getAnnotations("englishNames");
            for (Annotation nameAnnotation : nameAnnotations){
                DictionaryEntry de = (DictionaryEntry)nameAnnotation.getAnnotationObject();
                if (de.tags.contains("firstNames") && hasFirstName==0.0) hasFirstName = 1.0;
                if (de.tags.contains("lastNames") && hasLastName==0.0) hasLastName = 1.0;
                if (de.tags.contains("titles") && hasTitle==0.0) hasTitle = 1.0;
            }
            int wordCount = analyzedSentence.getWordsBetween(annotation.getStarts(), annotation.getEnds()).size();
            int namePartCount = nameAnnotations.size();
            AnnotationWithFeatures awf = (AnnotationWithFeatures) annotation;
            awf.featureBundle.addDoubleFeature("wordCount", (double)wordCount);
            awf.featureBundle.addDoubleFeature("namePartCount", (double)namePartCount);
            awf.featureBundle.addDoubleFeature("hasFirstName", hasFirstName);
            awf.featureBundle.addDoubleFeature("hasLastName", hasLastName);
            awf.featureBundle.addDoubleFeature("hasTitle", hasTitle);
        }
    }

    private static String createARRFFileContent(TextUnit analyzedSentence){
        StringBuilder sb = new StringBuilder();
        AnnotationList nameAnnotations = analyzedSentence.getAnnotations("nameSignatures");
        boolean addedHeader = false;
        for (Annotation nameAnnotation : nameAnnotations){
            AnnotationWithFeatures awf = (AnnotationWithFeatures) nameAnnotation;
            ARFFStringCreator arffStringCreator = new ARFFStringCreator(awf.featureBundle);
            if (!addedHeader){
                sb.append(arffStringCreator.getARFFHeader("englishNameDetection", true, true));
                addedHeader = true;
            }
            sb.append(arffStringCreator.getARFFLine(true,true)).append("\n");
        }
        return sb.toString();
    }

    private static String createCSVFileContent(TextUnit analyzedSentence){
        StringBuilder sb = new StringBuilder();
        AnnotationList nameAnnotations = analyzedSentence.getAnnotations("nameSignatures");
        boolean addedHeader = false;
        int i = 0;
        for (Annotation nameAnnotation : nameAnnotations){
            AnnotationWithFeatures awf = (AnnotationWithFeatures) nameAnnotation;
            CSVStringCreator csvStringCreator = new CSVStringCreator(awf.featureBundle);
            if (!addedHeader){
                sb.append(csvStringCreator.getCSVHeader("id", "surface",true,true)).append("\n");
                addedHeader = true;
            }
            sb.append(csvStringCreator.getCSVLine(i+"", nameAnnotation.getSurface(),true,true)).append("\n");
            i++;
        }
        return sb.toString();
    }


    /**
     * Perform initialization: Load OpenNLP tokenizer etc.
     */
    public void initialize() {
        this.openNLPTokenizer = new OpenNLPTokenizer(this.getLanguage(), this.openNlpModelDirectory);
        this.openNLPTagger = new OpenNLPTagger(this.getLanguage(), this.openNlpModelDirectory);
        this.posPatternAnnotator = new PosPatternAnnotator(this.language, this.dictionariesLocation, true);
        this.subsumedAnnotationsRemover = new SubsumedAnnotationsRemover(this.language, "nameSignatures");
    }

    /**
     * Creates a <code>TextUnit</code> from a string.
     * @param sentence The text to analyse. Output of sentence splitter. Should be one sentence.
     * @return A TextUnit containing an analysed representation of the input text.
     */
    public TextUnit create(String sentence) {
        TextUnit tu = new TextUnit(sentence);
        this.annotate(tu);
        return tu;
    }


    public void annotate(TextUnit sentence) {
        List<Span> tokens = this.openNLPTokenizer.getTokensAsSpans(sentence.getSurfaceText());
        List<Word> words = this.openNLPTagger.getTaggedWords(tokens);
        sentence.setWords(words);
        this.posPatternAnnotator.annotate(sentence);
        this.subsumedAnnotationsRemover.annotate(sentence);
    }

}
