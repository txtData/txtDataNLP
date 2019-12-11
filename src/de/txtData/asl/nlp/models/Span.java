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

package de.txtData.asl.nlp.models;

/*
 * Representation for a region in a text.
 * Please note: A Span has no meaning attached to it. If a text span is supposed to carry meaning, use annotation.
 */
public class Span{
    public String surface;
    public int starts;
    public int ends;

    public Span(String surface, int starts, int ends){
        this.surface = surface;
        this.starts = starts;
        this.ends   = ends;
    }

    public Span recreate(int offset){
        return new Span(this.surface, this.starts+offset, this.ends+offset);
    }

    public boolean contains(Span s){
        return (s.starts>=this.starts && s.ends<=this.ends);
    }

    public int getLength(){
        return this.ends-this.starts;
    }

    // returns a negative value if the spans overlap.
    public int differenceTo(Span other){
        int diff1 =  this.starts - other.ends;
        int diff2 =  this.ends - other.starts;
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
        Span result = new Span(null, this.starts, this.ends);
        if (s==null) return result;
        if (result.starts==-1 || result.starts>s.starts && s.starts!=-1){
            result.starts = s.starts;
        }
        if (result.ends==-1 || result.ends<s.ends && s.ends!=-1){
            result.ends = s.ends;
        }
        return result;
    }

    public boolean includes(Span s){
        return includes(s, true);
    }

    public boolean includes(Span s, boolean orEquals){
        if ((this.starts<s.starts && this.ends>s.ends)
                ||(this.starts<=s.starts && this.ends>s.ends)
                ||(this.starts<s.starts && this.ends>=s.ends)
                )return true;
        if (!orEquals) return false;
        return (this.starts<=s.starts && this.ends>=s.ends);
    }

    public boolean hasSamePositions(Span s){
        return (this.starts==s.starts && this.ends==s.ends);
    }

    public boolean somehowOverlaps(Span other){
        int differeceTo = this.differenceTo(other);
        return (differeceTo<=0);
    }

    public boolean contains(int index){
        return (this.starts<=index && this.ends>=index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Span span = (Span) o;
        if (ends != span.ends) return false;
        if (starts != span.starts) return false;
        if (surface != null ? !surface.equals(span.surface) : span.surface != null) return false;
        return true;
    }

    @Override
    public String toString(){
        return "'"+this.surface +"' "+this.starts+"-"+this.ends;
    }

    @Override
    public int hashCode(){
        return this.toString().hashCode();
    }
}