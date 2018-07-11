package SAT.models;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;

public class AllTypes {

	private Map<String, Type> types;

	public AllTypes() {

		this.types = new HashMap<>();

	}

	public Map<String, Type> getTypes() {
		return types;
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional
	 * operation) and returns it. More formally, adds the specified element e to
	 * this set if the set contains no element e2 such that (e==null ? e2==null :
	 * e.equals(e2)). If this set already contains the element, the call leaves the
	 * set unchanged and returns the existing element. In combination with the
	 * restriction on constructors, this ensures that sets never contain duplicate
	 * elements.
	 * 
	 * @param type
	 *            - the element that needs to be added
	 * @return The same element if it's a new one or the existing element if this
	 *         set contains the specified element.
	 */
	public Type addType(Type type) {
		Type tmpType;
		if ((tmpType = types.get(type.getTypeID())) != null) {
			return tmpType;
		} else {
			this.types.put(type.getTypeID(), type);
			return type;
		}
	}

	/**
	 * Returns the type to which the specified key is mapped, or null if this map
	 * contains no mapping for the type ID.
	 * 
	 * @param typeID
	 *            - the key whose associated value is to be returned
	 * @return the type to which the specified key is mapped, or null if this map
	 *         contains no mapping for the type ID
	 */
	public Type get(String typeID) {
		return this.types.get(typeID);
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param type
	 * @return
	 */
	public boolean existsType(Type type) {
		return types.containsKey(type.getTypeID());
	}

	public int size() {
		return types.size();
	}

	/**
	 * Returns a list of pairs of final types. Note that the abstract types are not
	 * returned, only the unique pairs of types that are representing leaf types in
	 * the taxonomy tree.
	 * 
	 * @return list of pairs of types
	 */
	private List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<>();

		List<Type> iterator = new ArrayList<>();
		for (Entry<String, Type> mapType : types.entrySet()) {
			Type type = mapType.getValue();
			if (type.isSimpleType())
				iterator.add(type);
		}

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Generating the mutual exclusion for each pair of tools from @modules
	 * (excluding abstract modules from the taxonomy) in each state
	 * of @moduleAutomaton.
	 * 
	 * @param modules
	 * @param typeAutomaton
	 * @param mappings 
	 * @return String representation of constraints
	 */
	public String typeMutualExclusion(TypeAutomaton typeAutomaton, AtomMapping mappings) {

		String constraints = "";

		for (Pair pair : getToolPairs()) {
			for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
				for (TypeState typeState : typeBlock.getTypeStates()) {
					constraints += "-" + mappings.add(pair.getFirst().getPredicate(), typeState.getStateName()) + " ";
					constraints += "-" + mappings.add(pair.getSecond().getPredicate(), typeState.getStateName()) + " 0\n";
				}
			}
		}

		return constraints;
	}

	/**
	 * Generating the mandatory usage constraints of root type @rootType in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootType
	 *            - represent the ID of the root type in the type taxonomy
	 * @param moduleAutomaton
	 *            - type automaton
	 * @return String representation of constraints
	 */
	public String typeMandatoryUsage(String rootType, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		Type type = types.get(rootType);
		for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
			for (TypeState typeState : typeBlock.getTypeStates()) {
				constraints += mappings.add(type.getPredicate(), typeState.getStateName()) + " ";
			}
		}
		constraints += "0\n";

		return constraints;
	}

}
