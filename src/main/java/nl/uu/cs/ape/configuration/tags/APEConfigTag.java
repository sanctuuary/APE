package nl.uu.cs.ape.configuration.tags;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.tags.validation.ValidationResults;

/**
 * ApeConfigTag is a contains the actual value of the tag along with
 * additional info that might be relevant for the user.
 * Call {@link APEConfigTag#getInfo()} to get an immutable version
 * of the tag.
 *
 * To implement a new Tag, the following methods should be implemented:
 * <ul>
 *   <li>String {@link APEConfigTag#getTagName()}</li>
 *   <li>String {@link APEConfigTag#getLabel()}</li>
 *   <li>TagType {@link APEConfigTag#getType()}</li>
 *   <li>String {@link APEConfigTag#getDescription()}</li>
 *   <li>JSONObject {@link APEConfigTag#constructFromJSON(JSONObject config)}</li>
 *   <li>APEConfigDefaultValue {@link APEConfigTag#getDefault()}</li>
 *   <li>ValidationResults {@link APEConfigTag#validate(Object obj, ValidationResults results)}</li>
 *   <li>JSONObject {@link APEConfigTag#getTagConstraints()} (OPTIONAL)</li>
 * </ul>
 *
 * Returning an anonymous value directly (e.g. {@link APEConfigTag#getTagName()})
 * creates static-like variable for one Tag implementation.
 *
 * @param <T> the data type that the tag should contain
 */
public abstract class APEConfigTag<T> {

    private T value;

    /**
     * Gets the tag name.
     * E.g. "inputs" in the json { "inputs": [] }
     *
     * @return the tag name
     */
    public abstract String getTagName();

    /**
     * Gets a readable label for the tag.
     * E.g. "Data dimensions" for the tag "dataDimensionsTaxonomyRoots"
     *
     * @return the label
     */
    public abstract String getLabel();

    /**
     * Gets the type enum for the tag. This will (only) be used
     * by the web application developers to determine what input
     * mechanism to use (e.g. open file button, integer input,
     * a slider etc.)
     *
     * E.g. {@link TagType#FILE_PATH} is used for both
     * "ontology_path" and "tool_annotations_path".
     *
     * @return the type
     */
    public abstract TagType getType();

    /**
     * Gets a description of the tag.
     *
     * @return the description
     */
    public abstract String getDescription();

    /**
     * Gets type constraints. Most tags return an empty
     * JSONObject since there are no constraints. {}
     *
     * But a tag that contains an integer might include
     * boundaries as constraints, e.g.:
     * { "min": 0, "max": 100 }
     *
     * @return the tag constraints
     */
    protected JSONObject getTagConstraints() {
        return new JSONObject();
    }

    /**
     * A tag that contains type T, must implement a method
     * to construct a T from a JSONObject (possibly using
     * {@link APEConfigTag#getTagName()} to read its contents).
     *
     * If T cannot be parsed from the provided configuration,
     * you should throw an {@link APEConfigException}.
     *
     * @param obj the configuration object
     * @return value T
     */
    protected abstract T constructFromJSON(JSONObject obj);

    /**
     * Gets the default value for the tag.
     * If the tag has a default value, this method should return:
     * APEConfigDefaultValue.withDefault(your_default_value_T);
     * If the tag has no default value, this method should return:
     * APEConfigDefaultValue.noDefault();
     *
     * @return the default
     */
    public abstract APEConfigDefaultValue<T> getDefault();

    /**
     * Gets the actual value of the Tag.
     * This will only throw an exception if no value was assigned
     * AND there is no default value for this tag.
     *
     * @return the value
     */
    public T getValue() {
        if (this.value != null) {
            return this.value;
        }

        final APEConfigDefaultValue<T> _default = getDefault();
        if (_default.hasValue()) {
            return _default.get();
        }

        throw APEConfigException.missingTag(getTagName());
    }

    /**
     * Sets the actual value of the Tag.
     * It uses {@link APEConfigTag#validateConfig(JSONObject obj)}
     * to check if the value is valid.
     * If the tag has a default value AND configuration does
     * not contain the tag, the default will be set.
     *
     * @param obj the obj
     */
    public void setValueFromConfig(JSONObject obj) {

        final ValidationResults results = validateConfig(obj);

        if (results.hasFails()) {
            throw APEConfigException.ruleViolations(results);
        }

        // check if tag is present (optional tags still return a positive validationResult when they are missing)
        if(obj.has(getTagName())){
            this.value = constructFromJSON(obj);
        }
    }

    /**
     * Sets the actual value of the Tag.
     * It uses {@link APEConfigTag#validate(Object)}
     * to check if the value is valid.
     *
     * @param value the value
     */
    public void setValue(T value) {

        final ValidationResults results = validate(value);

        if (results.hasFails()) {
            throw APEConfigException.ruleViolations(results);
        }

        this.value = value;
    }

