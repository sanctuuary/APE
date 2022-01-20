package nl.uu.cs.ape.models.satStruc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;

/**
 * Structure used to model the operation specification, used as a part of the next operation (<Op>) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxOperation extends SLTLxFormula {

	private AbstractModule module;
	private List<SATVariable> inputs;
	private List<SATVariable> outputs;
	


	public SLTLxOperation(AbstractModule module, List<SATVariable> inputs, List<SATVariable> outputs) {
		super();
		this.module = module;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		
		SLTLxFormula operationUsage = enforceOperation(stateNo, synthesisEngine);
		
		return operationUsage.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		
		SLTLxFormula operationModel = enforceOperation(stateNo, synthesisEngine);
		
		return operationModel.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}
	
	
	/**
	 * Create the SLTLx object that enforces usage of the operation and the corresponding inputs/outputs.
	 * @param stateNo - current state number
	 * @param synthesisEngine - synthesis engine
	 * @return Fact representing the existence of the operation.
	 */
	private SLTLxFormula enforceOperation(int stateNo, SATSynthesisEngine synthesisEngine) {
		State modulState = synthesisEngine.getModuleAutomaton().getSafe(stateNo);
		if(modulState == null) {
			return SLTLxAtom.getFalse();
		}
		SLTLxAtom moduleRule = new SLTLxAtom(AtomType.MODULE, this.module, modulState);
		
		
		Set<SLTLxFormula> allInputs = new HashSet<>();
		for(SATVariable inputVar : inputs) {
			Set<SLTLxAtomVar> inputAtoms = new HashSet<>();
			for(State inState : synthesisEngine.getTypeAutomaton().getUsedTypesBlock(stateNo).getStates()) {
				inputAtoms.add(new SLTLxAtomVar(AtomVarType.VAR_REF, inState, inputVar));
			}
			SLTLxDisjunction currInputStates = new SLTLxDisjunction(inputAtoms);
			allInputs.add(currInputStates);
		}
		SLTLxConjunction inputsRule = new SLTLxConjunction(allInputs);
		
		
		Set<SLTLxFormula> allOutputs = new HashSet<>();
		for(SATVariable outputVar : outputs) {
			Set<SLTLxAtomVar> outputAtoms = new HashSet<>();
			for(State outState : synthesisEngine.getTypeAutomaton().getMemoryTypesBlock(stateNo).getStates()) {
				outputAtoms.add(new SLTLxAtomVar(AtomVarType.VAR_REF, outState, outputVar));
			}
			SLTLxDisjunction outputPossibilities = new SLTLxDisjunction(outputAtoms);
			allOutputs.add(outputPossibilities);
		}
		SLTLxConjunction outputsRule = new SLTLxConjunction(allOutputs);
		
		
		return new SLTLxConjunction(moduleRule, inputsRule, outputsRule);
	}

}
