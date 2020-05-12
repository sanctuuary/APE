package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

/**
 * The {@code OWLReader} class is used to extract the classification information
 * regarding the modules and data types from the OWL ontology.
 * 
 * @author Vedran Kasalica
 *
 */
public class OWLReader {

	private final String ontologyPath;
	private final AllModules allModules;
	private final AllTypes allTypes;
	private Map<String, Set<String>> typeDimensions = new HashMap<String, Set<String>>();
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private Logger logger = Logger.getLogger("MyLog");

	/**
	 * Setting up the reader that will populate the provided module and type sets
	 * with objects from the ontology.
	 * 
	 * @param ontologyPath - path to the OWL file
	 * @param allModules   - set of all the modules in our system
	 * @param allTypes     - set of all the types in our system
	 */
	public OWLReader(APEDomainSetup domain, String ontologyPath) {
		this.ontologyPath = ontologyPath;
		this.allModules = domain.getAllModules();
		this.allTypes = domain.getAllTypes();
		this.factory = OWLManager.getOWLDataFactory();
	}

	/**
	 * Method used to read separately <b>ModulesTaxonomy</b> and
	 * <b>TypesTaxonomy</b> part of the ontology.
	 * 
	 * @throws OWLOntologyCreationException
	 * 
	 * @return {@code true} is the ontology was read correctly, {@code false}
	 *         otherwise.
	 * @throws ExceptionInInitializerError 
	 */
	public boolean readOntology() throws ExceptionInInitializerError {

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			File tempOntology = new File(ontologyPath);
			if (tempOntology.exists()) {
				ontology = manager.loadOntologyFromOntologyDocument(tempOntology);
			} else {
				logger.warning("Provided ontology does not exist.");
				return false;
			}
		} catch (OWLOntologyCreationException e) {
			logger.warning("Ontology is not properly provided.");
			return false;
		}
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		/* Get a root of the operations taxonomy. */
		String moduleRootIRI = allModules.getRootsIDs().get(0);
		OWLClass moduleRootClass = manager.getOWLDataFactory().getOWLClass(IRI.create(moduleRootIRI));
		if(!ontology.containsClassInSignature(IRI.create(moduleRootIRI))) {
			/* Handle scenario when the tool taxonomy root was not defined properly. */
			logger.warning("Provided ontology does not contain the " + moduleRootIRI
			+ " class, intended as a root for operation taxonomy.");
			return false;
		}
		
		/* Get roots for each of the data dimensions. */
		List<OWLClass> dimensionRootClasses = new ArrayList<OWLClass>();
		for(String dimensionID : allTypes.getRootsIDs()) {
			OWLClass dimensionClass = manager.getOWLDataFactory().getOWLClass(IRI.create(dimensionID));
			if(!ontology.containsClassInSignature(IRI.create(dimensionID))) {
				/* Handle scenario when the type taxonomy root was not defined properly. */
				logger.warning("Provided ontology does not contain the " + dimensionID
				+ " class, intended as a root for a data dimension.");
				return false;
			} else {
				dimensionRootClasses.add(dimensionClass);
			}
		}

		
		exploreModuleOntologyRec(reasoner, moduleRootClass, null, null);
		
		
			dimensionRootClasses.forEach(typeClass -> typeDimensions.put(getIRI(typeClass), new HashSet<String>()));
			dimensionRootClasses.forEach(
					typeClass -> exploreTypeOntologyRec(reasoner, typeClass, null, null));

		if(!dimensionsDisjoint(dimensionRootClasses)) {
			throw new ExceptionInInitializerError("The type dimensions cannot have common classes");
		}

		return true;
	}

	/**
	 * Calculate whether the type dimensions are disjoint or have overlaps.
	 * @param typeClasses 
	 * @return {code true} if the dimensions are disjoint, {@code false} otherwise.
	 */
	private boolean dimensionsDisjoint(List<OWLClass> typeClasses) {
		if(typeClasses.size() < 2) {
			return true;
		}
		for(OWLClass class1 : typeClasses) {
			for(OWLClass class2 : typeClasses) {
				String classID1 = getIRI(class1);
				String classID2 = getIRI(class2);
				if(!classID1.equals(classID2)) {
					if(!Collections.disjoint(typeDimensions.get(classID1), typeDimensions.get(classID2))) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@ling AbstractModule}) on each step of the way.
	 * 
	 * @param reasoner   - reasoner used to provide subclasses
	 * @param ontology   - our current ontology
	 * @param currClass  - the class (node) currently explored
	 * @param superClass - the superclass of the currClass
	 */
	private void exploreModuleOntologyRec(OWLReasoner reasoner, OWLClass currClass,
			OWLClass superClass, OWLClass rootClass) {
//		if(allModules.existsModule(getLabel(currClass))) {
//			return;
//		}
		AbstractModule superModule = allModules.get(getIRI(superClass));
		final OWLClass currRootClass;
		/*
		 * Defining the Node Type based on the node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if (getIRI(currClass).equals(allModules.getRootsIDs().get(0))) {
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
	 * @param reasoner   - reasoner used to provide subclasses
	 * @param ontology   - our current ontology
	 * @param currClass  - the class (node) currently explored
	 * @param superClass - the superclass of the currClass
	 */
	private void exploreTypeOntologyRec(OWLReasoner reasoner, OWLClass currClass,
			OWLClass superClass, OWLClass rootClass) {
//		if(allTypes.existsType(getLabel(currClass))) {
//			return;
//		}
		
		final OWLClass currRoot;
		Type superType, currType = null;
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

		/* Generate the Type that corresponds to the taxonomy class. */
		try {
			currType = allTypes.addPredicate(new Type(getLabel(currClass), getIRI(currClass), getIRI(currRoot), currNodeType));
			typeDimensions.get(getIRI(currRoot)).add(getIRI(currClass));
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}

		/* Add the current type as a sub-type of the super type. */
		if (superType != null && currType != null) {
			superType.addSubPredicate(currType);
		}
		/* Add the super-type for the current type */
		if (currNodeType != NodeType.ROOT) {
			currType.addSuperPredicate(superType);
		}

		List<OWLClass> subClasses = reasoner.getSubClasses(currClass, true).entities()
											.filter(child -> reasoner.isSatisfiable(child))
											.collect(Collectors.toList());
		
		subClasses.forEach(child -> exploreTypeOntologyRec(reasoner, child, currClass, currRoot));

		if (subClasses.isEmpty()) {
			currType.setToSimplePredicate();
			;
		}
	}

	/**
	 * Returning the label of the provided OWL class.
	 * 
	 * @param currClass - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getLabel(OWLClass currClass) {
		if (currClass == null || currClass.isOWLNothing()) {
			return "N/A";
		}
		String label, classID = currClass.toStringID();
		Optional<OWLAnnotation> classLabel = EntitySearcher.getAnnotations(currClass, ontology, factory.getRDFSLabel()).findFirst();
		if (classLabel.isPresent()) {
			OWLAnnotationValue val = classLabel.get().getValue();
			if (val instanceof OWLLiteral) return ((OWLLiteral) val).getLiteral();
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
	 * @param currClass - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getIRI(OWLClass currClass) {
		if (currClass == null) {
			return null;
		}
		return currClass.toStringID();
	}

}
