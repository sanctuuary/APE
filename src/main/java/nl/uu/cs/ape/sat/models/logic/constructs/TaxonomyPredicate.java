package nl.uu.cs.ape.sat.models.logic.constructs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.sat.models.AllPredicates;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code PredicateLabel} class (interface) represents a single
 * predicate/label used to depict an operation or a data type/format. <br>
 * <br>
 * Note: In order to be an atom, a relation needs to be added.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class TaxonomyPredicate implements PredicateLabel, Comparable<TaxonomyPredicate> {

	/**
	 * Describes the node in from the taxonomy hierarchy. The type can represent a
	 * root type, subroot type, an abstract or a simple (implemented leaf) term, or
	 * be an empty term.
	 */
	protected NodeType nodeType;
	/**
	 * Root of the Ontology tree that this node belongs to. Used to distinguish
	 * between mutually exclusive data taxonomy subtrees (type and format).
	 */
	private String rootNode;

	/**
	 * Describes whether the node is relevant in the described scenario. In other
	 * words, the node is relevant if it is part of the active domain (tool
	 * annotations).
	 */
	private boolean isRelevant;

	/**
	 * Set of all the predicates that are subsumed by the abstract predicate (null
	 * if the predicate is a leaf).
	 */
	private Set<TaxonomyPredicate> subPredicates;
	/**
	 * Set of all the predicates that contain the current predicate (null if the
	 * predicate is a root)
	 */
	private Set<TaxonomyPredicate> superPredicates;

	/**
	 * Create a taxonomy predicate.
	 * 
	 * @param rootType - root of the OWL tree that this node belongs to
	 * @param nodeType - type of the node
	 * @param nodeType
	 */
	public TaxonomyPredicate(String rootNode, NodeType nodeType) {
		this.rootNode = rootNode;
		this.nodeType = nodeType;
		this.isRelevant = false;
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subPredicates = new HashSet<TaxonomyPredicate>();
		}
		if (nodeType != NodeType.ROOT) {
			this.superPredicates = new HashSet<TaxonomyPredicate>();
		}

	}

	/**
	 * Create a taxonomy predicate based on an existing one.
	 * 
	 * @param oldPredicate - predicate that is copied
	 * @param nodeType     - type of the node
	 * @param nodeType
	 */
	public TaxonomyPredicate(TaxonomyPredicate oldPredicate, NodeType nodeType) {
		this.rootNode = oldPredicate.rootNode;
		this.nodeType = nodeType;
		this.isRelevant = oldPredicate.isRelevant;
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subPredicates = oldPredicate.getSubPredicates();
		}
		if (nodeType != NodeType.ROOT) {
			this.superPredicates = oldPredicate.getSuperPredicates();
		}

	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	/**
	 * Get root of the Ontology tree that this node belongs to. Used to distinguish
	 * between mutually exclusive data taxonomy subtrees (type and format).
	 * 
	 * @return String ID of the root class.
	 */
	public String getRootNode() {
		return rootNode;
	}

	/**
	 * Set root of the Ontology tree that this node belongs to.
	 */
	public void setRootNode(String rootType) {
		this.rootNode = rootType;
	}

	/**
	 * Returns the type of the node, based on the taxonomy hierarchy.
	 * 
	 * @return the {@link NodeType} object that represent the type of the node (e.g.
	 *         {@link NodeType#LEAF}, {@link NodeType#ROOT}).
	 */
	public NodeType getNodeType() {
		return this.nodeType;
	}

	/**
	 * Sets the node to be relevant.
	 */
	private void setIsRelevant() {
		this.isRelevant = true;
	}

	/**
	 * Returns whether the node is relevant for the scenario (if it can be used in
	 * practice).
	 * 
	 * @return {@code true} if the node can occur in our solution (as a type or
	 *         module), {@code false} otherwise.
	 */
	public boolean getIsRelevant() {
		return isRelevant;
	}

	/**
	 * Transform the main 2 characteristics of the term into a map.
	 * 
	 * @return
	 */
	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", this.getPredicateID());
		map.put("label", this.getPredicateLabel());
		return map;
	}

	/**
	 * Set the current predicate as a relevant part of the taxonomy and all the
	 * corresponding subClasses and superClasses.
	 * 
	 * @param allPredicates - Map of all the predicates of the given type.
	 * @return {@code true} if the predicates were successfully set to be relevant.
	 */
	public boolean setAsRelevantTaxonomyTerm(AllPredicates allPredicates) {
		if (this.isRelevant) {
			return true;
		}
		boolean succExe = true;
		if (allPredicates == null) {
			return false;
		}
		this.setIsRelevant();
		for (TaxonomyPredicate superPredicate : APEUtils.safe(this.superPredicates)) {
			succExe = succExe && superPredicate.setAsRelevantTaxonomyTermBottomUp(allPredicates);
		}
		for (TaxonomyPredicate subPredicate : APEUtils.safe(this.subPredicates)) {
			succExe = succExe && subPredicate.setAsRelevantTaxonomyTermTopDown(allPredicates);
		}
		return succExe;
	}

	/**
	 * Set the current predicate as a relevant part of the taxonomy and all the
	 * corresponding subClasses.
	 * 
	 * @param allPredicates - Map of all the predicates of the given type.
	 * @return {@code true} if the predicates were successfully set to be relevant.
	 */
	private boolean setAsRelevantTaxonomyTermTopDown(AllPredicates allPredicates) {
		if (this.isRelevant) {
			return true;
		}
		boolean succExe = true;
		if (allPredicates == null) {
			return false;
		}
		this.setIsRelevant();
		for (TaxonomyPredicate subPredicate : APEUtils.safe(this.subPredicates)) {
			succExe = succExe && subPredicate.setAsRelevantTaxonomyTermTopDown(allPredicates);
		}
		return succExe;
	}

	/**
	 * Set the current predicate as a relevant part of the taxonomy and all the
	 * corresponding superClasses.
	 * 
	 * @param allPredicates - Map of all the predicates of the given type.
	 * @return {@code true} if the predicates were successfully set to be relevant.
	 */
	private boolean setAsRelevantTaxonomyTermBottomUp(AllPredicates allPredicates) {
		if (this.isRelevant) {
			return true;
		}
		boolean succExe = true;
		if (allPredicates == null) {
			return false;
		}
		this.setIsRelevant();
		for (TaxonomyPredicate superPredicate : APEUtils.safe(this.superPredicates)) {
			succExe = succExe && superPredicate.setAsRelevantTaxonomyTermBottomUp(allPredicates);
		}
		return succExe;
	}

	/**
	 * Function is used to return the predicate identifier defined as String.
	 * 
	 * @return String representation of the predicate, used to uniquely identify the
	 *         predicate.
	 */
	public abstract String getPredicateID();

	/**
	 * Function is used to return the label that describes the predicate.
	 * 
	 * @return String representation of the predicate label, used for presentation
	 *         in case when the predicate id is too complex/long.
	 */
	public abstract String getPredicateLabel();

	/**
	 * The function is used to determine the type of the predicate
	 * [<b>type</b>,<b>module</b> or <b>abstract module</b>].
	 * 
	 * @return String [<b>type</b>,<b>module</b> or <b>abstract module</b>]
	 */
	public abstract String getType();

	/**
	 * Return a printable String version of the predicate.
	 * 
	 * @return predicate as printable String.
	 */
	public String toString() {

		return "|ID: " + getPredicateID() + ", Label:" + getPredicateLabel() + "|";
	}

	/**
	 * Print the ID of the current predicate.
	 * 
	 * @return PredicateLabel ID as a {@link String}
	 */
	public String toShortString() {
		return getPredicateLabel();
	}

	/**
	 * Print the tree shaped representation of the corresponding taxonomy.
	 * 
	 * @param str           - string that is helping the recursive function to
	 *                      distinguish between the tree levels
	 * @param allPredicates - set of all the predicates
	 */
	public void printTree(String str, AllPredicates allPredicates) {
		System.out.println(str + toShortString() + "[" + getNodeType() + "]");
		for (TaxonomyPredicate predicate : APEUtils.safe(this.subPredicates)) {
			predicate.printTree(str + ". ", allPredicates);
		}
	}

	/**
	 * Adds a sub-predicate to the current one, if they are not defined already.
	 * 
	 * @param predicate - predicate that will be added as a subclass
	 * @return True if sub-predicate was added, false otherwise.
	 */
	public boolean addSubPredicate(TaxonomyPredicate predicate) {
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			subPredicates.add(predicate);
			return true;
		} else {
			System.err.println("Cannot add subpredicate to a leaf or empty taxonomy term: " + getPredicateID() + ".");
			return false;
		}
	}

	/**
	 * Returns the list of the predicates that are directly subsumed by the current
	 * predicate.
	 * 
	 * @return List of the sub-predicates or null in case of a leaf predicate
	 */
	public Set<TaxonomyPredicate> getSubPredicates() {
		return this.subPredicates;
	}

	public boolean removeSubPredicate(TaxonomyPredicate subPredicateToRemove) {
		return this.subPredicates.remove(subPredicateToRemove);
	}

	public boolean removeAllSubPredicates(Collection<TaxonomyPredicate> subPredicatesToRemove) {
		boolean done = false;
		if (subPredicatesToRemove != null && !subPredicatesToRemove.isEmpty()) {
			done = done || this.subPredicates.removeAll(subPredicatesToRemove);
		}
		return done;
	}

	/**
	 * Adds a super-predicate to the current one, if it was not added present
	 * already.
	 * 
	 * @param predicate - predicate that will be added as a superclass
	 * @return {@code true} if super-predicate was added, false otherwise.
	 */
	public boolean addSuperPredicate(TaxonomyPredicate predicate) {
		if (predicate == null) {
			return false;
		}
		if (nodeType != NodeType.ROOT) {
			superPredicates.add(predicate);
			return true;
		} else {
			System.err.println("Cannot add super-predicate to a root taxonomy term!");
			return false;
		}
	}

