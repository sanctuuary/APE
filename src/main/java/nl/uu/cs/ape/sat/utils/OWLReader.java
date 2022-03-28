package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.NodeType;

import org.apache.commons.io.FileExistsException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The {@code OWLReader} class is used to extract the classification information
 * regarding the modules and data types from the OWL ontology.
 *
 * @author Vedran Kasalica
 */
public class OWLReader {

	/** File cinstianing the ontology */
	private final File ontologyFile;
	/** List of all modules in the domain */
	private final AllModules allModules;
	/** List of all types in the domain */
	private final AllTypes allTypes;
	/** Mapping from each dimension to the list of the types within it */
	private Map<String, Set<String>> typeDimensions = new HashMap<String, Set<String>>();

	private OWLOntology ontology;
	private OWLDataFactory factory;
	/** OWL logger */
	private Logger logger = Logger.getLogger("OWLReader.class");
	/** Holds information whether the domain was annotated under the strict rules of the output dependency. */
	private boolean useStrictToolAnnotations;

	/**
	 * Setting up the reader that will populate the provided module and type sets
	 * with objects from the ontology.
	 *
	 * @param domain       Domain information, including all the existing tools and types.
	 * @param ontologyFile Path to the OWL file.
	 */
	public OWLReader(APEDomainSetup domain, File ontologyFile) {
		this.ontologyFile = ontologyFile;
		this.allModules = domain.getAllModules();
		this.allTypes = domain.getAllTypes();
		this.factory = OWLManager.getOWLDataFactory();
		this.useStrictToolAnnotations = domain.getUseStrictToolAnnotations();
	}

	/**
	 * Method used to read separately <b>ModulesTaxonomy</b> and
	 * <b>TypesTaxonomy</b> part of the ontology.
	 *
	 * @return true is the ontology was read correctly, false otherwise.
	 * @throws APEDimensionsException Exception if Type dimensions have common
	 *                                     classes.
	 * @throws OWLOntologyCreationException Error in reading the OWL file.
	 */
	public boolean readOntology() throws APEDimensionsException, OWLOntologyCreationException {

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		if (ontologyFile.exists()) {
			ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
		} else {
			logger.warning("Provided ontology does not exist.");
			return false;
		}
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		/* Get a root of the operations taxonomy. */
		String moduleRootIRI = allModules.getAllRootIDs().get(0);
		OWLClass moduleRootClass = manager.getOWLDataFactory().getOWLClass(IRI.create(moduleRootIRI));
		if (!ontology.containsClassInSignature(IRI.create(moduleRootIRI))) {
			/* Handle scenario when the tool taxonomy root was not defined properly. */
			throw APEDimensionsException.notExistingDimension(String.format("Operation root %s does not exist in the ontology.", moduleRootIRI));
		}

		/* Get roots for each of the data dimensions. */
		List<OWLClass> dimensionRootClasses = new ArrayList<OWLClass>();
		for (String dimensionIRI : allTypes.getDataTaxonomyDimensionIDs()) {
			OWLClass dimensionClass = manager.getOWLDataFactory().getOWLClass(IRI.create(dimensionIRI));
			if (!ontology.containsClassInSignature(IRI.create(dimensionIRI))) {
				throw APEDimensionsException.notExistingDimension(String.format("Data dimension %s does not exist in the ontology.", dimensionIRI));
			} else {
				dimensionRootClasses.add(dimensionClass);
			}
		}

		exploreModuleOntologyRec(reasoner, moduleRootClass, null, null);

		dimensionRootClasses.forEach(typeClass -> typeDimensions.put(getIRI(typeClass), new HashSet<String>()));
		dimensionRootClasses.forEach(typeClass -> exploreTypeOntologyRec(reasoner, typeClass, null, null));

		String ovesrlap;
		if ((ovesrlap = dimensionsDisjoint(dimensionRootClasses)) != null) {
			throw APEDimensionsException.dimensionsOverlap("The dimensions '" + ovesrlap + "' have common classes.");
		}

		return true;
	}
	
