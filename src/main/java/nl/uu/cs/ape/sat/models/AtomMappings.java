package nl.uu.cs.ape.sat.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Atom;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 *  The {@code AtomMappings} class is used to store the data used for representing the atoms with integer numbers. Atoms are not a separate data structure, 
 * but a string combination of a {@link TaxonomyPredicate} and a {@link State} as am argument.<br/>
 * Required for the SAT representation of the CNF formula.
 * @author Vedran Kasalica
 *
 */
public class AtomMappings {

	private Map<Atom, Integer> mappings;
	private Map<Integer, Atom> reverseMapping;
	private Map<String, Atom> mapped;
	/** Number of mapped predicates */
	private int size;
	/** Number  of auxiliary introduced variables */
	private int auxiliary;
	/** Number of all auxiliary variables */
	private int auxMax = 100000;

	public AtomMappings() {
		mappings = new HashMap<Atom, Integer>();
		reverseMapping = new HashMap<Integer, Atom>();
		mapped = new HashMap<String, Atom>();
		/** First {@link #auxMax} variables are reserved for auxiliary variables */
		size = auxMax + 1;
		auxiliary = 1;
	}

	/**
	 * Function is returning the mapping number of the <b>{@code predicate(argument)}</b>. If the Atom did not occur before,
	 * it is added to the mapping set and the mapping value is returned, otherwise the existing mapping value is returned.
	 * @param predicate - predicate of the mapped atom
	 * @param usedInState - argument of the mapped atom (usually name of the type/module state)
	 * @return Mapping number of the atom (number is always > 0)
	 */
	public Integer add(PredicateLabel predicate, State usedInState, WorkflowElement elementType) {
		Atom atom = new Atom(predicate, usedInState, elementType);
		
		Integer id;
		if ((id = mappings.get(atom)) == null) {
			if( mapped.get(atom.toString()) != null) {
				throw new Error("Encoding error. Two or more mappings map the same string: '" + atom.toString() + "'.");
			}
			size++;
			mappings.put(atom, size);
			reverseMapping.put(size, atom);
			mapped.put(atom.toString(), atom);
			return size;
		}
		return id;
	}
	
	/**
	 * Function is returning the mapping number of the <b>{@code predicate[memoryState](usedState)}</b>. If the Atom did not occur before,
	 * it is added to the mapping set and the mapping value is returned, otherwise the existing mapping value is returned.
	 * @param predicate - predicate of the mapped atom
	 * @param memoryState - argument of the mapped atom that corresponds to the memory state in the type automaton when the predicate was created
	 * @param usedState - argument of the mapped atom that corresponds to the used type state in the type automaton when the predicate is being used as a tool input (or workflow output)
	 * @return Mapping (integer) number of the atom (number is always > 0)
	
	public Integer add(TaxonomyPredicate predicate,State usedInState, State referedState) {
		Atom atom = new Atom(predicate, usedInState, referedState);
		Integer id ;
		if ((id = mappings.get(atom)) == null) {
			size++;
			mappings.put(atom, size);
			reverseMapping.put(size, atom);
			return size;
		}
		return id;
	} */

	/**
	 * Return the mapping value (Integer) for the <b>atom</b>. If the <b>atom</b> was not mapped it returns null.
	 * @param atom - string representation of the atom
	 * @return mapping of the atom
	 */
	public Integer findMapping(Atom atom) {
		return mappings.get(atom);
	}
	
	
	/**
	 * Return the mapping value (Integer) for the<b>atom</b>. If the <b>atom</b> was not mapped it returns null.
	 * @param mapping - Integer mapping of the atom
	 * @return The original atom
	 */
	public Atom findOriginal(Integer mapping) {
		return reverseMapping.get(mapping);
		
	}

	/**
	 * Returns the size of the mapping set.
	 * @return
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * Get the next auxiliary number and increase the counter by 1.
	 * @return Mapping number that can be used for auxiliary variables.
	 */
	public int getNextAuxNum() {
		return auxiliary++;
	}
	
	public void resetAuxVariables() {
		auxiliary = 1;
	}
	
	
	/**
	 * Get the number of mapped auxiliary variables that are not part of the solution.
	 * @return Number of mapped auxiliary variables.
	 */
	public int getCurrNumOfMappedAuxVar() {
		return auxiliary;
	}
	
	/**
	 * Get the max number of mapped auxiliary variables that are not part of the solution.
	 * @return Max number of possible mapped auxiliary variables.
	 */
	public int getMaxNumOfMappedAuxVar() {
		return auxMax;
	}
	
	
	
}