//	/**
//	 * Adds a super-predicate to the current one, if it was not added present
//	 * already.
//	 * 
//	 * @param predicateID - ID of the predicate that will be added as a superclass
//	 *@return {@code true} if super-predicate was added, false otherwise.
//	 */
//	public boolean addSuperPredicate(String predicateID) {
//		if (nodeType != NodeType.ROOT) {
//			return superPredicates.add(predicateID);
//		} else {
//			System.err.println("Cannot add super-predicate to a root taxonomy term!");
//			return false;
//		}
//	}

	/**
	 * Returns the list of the predicates that contain the current predicate.
	 * 
	 * @return List of the super-predicates or null in case of a leaf predicate
	 */
	public Set<TaxonomyPredicate> getSuperPredicates() {
		return superPredicates;
	}

	/**
	 * Returns true if the type is a simple/leaf type, otherwise returns false - the
	 * type is an abstract (non-leaf) type.
	 * 
	 * @return true (simple/primitive/leaf type) or false (abstract/non-leaf type)
	 */
	public boolean isSimplePredicate() {
		return (this.nodeType == NodeType.LEAF);
	}

	/**
	 * Returns true if the type is an instance, otherwise returns false - the type
	 * is an abstract (non-leaf) type or a regular leaf type.
	 * 
	 * @return true (instance) or false (leaf type or abstract/non-leaf type)
	 */
	public boolean isInstancePredicate() {
		return this.nodeType == NodeType.INSTANCE;
	}

	/**
	 * Returns true if the type is an empty type, otherwise returns false - the type
	 * is an actual (abstract or non-abstract) type.
	 * 
	 * @return true (empty type) or false (implemented type)
	 */
	public boolean isEmptyPredicate() {
		return this.nodeType == NodeType.EMPTY;
	}

	/**
	 * Returns true if the type the root type, otherwise returns false - the type is
	 * not the root node of the taxonomy
	 * 
	 * @return true (root node) or false (non-root node)
	 */
	public boolean isRootPredicate() {
		return this.nodeType == NodeType.ROOT;
	}

	/**
	 * Returns true if the type the sub-root type, otherwise returns false - the
	 * type is not the sub-root node of the taxonomy
	 * 
	 * @return true (sub-root node) or false (non-root node)
	 */
	public boolean isSubRootPredicate() {
		return this.nodeType == NodeType.SUBROOT;
	}

	/**
	 * Set the type to be a simple type (LEAF type in the Taxonomy).
	 * 
	 */
	public void setToSimplePredicate() {
		this.nodeType = NodeType.LEAF;
	}

	/**
	 * Returns the type of the data node, based on the taxonomy.
	 * 
	 * @return The node type object
	 */
	public NodeType getNodePredicate() {
		return this.nodeType;
	}

	/**
	 * Sets the type of the data node, based on the taxonomy.
	 */
	public void setNodePredicate(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public int compareTo(TaxonomyPredicate otherPredicate) {
		return this.getPredicateID().compareTo(otherPredicate.getPredicateID());
	}

}
