package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.utils.APEDimensionsException;

/**
 * The {@code SLTLxParsingPredicatesException} exception will be thrown if the  variables are free or domain predicates (e.g., operations, data types, etc.) specified in the SLTLx formula are not specified correctly. 
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxParsingPredicatesException extends RuntimeException {
	
	/**
     * Instantiates a new Ape exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    private SLTLxParsingPredicatesException(String message) {
        super(message);
    }
	
    /**
     * Exception is thrown when the specified SLTLx formula contains a type not specified in the type taxonomy.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingPredicatesException typeDoesNoExists(String message) {
    	return new SLTLxParsingPredicatesException(String.format("SLTLx formula syntax error. One or more data types used in a specified formula cannot be recognised. %s", message));
    }

    /**
     * Exception is thrown when the specified SLTLx formula contains a module not specified in the type taxonomy.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingPredicatesException moduleDoesNoExists(String message) {
    	return new SLTLxParsingPredicatesException(String.format("SLTLx formula syntax error. One or more operations used in a specified formula cannot be recognised. %s", message));
    }
    
    /**
     * Exception is thrown when the specified SLTLx formula contains a free variable.
     * @param message - Application specific message that may help the user solve the problem.
     * @return SLTLx Parsing exception with information that may help the user solve the problem.
     */
    public static SLTLxParsingPredicatesException variableNotBound(String message) {
    	return new SLTLxParsingPredicatesException(String.format("SLTLx formula syntax error. A specified formula contains a free variable, which is not supported. %s", message));
    }
}
