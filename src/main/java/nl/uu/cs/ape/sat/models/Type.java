package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * 
 * The {@code Type} class represents data dimension (e.g. data type, data format, etc.) that can be used by operations/tools
 * from the domain. {@code Type} can be an actual data type of a dimension or an abstraction class.
 * 
 * @author Vedran Kasalica
 *
 */

public class Type extends TaxonomyPredicate {

	private final String typeName;
	private final String typeID;

	/**
	 * Constructor used to create a Type object.
	 * 
	 * @param typeName - Type name
	 * @param typeID   - Type ID
	 * @param rootNode - ID of the Taxonomy (Sub)Root node corresponding to the
	 *                 Type.
	 * @param nodeType - {@link NodeType} object describing the type w.r.t. the Type
	 *                 Taxonomy.
	 */
	public Type(String typeName, String typeID, String rootNode, NodeType nodeType) {
		super(rootNode, nodeType);
		this.typeName = typeName;
		this.typeID = typeID;
	}

	public String getPredicateLabel() {
		return typeName;
	}

	public String getPredicateID() {
		return typeID;
	}

	@Override
	public String getType() {
		return "type";
	}

	@Override
	public int hashCode() {
		return this.typeID.hashCode() * 11;
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

}
