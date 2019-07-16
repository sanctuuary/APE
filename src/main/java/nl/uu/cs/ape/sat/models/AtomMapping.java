package nl.uu.cs.ape.sat.models;

import java.util.HashMap;
import java.util.Map;

import nl.uu.cs.ape.sat.models.constructs.Predicate;

/**
 * Class is used to store the data used for representing the atoms with integer numbers. Atoms are not a separate data structure, 
 * but a string combination of a {@link Predicate} and a {@link State} as am argument.<br/>
 * Required for the SAT representation of the CNF formula.
 * @author Vedran Kasalica
 *
 */
public class AtomMapping {

	private Map<String, Integer> mappings;
	private Map<Integer, String> reverseMapping;
	/** Number of mapped predicates */
	private int size;
	/** Number  of auxiliary introduced variables */
	private int auxiliary;
	/** Number of all auxiliary variables */
	private int auxMax = 100000;

	public AtomMapping() {
		mappings = new HashMap<String, Integer>();
		reverseMapping = new HashMap<Integer, String>();
		/** First {@link #auxMax} variables are reserved for auxiliary variables */
		size = auxMax + 1;
		auxiliary = 1;
	}

	/**
	 * Function is returning the mapping number of the <b>atom</b> (>0). If the <b>atom</b> did not occur before,
	 * it is added to the mapping set and the mapping value is returned, otherwise just the existing mapping value is returned.
	 * @param atom - atom that is being mapped [format: <b>{@code predicate(argument)}</b> ]
	 * @return Mapping number of the atom (number is always > 0)
	 */
	public Integer add(String atom) {
		Integer id;
		if ((id = mappings.get(atom)) == null) {
			size ++;
			mappings.put(atom, size);
			reverseMapping.put(size, atom);
			return size;
		}
		return id;
	}
	
	
	/**
	 * Function is returning the mapping number of the <b>{@code predicate(argument)}</b>. If the Atom did not occur before,
	 * it is added to the mapping set and the mapping value is returned, otherwise the existing mapping value is returned.
	 * @param predicate - predicate of the mapped atom
	 * @param argument - argument of the mapped atom
	 * @return Mapping number of the atom (number is always > 0)
	 */
	public Integer add(String predicate, String argument) {
		String atom = predicate + "(" + argument + ")";
		Integer id;
		if ((id = mappings.get(atom)) == null) {
			size++;
			mappings.put(atom, size);
			reverseMapping.put(size, atom);
			return size;
		}
		if(id.toString().contains("@RESERVED_CNF_")) {
			System.out.println(id + " for: " + predicate + "(" + argument);
		}
		return id;
	}

	/**
	 * Return the mapping value (Integer) for the <b>atom</b>. If the <b>atom</b> was not mapped it returns null.
	 * @param atom - string representation of the atom
	 * @return mapping of the atom
	 */
	public Integer findMapping(String atom) {
		return mappings.get(atom);
	}
	
	/**
	 * Return the mapping value (Integer) for the<b>atom</b>. If the <b>atom</b> was not mapped it returns null.
	 * @param mapping - Integer mapping of the atom
	 * @return original atom
	 */
	public String findOriginal(Integer mapping) {
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
