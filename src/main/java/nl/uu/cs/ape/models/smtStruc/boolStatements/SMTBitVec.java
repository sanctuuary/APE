package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Elem;

/**
 * Interface used to present any predicate in the SMTLib2 structure.
 * @author Vedran Kasalica
 *
 */
public class SMTBitVec implements SMTFunctionArgument {

	private final int stateNumber; 
	private final SMTDataType stateType;
	
	
	public SMTBitVec(SMTDataType stateType, State stateUsed) {
		this.stateNumber = stateUsed.getTypeDependantStateNumber();
		this.stateType = stateType;
	}
	
	public SMTBitVec(SMTDataType stateType, int stateNumber) {
		this.stateNumber = stateNumber;
		this.stateType = stateType;
	}
	
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		return "(_ bv" + stateNumber + " " + this.calculateBitVecBase(stateType, synthesisEngine) + ")";
	}
	
	
	private int calculateBitVecBase(SMTDataType stateType, SMTSynthesisEngine synthesisEngine) {
		return SMTUtils.countBits(synthesisEngine.getAutomatonSize(stateType));
	}

	
}
