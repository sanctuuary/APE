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
	private int size;

	public AtomMapping() {
		mappings = new HashMap<String, Integer>();
		reverseMapping = new HashMap<Integer, String>();
		size = 0;
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
	 * it is added to the mapping set and the mapping value is returned, otherwise just the existing mapping value is returned.
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
	
	
	
}
