package nl.uu.cs.ape.sat.configuration.tags.validation;

import org.json.JSONObject;

public class ValidationResult {

    private final String tag, ruleDescription;
    private final boolean success;

    public ValidationResult(String tag, String ruleDescription, boolean success) {
        this.tag = tag;
        this.success = success;
        this.ruleDescription = ruleDescription;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFail() {
        return !this.success;
    }

    public String getRuleDescription() {
        return this.ruleDescription;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("tag", getTag())
                .put("description", getRuleDescription())
                .put("success", isSuccess());
    }
}
