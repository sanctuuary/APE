package nl.uu.cs.ape.models;

import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.commonwl.cwlsdk.cwl1_2.CommandInputParameter;
import org.commonwl.cwlsdk.cwl1_2.CommandOutputParameter;

import nl.uu.cs.ape.domain.APEDimensionsException;
import nl.uu.cs.ape.domain.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.utils.cwl_parser.CWLParser;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.NodeType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code Type} class represents data dimension (e.g. data type, data
 * format, etc.) that can be used by operations/tools from the domain.
 * {@code Type} can be an actual data type of a dimension or an abstraction
 * class.
 *
 * @author Vedran Kasalica
 */
public class Type extends TaxonomyPredicate {

	/** Name of the data type, e.g. "DNA sequence" */
	private final String typeName;
	/** Unique identifier of the data type. The identifier can be human-readable (e.g. "DNA_sequence") or
	 * machine-readable (e.g. "data_0001"). */
	private final String typeID;

	/** Plain type is an artificial type that is used to represent the "plain/simple" version of the abstract class/type. A plain type is the class that does not belong to any of its subclasses. For example, a "plain YAML" is a YAML format that has no extensions. */
	private Type plainType;

	/** Field identifier used in the CWL document to identify the input/output data type. The field ID cannot be set on a single type, but rather on an auxiliary type that is created from multiple types. */
	protected String cwlFieldID = null;
	
	/**
	 * Constructor used to create a Type object.
	 *
	 * @param typeName Type name.
	 * @param typeID   Type ID.
	 * @param rootNode ID of the Taxonomy (Sub)Root node corresponding to the Type.
	 * @param nodeType {@link NodeType} object describing the type w.r.t. the
	 *                 TypeTaxonomy.
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
		if (typeID.endsWith("_plain")) {
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

	/**
	 * Set plain type.
	 * 
	 * @param plainType The plain type value.
	 */
	public void setPlainType(Type plainType) {
		this.plainType = plainType;
	}

	/**
	 * Method returns an artificially created plain version of the abstract class in
	 * case of a strict tool annotations, or the type itself otherwise.
	 * 
	 * @return The type itself or an artificially created plain version of the type
	 *         when needed.
	 */
	public Type getPlainType() {
		return plainType;
	}

	public String getCwlFieldID() {
		return cwlFieldID;
	}

