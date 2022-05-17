package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * Class used to list all usages of the given variables within the formulas.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxVariableOccuranceCollection {

	/** Mapping variables to their predicate properties.*/
	private Map<SLTLxVariable, Set<PredicateLabel>> variableDataTypes;
	/** Mapping variables (depicting memory states) to tool inputs that reference them.*/
	private Map<SLTLxVariable, Set<State>> variableMemoryReferences;
	/** Mapping pairs variables to their usages under binary predicates.*/
	private Map<Pair<SLTLxVariable>, Set<AtomVarType>> binaryPredicates;
	/** Variable mapping to variables it is combined with under a pair. */
	private Map<SLTLxVariable, Set<Pair<SLTLxVariable>>> variablePairs;
			
	/**
	 * Create the variable usage class.
	 */
	public SLTLxVariableOccuranceCollection() {
		super();
		this.variableDataTypes = new HashMap<>();
		this.variableMemoryReferences = new HashMap<>();
		this.binaryPredicates = new HashMap<>();
		this.variablePairs = new HashMap<>();
	}


	/**
	 * Associate the data type to the corresponding variable. 
	 * @param dataType - state property
	 * @param variableState - variable used
	 * @return {@code true} if the property was associated with the variable, {@code false} otherwise.
	 */
	public boolean addDataType(PredicateLabel dataType, SLTLxVariable variableState) {
		if(this.variableDataTypes.get(variableState) == null) {
			Set<PredicateLabel> preds = new HashSet<>();
			boolean tmp = preds.add(dataType);
			this.variableDataTypes.put(variableState, preds);
			return tmp;
		} else {
			return this.variableDataTypes.get(variableState).add(dataType);
		}
		
	}
	
	/**
	 * Associate the tool input state to the corresponding variable. 
	 * @param usedState - state that represents data input
	 * @param variableState - variable used
	 * @return {@code true} if the state was associated with the variable, {@code false} otherwise.
	 */
	public boolean addMemoryReference(State usedState, SLTLxVariable variableState) {
		if(this.variableMemoryReferences.get(variableState) == null) {
			Set<State> preds = new HashSet<>();
			boolean tmp = preds.add(usedState);
			this.variableMemoryReferences.put(variableState, preds);
			return tmp;
		} else {
			return this.variableMemoryReferences.get(variableState).add(usedState);
		}
		
	}
	
	/**
	 * Associate the pair of variables (the order of the variables matter) with the type of atom they are used in.
	 * @param varPair - pair of the variables used
	 * @param relType - Atom type that has the pair of variables as arguments 
	 */
	public void addBinaryPred(Pair<SLTLxVariable> varPair, AtomVarType relType) {
		if(relType.equals(AtomVarType.VAR_VALUE)) {
			return;
		}
		// check if the first element is new
		if(this.variablePairs.get(varPair.getFirst()) == null) {
			// create the first element mapping as it did not occur earlier (and add the second element)
			Set<Pair<SLTLxVariable>> vars = new HashSet<Pair<SLTLxVariable>>();
			vars.add(varPair);
			this.variablePairs.put(varPair.getFirst(), vars);
		} else {
			// add the second element to the mapping of the first 
			this.variablePairs.get(varPair.getFirst()).add(varPair);
		}
		
		// check if second first element is new
		if(this.variablePairs.get(varPair.getSecond()) == null) {
			// create the second element mapping as it did not occur earlier (and add the first element)
			Set<Pair<SLTLxVariable>> vars = new HashSet<Pair<SLTLxVariable>>();
			vars.add(varPair);
			this.variablePairs.put(varPair.getSecond(), vars);
		} else {
			// add the first element to the mapping of the second 
			this.variablePairs.get(varPair.getSecond()).add(varPair);
		}
		
		// check whether the pair occurred earlier
		if(this.binaryPredicates.get(varPair) == null) {
			// create the pair as it did not occur earlier
			Set<AtomVarType> preds = new HashSet<>();
			preds.add(relType);
			this.binaryPredicates.put(varPair, preds);
		} else {
			this.binaryPredicates.get(varPair).add(relType);
		}
		
		
	}

	/**
	 * Get all data types that include the given variable.
	 * @param satVariable - the given variable
	 * @return Set (possibly empty) of memory references that are mentioned in combination with the given variable. 
	 */
	public Set<PredicateLabel> getDataTypes(SLTLxVariable satVariable) {
		Set<PredicateLabel> unaryPreds = this.variableDataTypes.get(satVariable);
		return ((unaryPreds == null) ? new HashSet<PredicateLabel>() : unaryPreds);
	}
	
	/**
	 * Get all memory references  that include the given variable.
	 * @param satVariable - the given variable
	 * @return Set (possibly empty) of memory references that are mentioned in combination with the given variable. 
	 */
	public Set<State> getMemoryReferences(SLTLxVariable satVariable) {
		Set<State> unaryPreds = this.variableMemoryReferences.get(satVariable);
		return ((unaryPreds == null) ? new HashSet<State>() : unaryPreds);
	}
	
	/**
	 * Get all binary relations that include the given variable pair as arguments (in the given order).
	 * @param varPair - the given variable pair
	 * @return Set (possibly empty) of binary predicates that were used over the variable pair.
	 */
	public Set<AtomVarType> getBinaryPredicates(Pair<SLTLxVariable> varPair) {
		Set<AtomVarType> binPreds = this.binaryPredicates.get(varPair);
		return ((binPreds == null) ? new HashSet<AtomVarType>() : binPreds);
	}
	
	
	/**
	 * Return the set of pairs that are used in the formulas, where the given variable is one of the two in pair.
	 * @param variable - variable that is in the pairs
	 * @return Set of Pair objects that are used in the formulas that contain the given variable.
	 */
	public Set<Pair<SLTLxVariable>> getPairsContainingVarAsArg(SLTLxVariable variable) {
		Set<Pair<SLTLxVariable>> pairs = new HashSet<>();
		for(Pair<SLTLxVariable> pair : APEUtils.safe(this.variablePairs.get(variable))){
			pairs.add(pair);
		}
		return pairs;
	}
	
}