	public static boolean verifyOntology(File ontologyFile, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots ) throws APEDimensionsException, OWLOntologyCreationException, FileExistsException {

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		AllModules allModules = new AllModules(toolTaxonomyRoot);
		AllTypes allTypes = new AllTypes(dataDimensionRoots);
		OWLOntology ontology = null;
		if (ontologyFile.exists()) {
			ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
		} else {
			throw new FileExistsException("Ontology file does not exist.");
		}
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
		OWLDataFactory factory = OWLManager.getOWLDataFactory();
		
		/* Get a root of the operations taxonomy. */
		String moduleRootIRI = allModules.getAllRootIDs().get(0);
		OWLClass moduleRootClass = manager.getOWLDataFactory().getOWLClass(IRI.create(moduleRootIRI));
		if (!ontology.containsClassInSignature(IRI.create(moduleRootIRI))) {
			/* Handle scenario when the tool taxonomy root was not defined properly. */
			throw APEDimensionsException.notExistingDimension(String.format("Operation root %s does not exist in the ontology.", moduleRootIRI));
		}

		/* Get roots for each of the data dimensions. */
		List<OWLClass> dimensionRootClasses = new ArrayList<OWLClass>();
		for (String dimensionIRI : allTypes.getDataTaxonomyDimensionIDs()) {
			OWLClass dimensionClass = manager.getOWLDataFactory().getOWLClass(IRI.create(dimensionIRI));
			if (!ontology.containsClassInSignature(IRI.create(dimensionIRI))) {
				/* Handle scenario when the type taxonomy root was not defined properly. */
				throw APEDimensionsException.notExistingDimension(String.format("Data dimension %s does not exist in the ontology.", dimensionIRI));
			} else {
				dimensionRootClasses.add(dimensionClass);
			}
		}

		return true;
	}