	/**
	 * Generate a taxonomy data instance that is defined based on one or more
	 * dimensions that describe it.
	 * 
	 * @param jsonParam    JSON representation of the data instance
	 * @param domainSetup  setup of the domain
	 * @param isOutputData {@code true} if the data is used to be module output,
	 *                     {@code false} otherwise
	 * @return A type object that represent the data instance given as the
	 *         parameter.
	 * @throws JSONException          if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types are not well defined
	 */
	public static Type taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup, boolean isOutputData)
			throws JSONException, APEDimensionsException {

		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<>();
		boolean labelDefined = false;
		AllTypes allTypes = domainSetup.getAllTypes();
		/* Iterate through each of the dimensions */
		for (String currRootLabel : jsonParam.keySet()) {
			String curRootIRI = currRootLabel;
			if (!allTypes.existsRoot(curRootIRI)) {
				curRootIRI = APEUtils.createClassIRI(currRootLabel, domainSetup.getOntologyPrefixIRI());
			}
			if (!allTypes.existsRoot(curRootIRI)) {
				throw APEDimensionsException
						.notExistingDimension("Data type was defined over a non existing data dimension: '" + curRootIRI
								+ "', in JSON: '" + jsonParam + "'");
			}
			LogicOperation logConn = LogicOperation.OR;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<>();
			/* for each dimensions a disjoint array of types/tools is given */
			for (String currTypeLabel : APEUtils.getListFromJson(jsonParam, currRootLabel, String.class)) {
				String currTypeIRI = currTypeLabel;

				Type currType = allTypes.get(currTypeIRI, curRootIRI);
				if (currType == null) {
					currTypeIRI = APEUtils.createClassIRI(currTypeLabel, domainSetup.getOntologyPrefixIRI());
					currType = allTypes.get(currTypeIRI, curRootIRI);
				}

				if (currRootLabel.equals(AllTypes.getLabelRootID())) {
					labelDefined = true;
				}

				if (currType != null) {
					/*
					 * if the type exists, make it relevant from the taxonomy perspective and add it
					 * to the list of allowed types
					 */
					currType.setAsRelevantTaxonomyTerm(allTypes);
					logConnectedPredicates.add(currType);

					if (isOutputData) {
						currType = currType.getPlainType();
					}
				} else if (currRootLabel.equals(AllTypes.getLabelRootID()) && isOutputData) {
					/* add a new label to the taxonomy */
					currType = allTypes.addPredicate(new Type(currTypeLabel, currTypeLabel, curRootIRI, NodeType.LEAF));

					allTypes.getLabelRoot().addSubPredicate(currType);
					currType.addParentPredicate(allTypes.getLabelRoot());

					/*
					 * make the type relevant from the taxonomy perspective and add it to the list
					 * of allowed types
					 */
					currType.setAsRelevantTaxonomyTerm(allTypes);
					logConnectedPredicates.add(currType);

				} else {
					throw APEDimensionsException.dimensionDoesNotContainClass(String.format(
							"Error in a JSON input, the type of data '%s' is not recognized. \nPotential reasons: \n1) there is no tool that can process the specified type (as input or output) or \n2) the type does not belong to the data dimension '%s'.",
							currTypeIRI, curRootIRI));
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
		return AuxTypePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND,
				domainSetup);

	}

	/**
	 * Generate a taxonomy data instance that is defined based on one or more
	 * dimensions that describe it. The data instance is defined as an input or
	 * output within a
	 * CWL file, and provided as a {@link CommandInputParameter} object.
	 * 
	 * @param cwlInputParam      CWL input parameter that contains the data type and format
	 * @param domainSetup  setup of the domain
	 * @return A type object that represent the data instance given as the
	 *         parameter.
	 * @throws JSONException          if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types are not well defined
	 */
	public static Type taxonomyInstanceFromCWLInput(CommandInputParameter cwlInputParam, APEDomainSetup domainSetup)
			throws JSONException, APEDimensionsException {

		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<>();
		AllTypes allTypes = domainSetup.getAllTypes();

		// TODO: This might break if there are syntax errors, so we should include error handling
		String dataTypeValue = (String) cwlInputParam.getExtensionFields().get(CWLParser.DATA_ROOT_IRI);
		if(dataTypeValue == null) {
			return null;
		}
		String expandedDataType = cwlInputParam.getLoadingOptions().expandUrl(dataTypeValue, "", false, false, null);
		Type dataType = compute(CWLParser.DATA_ROOT, expandedDataType, domainSetup,
				false);
		Type dataFormat = compute(CWLParser.FORMAT_ROOT, (String) cwlInputParam.getFormat(), domainSetup,
				false);
		parameterDimensions.add(dataType);
		parameterDimensions.add(dataFormat);
		parameterDimensions.add(allTypes.getLabelRoot());
		return AuxTypePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND,
				domainSetup);

	}

	/**
	 * Generate a taxonomy data instance that is defined based on one or more
	 * dimensions that describe it. The data instance is defined as an input or
	 * output within a
	 * CWL file, and provided as a {@link CommandOutputParameter} object.
	 * 
	 * @param cwlOutputParam      CWL output parameter that contains the data type and format
	 * @param domainSetup  setup of the domain
	 * @return A type object that represent the data instance given as the
	 *         parameter.
	 * @throws JSONException          if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types are not well defined
	 */
	public static Type taxonomyInstanceFromCWLOutput(CommandOutputParameter cwlOutputParam, APEDomainSetup domainSetup)
			throws JSONException, APEDimensionsException {

		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<>();
		AllTypes allTypes = domainSetup.getAllTypes();

		// TODO: This might break if there are syntax errors, so we should include error handling
		String dataTypeValue = (String) cwlOutputParam.getExtensionFields().get(CWLParser.DATA_ROOT_IRI);
		if(dataTypeValue == null) {
			return null;
		}
		String expandedDataType = cwlOutputParam.getLoadingOptions().expandUrl(dataTypeValue, "", false, false, null);
		Type dataType = compute(CWLParser.DATA_ROOT, expandedDataType, domainSetup,
				true);
		Type dataFormat = compute(CWLParser.FORMAT_ROOT, (String) cwlOutputParam.getFormat(), domainSetup,
				true);
		parameterDimensions.add(dataType);
		parameterDimensions.add(dataFormat);
		parameterDimensions.add(allTypes.getEmptyAPELabel());
		return AuxTypePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND,
				domainSetup);

	}

	/**
	 * Compute {@code Type} object from the given data values.
	 * 
	 * @param dimensionRoot
	 *                       The root of the dimension, e.g. "data_0006" for data
	 *                       type or "format_1915" for data format.
	 * @param dimensionValue
	 *                       The value of the dimension, e.g. "data_0001" for a
	 *                       specific data type or "format_1234" for a specific
	 *                       format.
	 * @param domainSetup
	 *                       The domain object, which contains the ontology and all
	 *                       types.
	 * @param isOutputData
	 *                       {@code true} if the data is used to be module output,
	 *                       {@code false} otherwise
	 * @return A {@code Type} object that represents the data instance given as the
	 *         parameter.
	 * @throws APEDimensionsException if the referenced types are not well defined
	 *                                or if the dimension does not contain the
	 *                                specified class.
	 */
	private static Type compute(String dimensionRoot, String dimensionValue, APEDomainSetup domainSetup, boolean isOutputData)
			throws APEDimensionsException {
		AllTypes allTypes = domainSetup.getAllTypes();
		if (!allTypes.existsRoot(dimensionRoot)) {
			dimensionRoot = APEUtils.createClassIRI(dimensionRoot, domainSetup.getOntologyPrefixIRI());
		}
		if (!allTypes.existsRoot(dimensionRoot)) {
			throw APEDimensionsException
					.notExistingDimension(
							"A type was defined over a non existing dimension: '" + dimensionRoot + "'.");
		}
		String currTypeIRI = dimensionValue;

		Type currType = allTypes.get(currTypeIRI, dimensionRoot);
		if (currType == null) {
			currTypeIRI = APEUtils.createClassIRI(currTypeIRI, domainSetup.getOntologyPrefixIRI());
			currType = allTypes.get(currTypeIRI, dimensionRoot);
		}

		if (currType != null) {
			/*
			 * if the type exists, make it relevant from the taxonomy perspective and add it
			 * to the list of allowed types
			 */
			currType.setAsRelevantTaxonomyTerm(allTypes);

			if (isOutputData) {
				currType = currType.getPlainType();
			}
		} else if (dimensionRoot.equals(AllTypes.getLabelRootID()) && isOutputData) {
			/* add a new label to the taxonomy */
			currType = allTypes.addPredicate(new Type(currTypeIRI, currTypeIRI, dimensionRoot, NodeType.LEAF));

			allTypes.getLabelRoot().addSubPredicate(currType);
			currType.addParentPredicate(allTypes.getLabelRoot());

			/*
			 * make the type relevant from the taxonomy perspective and add it to the list
			 * of allowed types
			 */
			currType.setAsRelevantTaxonomyTerm(allTypes);

		} else {
			throw APEDimensionsException.dimensionDoesNotContainClass(String.format(
					"Error in a JSON input, the type of data '%s' is not recognized. \nPotential reasons: \n1) there is no tool that can process the specified type (as input or output) or \n2) the type does not belong to the data dimension '%s'.",
					currTypeIRI, dimensionRoot));
		}
		return currType;
	}


}