    /**
     * Returns true if the tag is optional.
     * That is, if the tag has a default value.
     *
     * @return true if the tag is optional
     */
    public boolean isOptional() {
        return getDefault().hasValue();
    }

    /**
     * Returns true if the tag is obligatory.
     * That is, if the tag has no default value.
     *
     * @return true if the tag is obligatory
     */
    public boolean isObligatory() {
        return !isOptional();
    }

    /**
     * Validate the configuration file for this tag only.
     * Returns {@link ValidationResults} that contains
     * successes/failures for all validation criteria.
     * Call {@link ValidationResults#hasFails()} to check
     * whether any criteria failed.
     *
     * @param json the configuration object
     * @return the validation results
     */
    public ValidationResults validateConfig(JSONObject json) {

        final ValidationResults results = new ValidationResults();

        // JSON contains tag
        if (!json.has(getTagName())) {
            // If obligatory, configuration should have contained the tag. Add failure and return results.
            // If optional, the default value is always correct, return empty results (success).
            if (isObligatory()) {
                results.add(getTagName(), String.format("Value for tag '%s' is missing.", getTagName()), false);
            }
            return results;
        }

        // Can construct object T
        try {
            final T dummy = constructFromJSON(json);
            results.add(validate(dummy));
        } catch (JSONException | APEConfigException | IllegalArgumentException e) {
            results.add(getTagName(), e.getMessage(), false);
        }

        return results;
    }

    /**
     * Validate the value for this tag only.
     * Returns {@link ValidationResults} that contains
     * successes/failures for all validation criteria.
     * Call {@link ValidationResults#hasFails()} to check
     * whether any criteria failed.
     *
     * @param value the value to be validated
     * @return the validation results
     */
    public ValidationResults validate(T value) {
        final ValidationResults results = new ValidationResults();
        return validate(value, results);
    }

    /**
     * Validate the value for this tag only.
     * Returns {@link ValidationResults} that contains
     * successes/failures for all validation criteria.
     *
     * Use {@link ValidationResults#add(String tag_name, String rule_description, boolean success)}
     * to add successes/failures to the results parameter that the user can use.
     * After that, return the results.
     *
     * E.g.: {@literal results.add(getTagName(), "The maximum number of generated solutions should be greater or equal to 0.", value >= 0);}
     *
     * @param value   the value
     * @param results the results
     * @return the validation results
     */
    protected abstract ValidationResults validate(T value, ValidationResults results);

    /**
     * Gets an immutable version of a APEConfigTag: {@link Info}
     *
     * @return an immutable version of a APEConfigTag: {@link Info}
     */
    public Info<T> getInfo() {
        return new Info<>(this);
    }

    /**
     * The TagTypes for the tag. This will (only) be used
     * by the web application developers to determine what input
     * mechanism to use (e.g. open file button, integer input,
     * a slider etc.)
     *
     * E.g. {@link TagType#FILE_PATH} is used for both
     * "ontology_path" and "tool_annotations_path".
     */
    public enum TagType {
        FILE_PATH,
        FOLDER_PATH,
        IRI,
        JSON,
        INTEGER,
        INTEGER_RANGE,
        BOOLEAN,
        ENUM,
        DATA_DIMENSIONS,
        DATA_INSTANCES,
        MODULE
    }

    /**
     * APEConfigTag.Info is an immutable version of a APEConfigTag.
     * To understand what these variables represent see {@link APEConfigTag}.
     *
     * @param <T> the type parameter of the tag.
     */
    public static class Info<T> {

    	/** Tag name. */
        public final String tag_name;
        /** Label. */
        public final String label;
        /** description. */
        public final String description;
        /** Is it ptional. */
        public final boolean optional;
        /** Type. */
        public final TagType type;
        /** Default value. */
        public final T _default;
        /** Constraints. */
        public final JSONObject constraints;

        /**
         * Instantiates a new Info from an existing tag.
         *
         * @param tag the tag
         */
        protected Info(APEConfigTag<T> tag) {
            this.tag_name = tag.getTagName();
            this.label = tag.getLabel();
            this.description = tag.getDescription();
            this.optional = tag.isOptional();
            this.type = tag.getType();
            this._default = this.optional ? tag.getDefault().get() : null;
            this.constraints = tag.getTagConstraints();
        }

        /**
         * Creates a JSONObject from {@link APEConfigTag.Info}.
         * It contains relevant information for the web application programmers.
         *
         * @return a JSONObject.
         */
        public JSONObject toJSON() {
            final JSONObject json = new JSONObject()
                    .put("tag", tag_name)
                    .put("label", label)
                    .put("description", description)
                    .put("type", type)
                    .put("optional", optional);

            if (optional) {
                json.put("default", _default == null ? "" : _default);
            }

            if (!constraints.isEmpty()) {
                json.put("constraints", constraints);
            }

            return json;
        }
    }
}


