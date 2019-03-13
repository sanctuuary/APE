package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeBlock;
import nl.uu.cs.ape.sat.automaton.TypeState;

/**
 * The {@code AllTypes} class represent the set of all data types/formats that
 * can be used in our program.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllTypes {

	private Map<String, Type> types;

	/**
	 * {@link Type} object representing the "empty type".
	 */
	private Type emptyType;

	public AllTypes() {

		this.types = new HashMap<>();
		emptyType = new Type("empty", "empty", APEConfig.getConfig().getTYPE_TAXONOMY_ROOT(), NodeType.EMPTY);
		addType(emptyType);
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
	 * @param type - the element that needs to be added
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
	 * Returns the type to which the specified key is mapped to, or {@code null} if
	 * the typeID has no mappings.
	 * 
	 * @param typeID - the key whose associated value is to be returned
	 * @return {@link Type} to which the specified key is mapped to, or {@code null}
	 *         if the typeID has no mappings
	 */
	public Type get(String typeID) {
		return this.types.get(typeID);
	}

	/**
	 * Returns the root type of the taxonomy.
	 * 
	 * @return The root type.
	 */
	public Type getRootType() {
		return this.types.get(APEConfig.getConfig().getTYPE_TAXONOMY_ROOT());
	}

	/**
	 * Returns the type representation of the empty type.
	 * 
	 * @return The empty type.
	 */
	public Type getEmptyType() {
		return this.emptyType;
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param type
	 * @return {@code true} if the type exists in the set.
	 */
	public boolean existsType(Type type) {
		return types.containsKey(type.getTypeID());
	}

	public int size() {
		return types.size();
	}

	/**
	 * Returns a list of pairs of simple types. Note that the abstract types are not
	 * returned, only the unique pairs of types that are representing leaf types in
	 * the taxonomy tree.
	 * 
	 * @return list of pairs of types
	 */
	private List<Pair> getTypePairs() {
		List<Pair> pairs = new ArrayList<>();

		List<Type> iterator = new ArrayList<>();
		for (Entry<String, Type> mapType : types.entrySet()) {
			Type type = mapType.getValue();
			if (type.isSimpleType() || type.isEmptyType()) {
				iterator.add(type);
			}
		}

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Returns a list of pairs of simple types, pairing the types based on the
	 * taxonomy subtree they belong to. Note that the abstract types are not
	 * returned, only the unique pairs of types that are representing leaf types in
	 * the same taxonomy sub tree, including the empty type (e.g. DataTypeTaxonomy
	 * or DataFormatTaxonomt tree)
	 * 
	 * @return list of pairs of types
	 */
	private List<Pair> getTypePairsForEachSubTaxonomy() {
		List<Pair> pairs = new ArrayList<>();
		List<String> subRoots = APEConfig.getConfig().getData_Taxonomy_SubRoots();

/*
 * 		Create a list for each subtree of the Data Taxonomy (e.g. TypeSubTaxonomy, FormatSubTaxonomy). Each of these lists represents a class of mutually exclusive types.
 */
		Map<String, List<Type>> subTreesMap = new HashMap<String, List<Type>>();
		subTreesMap.put(APEConfig.getConfig().getTYPE_TAXONOMY_ROOT(),  new ArrayList<Type>());
		for (String subRoot : subRoots) {
			subTreesMap.put(subRoot, new ArrayList<Type>());
		}

		for (Entry<String, Type> mapType : types.entrySet()) {
			Type type = mapType.getValue();
			if (type.isSimpleType()) {
				List<Type> currSubTree = subTreesMap.get(type.getRootNode());
				if(currSubTree!=null) {
					currSubTree.add(type);
				}
			} else if (type.isEmptyType()) {
				/*
				 * Add empty type to each mutual exclusive class
				 */
				for (List<Type> currSubTree : subTreesMap.values()) {
					currSubTree.add(type);
				}
			}
		}

		System.out.println(APEConfig.getConfig().getTYPE_TAXONOMY_ROOT() + ": " + subTreesMap.get(APEConfig.getConfig().getTYPE_TAXONOMY_ROOT()).size());
		for (String subRoot : subRoots) {
			System.out.println(subRoot + ": " + subTreesMap.get(subRoot).size());
		}
		for (List<Type> iterator : subTreesMap.values()) {
			for (int i = 0; i < iterator.size() - 1; i++) {
				for (int j = i + 1; j < iterator.size(); j++) {
					pairs.add(new Pair(iterator.get(i), iterator.get(j)));
				}
			}
		}

		return pairs;
	}

	/**
	 * Returns a list of pairs of final types. Note that the abstract types are not
	 * returned, only the unique pairs of types that are representing leaf types in
	 * the taxonomy tree.
	 * 
	 * @return list of pairs of types
	 */
	private List<Type> getAllNonEmptyTypes() {

		List<Type> allNonEmptyTypes = new ArrayList<>();
		for (Entry<String, Type> mapType : types.entrySet()) {
			Type type = mapType.getValue();
			if (!(type.isEmptyType() || type.isRootType())) {
//				System.out.println("Type: " + type.getPredicate() + ", is root:" + type.isRootType() + ", is empty:" + type.isEmptyType());
				allNonEmptyTypes.add(type);
			}
		}

		return allNonEmptyTypes;
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

		for (Pair pair : getTypePairsForEachSubTaxonomy()) {
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
	 * @param rootTypeID      - represent the ID of the root type in the type
	 *                        taxonomy
	 * @param moduleAutomaton - type automaton
	 * @return String representation of constraints
	 */
	public String typeMandatoryUsage(Type type, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

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
	 * @param rootTypeID    - represent the ID of the root type in the type taxonomy
	 * @param emptyTypeID   - represent the ID of the empty type in the type
	 *                      taxonomy
	 * @param typeAutomaton - type automaton
	 * @param mappings      - mapping function
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public String typeEnforceTaxonomyStructure(String rootTypeID, TypeAutomaton typeAutomaton, AtomMapping mappings) {

		String constraints = "";
		for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
			for (TypeState typeState : typeBlock.getTypeStates()) {
				constraints += typeEnforceTaxonomyStructureForState(rootTypeID, typeAutomaton, mappings, typeState);
			}
		}
		return constraints;
	}

	/**
	 * Supporting recursive method for typeEnforceTaxonomyStructure.
	 */
	private String typeEnforceTaxonomyStructureForState(String rootTypeID, TypeAutomaton typeAutomaton,
			AtomMapping mappings, TypeState typeState) {
		Type currType = types.get(rootTypeID);
		String constraints = "";
		String superType_State = mappings.add(currType.getPredicate(), typeState.getStateName()).toString();
		String currConstraint = "-" + superType_State + " ";
		List<String> subTypes_States = new ArrayList<>();
		if (!(currType.getSubTypes() == null || currType.getSubTypes().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (String subTypeeID : currType.getSubTypes()) {
				Type subType = types.get(subTypeeID);

				String subType_State = mappings.add(subType.getPredicate(), typeState.getStateName()).toString();
				currConstraint += subType_State + " ";
				subTypes_States.add(subType_State);

				constraints += typeEnforceTaxonomyStructureForState(subTypeeID, typeAutomaton, mappings, typeState);
			}
			currConstraint += "0\n";
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (String subType_State : subTypes_States) {
				currConstraint += "-" + subType_State + " " + superType_State + " 0\n";
			}
			return currConstraint + constraints;
		} else {
			return "";
		}
	}

}
