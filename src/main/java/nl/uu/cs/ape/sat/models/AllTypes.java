package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.configuration.APECoreConfig;
import nl.uu.cs.ape.sat.utils.APEDimensionsException;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code AllTypes} class represent the set of all the data dimensions that can be used in our program. The data 
 * can be grouped in multiple data dimensions (e.g. types,formats, etc.) and each {@link Type} will belong to one of those dimensions.
 *
 * @author Vedran Kasalica
 */
public class AllTypes extends AllPredicates {

	 private static String empty = "empty";
	 private static String apeLabel = "APE_label";
	 private static String emptyLabel = "emptyLabel";
	
    /**
     * {@link Type} object representing the "empty type".
     */
    private Type emptyType;

    /**
<<<<<<< HEAD
=======
     * {@link Type} object representing the "empty label root".
     */
    private Type apeLabelRoot;
    
    /**
     * {@link Type} object representing the "empty label type".
     */
    private Type emptyLabelType;
   
    /**
>>>>>>> master
     * Instantiates a new AllTypes object.
     *
     * @param config the config
     */
    public AllTypes(APECoreConfig config) {
        super(Stream.concat(config.getDataDimensionRoots().stream(), Stream.of(apeLabel))
                             .collect(Collectors.toList()));
        emptyType = new Type(empty, empty, empty, NodeType.EMPTY);
        apeLabelRoot = new Type(apeLabel, apeLabel, apeLabel, NodeType.ROOT);
        emptyLabelType = new Type(emptyLabel, emptyLabel, apeLabel, NodeType.EMPTY_LABEL);
        setRelevant(emptyType);
        setRelevant(apeLabelRoot);
        setRelevant(emptyLabelType);
        apeLabelRoot.addSubPredicate(emptyLabelType);
        emptyLabelType.addSuperPredicate(apeLabelRoot);
    }
    
    /**
     * Instantiates a new AllTypes object.
     *
     * @param typeTaxonomyRoots the list of data dimension roots
     */
    public AllTypes(List<String> typeTaxonomyRoots) {
    	super(Stream.concat(typeTaxonomyRoots.stream(), Stream.of(apeLabel))
                .collect(Collectors.toList()));
    	 emptyType = new Type(empty, empty, empty, NodeType.EMPTY);
         Type apeLabelRoot = new Type(apeLabel, apeLabel, apeLabel, NodeType.ROOT);
         emptyLabelType = new Type(emptyLabel, emptyLabel, apeLabel, NodeType.EMPTY_LABEL);
         setRelevant(emptyType);
         setRelevant(apeLabelRoot);
         setRelevant(emptyLabelType);
         apeLabelRoot.addSubPredicate(emptyLabelType);
         emptyLabelType.addSuperPredicate(apeLabelRoot);
    }
    
    /**
     * Helper method that sets the type to be relevant in the current domain
     * @param type - Type that should be relevant
     */
    private void setRelevant(Type type) {
    	type.setAsRelevantTaxonomyTerm(this);
        getMappedPredicates().put(type.getPredicateID(), type);
		
	}

