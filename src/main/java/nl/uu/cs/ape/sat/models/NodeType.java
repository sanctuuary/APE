package nl.uu.cs.ape.sat.models;

/**
 * Defines the values describing the nodes in the taxonomy trees.
 * <br>
 * <br>
 * values:
 * <br>
 * {@code ROOT, SUBROOT, ABSTRACT, LEAF, EMPTY}
 */
public enum NodeType{
	
	/**
	 * Root node of the (tool or data) taxonomy.
	 */
	ROOT,
	/**
	 * Direct children of the taxonomy root. Used to create sub taxonomies (e.g type and format data taxonomies)
	 */
	SUBROOT,
	/**
	 * Non-leaf node in the taxonomy. Usually represents an abstraction over the actual implementation of a tool or data type/format. 
	 */
	ABSTRACT, 
	/**
	 * Leaf in the taxonomy. usually represent the implementation of the tool, simple data type or format, etc.
	 */
	LEAF,
	/**
	 * Represents the empty predicate.
	 */
	EMPTY,
	/**
	 * Unknown Node Type. Usually used  for the undefined types used in the tool annotation file.
	 */
	UNKNOWN
	
}