package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class APEConfig {

    public abstract APEConfigTag<?>[] getAllTags();

    public List<APEConfigTag<?>> getObligatoryTags(){
        return Arrays.stream(getAllTags()).filter(APEConfigTag::isObligatory).collect(Collectors.toList());
    }

    public List<APEConfigTag<?>> getOptionalTags(){
        return Arrays.stream(getAllTags()).filter(APEConfigTag::isOptional).collect(Collectors.toList());
    }

    public JSONArray getAllTagInfoJSON(){
        return new JSONArray(APEUtils.map(getAllTags(), APEConfigTag::toJSON));
    }

}
