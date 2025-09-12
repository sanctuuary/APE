package nl.uu.cs.ape.utils.cwl_parser;

/**
 * This exception is thrown to indicate issues with the formatting of a CWL (Common Workflow Language)
 * tool description file. It is used to signal errors such as missing required fields for annotation
 * or general formatting problems that prevent the file from being processed correctly.
 */
public class CWLFormattingException extends RuntimeException {
    
    /**
     * Instantiates a new CWL formatting exception.
     *
     * @param message The message that will be passed to the {@link Exception} super
     *                class.
     */
    private CWLFormattingException(String message) {
        super(message);
    }

    /**
     * Exception is thrown when the provided CWL tool description is not well formatted.
     * 
     * @param message Application specific message that may help the user solve
     *                the problem.
     * @return CWL formatting exception with information that may help the user solve
     */
    public static CWLFormattingException badCwlFormattingException(String message) {
        return new CWLFormattingException(String.format("CWL tool description file is not well formatted. %s", message));
    }

    /**
     * Exception is thrown when the provided CWL tool description does not contain the required fields for tool annotation.
     * 
     * @param message Application specific message that may help the user solve
     *                the problem.
     * @return CWL formatting exception with information that may help the user solve
     */
    public static CWLFormattingException missingAnnotationField(String message) {
        return new CWLFormattingException(String.format("CWL tool description file is missing annotations required by APE. %s", message));
    }
}
