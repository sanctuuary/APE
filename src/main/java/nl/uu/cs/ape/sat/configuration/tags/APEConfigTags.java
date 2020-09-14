package nl.uu.cs.ape.sat.configuration.tags;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class APEConfigTags {

    private final APEConfigTag<?>[] all_tags;

    public APEConfigTags(APEConfigTag<?>... all_tags) {
        this.all_tags = all_tags;
    }

    public List<APEConfigTag.Info> getObligatory() {
        return getAll(APEConfigTag::isObligatory);
    }

    public List<APEConfigTag.Info> getOptional() {
        return getAll(APEConfigTag::isOptional);
    }

    public List<APEConfigTag.Info> getAll(Predicate<APEConfigTag<?>> filter) {
        return Arrays.stream(all_tags).filter(filter).map(APEConfigTag::getInfo).collect(Collectors.toList());
    }

    public List<APEConfigTag.Info> getAll() {
        return Arrays.stream(all_tags).map(APEConfigTag::getInfo).collect(Collectors.toList());
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("tags", new JSONArray(getAll().stream().map(APEConfigTag.Info::toJSON).collect(Collectors.toSet())));
    }
}
