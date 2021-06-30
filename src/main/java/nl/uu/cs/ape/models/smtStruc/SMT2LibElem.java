package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.models.SMTPredicateMappings;

/**
 * Interface used to present any element in the smt2lib structure.
 * @author Vedran Kasalica
 *
 */
public interface SMT2LibElem {

	/**
	 * Get striNg representation of the smt2lib element.
	 * @param mapping - SMT predicate mapping
	 * @return String representing the SMT2Lib element.
	 */
	public String toString(SMTPredicateMappings mapping);
}
