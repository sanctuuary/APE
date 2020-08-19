package nl.uu.cs.ape.sat.configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract class APEConfig {

    private final APEConfigTag<?>[] allTags;

    /**
     * Tags separated in the categories: obligatory, optional, core and run.
     * The obligatory tags are used in the constructor to check the presence of tags.
     * Optional tags or All tags are mostly used by test cases.
     */
    private final APEConfigTag<?>[] obligatoryTags;
    private final APEConfigTag<?>[] optionalTags;

    /**
     * Fields
     */
    private final Map<APEConfigTag<?>, APEConfigField<?>> fields = new HashMap<>();

    public APEConfig(){
        final List<APEConfigTag<?>> foundTags = new ArrayList<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            if(field.getType().equals(APEConfigTag.class)){
                try {
                    foundTags.add((APEConfigTag<?>) field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        foundTags.sort(Comparator.comparing(APEConfigTag::getName));

        this.allTags = (APEConfigTag<?>[]) foundTags.toArray();
        this.obligatoryTags = (APEConfigTag<?>[]) foundTags.stream().filter(APEConfigTag::isMandatory).toArray();
        this.optionalTags = (APEConfigTag<?>[]) foundTags.stream().filter(APEConfigTag::isOptional).toArray();

        for(APEConfigTag<?> tag : this.allTags){
            fields.put(tag, tag.createField());
        }
    }

    public APEConfigTag<?>[] getAllTags() {
        return allTags;
    }

    /**
     * Get all obligatory JSON tags to set up the framework.
     *
     * @return All obligatory JSON tags to set up the framework.
     */
    public APEConfigTag<?>[] getObligatoryTags() {
        return obligatoryTags;
    }

    /**
     * Get all optional JSON tags to set up the framework.
     *
     * @return All optional JSON tags to set up the framework.
     */
    public APEConfigTag<?>[] getOptionalTags() {
        return optionalTags;
    }

    protected <T> APEConfigField<T> getField(APEConfigTag<T> key){
        return (APEConfigField<T>) fields.get(key);
    }

    protected Collection<APEConfigField<?>> getFields() {
        return fields.values();
    }

    public APEConfigTag<?> getTag(String tagName){
        return Arrays.stream(getAllTags()).filter(tag -> tag.getName().equals(tagName)).findAny().orElse(null);
    }
}
