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
package de.txtdata.asl.util.dataStructures;

import java.io.Serializable;
import java.util.*;

/**
 * Data structure that keeps counts (or scores) of entities:
 * Each time an item is added, it is checked whether this item was added before.
 * If not, it is added with count 1.0; if yes, this item's count is increased by 1.0.
 * This class is also useful for various ranking, scoring and sorting tasks.
 *
 * @param <T> The entity that should be counted.
 */
public class Bag<T> implements Serializable{

    private HashMap<T, Double> hashMap;

	/**
	 * Constructor which creates an empty bag.
	 */
    public Bag(){
		hashMap = new LinkedHashMap<>();
	}

    /**
     * Constructor which creates an empty bag.
     * @param preserveInsertionOrder If true the order in which items were added is preserved.
     */
    public Bag(boolean preserveInsertionOrder){
        if (preserveInsertionOrder){
            hashMap = new LinkedHashMap<>();
        }else{
            hashMap = new HashMap<>();
        }
    }

	/**
	 * Copy constructor.
	 */
    public Bag(Bag old){
        if (old.hashMap instanceof LinkedHashMap){
            this.hashMap = new LinkedHashMap<>(old.hashMap);
        }else{
            this.hashMap = new HashMap<>(old.hashMap);
        }
    }

    /**
	 * Add a value with weight '1'.
	 */
	public void add(T key){
		if (hashMap.containsKey(key)){
		    double d = hashMap.get(key);
            hashMap.put(key, d + 1.0);
		}else{
            hashMap.put(key, 1.0);
		}
	}

    /**
     * Add a value with the specific weight.
     */
	public void add(T key, double value){
		if (hashMap.containsKey(key)){
			double d = hashMap.get(key);
			d = d + value;
            hashMap.put(key, d);
		}else{
            hashMap.put(key, value);
		}
	}

	/**
	 * Overrides the specified key with a new value. The old value is lost.
	 */
    public void override(T key, double newValue){
        hashMap.put(key, newValue);
    }

	/**
	 * Multiplies the key's value with <code>modifier</code>.
	 * If the bag does not contain key, no action is taken.
	 */
	public void multiply(T key, double multiplier){
        if (hashMap.containsKey(key)){
            double d = hashMap.get(key);
            d *= multiplier;
            hashMap.put(key, d);
        }
    }

	/**
	 * Add the contents of another bag to this bag.
	 * @param bag The bag that should be added.
	 * @return This bag.
	 */
	public void addBag(Bag<T> bag){
		for (T key : bag.keySet()) {
        	double value = bag.getValue(key);
        	this.add(key, value);
     	}
	}

	/**
	 * Add the contents of another bag to this bag
	 * @param bag The bag that should be added.
	 * @param weightMultiplier The values of bag will be multiplied with this value before they are added.
	 * @return This bag.
	 */
	public void addBag(Bag<T> bag, double weightMultiplier){
        for (T key : bag.keySet()){
        	double value = bag.getValue(key)*weightMultiplier;
        	this.add(key,value);
     	}
	}

	/**
	 * Add the contents of a list.
	 * @param list The list that should be added.
	 */
	public void addList(List<T> list){
		for (T item : list) {
        	this.add(item);
     	}
	}

	/**
	 * Add the contents of a list.
	 * @param list The list that should be added.
	 * @param value The list's entries will be added with this value.
	 */
	public void addList(List<T> list, double value){
        for (T item : list) {
        	this.add(item, value);
     	}
	}

	/**
	 * Returns the number of keys in the bag. Note that keys with value 0 will also be counted.
	 * @return The number of keys in the bag.
	 */
    public int size(){
        return this.hashMap.size();
    }

	/**
	 * Returns true if this bag has no entries.
	 * @return True if this bag has no qntries.
	 */
    public boolean isEmpty(){
        return this.hashMap.isEmpty();
    }

    /**
     * Checks if this bag contains the specified key.
     */
    public boolean containsKey(T key){
        return this.hashMap.containsKey(key);
    }

    /**
     * Returns the key in this bag that equals the specified key.
     */
    public T getEqualKey(T key){
        if (!this.hashMap.containsKey(key)) return null;
        for (T existing : this.hashMap.keySet()){
            if (key.equals(existing)){
                return existing;
            }
        }
        return null;
    }

    /**
     * Returns the value for the specified key.
     */
	public double getValue(T key){
		double d = 0;
		if (hashMap.get(key)!=null) {
            d = hashMap.get(key);
        }
		return d;
	}

	/**
	 * Returns the sum of all values for all keys currently in the bag.
	 */
	public double getValueSum(){
		double result=0;
		for (T key : this.keySet()) {
			if (hashMap.get(key)!=null) {
				double d = hashMap.get(key);
				result = result + d;
			}
		}
		return result;
	}

    /**
     * Returns a set representation of all keys in this bag.
     */
    public Set<T> keySet(){
        return this.hashMap.keySet();
    }


    public ArrayList<T> getKeysAsList(){
		return new ArrayList<T>(this.hashMap.keySet());
	}