	/**
	 * Calculate whether the type dimensions are disjoint or have overlaps.
	 *
	 * @return {code true} if the dimensions are disjoint, false otherwise.
	 */
	private String dimensionsDisjoint(List<OWLClass> typeClasses) {
		if (typeClasses.size() < 2) {
			return null;
		}
		for (OWLClass class1 : typeClasses) {
			for (OWLClass class2 : typeClasses) {
				String classID1 = getIRI(class1);
				String classID2 = getIRI(class2);
				if (!classID1.equals(classID2)) {
					if (!Collections.disjoint(typeDimensions.get(classID1), typeDimensions.get(classID2))) {
						return classID1 + " & " + classID2;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@link AbstractModule}) on each step of the way.
	 *
	 * @param reasoner   Reasoner used to provide subclasses.
	 * @param currClass  The class (node) currently explored.
	 * @param superClass The superclass of the currClass.
	 */
	private void exploreModuleOntologyRec(OWLReasoner reasoner, OWLClass currClass, OWLClass superClass,
			OWLClass rootClass) {
		AbstractModule superModule = allModules.get(getIRI(superClass));
		final OWLClass currRootClass;
		/*
		 * Defining the Node Type based on the node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if (getIRI(currClass).equals(allModules.getAllRootIDs().get(0))) {
			currNodeType = NodeType.ROOT;
			currRootClass = currClass;
		} else {
			currRootClass = rootClass;
		}
		/* Generate the AbstractModule that corresponds to the taxonomy class. */
		AbstractModule currModule = null;
		try {
			currModule = allModules.addPredicate(
					new AbstractModule(getLabel(currClass), getIRI(currClass), getIRI(currRootClass), currNodeType));
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
		/* Add the current module as a sub-module of the super module. */
		if (superModule != null && currModule != null) {
			superModule.addSubPredicate(currModule);
		}
		/* Add the super-type for the current type */
		if (currNodeType != NodeType.ROOT) {
			currModule.addSuperPredicate(superModule);
		}
		reasoner.getSubClasses(currClass, true).entities().filter(child -> reasoner.isSatisfiable(child))
				.forEach(child -> exploreModuleOntologyRec(reasoner, child, currClass, currRootClass));
	}

	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@link Type}) on each step of the way.
	 *
	 * @param reasoner   Reasoner used to provide subclasses.
	 * @param currClass  The class (node) currently explored.
	 * @param superClass The superclass of the currClass.
	 */
	private void exploreTypeOntologyRec(OWLReasoner reasoner, OWLClass currClass, OWLClass superClass,
			OWLClass rootClass) {

		final OWLClass currRoot;
		Type superType = null;
		Type currType = null;
			
		superType = allTypes.get(getIRI(superClass), getIRI(rootClass));
		/*
		 * Check whether the current node is a root or subRoot node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if (allTypes.getDataTaxonomyDimensionIDs().contains(getIRI(currClass))) {
			currNodeType = NodeType.ROOT;
			currRoot = currClass;
		} else {
			currRoot = rootClass;
		}

		currType = addNewTypeToAllTypes(getLabel(currClass), getIRI(currClass), getIRI(currRoot), currNodeType);

		
		/* Add the current type as a sub-type of the super type. */
		if (superType != null && currType != null) {
			superType.addSubPredicate(currType);
		}
		/* Add the super-type for the current type */
		if (currNodeType != NodeType.ROOT) {
			currType.addSuperPredicate(superType);
		}

		List<OWLClass> subClasses = reasoner.getSubClasses(currClass, true).entities()
				.filter(child -> reasoner.isSatisfiable(child)).collect(Collectors.toList());

		subClasses.forEach(child -> exploreTypeOntologyRec(reasoner, child, currClass, currRoot));

		if (subClasses.isEmpty()) {
			currType.setNodePredicate(NodeType.LEAF);
		} else if (useStrictToolAnnotations) {
			Type artificialSubType = addNewTypeToAllTypes(getLabel(currClass) + "_p", getIRI(currClass) + "_plain",
					getIRI(currRoot), NodeType.ARTIFICIAL_LEAF);
			if (artificialSubType != null) {
				currType.addSubPredicate(artificialSubType);
				currType.setPlainType(artificialSubType);
				
				artificialSubType.addSuperPredicate(currType);
				artificialSubType.setNodePredicate(NodeType.LEAF);
			} else {
				System.err.println("Artificial predicate '" + getLabel(currClass) + "' was not created correctly.");
			}
		}
	}
	
	private Type addNewTypeToAllTypes(String classLabel, String classID, String rootID, NodeType currNodeType) {
		Type currType = null;
		/* Generate the Type that corresponds to the taxonomy class. */
		try {
			currType = allTypes
					.addPredicate(new Type(classLabel, classID, rootID, currNodeType));
			typeDimensions.get(rootID).add(classID);
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
		return currType;
	}

	/**
	 * Returning the label of the provided OWL class.
	 *
	 * @param currClass Provided OWL class.
	 * @return String representation of the class name.
	 */
	private String getLabel(OWLClass currClass) {
		if (currClass == null || currClass.isOWLNothing()) {
			return "N/A";
		}
		String label, classID = currClass.toStringID();
		Optional<OWLAnnotation> classLabel = EntitySearcher.getAnnotations(currClass, ontology, factory.getRDFSLabel())
				.findFirst();
		if (classLabel.isPresent()) {
			OWLAnnotationValue val = classLabel.get().getValue();
			if (val instanceof OWLLiteral)
				return ((OWLLiteral) val).getLiteral();
		} else if (classID.contains("#")) {
			label = classID.substring(classID.indexOf('#') + 1);
//			label = label.replace(" ", "_");
			return label;
		}
		logger.fine("Class '" + classID + "' has no label.");
		return classID;

	}

	/**
	 * Returning the IRI of the provided OWL class.
	 *
	 * @param currClass Provided OWL class.
	 * @return String representation of the class name.
	 */
	private String getIRI(OWLClass currClass) {
		if (currClass == null) {
			return null;
		}
		return currClass.toStringID();
	}
}
