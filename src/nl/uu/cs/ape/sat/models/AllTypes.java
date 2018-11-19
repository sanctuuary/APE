package nl.uu.cs.ape.sat.models;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.ModuleState;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeBlock;
import nl.uu.cs.ape.sat.automaton.TypeState;

/**
 * The {@code AllTypes} class represent the set of all data types/formats that can be used in our program.
 * 
 * @author Vedran Kasalica
 *
 */
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
	 * Returns the type to which the specified key is mapped to, or {@code null} if the typeID has no mappings.
	 * 
	 * @param typeID
	 *            - the key whose associated value is to be returned
	 * @return {@link Type} to which the specified key is mapped to, or {@code null} if the typeID has no mappings
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
					constraints += "-" + mappings.add(pair.getSecond().getPredicate(), typeState.getStateName())
							+ " 0\n";
				}
			}
		}

		return constraints;
	}

	/**
	 * Generating the mandatory usage constraints of root type @rootType in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootTypeID
	 *            - represent the ID of the root type in the type taxonomy
	 * @param moduleAutomaton
	 *            - type automaton
	 * @return String representation of constraints
	 */
	public String typeMandatoryUsage(String rootTypeID, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		Type type = types.get(rootTypeID);
		for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
			for (TypeState typeState : typeBlock.getTypeStates()) {
				constraints += mappings.add(type.getPredicate(), typeState.getStateName()) + " 0\n";
			}
		}

		return constraints;
	}


	/**
	 * Generating the mandatory usage of a subtypes in case of the parent type being
	 * used, with respect to the Type Taxonomy. The rule starts from the @rootType
	 * and it's valid in each state of @typeAutomaton. @emptyType denotes the type
	 * that is being used if the state has no type.
	 * 
	 * @param rootTypeID
	 *            - represent the ID of the root type in the type taxonomy
	 * @param emptyTypeID
	 *            - represent the ID of the empty type in the type taxonomy
	 * @param typeAutomaton
	 *            - type automaton
	 * @param mappings
	 *            - mapping function
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public String typeEnforceTaxonomyStructure(String rootTypeID, String emptyTypeID, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {

		String constraints = "";
		for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
			for (TypeState typeState : typeBlock.getTypeStates()) {
				constraints += typeEnforceTaxonomyStructureForState(rootTypeID, emptyTypeID, typeAutomaton, mappings,
						typeState);
			}
		}
		return constraints;
	}

	/**
	 * Supporting recursive method for typeEnforceTaxonomyStructure.
	 */
	private String typeEnforceTaxonomyStructureForState(String rootTypeID, String emptyTypeID,
			TypeAutomaton typeAutomaton, AtomMapping mappings, TypeState typeState) {
		Type currType = types.get(rootTypeID);
		String constraints = "";
		String superType_State = mappings.add(currType.getPredicate(), typeState.getStateName()).toString();
		String currConstraint = "-" + superType_State + " ";
		List<String> subTypes_States = new ArrayList<>();
		if (!(currType.getSubTypes() == null || currType.getSubTypes().isEmpty())) {
			for (String subTypeeID : currType.getSubTypes()) {
				Type subType = types.get(subTypeeID);
				
				String subType_State = mappings.add(subType.getPredicate(), typeState.getStateName()).toString();
				currConstraint += subType_State + " ";
				subTypes_States.add(subType_State);
				
				constraints += typeEnforceTaxonomyStructureForState(subTypeeID, emptyTypeID, typeAutomaton, mappings,
						typeState);
			}
			currConstraint += "0\n";
			for(String subType_State : subTypes_States) {
				currConstraint += "-" + subType_State + " " + superType_State + " 0\n";
			}
			return currConstraint + constraints;
		} else {
			return "";
		}
	}

}
