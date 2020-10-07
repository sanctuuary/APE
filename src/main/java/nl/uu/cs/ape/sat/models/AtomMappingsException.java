/**
 * 
 */
package nl.uu.cs.ape.sat.models;

/**
 * The {@code APEDataDimensionsOverlapException} exception will be thrown if the data dimensions are not annotated properly.
 * 
 * @author Vedran Kasalica
 *
 */
public class AtomMappingsException extends RuntimeException {

	/**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    private AtomMappingsException(String message) {
        super(message);
    }
	
    /**
     * Exception is thrown when the dimensions described in the OWL file have overlaps.
     * @param message - Application specific message that may help the user solve the problem.
     * @return Dimensions exception with information that may help the user solve the problem.
     */
    public static AtomMappingsException mappedAtomsSignaturesOverlap(String message) {
    	return new AtomMappingsException(String.format("Two or more atoms share the same signature (ID + state). %s", message));
    }
    
}
