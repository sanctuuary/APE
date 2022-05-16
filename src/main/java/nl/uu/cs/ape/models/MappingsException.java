/**
 * 
 */
package nl.uu.cs.ape.models;

/**
 * The {@code APEDataDimensionsOverlapException} exception will be thrown if the data dimensions are not annotated properly.
 * 
 * @author Vedran Kasalica
 *
 */
public class MappingsException extends RuntimeException {

	/**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    public MappingsException(String message) {
        super(message);
    }
	
    /**
     * Exception is thrown when two or more atoms share the same signature (ID + state)
     * @param message - Application specific message that may help the user solve the problem.
     * @return Dimensions exception with information that may help the user solve the problem.
     */
    public static MappingsException mappedAtomsSignaturesOverlap(String message) {
    	return new MappingsException(String.format("Two or more atoms share the same signature (ID + state). %s", message));
    }
    
    
    /**
     * Exception is thrown when two or more predicates share the same signature.
     * @param message - Application specific message that may help the user solve the problem.
     * @return Dimensions exception with information that may help the user solve the problem.
     */
    public static MappingsException mappedPredicateSignaturesOverlap(String message) {
    	return new MappingsException(String.format("Two or more predicates share the same signature (ID). %s", message));
    }
    
}