	/**
	 * Returns the entries as a sorted list, highest first.
     * Note that behavior for NaN is undefined, NaNs might end up at the top of the list.
	 */
	public List<T> getAsSortedList(){
		List<T> results = new ArrayList<>();
		Collection<List<T>> sorted = this.getSorted().descendingMap().values();
		for (List<T> list : sorted){
			results.addAll(list);
		}
		return results;
	}

    /**
     * Gets a sorted TreeMap representation of this bag.
     */
	public TreeMap<Double,List<T>> getSorted(){
		TreeMap<Double,List<T>> treeMap = new TreeMap<>();
		for (T key : this.keySet()) {
			Double value = hashMap.get(key);
            if (value==null) {
                // do nothing.
            }else if (treeMap.containsKey(value)){
				List<T> valueList = treeMap.get(value);
				valueList.add(key);
			}else{
				List<T> valueList = new ArrayList<>();
				valueList.add(key);
				treeMap.put(value, valueList);
			}
		}
		return treeMap;
	}

    /**
     * Returns the highest value currently present in the bag.
     */
	public double getHighestValue(){
		Double highestSoFar = null;
        for (T key : this.keySet()) {
            double d= hashMap.get(key);
            if (highestSoFar==null || d>=highestSoFar)
            	highestSoFar=d;
		}
		return highestSoFar;
	}

    /**
     * Returns the key that has the highest value, or more than one key if several keys with that value exist.
     */
	public List<T> getKeysForHighestValue(){
		return getKeysForHighestValue(false);
	}

    /**
     * Returns the key that has the highest value, or more than one key if several keys with that value exist.
     * @param removeKeys If true, the returned items are removed from this bag.
     */
	public List<T> getKeysForHighestValue(boolean removeKeys){
		ArrayList<T> results = new ArrayList<>();
		Double highestSoFar = null;
		for (T key : this.keySet()) {
            double d = hashMap.get(key);
            if (highestSoFar==null || d>highestSoFar){
            	results = new ArrayList<>();
            	results.add(key);
            	highestSoFar = d;
            }else if(d==highestSoFar){
            	results.add(key);
            }
		}
        if (results.size()==0 && this.size()>0){
            // e.g. NaN Values
            results.addAll(this.keySet());
        }
		if (removeKeys){
			for (T key: results){
                hashMap.remove(key);
			}
		}
		return results;
	}

    /**
     * Returns the lowest value currently present in the bag.
     */
	public double getLowestValue(){
		Double lowestSoFar = null;
        for (T key : this.keySet()) {
            double d = hashMap.get(key);
            if (lowestSoFar==null || lowestSoFar>d)
            	lowestSoFar=d;
		}
		if (lowestSoFar!=null){
			return lowestSoFar;
		}else{
			return -1;
		}
	}

    /**
     * Returns the key that has the lowest value, or more than one key if several keys with that value exist.
     */
	public List<T> getKeysForLowestValue(){
		return getKeysForLowestValue(false);
	}

    /**
     * Returns the key that has the lowest value, or more than one key if several keys with that value exist.
     * @param removeKeys If true, the returned items are removed from this bag.
     */
	public List<T> getKeysForLowestValue(boolean removeKeys){
		List<T> results = new ArrayList<>();
		Double lowestSoFar = null;
        for (T key : this.keySet()){
            double d = hashMap.get(key);
            if (lowestSoFar==null || lowestSoFar>d){
            	results = new ArrayList<>();
            	results.add(key);
            	lowestSoFar = d;
            }else if(d==lowestSoFar){
            	results.add(key);
            }
		}
		if (removeKeys){
			for (T key : results){
                hashMap.remove(key);
			}
		}
		return results;
	}

	/**
	 * Returns a list of the keys with the highest values. Its size will be x, or higher.
	 * (If key x has value y, and other keys with value y exist, they will also be added.)
	 */
	public List<T> getTopXKeysAsList(int x){
		List<T> results = new ArrayList<>();
		Bag<T> bag = new Bag<>(this);
		while(results.size()<x && bag.hashMap.size()>0){
			List<T> highest = bag.getKeysForHighestValue(true);
			results.addAll(highest);
		}
		return results;
	}

	/**
	 * Returns a list of the keys with the highest values. Its size will be x, or higher.
	 * (If key x has value y, and other keys with value y exist, they will also be added.)
	 */
	public Bag<T> getTopXKeysAsBag(int x){
		Bag<T> results = new Bag<>();
		Bag<T> bag = new Bag<>(this);
		while(results.hashMap.size()<x && bag.hashMap.size()>0){
			double highestValue = bag.getHighestValue();
			List<T> highestKeys = bag.getKeysForHighestValue(true);
			for (T key: highestKeys){
        		results.hashMap.put(key, highestValue);
        	}
		}
		return results;
	}

	/**
	 * Subtracts the specified bag from this bag.
	 */
	public void subtract(Bag<T> bag){
		this.subtract(bag, false);
	}

