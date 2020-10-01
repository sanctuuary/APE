package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.constraints.ConstraintTemplateParameter;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The {@code DataInstance} class represents a data instance characterized by one or more data dimensions. Object of this class correspond
 * to a single data instance (e.g. to a single input or output). Usually the type is defined by one (e.g. data type) or
 * two (e.g. data type and format) dimensions. However, in some domains more dimensions are useful.<br>
 * e.g: {@link DataInstance} is described with a pair: {@code <Map_type, PDF_format>}
 *
 * @author Vedran Kasalica
 */
public class DataInstance extends TaxonomyPredicate {

    /**
     * List of data types that describe different data dimensions and correspond to a single data instance.
     */
    private List<TaxonomyPredicate> types;

    /**
     * Create a new data instance. The instance will be characterized by different data type dimensions.
     */
    public DataInstance(TaxonomyPredicate type) {
    	super(type.getRootNodeID(), NodeType.LEAF);
        this.types = new ArrayList<TaxonomyPredicate>();
    }

    /**
     * Add a new data dimension to characterize the data instance.
     *
     * @param type Data type that characterizes the data instance.
     */
    public void addType(TaxonomyPredicate type) {
        if (type == null) {
            System.err.println("Cannot add null as data instance!");
        } else {
            types.add(type);
        }
    }

    /**
     * Get a list of types/formats that correspond to the specific data instance.
     *
     * @return List of {@link TaxonomyPredicate}.
     */
    public List<TaxonomyPredicate> getTypes() {
        return types;
    }
    
    /**
	 * Generate a taxonomy instance (tool or type) that is defined based on one or
	 * more dimensions that describe it.
	 * 
	 * @param jsonParam
	 * @param domainSetup
	 * @return
	 */
	public DataInstance taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup)
			throws JSONException {
		ConstraintTemplateParameter parameter = null;
		/* Iterate through each of the dimensions */
		for (String currRootLabel : jsonParam.keySet()) {
			String curRootURI = APEUtils.createClassURI(currRootLabel, domainSetup.getOntologyPrefixURI());

			LogicOperation logConn = LogicOperation.OR;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<TaxonomyPredicate>();
			/* for each dimensions a disjoint array of types/tools is given */
			for (String currTypeLabel : APEUtils.getListFromJson(jsonParam, currRootLabel, String.class)) {
				String currTypeURI = APEUtils.createClassURI(currTypeLabel, domainSetup.getOntologyPrefixURI());

				if (domainSetup.getAllTypes().get(currTypeURI) == null) {
					System.err.println("Data type \"" + currTypeURI.toString()
							+ "\" used in the tool annotations does not exist in the " + currRootLabel
							+ " taxonomy. This might influence the validity of the solutions.");
				}
				Type currType = domainSetup.getAllTypes().get(currTypeURI, curRootURI);
				if (currType != null) {
					/*
					 * if the type exists, make it relevant from the taxonomy perspective and add it
					 * to the outputs
					 */
					currType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
					logConnectedPredicates.add(currType);
				} else {
					throw new JSONException("Error in the tool annotation file. The data type '" + currTypeURI
							+ "', used as a operation input/output, was not defined or does not belong to the dimension '"
							+ currRootLabel + "'.");
				}
			}

			/*
			 * Create a new type, that represents a disjunction/ of the types, that can be
			 * used to abstract over each of the tools individually.
			 */
			TaxonomyPredicate newAbsType = domainSetup.generateAuxiliaryPredicate(logConnectedPredicates, logConn);
			if (newAbsType != null) {
				newAbsType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
//	                  dataInstance.addType(newAbsType);
			}

		}

		return null;
	}
}
