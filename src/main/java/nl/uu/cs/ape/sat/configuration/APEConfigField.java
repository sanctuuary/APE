package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APEConfigException;
import org.json.JSONObject;

public class APEConfigField<T> {

    private T value = null;
    private final APEConfigTag<T> tag;

    public APEConfigField(APEConfigTag<T> tag){
        this.tag = tag;
    }

    public void setValue(JSONObject obj) {

        final String tagName = tag.getName();
        final T t;

        // missing tag
        if(obj.has(tagName)){
            t = tag.constructFromJSON(obj);
        }
        else if(tag.isOptional()){
            t = tag.getDefault();
        }
        else{
            throw APEConfigException.missingTag(tagName);
        }

        setValue(t);
    }

    public void setValue(T value) {

        ValidationResults results = tag.validate(value);

        if(results.fail()){
            throw APEConfigException.ruleViolations(results);
        }

        this.value = value;

    }

    public T getValue(){

        if(this.value != null){
            return this.value;
        }

        if(this.tag.getDefault() != null){
            return this.tag.getDefault();
        }

        throw APEConfigException.fieldNotSpecified(this.tag.getName(), this.tag.getType().getSimpleName());
    }
}
