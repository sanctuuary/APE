package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.SLTLxVariable;
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
	private List<SLTLxVariable> inputs;
	private List<SLTLxVariable> outputs;
	


	public SLTLxOperation(AbstractModule module, List<SLTLxVariable> inputs, List<SLTLxVariable> outputs) {
		super();
		this.module = module;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		
		SLTLxFormula operationUsage = enforceOperation(stateNo, synthesisEngine);
		
		return operationUsage.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		
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
		
		SLTLxFormula inputsRule;
		/* In case no inputs were specified, the input rule is true by default. */
		if(inputs.isEmpty()) {
			inputsRule = SLTLxAtom.getTrue();
		} else {
			Set<SLTLxFormula> allInputs = new HashSet<>();
			for(SLTLxVariable inputVar : inputs) {
				Set<SLTLxAtomVar> inputAtoms = new HashSet<>();
				for(State inState : synthesisEngine.getTypeAutomaton().getUsedTypesBlock(stateNo).getStates()) {
					inputAtoms.add(new SLTLxAtomVar(AtomVarType.VAR_VALUE, inState, inputVar));
				}
				SLTLxDisjunction currInputStates = new SLTLxDisjunction(inputAtoms);
				allInputs.add(currInputStates);
			}
			inputsRule = new SLTLxConjunction(allInputs);
		}
		
		SLTLxFormula outputsRule;
		/* In case no outputs were specified, the output rule is true by default. */
		if(outputs.isEmpty()) {
			outputsRule = SLTLxAtom.getTrue();
		} else {
			Set<SLTLxFormula> allOutputs = new HashSet<>();
			for(SLTLxVariable outputVar : outputs) {
				Set<SLTLxAtomVar> outputAtoms = new HashSet<>();
				for(State outState : synthesisEngine.getTypeAutomaton().getMemoryTypesBlock(stateNo + 1).getStates()) {
					outputAtoms.add(new SLTLxAtomVar(AtomVarType.VAR_VALUE, outState, outputVar));
				}
				SLTLxDisjunction outputPossibilities = new SLTLxDisjunction(outputAtoms);
				allOutputs.add(outputPossibilities);
			}
			outputsRule = new SLTLxConjunction(allOutputs);
		}
		
		return new SLTLxConjunction(moduleRule, inputsRule, outputsRule);
	}

}
