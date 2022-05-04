package nl.uu.cs.ape.automaton;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.sltlxStruc.CNFClause;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtomVar;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxEquivalence;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxImplication;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegation;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxVariableOccuranceCollection;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxVariableSubstitutionCollection;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxXOR;

/***
 * The {@code State} class is used to represent a variable for type states. The variable only represents states from the type automatons, excluding the module automaton states. 
 * <p>
 * Labeling of the automaton is provided in
 * <a href="https://github.com/sanctuuary/APE/blob/master/res/WorkflowAutomaton_Implementation.png">/APE/res/WorkflowAutomaton_Implementation.png</a>.
 *
 * @author Vedran Kasalica
 */
public class SLTLxVariable implements StateInterface, PredicateLabel {

	/** Unique name of the type state variable */
    private final String variableID;


    /**
     * Create new type state variable.
     * @param variableName - Unique variable name
     */
    public SLTLxVariable(String variableName) {
		super();
		this.variableID = variableName;
	}

	/**
	 * @return the variableID
	 */
	public String getVariableName() {
		return variableID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variableID == null) ? 0 : variableID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SLTLxVariable other = (SLTLxVariable) obj;
		if (variableID == null) {
			if (other.variableID != null)
				return false;
		} else if (!variableID.equals(other.variableID))
			return false;
		return true;
	}


	@Override
	public String getPredicateID() {
		return variableID;
	}

	public int compareTo(PredicateLabel other) {
        return this.getPredicateID().compareTo(other.getPredicateID());
    }

	@Override
	public String getPredicateLabel() {
		return variableID;
	}

	@Override
	public String getPredicateLongLabel() {
		return variableID;
	}

	/**
	 * Get the set of clauses that enforce substitution of the variable under the existential
	 * quantification for the given set of memory states.
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<String> getExistentialCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableSubtitutions, SATSynthesisEngine synthesisEngine) {
		Set<SLTLxFormula> varRefs = new HashSet<SLTLxFormula>();
		for(State state : getVariableDomain(stateNo, synthesisEngine)) {
			SLTLxAtomVar currAtomVar = new SLTLxAtomVar(AtomVarType.VAR_VALUE, state, this);
			SLTLxAtom currIsEmptyState = new SLTLxAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SLTLxConjunction(currAtomVar, new SLTLxNegation(currIsEmptyState)));
		}
		SLTLxDisjunction allVars = new SLTLxDisjunction(varRefs);
		
		return allVars.getCNFEncoding(stateNo, variableSubtitutions, synthesisEngine);
	}
	
	/**
	 * Get the set of clauses that enforce substitution of the variable under the universal
	 * quantification for the given set of states. In addition, the encoding ensures that the 
	 * usage of variables in atoms will imply usage of the corresponding states as well (mimicking substitution).<br/><br/>
	 * 
	 * <b>IMPORTANT: This method should be call after the binded subformula was visited (the corresponding CNF was generated),
	 * in order to ensure that all occurrences of the variable were taken into account.</b>
	 * 
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<String> getUniversalCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableSubtitutions, SATSynthesisEngine synthesisEngine) {
		/** Setting up the domain of the variable. */
		Set<SLTLxFormula> varRefs = new HashSet<SLTLxFormula>();
		for(State state : getVariableDomain(stateNo, synthesisEngine)) {
			System.out.println(state.getPredicateID());
			SLTLxAtomVar currAtomVar = new SLTLxAtomVar(AtomVarType.VAR_VALUE, state, this);
			SLTLxAtom currIsEmptyState = new SLTLxAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SLTLxXOR(currAtomVar, currIsEmptyState));
		}
		SLTLxConjunction allVars = new SLTLxConjunction(varRefs);
		return allVars.getCNFEncoding(stateNo, variableSubtitutions, synthesisEngine);
	}
	
	
	/**
	 * The encoding ensures that the variable substitution preserves the properties of the data objects,
	 * i.e., if a variable X substitutes state S, the two have to satisfy the same properties.<br/> 
	 * <i> e.g., (VAL(?x,a) => (P(?x) <=> P(a))</i>
	 * <br/><br/>
	 * 
	 * <b>IMPORTANT: This method should be call after the binded subformula was visited (the corresponding CNF was generated),
	 * in order to ensure that all the occurrences of the variable were taken into account.</b>
	 * 
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<String> getVariableSubstitutionToPresereProperties(int stateNo, SLTLxVariableSubstitutionCollection variableSubtitutions, SATSynthesisEngine synthesisEngine) {
		Set<SLTLxFormula> allFacts = new HashSet<>();
		SLTLxVariableOccuranceCollection varOccurances = synthesisEngine.getVariableUsage(); 
		
		/** Introduce rules to enforce substitution over unary predicates. 
		 * E.g., Val(?x,s1) => (P(?x) <=> P(s1)) */
		allFacts.addAll(generateUnarySubstitutionRules(this, variableSubtitutions, varOccurances));
		
		/** Introduce rules to enforce substitution over binary predicates
		 *  * E.g., Val(?x,s1) & Val(?y,s2) => (IS_V(?x,?y) <=> IS(s1,s2))
		 *  
		 * ..in all the variable pairs where the current variable occurs. */
		varOccurances.getPairsContainingVarAsArg(this).forEach(
				pair -> 
				allFacts.addAll(generateBinarySubstitutionRules(pair, variableSubtitutions, varOccurances)));
		
		SLTLxConjunction andFacts = new SLTLxConjunction(allFacts);
		
		return andFacts.getCNFEncoding(stateNo, variableSubtitutions, synthesisEngine);
	}

	/**
	 * Generate the rules that enforce substitution over binary predicates.<br/> <i> e.g., (VAL(?x,a) => (P(?x) <=> P(a))</i>
	 * @param pair - a pair of variables that will be substituted
	 * @param variableSubtitutions - collection of substitutions for each variable
	 * @param varOccurances - collection that tracks occurrences of variables
	 * @return Set of formulas that represent the encoding of the rules 
	 */
	private static Set<SLTLxFormula> generateUnarySubstitutionRules(SLTLxVariable variable, SLTLxVariableSubstitutionCollection variableSubtitutions, SLTLxVariableOccuranceCollection varOccurances) {
		Set<SLTLxFormula> allFacts = new HashSet<>();
		
		/** Introduce rules to enforce substitution over unary predicates when needed. 
		 * e.g., (VAL(?x,a) => (P(?x) <=> P(a)) 
		 * */
		for(PredicateLabel usedPred : varOccurances.getUnaryPredicates(variable)) {
			for(State varState : variableSubtitutions.getVariableDomain(variable)) {
				/* (VAL(?x,a) => (P(?x) <=> P(a)) */
				allFacts.add(new SLTLxImplication(
												new SLTLxAtomVar(
														AtomVarType.VAR_VALUE,
														varState,
														variable),
										new SLTLxEquivalence(
												new SLTLxAtomVar(
														AtomVarType.TYPE_V,
														usedPred,
														variable),
												new SLTLxAtom(
														varState.getWorkflowStateType(),
														usedPred,
														varState)
												)
										));
			}
		}
		
		
		
		return allFacts;
	}
		
		
	
	/**
	 * Generate the rules that enforce substitution over binary predicates. <br/> <i> e.g., VAL(?x,a) & VAL(?y,b) =>  (R_v(x,y) <=> R(a,b))  </i> 
	 * @param pair - a pair of variables that will be substituted
	 * @param variableSubtitutions - collection of substitutions for each variable
	 * @param varOccurances - collection that tracks occurrences of variables
	 * @return Set of formulas that represent the encoding of the rules 
	 */
	private static Set<SLTLxFormula> generateBinarySubstitutionRules(Pair<SLTLxVariable> pair, SLTLxVariableSubstitutionCollection variableSubtitutions, SLTLxVariableOccuranceCollection varOccurances) {
		Set<SLTLxFormula> allFacts = new HashSet<>();
		
		SLTLxVariable var1 = pair.getFirst();
		SLTLxVariable var2 = pair.getSecond();
		
		/*	Skip the pair if one of the variables is not in the scope (the rules were implemented already). */
		if(variableSubtitutions.getVariableDomain(var1) == null || variableSubtitutions.getVariableDomain(var2) == null) {
			return allFacts;
		}
		for(AtomVarType atomVarType : varOccurances.getBinaryPredicates(pair)) {
			AtomType atomType = inferAtomType(atomVarType);
			if(atomType == null) continue;
			
			for(State var1State : variableSubtitutions.getVariableDomain(var1)) {
				for(State var2State : variableSubtitutions.getVariableDomain(var2)) {
					/* VAL(?x,a) & VAL(?y,b) =>  (P(?x,?y) <=> P(a,b)) */ 
				allFacts.add(new SLTLxImplication(
										new SLTLxConjunction(
												new SLTLxAtomVar(
														AtomVarType.VAR_VALUE,
														var1State,
														var1),
												new SLTLxAtomVar(
														AtomVarType.VAR_VALUE,
														var2State,
														var2)
												),
										new SLTLxEquivalence(
												new SLTLxAtomVar(
														atomVarType,
														var1,
														var2),
												new SLTLxAtom(
														atomType,
														var1State,
														var2State)
												)
										));
				}
			}
		}
		return allFacts;
	}

	/**
	 * When a constant is substituting a variable, infer type of the new atom based on the type of the one 
	 * containing the variable.
	 * @param atomVarType - type of the atom containing a variable
	 * @return Type of the new atom as long as the substitution is applicable, {@code null} otherwise.
	 */
	private static AtomType inferAtomType(AtomVarType atomVarType) {
		if(atomVarType.equals(AtomVarType.VAR_EQUIVALENCE)) {
			return AtomType.IDENTITI_RELATION;
		} else if(atomVarType.equals(AtomVarType.R_RELATION_V)) {
		 return AtomType.R_RELATON; 
		} else {
			return null;
		}
	}

	/**
	 * Get set of states that correspond to the domain of the variable. The states include all the existing data objects
	 * including those that will be the output of the next operation.
	 * 
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine
	 * @return
	 */
	public static Set<State> getVariableDomain(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<State> variableDomain = new HashSet<State>();
		/** Domain includes the objects generated by the next tool, 
		 * and thus we use the next state to get the domain of the variable.*/
		int nextStateNo = stateNo + 1;
		for(State state :synthesisEngine.getTypeAutomaton().getAllMemoryStatesUntilBlockNo(nextStateNo)) {
			variableDomain.add(state);
		}
		return variableDomain;
	}

}