    /**
     * Returns the set of {@link Type}s that are currently defined.
     *
     * @return The {@link Collection} of {@link Type}s.
     */
    public Collection<? extends TaxonomyPredicate> getTypes() {
        Collection<? extends TaxonomyPredicate> types = getMappedPredicates().values();
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
     * @param type The element that needs to be added.
     * @return The same element if it's a new one or the existing element if this set contains the specified element..
     */
    private Type put(Type type) {
        Type tmpType;
        if ((tmpType = get(type.getPredicateID(), type.getRootNodeID())) != null) {
            return tmpType;
        } else {
            getMappedPredicates().put(type.getPredicateID(), type);
            return type;
        }
    }

    /**
     * The method is used to check weather the type with typeID was already
     * introduced earlier on in {@code allTypes}. In case it was, it returns the item,
     * otherwise the new element is generated and returned.
     * <p>
     * In case of generating a new Type, the object is added to the set of all the
     * DataInstance and added as a subType to the parent Type.
     *
     * @param newType The {@link TaxonomyPredicate} to be added.
     * @return The Type object.
     * @throws ExceptionInInitializerError Exception in case of a type mismatch.
     */
    public Type addPredicate(TaxonomyPredicate newType) throws ExceptionInInitializerError {
        Type tmpType;
        if ((tmpType = get(newType.getPredicateID(), newType.getRootNodeID())) == null) {
            if (newType instanceof Type) {
                this.put((Type) newType);
                tmpType = (Type) newType;
            } else {
                throw new ExceptionInInitializerError(String.format("Type error. Only 'Type' PredicateLabel can be added to the set of all types. '%s' is not a type", newType.getPredicateID()));
            }
        }
        return tmpType;

    }

    /*
     * Returns the type to which the specified key is mapped to, or null if
     * the typeID has no mappings.
     *
     * @param typeID - the key whose associated value is to be returned
     * @return The {@link Type} to which the specified key is mapped to, or null
     *         if the typeID has no mappings

    public Type get(String typeID) {
    return (Type) getPredicates().get(typeID);
    }*/

    /**
     * Returns the type to which the specified key is mapped to under the given dimension, or null if
     * the typeID has no mappings or does not belong to the given dimension.
     *
     * @param predicateID      The key whose associated value is to be returned.
     * @param dimensionID The ID of the dimension to which the type belongs to.
     * @return {@link Type} to which the specified key is mapped to, or null if the typeID has no mappings or does not belong to the given dimension.
     */
    public Type get(String predicateID, String dimensionID) throws APEDimensionsException {
    	Type predicate = (Type) get(predicateID);
    	if(predicate != null && predicate.getRootNodeID().equals(dimensionID)) {
    		return predicate;
    	} else {
    		return null;
    	}
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
     * Returns true if this set contains the specified type element. More formally,
     * returns true if and only if this set contains an element e such that {@code (o==null ? e==null : o.equals(e))}.
     *
     * @param type Type that is searched for.
     * @return true if the type exists in the set.
     */
    public boolean existsType(Type type) {
        return getMappedPredicates().containsKey(type.getPredicateID());
    }

    /**
     * Returns true if this type exists in the current domain.
     *
     * @param typeID ID of the type that is searched for.
     * @return true if the typeID exists in the domain.
     */
    public boolean existsType(String typeID) {
        return getMappedPredicates().containsKey(typeID);
    }
    
    /**
     * Returns true if this dimension exists in the current domain.
     *
     * @param dimensionID ID of the data dimension that is searched for.
     * @return true if the dimensionID exists in the domain.
     */
    public boolean existsRoot(String dimensionID) {
    	return getAllRootIDs().contains(dimensionID);
    }

    /**
     * Returns number of types currently defined.
     *
     * @return Number of types.
     */
    public int size() {
        return getMappedPredicates().size();
    }

    public Class<?> getPredicateClass() {
        return Type.class;
    }

    /**
     * Returns a list of pairs of simple types, pairing the types based on the
     * taxonomy subtree they belong to. Note that the abstract types are not
     * returned, only the unique pairs of types that are representing leaf types in
     * the same taxonomy sub tree, including the empty type (e.g. DataTypeTaxonomy
     * or DataFormatTaxonomy tree)
     *
     * @return List of pairs of types.
     */
    public List<Pair<PredicateLabel>> getTypePairsForEachSubTaxonomy() {
        List<Pair<PredicateLabel>> pairs = new ArrayList<Pair<PredicateLabel>>();

        /*
         * Create a list for each subtree of the Data Taxonomy (e.g. TypeSubTaxonomy,
         * FormatSubTaxonomy). Each of these lists represents a class of mutually
         * exclusive types.
         */
        Map<String, List<TaxonomyPredicate>> subTreesMap = new HashMap<String, List<TaxonomyPredicate>>();
        // Add each of the dimension roots (type and format taxonomy) to the list
        for (String subRoot : APEUtils.safe(getAllRootIDs())) {
            subTreesMap.put(subRoot, new ArrayList<TaxonomyPredicate>());
        }

        /*
         * Allocate each simple type to the corresponding subtree, according to the
         * field Type.rootNode
         */
        for (TaxonomyPredicate type : getMappedPredicates().values()) {
            if (type.isSimplePredicate()) {
                // If the root type for the curr type exists in our list, add the type to it
                if (subTreesMap.get(type.getRootNodeID()) != null) {
                    subTreesMap.get(type.getRootNodeID()).add(type);
                } else {
                    System.err.println("ERROR!!");
                }
            } else if (type.isEmptyPredicate()) {

                /*
                 * Add empty type to each mutual exclusive class
                 */
                for (List<TaxonomyPredicate> currSubTree : subTreesMap.values()) {
                    currSubTree.add(type);
                }
            }
        }

        for (List<TaxonomyPredicate> iterator : subTreesMap.values()) {
            for (int i = 0; i < iterator.size() - 1; i++) {
                for (int j = i + 1; j < iterator.size(); j++) {
                    pairs.add(new Pair<PredicateLabel>(iterator.get(i), iterator.get(j)));
                }
            }
        }

        return pairs;
    }

    /**
     * Return the list of dimensions that represent the data. Each dimension represents
     * a node in the data taxonomy and the root for the corresponding dimension (this excludes the "APE label" dimension).
     *
     * @return List of abstract types that represent dimensions.
     */
    public List<String> getDataTaxonomyDimensionIDs() {
    	List<String> taxonomyRoot = new ArrayList<String>();
    	for(String dimension : getAllRootIDs()) {
    		if(!dimension.equals(getLabelRootID())) {
    			taxonomyRoot.add(dimension);
    		}
    	}
        return taxonomyRoot;
    }

    /**
     * Return the list of dimensions that represent the data. Each dimension represents
     * a node in the data taxonomy and the root for the corresponding dimension (this excludes the "APE label" dimension).
     *
     * @return List of abstract types that represent dimensions.
     */
    public List<TaxonomyPredicate> getDataTaxonomyDimensions() {
        List<TaxonomyPredicate> dimensionTypes = new ArrayList<TaxonomyPredicate>();
        this.getDataTaxonomyDimensionIDs().stream().filter(dimensionID -> get(dimensionID) != null)
                .forEach(dimensionID -> dimensionTypes.add(get(dimensionID)));
        return dimensionTypes;
    }

    /**
     * Return the SortedSet of dimensions that represent the data. Each dimension represents
     * a node in the data taxonomy and the root for the corresponding dimension (this excludes the "APE label" dimension).
     *
     * @return SortedSet of abstract types that represent dimensions.
     */
    public SortedSet<TaxonomyPredicate> getDataTaxonomyDimensionsAsSortedSet() {
        SortedSet<TaxonomyPredicate> dimensionTypes = new TreeSet<TaxonomyPredicate>();
        this.getDataTaxonomyDimensionIDs().stream().filter(dimensionID -> get(dimensionID) != null)
                .forEach(dimensionID -> dimensionTypes.add(get(dimensionID)));
        return dimensionTypes;
    }

	/**
	 * Return ID of the taxonomy root containing APE type labels (variables).
	 * @return String representing the root ID
	 */
    public String getLabelRootID() {
		return apeLabel;
	}
    
	/**
	 * Return the taxonomy root containing APE type labels (variables).
	 * @return Type representing the root 
	 */
    public Type getLabelRoot() {
		return apeLabelRoot;
	}
    
    /**
	 * Return the empty APE type label. It corresponds to the type not beeing labeled.
	 * @return Type that represents empty label
	 */
    public Type getEmptyAPELabel() {
		return emptyLabelType;
	}
    
}
