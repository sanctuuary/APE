package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model (or x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class BoolVal implements Fact {

	private String boolVal;
	
	
	public BoolVal(String boolVal) {
		this.boolVal = boolVal;
	}


	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		
		constraints.append("(").append(boolVal).append(")");
		
		return constraints.toString();
	}
}
