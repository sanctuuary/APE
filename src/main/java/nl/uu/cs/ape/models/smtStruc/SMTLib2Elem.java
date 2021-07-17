package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Interface used to present any element in the SMTLib2 structure.
 * @author Vedran Kasalica
 *
 */
public interface SMTLib2Elem {

	/**
	 * Get string representation of the SMTLIB2 element.
	 * @param synthesisEngine - SMT predicate mapping
	 * @return String representing the SMTLib2 element.
	 */
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine);
}
