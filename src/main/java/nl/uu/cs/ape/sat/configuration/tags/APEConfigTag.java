package nl.uu.cs.ape.sat.configuration.tags;

import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.configuration.APEConfigException;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class APEConfigTag<T> {

    private T value;

    public abstract String getTagName();

    public abstract String getLabel();

    public abstract APEConfigTagType getTagType();

    public abstract String getDescription();

    public void addTypeInfo(JSONObject typeInfo){ }

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

        throw APEConfigException.fieldNotSpecified(getTagName(), getTagType().toString());
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

    public JSONObject toJSON() {

        final JSONObject json = new JSONObject()
                .put("tag_name", getTagName())
                .put("label", getLabel())
                .put("description", getDescription())
                .put("type", getTagType().toJSON())
                .put("optional", isOptional());

        if (isOptional()){
            json.put("default", getDefault().get());
        }

        return json;
    }
}


