package nl.uu.cs.ape.sat.configuration.tags;

import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.configuration.APEConfigException;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class APEConfigTag<T> {

    private T value;

    public abstract String getTagName();

    public abstract String getLabel();

    public enum TagType {
        FILE_PATH,
        FOLDER_PATH,
        URI,
        INTEGER,
        INTEGER_RANGE,
        BOOLEAN,
        ENUM,
        DATA_DIMENSIONS,
        DATA_INSTANCES,
        MODULE
    }

    public abstract TagType getType();

    public abstract String getDescription();

    protected JSONObject getTypeConstraints() { return new JSONObject(); }

    protected abstract T constructFromJSON(JSONObject obj);

    public abstract APEConfigDefaultValue<T> getDefault();

    public void setValue(JSONObject obj) {

        final ValidationResults results = validate(obj);

        if(results.fail()){
            throw APEConfigException.ruleViolations(results);
        }

        this.value = constructFromJSON(obj);
    }

    public void setValue(T value){

        final ValidationResults results = validate(value);

        if(results.fail()){
            throw APEConfigException.ruleViolations(results);
        }

        this.value = value;
    }

    public T getValue(){
        if(this.value != null){
            return this.value;
        }

        final APEConfigDefaultValue<T> _default = getDefault();
        if(_default.hasValue()){
            return _default.get();
        }

        throw APEConfigException.fieldNotSpecified(getTagName(), getType().toString());
    }

    public boolean isOptional(){
        return getDefault().hasValue();
    }

    public boolean isObligatory(){
        return !isOptional();
    }

    public ValidationResults validate(JSONObject json){

        final ValidationResults results = new ValidationResults();

        // JSON contains tag
        if(!json.has(getTagName())){
            if(isObligatory()){
                results.add(getTagName(), String.format("Value for tag '%s' is missing.", getTagName()), false);
            }
            return results;
        }

        // Can construct object T
        try{
            final T dummy = constructFromJSON(json);
            results.add(validate(dummy));
        }
        catch (JSONException | APEConfigException e){
            results.add(getTagName(), e.getMessage(), false);
        }

        return results;
    }

    public ValidationResults validate(T value){
        final ValidationResults results = new ValidationResults();
        return validate(value, results);
    };

    protected abstract ValidationResults validate(T value, ValidationResults results);

    public Info<T> getInfo(){
        return new Info(this);
    }

    public JSONObject toJSON(){
        return getInfo().toJSON();
    }

    public static class Info <T> {

        public final String tag_name, label, description;
        public final boolean optional;
        public final TagType type;
        public final T _default;
        public final JSONObject constraints;

        protected Info(APEConfigTag<T> tag) {
            this.tag_name = tag.getTagName();
            this.label = tag.getLabel();
            this.description = tag.getDescription();
            this.optional = tag.isOptional();
            this.type = tag.getType();
            this._default = this.optional ? tag.getDefault().get() : null;
            this.constraints = tag.getTypeConstraints();
        }

        public JSONObject toJSON() {
            final JSONObject json = new JSONObject()
                        .put("tag", tag_name)
                        .put("label", label)
                        .put("description", description)
                        .put("type", type)
                        .put("optional", optional);

            if(optional){
                json.put("default", _default == null ? "" : _default);
            }

            if(!constraints.isEmpty()){
                json.put("constraints", constraints);
            }

            return json;
        }
    }
}


