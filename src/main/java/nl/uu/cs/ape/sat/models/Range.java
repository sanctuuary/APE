package nl.uu.cs.ape.sat.models;

import org.json.JSONObject;

public class Range {

    public final static String MIN_TAG = "minimal";
    public final static String MAX_TAG = "maximal";

    final int min;
    final int max;

    private Range(int min, int max){
        this.min = min;
        this.max = max;
    }

    public JSONObject toJSON(){
        return new JSONObject()
                .put(MIN_TAG, this.min)
                .put(MAX_TAG, this.max);
    }

    public static Range from(JSONObject json){
        return new Range(json.getInt(MIN_TAG), json.getInt(MAX_TAG));
    }

    public static Range of(int minimum, int maximum){
        return new Range(minimum, maximum);
    }

    public int getMin(){
        return this.min;
    }

    public int getMax() {
        return this.max;
    }
}
