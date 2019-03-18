package nl.uu.cs.ape.sat.models.constructs;

import nl.uu.cs.ape.sat.models.NodeType;

/**
 * The {@code Predicate} class (interface) represents a single predicate/label used to depict a module or a data type/format. <br> <br>
 * Note: In order to be an atom, a relation needs to be added.

 * @author Vedran Kasalica
 *
 */
public abstract class Predicate {
	
	/**
	 * Describes the data node in from the taxonomy. The type can represent a root type, subroot type, an abstract or a simple (implemented leaf) type, or be an empty type.
	 */
	protected NodeType nodeType;
	/**
	 * Root of the Ontology tree that this node belongs to. Used to distinguish between mutually exclusive data taxonomy subtrees (type and format).
	 */
	private String rootNode;

	/**
	 * Setup the taxonomy related information
	 * @param rootType - root of the OWL tree that this node belongs to
	 * @param nodeType - type of the node
	 */
	public Predicate(String rootNode, NodeType nodeType) {
		this.nodeType = nodeType;
		this.rootNode = rootNode;
	}
	
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
	 * Returns the type of the module node, based on the taxonomy.
	 * The node type object
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
	 * Function is used to return the predicate defined as String.
	 * @return String representation of the predicate.
	 */
	public abstract String getPredicate();
	
	/**
	 * The function is used to determine the type of the predicate [<b>type</b>,<b>module</b> or <b>abstract module</b>].
	 * @return String [<b>type</b>,<b>module</b> or <b>abstract module</b>]
	 */
	public abstract String getType();
	
}

