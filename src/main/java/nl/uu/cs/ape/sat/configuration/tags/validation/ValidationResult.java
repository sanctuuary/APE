package nl.uu.cs.ape.sat.configuration.tags.validation;

import org.json.JSONObject;

/**
 * This class contains information about the outcome of a validation.
 * More specifically: the tag name that was being tested, the description of the rule
 * and a boolean that represents a success or fail.
 */
public class ValidationResult {

    private final String tag, ruleDescription;
    private final boolean success;

    /**
     * Instantiates a new Validation result.
     *
     * @param tag             The tag name that was being tested.
     * @param ruleDescription The description of the rule.
     * @param success         Represents a success or fail.
     */
    public ValidationResult(String tag, String ruleDescription, boolean success) {
        this.tag = tag;
        this.success = success;
        this.ruleDescription = ruleDescription;
    }

    /**
     * Gets the tag name that was being tested.
     *
     * @return the tag
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Result is a success.
     *
     * @return success
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Result is a fail.
     *
     * @return fail
     */
    public boolean isFail() {
        return !this.success;
    }

    /**
     * Gets rule description.
     *
     * @return the rule description
     */
    public String getRuleDescription() {
        return this.ruleDescription;
    }

    /**
     * To JSONObject.
     *
     * @return the json object containing the tags: "tag", "description" and "success"
     */
    public JSONObject toJSON() {
        return new JSONObject()
                .put("tag", getTag())
                .put("description", getRuleDescription())
                .put("success", isSuccess());
    }
}
