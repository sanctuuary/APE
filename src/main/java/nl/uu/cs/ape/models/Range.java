package nl.uu.cs.ape.models;

import org.json.JSONObject;

/**
 * The type Range.
 */
public class Range {

    /**
     * The constant tags.
     */
    public final static String MIN_TAG = "min";
    public final static String MAX_TAG = "max";

    /**
     * Min (inclusive) and Max (exclusive) range
     */
    final int min, max;

    private Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Range from JSONObject.
     *
     * @param json the json
     * @return the range
     */
    public static Range from(JSONObject json) {
        return new Range(json.getInt(MIN_TAG), json.getInt(MAX_TAG));
    }

    /**
     * Range from JSONObject String.
     *
     * @param json the json
     * @return the range
     */
    public static Range from(String json) {
        return from(new JSONObject(json));
    }

    /**
     * Range from minimum and maximum.
     *
     * @param minimum the minimum
     * @param maximum the maximum
     * @return the range
     */
    public static Range of(int minimum, int maximum) {
        return new Range(minimum, maximum);
    }

    /**
     * To json json object.
     *
     * @return the json object
     */
    public JSONObject toJSON() {
        return new JSONObject()
                .put(MIN_TAG, this.min)
                .put(MAX_TAG, this.max);
    }

    /**
     * Get Min (inclusive) of range.
     *
     * @return the int
     */
    public int getMin() {
        return this.min;
    }

    /**
     * Get Max (exclusive) of range.
     *
     * @return the max
     */
    public int getMax() {
        return this.max;
    }

    /**
     * Get the length of the range.
     *
     * @return length
     */
    public int getLength() {
        return this.max - this.min;
    }
}
