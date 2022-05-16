package nl.uu.cs.ape.sat.models;

import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDimensionsException;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code Type} class represents data dimension (e.g. data type, data
 * format, etc.) that can be used by operations/tools from the domain.
 * {@code Type} can be an actual data type of a dimension or an abstraction
 * class.
 *
 * @author Vedran Kasalica
 */
public class Type extends TaxonomyPredicate {

    private final String typeName;
    private final String typeID;
    private Type plainType;

    /**
     * Constructor used to create a Type object.
     *
     * @param typeName Type name.
     * @param typeID   Type ID.
     * @param rootNode ID of the Taxonomy (Sub)Root node corresponding to the Type.
     * @param nodeType {@link NodeType} object describing the type w.r.t. the TypeTaxonomy.
     */
    public Type(String typeName, String typeID, String rootNode, NodeType nodeType) {
        super(rootNode, nodeType);
        this.typeName = typeName;
        this.typeID = typeID;
        this.plainType = this;
    }

    public String getPredicateLabel() {
        return typeName;
    }
    
    public String getPredicateLongLabel() {
    	if(typeID.endsWith("_plain")) {
    		return APEUtils.removeNLastChar(typeID, 6);
    	} else {
    		return typeID;
    	}
    }

    public String getPredicateID() {
        return typeID;
    }

    @Override
    public String getType() {
        return "type";
    }
    
    public void setPlainType(Type plainType) {
    	this.plainType = plainType;
    }
    
    /** 
     * Method returns an artificially created plain version of the abstract class in case of a strict tool annotations, or the type itself otherwise.
     * @return The type itself or an artificially created plain version of the type when needed.
     */
    public Type getPlainType() {
    	return plainType;
    }

	/**
	 * Generate a taxonomy data instance that is defined based on one or more
	 * dimensions that describe it.
	 * 
	 * @param jsonParam    - JSON representation of the data instance
	 * @param domainSetup  - setup of the domain
	 * @param isOutputData - {@code true} if the data is used to be module output,
	 *                     {@code false} otherwise
	 * @return A type object that represent the data instance given as the
	 *         parameter.
	 * @throws JSONException          if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types are not well defined
	 */
	public static Type taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup, boolean isOutputData)
			throws JSONException, APEDimensionsException {
		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<TaxonomyPredicate>();
		boolean labelDefined = false;
		AllTypes allTypes = domainSetup.getAllTypes();
		/* Iterate through each of the dimensions */
		for (String currRootLabel : jsonParam.keySet()) {
			String curRootURI = currRootLabel;
			if(!allTypes.existsRoot(curRootURI)) {
				curRootURI = APEUtils.createClassURI(currRootLabel, domainSetup.getOntologyPrefixIRI());
			}
			if (!allTypes.existsRoot(curRootURI)) {
				throw APEDimensionsException
						.notExistingDimension("Data type was defined over a non existing data dimension: '" + curRootURI
								+ "', in JSON: '" + jsonParam + "'");
			}
			LogicOperation logConn = LogicOperation.OR;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<TaxonomyPredicate>();
			/* for each dimensions a disjoint array of types/tools is given */
			for (String currTypeLabel : APEUtils.getListFromJson(jsonParam, currRootLabel, String.class)) {
				String currTypeURI = currTypeLabel;
				if(allTypes.get(currTypeURI, curRootURI) == null) {
					currTypeURI = APEUtils.createClassURI(currTypeLabel, domainSetup.getOntologyPrefixIRI());
				}
				
				if (currRootLabel.equals(allTypes.getLabelRootID())) {
					labelDefined = true;
				}
				Type currType = allTypes.get(currTypeURI, curRootURI);
				if (currType != null) {
					if (isOutputData) {
						currType.setAsRelevantTaxonomyTerm(allTypes);
						currType = currType.getPlainType();
					}
					/*
					 * if the type exists, make it relevant from the taxonomy perspective and add it
					 * to the list of allowed types
					 */
					currType.setAsRelevantTaxonomyTerm(allTypes);
					logConnectedPredicates.add(currType);
				} else if (currRootLabel.equals(allTypes.getLabelRootID()) && isOutputData) {
					/* add a new label to the taxonomy */
					currType = allTypes.addPredicate(new Type(currTypeLabel, currTypeLabel, curRootURI, NodeType.LEAF));

					allTypes.getLabelRoot().addSubPredicate(currType);
					currType.addSuperPredicate(allTypes.getLabelRoot());

					/*
					 * make the type relevant from the taxonomy perspective and add it to the list
					 * of allowed types
					 */
					currType.setAsRelevantTaxonomyTerm(allTypes);
					logConnectedPredicates.add(currType);

				} else {
					throw APEDimensionsException.dimensionDoesNotContainClass(String.format(
							"Error in a JSON input. The data type '%s' was not defined or does not belong to the data dimension '%s'.",
							currTypeURI, curRootURI));
				}
			}

			/*
			 * Create a new type, that represents a disjunction of the types, that can be
			 * used to abstract over each of the types individually and represents
			 * specification over one dimension.
			 */
			Type abstractDimensionType = AuxTypePredicate.generateAuxiliaryPredicate(logConnectedPredicates, logConn,
					domainSetup);
			if (abstractDimensionType != null) {
				parameterDimensions.add(abstractDimensionType);
			}

		}
		/* If label was not defined it should be an empty label. */
		if (!labelDefined) {
			if (isOutputData) {
				parameterDimensions.add(allTypes.getEmptyAPELabel());
			} else {
				parameterDimensions.add(allTypes.getLabelRoot());
			}
		}
		Type taxonomyInstance = AuxTypePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND,
				domainSetup);

		return taxonomyInstance;
	}

}
