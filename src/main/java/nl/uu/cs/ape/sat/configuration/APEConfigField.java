package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APEConfigException;
import org.json.JSONObject;

public class APEConfigField<T> {

    private T value = null;

    private final APEConfigTag<T> info;

    public APEConfigField(APEConfigTag<T> info){
        this.info = info;
    }

    public void setValue(JSONObject obj) {

        final T t;

        // missing tag
        if(obj.has(info.getName())){
            t = info.constructFromJSON(obj);
        }
        else if(info.isOptional()){
            t = info.getDefault();
        }
        else{
            throw APEConfigException.missingTag(info.getName());
        }

        setValue(t);
    }

    public void setValue(T value) {

        ValidationResults results = info.validate(value);

        if(results.fail()){
            throw APEConfigException.ruleViolations(results);
        }

        this.value = value;
    }

    public T getValue(){

        if(this.value != null){
            return this.value;
        }

        if(info.getDefault() != null){
            return info.getDefault();
        }

        throw APEConfigException.fieldNotSpecified(info.getName(), info.getTagType().getSimpleName());
    }

}
