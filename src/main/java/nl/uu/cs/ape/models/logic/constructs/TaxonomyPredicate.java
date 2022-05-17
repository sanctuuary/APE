package nl.uu.cs.ape.models.logic.constructs;

import java.util.*;

import org.json.JSONObject;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.AllPredicates;
import nl.uu.cs.ape.models.enums.NodeType;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code TaxonomyPredicate} class represents a single
 * class in the taxonomy as a predicate. The predicate might have parent classes (super-predicate) and child classes (sub-predicate).<br>
 * <b>Note:</b> Taxonomy predicates in combination with {@link State}s create {@link SLTLxAtom}s.
 *
 * @author Vedran Kasalica
 */
public abstract class TaxonomyPredicate implements PredicateLabel {

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
    private String rootNodeID;

    /**
     * Describes whether the node is relevant in the described scenario.
     * In other words, the node is relevant if it is part of the active
     * domain (tool annotations).
     */
    private boolean isRelevant;

    /**
     * Set of all the predicates that are subsumed by the abstract
     * predicate (null if the predicate is a leaf).
     */
    private Set<TaxonomyPredicate> subPredicates;

    /**
     * Set of all the predicates that contain the current predicate (null if the predicate is a root).
     */
    private Set<TaxonomyPredicate> superPredicates;

    /**
     * Create a taxonomy predicate.
     *
     * @param rootNode Root of the OWL tree that this node belongs to.
     * @param nodeType Type of the node.
     */
    public TaxonomyPredicate(String rootNode, NodeType nodeType) {
        this.rootNodeID = rootNode;
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
     * @param oldPredicate Predicate that is copied
     * @param nodeType     Type of the node
     */
    public TaxonomyPredicate(TaxonomyPredicate oldPredicate, NodeType nodeType) {
        this.rootNodeID = oldPredicate.rootNodeID;
        this.nodeType = nodeType;
        this.isRelevant = oldPredicate.isRelevant;
        if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
            this.subPredicates = oldPredicate.getSubPredicates();
        }
        if (nodeType != NodeType.ROOT) {
            this.superPredicates = oldPredicate.getSuperPredicates();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPredicateID() == null) ? 0 : getPredicateID().hashCode());
        result = prime * result + ((rootNodeID == null) ? 0 : rootNodeID.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaxonomyPredicate other = (TaxonomyPredicate) obj;
        if (getPredicateID() == null) {
            if (other.getPredicateID() != null)
                return false;
        } else if (!getPredicateID().equals(other.getPredicateID())) {
            return false;
        }
        if (rootNodeID == null) {
            if (other.rootNodeID != null)
                return false;
        } else if (!rootNodeID.equals(other.rootNodeID))
            return false;
        return true;
    }

    public int compareTo(PredicateLabel other) {
        if (!(other instanceof TaxonomyPredicate)) {
            return this.getPredicateID().compareTo(other.getPredicateID());
        }
        TaxonomyPredicate otherPredicate = (TaxonomyPredicate) other;
        int diff = 0;
        if ((diff = this.getRootNodeID().compareTo(otherPredicate.getRootNodeID())) != 0) {
            return diff;
        } else {
            return this.getPredicateID().compareTo(otherPredicate.getPredicateID());
        }
    }

    /**
     * Get root of the Ontology tree that this node belongs to. Used to distinguish
     * between mutually exclusive data taxonomy subtrees (type and format).
     *
     * @return String ID of the root class.
     */
    public String getRootNodeID() {
        return rootNodeID;
    }

    /**
     * Sets root node.
     *
     * @param rootType Set root of the Ontology tree that this node belongs to.
     */
    public void setRootNode(String rootType) {
        this.rootNodeID = rootType;
    }

    /**
     * Returns the type of the node, based on the taxonomy hierarchy.
     *
     * @return the {@link NodeType} object that represent the type of the node (e.g. {@link NodeType#LEAF}, {@link NodeType#ROOT}).
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
     * @return true if the node can occur in our solution (as a type or module), false otherwise.
     */
    public boolean getIsRelevant() {
        return isRelevant;
    }

    /**
     * To map map.
     *
     * @return Transform the main 2 characteristics of the term into a map.
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
     * TODO Should it be topdown??
     *
     * @param allPredicates Map of all the predicates of the given type.
     * @return true if the predicates were successfully set to be relevant.
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
     * Set the current predicate as a relevant part of the taxonomy
     * and all the corresponding subClasses.
     *
     * @param allPredicates Map of all the predicates of the given type.
     * @return true if the predicates were successfully set to be relevant.
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
     * Set the current predicate as a relevant part of the taxonomy
     * and all the corresponding superClasses.
     *
     * @param allPredicates Map of all the predicates of the given type.
     * @return true if the predicates were successfully set to be relevant.
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
     * Function is used to return the unique predicate identifier defined as String.
     *
     * @return String representation of the predicate, used to uniquely identify the predicate.
     */
    public abstract String getPredicateID();

