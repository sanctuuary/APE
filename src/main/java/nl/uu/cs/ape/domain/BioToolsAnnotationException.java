/**
 * 
 */
package nl.uu.cs.ape.domain;

/**
 * The {@code BioToolsAnnotationException} exception will be thrown if the
 * a tool in bio.tools is not annotated properly.
 * 
 * @author Vedran Kasalica
 *
 */
public class BioToolsAnnotationException extends RuntimeException {

    /**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super
     *                class.
     */
    private BioToolsAnnotationException(String message) {
        super(message);
    }

    /**
     * Exception is thrown when a tool that does not have a data type of data specified in the bio.tools annotations.
     * @param toolID - ID of the tool that does not have the data type specified.
     * @return BioToolsAnnotationException with information that may help the user solve the problem.
     */
    public static BioToolsAnnotationException notExistingType(String toolID) {
        return new BioToolsAnnotationException(String.format("The tool with ID %s does not have data type specified (for input or output).", toolID));
    }

    /**
     * Exception is thrown when a tool that does not have a data format of data specified in the bio.tools annotations.
     * @param toolID - ID of the tool that does not have the format specified.
     * @return BioToolsAnnotationException with information that may help the user solve the problem.
     */
    public static BioToolsAnnotationException notExistingFormat(String toolID) {
        return new BioToolsAnnotationException(String.format("The tool with ID %s does not have data format specified (for input or output).", toolID));
    }
}
