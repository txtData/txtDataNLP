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

package de.txtdata.asl.nlp.annotations;

/**
 * Semantic container for an annotation.
 * Could represent a person, a date, a location or maybe a paragraph or a sentence.
 * Type is a descriptor for the semantic type of the annotation. Please note that different instances of one
 * the same AnnotationObject class can have different semantic types.
 * A NamedEntity class for example could return "PERSON", "DATE", "LOCATION" for type.
 * Any details are handled in the implementations.
 */
public interface IAnnotationObject{
    public String getType();
    public void setType(String type);
}
