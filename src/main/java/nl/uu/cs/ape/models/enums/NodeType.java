package nl.uu.cs.ape.models.enums;

/**
 * Defines the values describing the nodes in the taxonomy trees.
 * <p>
 * Values: [{@code ROOT}, {@code SUBROOT}, {@code ABSTRACT}, {@code LEAF}, {@code INSTANCE}, {@code EMPTY}]
 * 
 *  @author Vedran Kasalica
 */
public enum NodeType {

    /**
     * Root node of the (tool or data) taxonomy.
     */
    ROOT,

    /**
     * Non-leaf node in the taxonomy. Usually represents an abstraction over the actual implementation of a tool or data type/format.
     */
    ABSTRACT,

    /**
     * Leaf in the taxonomy. Usually represent the implementation of the tool, simple data type or format, etc.
     */
    LEAF,

    /**
     * Artificially created {@link #LEAF} taxonomy element from an existing abstract class, which is disjoint with other subclasses of the abstract class.
     */
    ARTIFICIAL_LEAF,

    /**
     * Represents the empty predicate.
     */
    EMPTY,
    
    /**
     * Represents the empty label.
     */
    EMPTY_LABEL
}