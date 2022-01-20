package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.SynthesisEngine;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;

/**
 * Structure used to model the operation specification, used as a part of the next operation (<Op>) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATOperation extends SATFact {

	private AbstractModule module;
	private List<SATVariable> inputs;
	private List<SATVariable> outputs;
	


	public SATOperation(AbstractModule module, List<SATVariable> inputs, List<SATVariable> outputs) {
		super();
		this.module = module;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		
		SATFact operationUsage = enforceOperation(stateNo, synthesisEngine);
		
		return operationUsage.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		
		SATFact operationModel = enforceOperation(stateNo, synthesisEngine);
		
		return operationModel.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}
	
	
	/**
	 * Create the SLTLx object that enforces usage of the operation and the corresponding inputs/outputs.
	 * @param stateNo - current state number
	 * @param synthesisEngine - synthesis engine
	 * @return Fact representing the existence of the operation.
	 */
	private SATFact enforceOperation(int stateNo, SATSynthesisEngine synthesisEngine) {
		State modulState = synthesisEngine.getModuleAutomaton().getSafe(stateNo);
		if(modulState == null) {
			return SATAtom.getFalse();
		}
		SATAtom moduleRule = new SATAtom(AtomType.MODULE, this.module, modulState);
		
		
		Set<SATFact> allInputs = new HashSet<>();
		for(SATVariable inputVar : inputs) {
			Set<SATAtomVar> inputAtoms = new HashSet<>();
			for(State inState : synthesisEngine.getTypeAutomaton().getUsedTypesBlock(stateNo).getStates()) {
				inputAtoms.add(new SATAtomVar(AtomVarType.VAR_REF, inState, inputVar));
			}
			SATOrStatement currInputStates = new SATOrStatement(inputAtoms);
			allInputs.add(currInputStates);
		}
		SATAndStatement inputsRule = new SATAndStatement(allInputs);
		
		
		Set<SATFact> allOutputs = new HashSet<>();
		for(SATVariable outputVar : outputs) {
			Set<SATAtomVar> outputAtoms = new HashSet<>();
			for(State outState : synthesisEngine.getTypeAutomaton().getMemoryTypesBlock(stateNo).getStates()) {
				outputAtoms.add(new SATAtomVar(AtomVarType.VAR_REF, outState, outputVar));
			}
			SATOrStatement outputPossibilities = new SATOrStatement(outputAtoms);
			allOutputs.add(outputPossibilities);
		}
		SATAndStatement outputsRule = new SATAndStatement(allOutputs);
		
		
		return new SATAndStatement(moduleRule, inputsRule, outputsRule);
	}

}