    /**
     * Subtracts the specified bag from this bag.
     * @param normalize If true, the relative sizes of the bags will be taken into account.
     */
    public void subtract(Bag<T> bag, boolean normalize){
		double normalizer = 1.0;
		if (normalize) {
            double thisSum = this.getValueSum();
            double otherSum = bag.getValueSum();
			normalizer = thisSum / otherSum;
		}

        for (T key : this.keySet()) {
            double thisValue  = this.getValue(key);
            double otherValue = bag.getValue(key);
            double newValue   = thisValue - otherValue * normalizer;
            hashMap.put(key, newValue);
        }

        for (T key : bag.keySet()) {
            double otherValue = bag.getValue(key);
            if (!hashMap.containsKey(key)){
                hashMap.put(key, 0.0 - (otherValue * normalizer));
            }
        }
    }

    /**
     * Multiplies each value for all keys in this bag with the specified value.
     * @param multiplier
     */
	public void multiplyWeights(double multiplier){
        Bag<T> bag = new Bag<>(this);
        for (T key : bag.keySet()) {
        	double value = this.getValue(key) * multiplier;
            hashMap.remove(key);
            hashMap.put(key,value);
     	}
	}

    /**
     * After this operation, the sum of all values will be 1.0.
     */
	public void normalize(){
		this.multiplyWeights(1 / this.getValueSum());
	}

    /**
     * Removes the specified key.
     */
    public void remove(T key){
        this.hashMap.remove(key);
    }

    /**
     * Removes all keys in the specified list from this bag.
     */
    public void removeList(List<T> list){
        for (T item: list) {
            hashMap.remove(item);
        }
    }

    /**
     * Removes all keys whose values are less or equal the specified value.
     */
	public void removeValuesLessOrEqualThan(double d){
        List<T> toRemove = new ArrayList<>();
        for (T key : this.keySet()) {
        	double value = this.getValue(key);
        	if (d>=value) toRemove.add(key);
     	}
        for (T key : toRemove) {
            hashMap.remove(key);
        }
	}

    /**
     * Removes all keys whose values are smaller than the specified value.
     */
	public void removeValuesLessThan(double d){
        List<T> toRemove = new ArrayList<>();
        for (T key : this.keySet()) {
            double value = this.getValue(key);
            if (d>value) toRemove.add(key);
        }
        for (T key : toRemove) {
            hashMap.remove(key);
        }
	}

    /**
     * Removes keys whose value is zero or NaN.
     */
    public void clean(){
        List<T> toRemove = new ArrayList<>();
        for (T key : this.keySet()) {
            double value = this.getValue(key);
            if (0.0==value || Double.isNaN(value)){
                toRemove.add(key);
            }
        }
        for (T key : toRemove) {
            hashMap.remove(key);
        }
    }

	private String toString_internal(boolean keysOnly){
        String result = "";
        List<T> list = this.getAsSortedList();
        int i = 1;
        for (T key : list) {
            double value = this.getValue(key);
            result = result + key;
            if (!keysOnly) result = result + ":" + value;
            result = result + ", ";
            if (i>=10) break;
            i++;
        }
        if (result.length()>2){
            result = result.substring(0,result.length() - 2);
        }
        if (list.size()==0){
            result = result + "[Empty]";
        }
        if (list.size()>10){
            int more = list.size() - 10;
            result = result + " ... plus " + more + " more.";
        }
        return result;
	}

	public String toString(){
		return this.toString_internal(false);
	}

	public String toStringKeysOnly() {
		return this.toString_internal(true);
	}

	public String toString(String indent){
		return this.toString(indent, true, 0.0);
	}

	public String toString(boolean combineLines){
		return this.toString("", combineLines, 0.0);
	}
	
	public String toString(String indent, boolean combineLines, double ignoreLessThan){
		String result = "";
		TreeMap<Double, List<T>> tm = this.getSorted();
        while (tm.size()>0){
            Double key = tm.lastKey();
            if (key>=ignoreLessThan){
	            List<T> value = tm.get(key);
	            if (combineLines){
	            	boolean alreadyFound = false;
		            for (T o : value) {
		                if (!alreadyFound){
		                	String keyString = key.toString();
		                	if (keyString.length()>8) keyString = keyString.substring(0,8);
		                    result = result + indent + keyString + ":\t";
		                    alreadyFound=true;
		                }
		                if (o instanceof String)
		                	result = result + "\"" + o + "\" ";
		                else if (o!=null)
		                	result = result + o.toString() + " ";
                        else
                            result = result + "NULL ";
		            }
		            if (alreadyFound) result = result+"\n";
		        }else{
		        	for (Object o : value) {
		                String keyString = key.toString();
	                	if (keyString.length()>8) keyString = keyString.substring(0,8);
		                result = result + indent + keyString + ": ";
		                if (o instanceof String)
		                	result = result + "\"" + o.toString() + "\" ";
		           		else
		           			result = result + o.toString() + " ";
		           			result = result + "\n";
		           	}
		        }
	        }
	        tm.remove(key);
        }
        return result;
	}

	public String toString(String caption, String indent, boolean combineLines, double ignoreLessThan){
		String result = indent+caption+"\n";
     	if (!hashMap.isEmpty()){
     		result += this.toString(indent, combineLines,ignoreLessThan)+"\n";
        }else{
     		result += indent+"  No results.\n";
        }
     	return	result;
	}

}
