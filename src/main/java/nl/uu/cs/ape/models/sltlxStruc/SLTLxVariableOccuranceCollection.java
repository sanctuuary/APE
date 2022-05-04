package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.automaton.SLTLxVariable;
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

	/** Mapping variables to their usages under unary predicates.*/
	private Map<SLTLxVariable, Set<PredicateLabel>> unaryPredicates;
	/** Mapping pairs variables to their usages under binary predicates.*/
	private Map<Pair<SLTLxVariable>, Set<AtomVarType>> binaryPredicates;
	/** Variable mapping to variables it is combined with under a pair. */
	private Map<SLTLxVariable, Set<Pair<SLTLxVariable>>> variablePairs;
			
	/**
	 * Create the variable usage class.
	 */
	public SLTLxVariableOccuranceCollection() {
		super();
		this.unaryPredicates = new HashMap<>();
		this.binaryPredicates = new HashMap<>();
		this.variablePairs = new HashMap<>();
	}


	/**
	 * Associate the the unary predicate to the corresponding variable. 
	 * @param argumentState - variable used
	 * @param predicate - unary predicate
	 * @return {@code true} if the predicated was associated with the variable, {@code false} otherwise.
	 */
	public boolean addUnaryPair(SLTLxVariable argumentState, PredicateLabel predicate) {
		if(this.unaryPredicates.get(argumentState) == null) {
			Set<PredicateLabel> preds = new HashSet<>();
			boolean tmp = preds.add(predicate);
			this.unaryPredicates.put(argumentState, preds);
			return tmp;
		} else {
			return this.unaryPredicates.get(argumentState).add(predicate);
		}
		
	}
	/**
	 * Associate the the binary predicate ({@link AtomVarType.VAR_EQUIVALENCE} or {@link AtomVarType.TYPE_DEPENDENCY_VAR}) 
	 * to the corresponding pair of variables (the order of the variables matter). 
	 * @param varPair - pair of the variables used
	 * @param relType - binary predicate type (<b>NOTE: The relation cannot be {@link AtomVarType.VAR_REF}</b>)
	 * @return {@code true} if the predicated was associated with the variable pair, {@code false} otherwise.
	 */

	public void addBinaryPair(Pair<SLTLxVariable> varPair, AtomVarType relType) {
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
	 * Get all unary predicates that include the given variable.
	 * @param satVariable - the given variable
	 * @return Set (possibly empty) of unary predicates that are mentioned in combination with the given variable. 
	 */
	public Set<PredicateLabel> getUnaryPredicates(SLTLxVariable satVariable) {
		Set<PredicateLabel> unaryPreds = this.unaryPredicates.get(satVariable);
		return ((unaryPreds == null) ? new HashSet<PredicateLabel>() : unaryPreds);
	}
	
	/**
	 * Get all binary relations that include the given variable pair as arguments (in the given order).
	 * @param satVariable - the given variable pair
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