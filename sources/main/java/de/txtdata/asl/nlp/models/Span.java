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

package de.txtdata.asl.nlp.models;

/*
 * Representation for a region in a text.
 * Please note: A Span has no meaning attached to it. If a text span is supposed to carry meaning, use annotation.
 */
public class Span{
    private String surface;
    private int starts;
    private int ends;

    public Span(String surface, int starts, int ends){
        this.setSurface(surface);
        this.setStarts(starts);
        this.setEnds(ends);
    }

    public Span recreate(int offset){
        return new Span(this.getSurface(), this.getStarts() +offset, this.getEnds() +offset);
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public int getStarts() {
        return starts;
    }

    public void setStarts(int starts) {
        this.starts = starts;
    }

    public int getEnds() {
        return ends;
    }

    public void setEnds(int ends) {
        this.ends = ends;
    }

    public boolean contains(Span s){
        return (s.getStarts() >= this.getStarts() && s.getEnds() <= this.getEnds());
    }

    public int getLength(){
        return this.getEnds() - this.getStarts();
    }

    // returns a negative value if the spans overlap.
    public int differenceTo(Span other){
        int diff1 =  this.getStarts() - other.getEnds();
        int diff2 =  this.getEnds() - other.getStarts();
        int diff1a = Math.abs(diff1);
        int diff2a = Math.abs(diff2);
        if ((diff1!=diff1a) != (diff2!=diff2a)){
            return 0-Math.min(diff1a,diff2a);
        }
        return Math.min(diff1a,diff2a);
    }

    /**
    *
    * @param s another span.
    * @return A new span. Please note that the 'text' field will always be null;
    */
    public Span subsume(Span s){
        Span result = new Span(null, this.getStarts(), this.getEnds());
        if (s==null) return result;
        if (result.getStarts() ==-1 || result.getStarts() > s.getStarts() && s.getStarts() !=-1){
            result.setStarts(s.getStarts());
        }
        if (result.getEnds() ==-1 || result.getEnds() < s.getEnds() && s.getEnds() !=-1){
            result.setEnds(s.getEnds());
        }
        return result;
    }

    public boolean includes(Span s){
        return includes(s, true);
    }

    public boolean includes(Span s, boolean orEquals){
        if ((this.getStarts() < s.getStarts() && this.getEnds() > s.getEnds())
                ||(this.getStarts() <= s.getStarts() && this.getEnds() > s.getEnds())
                ||(this.getStarts() < s.getStarts() && this.getEnds() >= s.getEnds())
                )return true;
        if (!orEquals) return false;
        return (this.getStarts() <= s.getStarts() && this.getEnds() >= s.getEnds());
    }

    public boolean hasSamePositions(Span s){
        return (this.getStarts() == s.getStarts() && this.getEnds() == s.getEnds());
    }

    public boolean somehowOverlaps(Span other){
        int differeceTo = this.differenceTo(other);
        return (differeceTo<=0);
    }

    public boolean contains(int index){
        return (this.getStarts() <=index && this.getEnds() >=index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Span span = (Span) o;
        if (getEnds() != span.getEnds()) return false;
        if (getStarts() != span.getStarts()) return false;
        if (getSurface() != null ? !getSurface().equals(span.getSurface()) : span.getSurface() != null) return false;
        return true;
    }

    @Override
    public String toString(){
        return "'"+ this.getSurface() +"' "+ this.getStarts() +"-"+ this.getEnds();
    }

    @Override
    public int hashCode(){
        return this.toString().hashCode();
    }

}