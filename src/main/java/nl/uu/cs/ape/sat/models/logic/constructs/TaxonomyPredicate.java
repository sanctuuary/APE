package nl.uu.cs.ape.sat.models.logic.constructs;

import nl.uu.cs.ape.sat.models.enums.NodeType;

/**
 * The {@code Predicate} class (interface) represents a single predicate/label used to depict a module or a data type/format. <br> <br>
 * Note: In order to be an atom, a relation needs to be added.

 * @author Vedran Kasalica
 *
 */
public abstract class TaxonomyPredicate implements Predicate {
	
	/**
	 * Describes the node in from the taxonomy hierarchy. The type can represent a root type, subroot type, an abstract or a simple (implemented leaf) term, or be an empty term.
	 */
	protected NodeType nodeType;
	/**
	 * Root of the Ontology tree that this node belongs to. Used to distinguish between mutually exclusive data taxonomy subtrees (type and format).
	 */
	private String rootNode;
	
	/**
	 * Describes whether the node is relevant in the described scenario. In other words, the node is relevant if it is part of the active domain (tool annotations).
	 */
	private boolean isRelevant;
	/**
	 * Setup the taxonomy related information
	 * @param rootType - root of the OWL tree that this node belongs to
	 * @param nodeType - type of the node
	 */
	public TaxonomyPredicate(String rootNode, NodeType nodeType) {
		this.rootNode = rootNode;
		this.nodeType = nodeType;
		isRelevant = false;
	}
	
	
	
	@Override
	public abstract int hashCode();


	@Override
	public abstract boolean equals(Object obj);


	/**
	 * Get root of the Ontology tree that this node belongs to. Used to distinguish between mutually exclusive data taxonomy subtrees (type and format).
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
	 * @return the {@link NodeType} object that represent the type of the node (e.g. {@link NodeType#LEAF},  {@link NodeType#ROOT}).
	 */
	public NodeType getNodeType() {
		return this.nodeType;
	}
	
	/**
	 * Sets the type of the module node, based on the taxonomy.
	 */
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	/**
	 * Sets the node to be relevant.
	 */
	public void setIsRelevant() {
		this.isRelevant = true;
	}
	
	/**
	 * Returns whether the node is relevant for the scenario (if it can be used in practice).
	 * @return {@code true} if the node can occur in our solution (as a type or module), {@code false} otherwise.
	 */
	public boolean getIsRelevant() {
		return isRelevant;
	}
	
	/**
	 * Function is used to return the predicate identifier defined as String.
	 * @return String representation of the predicate, used to uniquely identify the predicate.
	 */
	public abstract String getPredicateID();
	
	/**
	 * Function is used to return the label that describes the predicate.
	 * @return String representation of the predicate label, used for presentation in case when the predicate id is too complex/long.
	 */
	public abstract String getPredicateLabel();
	
	/**
	 * The function is used to determine the type of the predicate [<b>type</b>,<b>module</b> or <b>abstract module</b>].
	 * @return String [<b>type</b>,<b>module</b> or <b>abstract module</b>]
	 */
	public abstract String getType();
}

