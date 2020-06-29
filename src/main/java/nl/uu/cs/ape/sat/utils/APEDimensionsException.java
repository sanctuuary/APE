/**
 * 
 */
package nl.uu.cs.ape.sat.utils;

/**
 * The {@code APEDataDimensionsOverlapException} exception will be thrown if the data dimensions are not annotated properly.
 * 
 * @author Vedran Kasalica
 *
 */
public class APEDimensionsException extends RuntimeException {

	/**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    private APEDimensionsException(String message) {
        super(message);
    }
	
    /**
     * Exception is thrown when the dimensions described in the OWL file have overlaps.
     * @param message - Application specific message that may help the user solve the problem.
     * @return Dimensions exception with information that may help the user solve the problem.
     */
    public static APEDimensionsException dimensionsOverlap(String message) {
    	return new APEDimensionsException(String.format("The data dimensions cannot overlap. %s", message));
    }
}
