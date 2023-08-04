package nl.uu.cs.ape.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code ToolAnnotationTag} enum contains the tags that are used to
 * annotate tools.
 */
@AllArgsConstructor
public enum ToolAnnotationTag {

    /**
     * Represents the ID of a tool.
     */
    ID("id"),

    /**
     * Represents a label attribute of a tool.
     */
    LABEL("label"),

    /**
     * Represents the inputs of a tool.
     */
    INPUTS("inputs"),

    /**
     * Represents the taxonomy operations of a tool.
     */
    TAXONOMY_OPERATIONS("taxonomyOperations"),

    /**
     * Represents the outputs of a tool.
     */
    OUTPUTS("outputs"),

    /**
     * Represents the implementation details of a tool.
     */
    IMPLEMENTATION("implementation"),

    /**
     * Represents the code of a tool.
     */
    CODE("code");

    @Getter
    private final String tagName;

    /**
     * Returns the Type by its type name.
     *
     * @param tagName The type name to look for.
     * @return The corresponding Type, or null if no such Type exists.
     */
    public static ToolAnnotationTag fromTypeName(String tagName) {
        for (ToolAnnotationTag tag : ToolAnnotationTag.values()) {
            if (tag.getTagName().equals(tagName)) {
                return tag;
            }
        }
        return null;
    }
}
