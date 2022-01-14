package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model (or x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class BitVecComparisonStatement implements SMTFact {

	private SMTBitVectorOp operation;
	private SMTBoundedVar boundedVar;
	private SMTBitVec bitVecNumber;
	
	
	public BitVecComparisonStatement(SMTFact arg1, SMTFact arg2) {
		super();
	}


	public BitVecComparisonStatement(SMTBitVectorOp operation, SMTBoundedVar boundedVar, SMTBitVec bitVecNumber) {
		this.operation = operation;
		this.boundedVar = boundedVar;
		this.bitVecNumber = bitVecNumber;
	}


	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(")
				.append(operation.toString())
				.append(" ")
				.append(boundedVar.getSMT2Encoding(synthesisEngine))
				.append(" ")
				.append(bitVecNumber.getSMT2Encoding(synthesisEngine))
			.append(")");
		
		return constraints.toString();
	}
}
