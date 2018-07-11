package SAT.automaton;

import java.util.HashMap;
import java.util.Map;

/**
 * Class is used to store the data used for representing the atoms with integer numbers. 
 * Required for the SAT representation of the CNF formula.
 * @author VedranPC
 *
 */
public class AtomMapping {

	private Map<String, Integer> mappings;
	private int size;

	public AtomMapping() {
		mappings = new HashMap<>();
		size = 0;
	}

	/**
	 * Function is returning the mapping number of the @atom (>1). If the Atom did not occur before,
	 * it is added to the mapping set and then mapping value is returned.
	 * @param atom - atom that is being mapped
	 * @return Mapping number of the atom (>1)
	 */
	public Integer add(String atom) {
		Integer id;
		if ((id = mappings.get(atom)) == null) {
			mappings.put(atom, size++);
			return size;
		}
		return id;
	}
	
	
	/**
	 * Function is returning the mapping number of the @predicate ( @argument). If the Atom did not occur before,
	 * it is added to the mapping set and then mapping value is returned.
	 * @param predicate - predicate of the mapped atom
	 * @param argument - argument of the mapped atom
	 * @return Mapping number of the atom (>1)
	 */
	public Integer add(String predicate, String argument) {
		String atom = predicate + "(" + argument + ")";
		Integer id;
		if ((id = mappings.get(atom)) == null) {
			mappings.put(atom, size++);
			return size;
		}
		return id;
	}

	/**
	 * Return the mapping value (Integer) for the @atom. If the @atom was not mapped it returns null.
	 * @param atom
	 * @return
	 */
	public Integer find(String atom) {
		return mappings.get(atom);
	}

	/**
	 * Returns the size of the mapping set.
	 * @return
	 */
	public int getSize(){
		return size;
	}
	
}
