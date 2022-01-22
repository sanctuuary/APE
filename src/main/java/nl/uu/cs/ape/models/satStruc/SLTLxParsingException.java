package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.utils.APEDimensionsException;

/**
 * The {@code SLTLxParsingException} exception will be thrown if the given SLTLx formula is not specified correctly. 
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxParsingException extends RuntimeException {
	
	/**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    private SLTLxParsingException(String message) {
        super(message);
    }
	
    /**
     * Exception is thrown when the specified SLTLx formula contains a type not specified in the type taxonomy.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingException typeDoesNoExists(String message) {
    	return new SLTLxParsingException(String.format("The SLTLx constraints error. One or more data types used in the formula cannot be recognised. %s", message));
    }

    /**
     * Exception is thrown when the specified SLTLx formula contains a module not specified in the type taxonomy.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingException moduleDoesNoExists(String message) {
    	return new SLTLxParsingException(String.format("The SLTLx constraints error. One or more operations used in the formula cannot be recognised. %s", message));
    }
    
    
    /**
     * Exception is thrown when the specified SLTLx formula contains a free variable.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingException variableNotBound(String message) {
    	return new SLTLxParsingException(String.format("The SLTLx formula contains a non-bound (free) variable. %s", message));
    }
}
