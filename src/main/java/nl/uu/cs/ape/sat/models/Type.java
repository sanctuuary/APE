package nl.uu.cs.ape.sat.models;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.sat.models.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.models.enums.NodeType;

/**
 * 
 * The {@code Type} class represents data type/format that can be used by our
 * tools. {@code Type} can be an actual data type or an abstraction class.
 * 
 * @author Vedran Kasalica
 *
 */

public class Type extends TaxonomyPredicate {

	private final String typeName;
	private final String typeID;
	/**
	 * Set of subtypes, null in case of a simple type or an empty type
	 */
	private Set<String> subTypes;

	/**
	 * Constructor used to create a Type object.
	 * 
	 * @param typeName	- Type name
	 * @param typeID	- Type ID
	 * @param rootNode	- ID of the Taxonomy (Sub)Root node corresponding to the Type.
	 * @param nodeType	- {@link NodeType} object describing the type w.r.t. the Type Taxonomy.
	 */
	public Type(String typeName, String typeID, String rootNode, NodeType nodeType) {
		super(rootNode, nodeType);
		this.typeName = typeName;
		this.typeID = typeID;
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subTypes = new HashSet<String>();
		}
	}


	@Override
	public String getPredicateLabel() {
		return typeName;
	}

	@Override
	public String getPredicateID() {
		return typeID;
	}

	@Override
	public String getType() {
		return "type";
	}

	/**
	 * Adds a subtype to a non-simple type.
	 * 
	 * @param type - type that will be added as a subclass
	 * @return True if subtype was added, false otherwise.
	 */
	public boolean addSubType(Type type) {
		if (!(nodeType == NodeType.EMPTY)) {
			subTypes.add(type.getPredicateID());
			return true;
		} else {
			System.err.println("Cannot add subtypes to an empty type!");
			return false;
		}
	}

	/**
	 * Adds a subtype to a non-simple type, if it was not added present already.
	 * 
	 * @param typeID - ID of the type that will be added as a subclass
	 * @return True if subtype was added, false otherwise.
	 */
	public boolean addSubType(String typeID) {
		if (!(nodeType == NodeType.EMPTY)) {
			return subTypes.add(typeID);
		} else {
			System.err.println("Cannot add subtypes to an empty type!");
			return false;
		}
	}

	/**
	 * Returns the list of the types that are directly subsumed by the type.
	 * 
	 * @return List of the subtypes or null in case of a simple/leaf type or an empty type
	 */
	public Set<String> getSubTypes() {
		return subTypes;
	}


	


	@Override
	public int hashCode() {
		return this.typeID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		return this.typeID.equals(other.getPredicateID());
	}


	/**
	 * Returns true if the type is a simple/primitive/leaf type, otherwise returns false - the
	 * type is an abstract (non-leaf) type.
	 * 
	 * @return true (simple/primitive/leaf type) or false (abstract/non-leaf type)
	 */
	public boolean isSimpleType() {
		return (this.nodeType == NodeType.LEAF);
	}

	/**
	 * Returns true if the type is an instance, otherwise returns false - the
	 * type is an abstract (non-leaf) type or a regular leaf type.
	 * 
	 * @return true (instance) or false (leaf type or abstract/non-leaf type)
	 */
	public boolean isInstanceType() {
		return this.nodeType == NodeType.INSTANCE;
	}
	
	/**
	 * Returns true if the type is an empty type, otherwise returns false - the type
	 * is an actual (abstract or non-abstract) type.
	 * 
	 * @return true (empty type) or false (implemented type)
	 */
	public boolean isEmptyType() {
		return this.nodeType == NodeType.EMPTY;
	}

	/**
	 * Returns true if the type the root type, otherwise returns false - the type is
	 * not the root node of the taxonomy
	 * 
	 * @return true (root node) or false (non-root node)
	 */
	public boolean isRootType() {
		return this.nodeType == NodeType.ROOT;
	}
	
	/**
	 * Returns true if the type the sub-root type, otherwise returns false - the type is
	 * not the sub-root node of the taxonomy
	 * 
	 * @return true (sub-root node) or false (non-root node)
	 */
	public boolean isSubRootType() {
		return this.nodeType == NodeType.SUBROOT;
	}

	/**
	 * Set the type to be a simple type (LEAF type in the Type Taxonomy).
	 * 
	 */
	public void setToSimpleType() {
		this.nodeType = NodeType.LEAF;
	}
	
	/**
	 * Returns the type of the data node, based on the taxonomy.
	 * @return The node type object
	 */
	public NodeType getNodeType() {
		return this.nodeType;
	}
	
	/**
	 * Sets the type of the data node, based on the taxonomy.
	 */
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * The class is used to check weather the type with typeID was already
	 * introduced earlier on in allTypes. In case it was, it returns the item,
	 * otherwise the new element is generated and returned.
	 * <br>
	 * <br>
	 * In case of generating a new Type, the object is added to the set of all the Types and added as a subType to the parent Type.
	 * 
	 * @param typeName  - Type name
	 * @param typeID	- Unique Type identifier
	 * @param rootType	- Determines whether the Type is a simple/leaf type
	 * @param nodeType	- {@link NodeType} object describing the type w.r.t. the Type Taxonomy.
	 * @param allTypes  - Set of all the types created
	 * @param superType - The Parent (abstract) Type of the current Type
	 * @return The Type object.
	 */
	public static Type generateType(String typeName, String typeID, String rootType, NodeType nodeType,  AllTypes allTypes, Type superType) {

		Type tmpType;
		if ((tmpType = allTypes.get(typeID)) == null) {
			tmpType = new Type(typeName, typeID, rootType, nodeType);
			allTypes.addType(tmpType);
			
		}
		/*
		 * Adding class as a subtype to the superclass, even if currType was already introduced (extending taxonomy tree) 
		 */
		if(superType != null) {
			superType.addSubType(typeID);
		}
		
		return tmpType;

	}

	/**
	 * Print the tree shaped representation of the type taxonomy
	 * 
	 * @param str      - string that is helping the recursive function to
	 *                 distinguish between the tree levels
	 * @param allTypes - set of all the types
	 */
	public void printTree(String str, AllTypes allTypes) {
		System.out.println(str + print());

		if (subTypes != null)
			for (String typeID : subTypes) {
				allTypes.get(typeID).printTree(str + ". ", allTypes);
			}
	}

	private String print() {
		if (isSimpleType()) {
			return typeID + "["+getNodeType()+"]";
		} else {
			return typeID + "["+getNodeType()+"]";
		}
	}

}
