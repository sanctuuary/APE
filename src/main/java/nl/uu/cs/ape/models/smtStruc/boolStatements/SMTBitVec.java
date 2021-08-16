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
	/** type of state that is used */
	private SMTDataType stateType;
	/** Size of the bit vector that should be used (number of bits) */
	private int bitVecBase;
	/**
	 * Create Bit Vector representation of a decimal number.
	 * @param stateType - type of state that is used
	 * @param stateUsed - state that should be represented as BitVec
	 */
	public SMTBitVec(SMTDataType stateType, State stateUsed) {
		this.stateNumber = stateUsed.getTypeDependantStateNumber();
		this.stateType = stateType;
	}
	
	/**
	 * Create Bit Vector representation of a decimal number.
	 * @param stateType - type of state that is used
	 * @param stateNumber - decimal number that should be defined
	 */
	public SMTBitVec(SMTDataType stateType, int stateNumber) {
		this.stateNumber = stateNumber;
		this.stateType = stateType;
	}
	
	/**
	 * Create Bit Vector representation of a decimal number.
	 * @param bitVecBase - size of the bit vector that should be used (number of bits)
	 * @param stateNumber - decimal number that should be defined
	 */
	public SMTBitVec(int bitVecBase, int stateNumber) {
		this.stateNumber = stateNumber;
		this.bitVecBase = bitVecBase;
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {	
		if(stateType != null) {
			this.bitVecBase = this.calculateBitVecBase(stateType, synthesisEngine);
		}
		return "(_ bv" + stateNumber + " " + this.bitVecBase + ")";
	}
	
	
	private int calculateBitVecBase(SMTDataType stateType, SMTSynthesisEngine synthesisEngine) {
		return SMTUtils.countBits(synthesisEngine.getAutomatonSize(stateType));
	}

	
}
