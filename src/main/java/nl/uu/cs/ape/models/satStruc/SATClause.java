package nl.uu.cs.ape.models.satStruc;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * A fact used for the SAT encoding. Represents a clause in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public interface SATClause extends SATElem {

	public String getCNFEncoding(SATSynthesisEngine synthesisEngine);
	
}
