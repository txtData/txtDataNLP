/***
 * Copyright 2013-2015 Michael Kaisser
 ***/

package de.txtData.asl.nlp.models;

import java.lang.Math;

/*
 * Representation for a region in a text.
 * Please note: A Span has no meaning attached to it. If a text span has a meaning, it becomes an annotation.
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
        if (s.starts>=this.starts && s.ends<=this.ends){
            return true;
        }
        return false;
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
    * @param s
    * @return Please note that the 'text' field will always be null;
    */
    public Span subsume(Span s){
        Span result = new Span(null, this.starts, this.ends);
        if (s==null) return result;

        if (result.starts==-1){
            result.starts = s.starts;
        }else if (result.starts>s.starts && s.starts!=-1){
            result.starts = s.starts;
        }

        if (result.ends==-1){
            result.ends = s.ends;
        }else if (result.ends<s.ends && s.ends!=-1){
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
        if (this.starts<=s.starts && this.ends>=s.ends) return true;
        return false;
    }

    public boolean hasSamePositions(Span s){
        if (this.starts==s.starts && this.ends==s.ends) return true;
        return false;
    }

    public boolean somehowOverlaps(Span other){
        int differeceTo = this.differenceTo(other);
        if (differeceTo<=0) return true;
        return false;
    }

    /*public boolean crosses(Span other){
        if (this.starts>=other.starts){
            if (this.starts<=other.ends) return true;
        }else{
            if (this.ends>=other.starts) return true;
        }
        return false;
    }*/

    public boolean contains(int index){
        if (this.starts<=index && this.ends>=index) return true;
        return false;
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