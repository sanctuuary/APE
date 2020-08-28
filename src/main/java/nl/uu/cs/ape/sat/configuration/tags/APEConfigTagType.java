package nl.uu.cs.ape.sat.configuration.tags;

import org.json.JSONObject;

import java.util.function.Function;

public class APEConfigTagType {

    public enum Type {
        FILE_PATH,
        FOLDER_PATH,
        URI,
        INTEGER,
        INTEGER_RANGE,
        BOOLEAN,
        CONFIG_ENUM,
        DATA_DIMENSIONS,
        DATA_INSTANCES,
        TOOL_ROOT
    }

    private final Type type;
    private final Function<JSONObject, JSONObject> additionalInfo;

    public APEConfigTagType(Type type){
        this.type = type;
        this.additionalInfo = json -> json;
    }

    public APEConfigTagType(Type type, Function<JSONObject, JSONObject> additionalInfo){
        this.type = type;
        this.additionalInfo = additionalInfo;
    }

    public Type getType(){
        return type;
    }

    public JSONObject toJSON(){
        final JSONObject json = new JSONObject()
                .put("type_name", this.type.toString());
        additionalInfo.apply(json);
        return json;
    }
}