    /**
     * Function is used to return the label that describes the predicate.
     *
     * @return String representation of the predicate label, used for presentation
     * in case when the predicate id is too complex/long.
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
     * @return StateInterface ID as a String
     */
    public String toShortString() {
        return getPredicateLabel();
    }

    /**
     * Print the tree shaped representation of the corresponding taxonomy.
     *
     * @param str           String that is helping the recursive function to distinguish between the tree levels.
     * @param allPredicates Set of all the predicates.
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
     * @param predicate Predicate that will be added as a subclass.
     * @return true if sub-predicate was added, false otherwise.
     */
    public boolean addSubPredicate(TaxonomyPredicate predicate) {
        if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
            subPredicates.add(predicate);
            return true;
        } else {
            System.err.println("Cannot add sub-predicate to a leaf or empty taxonomy term: " + getPredicateID() + ".");
            return false;
        }
    }

    /**
     * Returns the list of the predicates that are directly subsumed by the current predicate.
     *
     * @return List of the sub-predicates or null in case of a leaf predicate.
     */
    public Set<TaxonomyPredicate> getSubPredicates() {
        return this.subPredicates;
    }

    /**
     * Remove sub predicate boolean.
     *
     * @param subPredicateToRemove the sub predicate to remove
     * @return the boolean
     */
    public boolean removeSubPredicate(TaxonomyPredicate subPredicateToRemove) {
        return this.subPredicates.remove(subPredicateToRemove);
    }

    /**
     * Remove all sub predicates boolean.
     *
     * @param subPredicatesToRemove the sub predicates to remove
     * @return the boolean
     */
    public boolean removeAllSubPredicates(Collection<TaxonomyPredicate> subPredicatesToRemove) {
        boolean done = false;
        if (subPredicatesToRemove != null && !subPredicatesToRemove.isEmpty()) {
            done = done || this.subPredicates.removeAll(subPredicatesToRemove);
        }
        return done;
    }

    /**
     * Adds a super-predicate to the current one, if it was not added present already.
     *
     * @param predicate Predicate that will be added as a superclass.
     * @return true if super-predicate was added, false otherwise.
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

    /**
     * Returns the list of the predicates that contain the current predicate.
     *
     * @return List of the super-predicates or null in case of a leaf predicate.
     */
    public Set<TaxonomyPredicate> getSuperPredicates() {
        return superPredicates;
    }

    
    /**
     * Returns true if the type is a simple/leaf type, otherwise returns false - the
     * type is an abstract (non-leaf) type.
     * @param nodeType - node type
     *
     * @return true (simple/primitive/leaf type) or false (abstract/non-leaf type).
     */
    public boolean isNodeType(NodeType nodeType) {
        return this.nodeType.equals(nodeType);
    }
    
    /**
     * Returns true if the type is a simple/leaf type, otherwise returns false - the
     * type is an abstract (non-leaf) type.
     *
     * @return true (simple/primitive/leaf type) or false (abstract/non-leaf type).
     */
    public boolean isSimplePredicate() {
        return (this.nodeType == NodeType.LEAF || this.nodeType == NodeType.EMPTY_LABEL);
    }

    /**
     * Returns true if the type is an artificial predicate, otherwise returns false - it
     * is a predicate that exists in the taxonomy.
     *
     * @return true (artificial leaf) or false (taxonomy term).
     */
    public boolean isArtificialLeaf() {
        return this.nodeType == NodeType.ARTIFICIAL_LEAF;
    }

    /**
     * Returns true if the type is an empty type, otherwise returns false - the type
     * is an actual (abstract or non-abstract) type.
     *
     * @return true (empty type) or false (implemented type).
     */
    public boolean isEmptyPredicate() {
        return this.nodeType == NodeType.EMPTY;
    }
    
    /**
     * Returns true if the type is <b>not an empty type</b> , i.e., is a (abstract or non-abstract) type.), otherwise returns false - the type
     * is empty.
     *
     * @return true (empty type) or false (implemented type).
     */
    public boolean notEmptyPredicate() {
        return this.nodeType != NodeType.EMPTY;
    }

    /**
     * Returns true if the type the root type, otherwise returns false - the type is
     * not the root node of the taxonomy.
     *
     * @return true (root node) or false (non-root node).
     */
    public boolean isRootPredicate() {
        return this.nodeType == NodeType.ROOT;
    }

    /**
     * Returns the type of the data node, based on the taxonomy.
     *
     * @return The node type object.
     */
    public NodeType getNodePredicate() {
        return this.nodeType;
    }

    /**
     * Sets node predicate.
     *
     * @param nodeType sets the type of the data node, based on the taxonomy.
     */
    public void setNodePredicate(NodeType nodeType) {
        this.nodeType = nodeType;
    }

}
