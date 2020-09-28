package nl.uu.cs.ape.sat.configuration.tags;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A container for APEConfigTag's, that makes it easier to filter tags
 */
public class APEConfigTags {

    private final APEConfigTag<?>[] all_tags;

    /**
     * Instantiates a new APEConfigTags based on a set of tags.
     *
     * @param all_tags the all tags
     */
    public APEConfigTags(APEConfigTag<?>... all_tags) {
        this.all_tags = all_tags;
    }

    /**
     * Gets all tags that are obligatory.
     *
     * @return all tags that are obligatory
     */
    public List<APEConfigTag.Info<?>> getObligatory() {
        return getAll(APEConfigTag::isObligatory);
    }

    /**
     * Gets all tags that are optional.
     *
     * @return all tags that are optional.
     */
    public List<APEConfigTag.Info<?>> getOptional() {
        return getAll(APEConfigTag::isOptional);
    }

    /**
     * Gets all tags that match the predicate in list format.
     *
     * @param filter the filter (e.g. tag -> tag.isOptional())
     * @return Gets all tags that match the predicate in list format
     */
    public List<APEConfigTag.Info<?>> getAll(Predicate<APEConfigTag<?>> filter) {
        return Arrays.stream(all_tags).filter(filter).map(APEConfigTag::getInfo).collect(Collectors.toList());
    }

    /**
     * Gets all tags in list format.
     *
     * @return all tags in list format
     */
    public List<APEConfigTag.Info<?>> getAll() {
        return Arrays.stream(all_tags).map(APEConfigTag::getInfo).collect(Collectors.toList());
    }

    /**
     * Create a JSONObject containing all the info on tags, e.g.: {"tags": [ .. tags ..]}
     * This method uses {@link APEConfigTag.Info#toJSON()} to convert the tags to json.
     *
     * @return a JSONObject containing all the info on tags
     */
    public JSONObject toJSON() {
        return new JSONObject()
                .put("tags", new JSONArray(getAll().stream().map(APEConfigTag.Info::toJSON).collect(Collectors.toSet())));
    }
}
