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
 * The {@code Type} class represents data dimension (e.g. data type, data format, etc.) that can be used by operations/tools
 * from the domain. {@code Type} can be an actual data type of a dimension or an abstraction class.
 *
 * @author Vedran Kasalica
 */
public class Type extends TaxonomyPredicate {

    private final String typeName;
    private final String typeID;

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

	/**
	 * Generate a taxonomy instance (tool or type) that is defined based on one or
	 * more dimensions that describe it.
	 * 
	 * @param jsonParam
	 * @param domainSetup
	 * @return
	 */
	public static Type taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup)
			throws JSONException, APEDimensionsException {
		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<TaxonomyPredicate>();
		/* Iterate through each of the dimensions */
		for (String currRootLabel : jsonParam.keySet()) {
			String curRootURI = APEUtils.createClassURI(currRootLabel, domainSetup.getOntologyPrefixURI());
			if(!domainSetup.getAllTypes().existsRoot(curRootURI)) {
				throw APEDimensionsException.notExistingDimension("Data type was defined over a non existing data dimension: '" + curRootURI + "', in JSON: '" + jsonParam + "'");
			}
			LogicOperation logConn = LogicOperation.OR;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<TaxonomyPredicate>();
			/* for each dimensions a disjoint array of types/tools is given */
			for (String currTypeLabel : APEUtils.getListFromJson(jsonParam, currRootLabel, String.class)) {
				String currTypeURI = APEUtils.createClassURI(currTypeLabel, domainSetup.getOntologyPrefixURI());
				
				Type currType = domainSetup.getAllTypes().get(currTypeURI, curRootURI);
				if (currType != null) {
					/*
					 * if the type exists, make it relevant from the taxonomy perspective and add it
					 * to the outputs
					 */
					currType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
					logConnectedPredicates.add(currType);
				} else {
					throw APEDimensionsException.dimensionDoesNotContainClass(String.format("Error in a JSON input. The data type '%s' was not defined or does not belong to the data dimension '%s'.", currTypeURI, curRootURI));
				}
			}

			/*
			 * Create a new type, that represents a disjunction of the types, that can be
			 * used to abstract over each of the types individually and represents specificaion over one dimension.
			 */
			Type abstractDimensionType = AuxTypePredicate.generateAuxiliaryPredicate(logConnectedPredicates, logConn, domainSetup);
			if (abstractDimensionType != null) {
	            parameterDimensions.add(abstractDimensionType);
			}

		}
		Type taxonomyInstance = AuxTypePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND, domainSetup);

		return taxonomyInstance;
	}

}
