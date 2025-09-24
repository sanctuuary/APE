package nl.uu.cs.ape.models.enums;

/**
 * Defines the values describing the types of annotations that can be added to the tools.
 * <p>
 * Values: [
 */
public enum ToolAnnotationType {

    /**
     * Default APE annotation, used to explicitly specify tool operations, inputs and outputs.
     */
    APE_ANNOTATION,
    /**
     * CWL annotation, annotations are embedded in the CWL (Common Workflow Language) file.
     * In addition to the existing input and output data formats, the format was extended to include the data type and tool operations according to the EDAM ontology.
     */
    CWL_ANNOTATION,
    
    /**
     * Unknown annotation type.
     */
    UNKNOWN;

    /**
     * Returns the ToolAnnotationTypes enum constant that matches the given text, ignoring case considerations.
     *
     * @param text the text to match
     * @return the matching ToolAnnotationTypes constant, or null if no match is found
     */
    public static ToolAnnotationType fromString(String text) {
        for (ToolAnnotationType type : ToolAnnotationType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return UNKNOWN;
    }



}
