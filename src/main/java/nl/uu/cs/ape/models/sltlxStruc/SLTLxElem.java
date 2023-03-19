package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Class represents any element of the SLTLx encoding.
 * 
 * @author Vedran Kasalica
 *
 */
public interface SLTLxElem {

	/**
	 * Create the CNF encoding of the statement and return the string
	 * representation.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return The string that represent cnf clauses.
	 */
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine);

	/**
	 * CreatE the CNF encoding of the negation of the statement and return the
	 * string representation.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return The string that represent the negated cnf clauses.
	 */
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine);

}
