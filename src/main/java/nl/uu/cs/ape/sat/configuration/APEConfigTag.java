package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APECoreConfig;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class APEConfigTag<T> {

    /* Serializable */
    private final String tagName;
    private final Class<T> type;
    private T _default = null;
    private String tagDescription;

    /* Not serializable */
    private final List<ValidationRule<T>> validations = new ArrayList<>();

    /**
     * Instantiates a new Ape config field info.
     *
     * @param tagName the tag name
     * @param type    the type
     */
    public APEConfigTag(String tagName, Class<T> type) {
        this.tagName = tagName;
        this.type = type;
    }

    /**
     * Set a default value for this field.
     *
     * @param value the value
     * @return the ape config field info
     */
    public APEConfigTag<T> withDefaultValue(T value) {
        this._default = value;
        return this;
    }

    /**
     * Set a description for this field.
     *
     * @param tagDescription the description
     * @return the ape config field info
     */
    public APEConfigTag<T> withTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
        return this;
    }

    public APEConfigTag<T> addValidationFunction(Predicate<T> predicate, String ruleDescription){
        validations.add(new ValidationRule<T>(getName(), ruleDescription, predicate));
        return this;
    }

    public ValidationResults validate(T t){
        return new ValidationResults(this.validations.stream()
                .map(rule -> rule.test(t))
                .collect(Collectors.toList()));
    }

    public ValidationResults validate(JSONObject json){

        final ValidationResults results = new ValidationResults();

        // JSON contains tag
        if(!json.has(getName())){
            results.add(getName(), String.format("Value for tag '%s' is missing.", getName()), false);
            return results;
        }

        // Can construct object T
        try{
            final T dummy = constructFromJSON(json);
            results.add(validate(dummy));
        }
        catch (Exception e){
            results.add(getName(), e.getMessage(), false);
        }

        return results;
    }

    protected abstract T constructFromJSON(JSONObject obj);

    public APEConfigField<T> createField(){
        return new APEConfigField<>(this);
    }

    /**
     * To json object.
     *
     * @return the json object
     */
    public JSONObject toJSON() {

        JSONObject obj = new JSONObject()
                .put("tag_name", getName())
                .put("type", getTagType().getSimpleName())
                .put("optional", isOptional())
                .put("description", getTagDescription());

        if (isOptional()) {
            obj.put("default", getDefault());
        }

        return obj;
    }

    /**
     * Get tag name.
     *
     * @return the string
     */
    public String getName() {
        return this.tagName;
    }

    /**
     * Get type class of the data.
     *
     * @return the class
     */
    public Class<T> getTagType() {
        return this.type;
    }

    /**
     * If the field has a default value, this field is optional.
     *
     * @return the boolean
     */
    public boolean isOptional() {
        return !this.isMandatory();
    }

    /**
     * If the field does not have a default value, this field is mandatory.
     *
     * @return the boolean
     */
    public boolean isMandatory() {
        return this._default == null;
    }

    /**
     * Get description for this field.
     *
     * @return the string
     */
    public String getTagDescription() {
        return this.tagDescription;
    }

    /**
     * Gets default value for this field.
     *
     * @return the default
     */
    public T getDefault() {
        return this._default;
    }

}
