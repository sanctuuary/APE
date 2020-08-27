package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class APEConfig {

    public abstract APEConfigTag<?>[] getAllTags();

    public APEConfigTag<?>[] getObligatoryTags(){
        return APEUtils.filter(getAllTags(), APEConfigTag::isObligatory);
    }

    public APEConfigTag<?>[] getOptionalTags(){
        return APEUtils.filter(getAllTags(), APEConfigTag::isOptional);
    }

    public JSONArray getAllTagInfoJSON(){
        return new JSONArray(APEUtils.map(getAllTags(), APEConfigTag::toJSON));
    }

    public void debug(){
        final JSONObject json = new JSONObject();
        for(APEConfigTag<?> tag : getAllTags()){
            json.put(tag.getTagName(), tag.getValue().toString());
        }
        System.out.println(json.toString(3));
    }
}
