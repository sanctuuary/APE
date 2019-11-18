package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import nl.uu.cs.ape.sat.utils.APEConfig;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.NodeType;

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
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private boolean typeRootExists;

	/**
	 * Setting up the reader that will populate the provided module and type sets
	 * with objects from the ontology.
	 * 
	 * @param ontologyPath
	 *            - path to the OWL file
	 * @param allModules
	 *            - set of all the modules in our system
	 * @param allTypes
	 *            - set of all the types in our system
	 */
	public OWLReader(AllModules allModules, AllTypes allTypes, String ontologyPath) {
		this.ontologyPath = ontologyPath;
		this.allModules = allModules;
		this.allTypes = allTypes;
		this.factory = OWLManager.getOWLDataFactory();
		typeRootExists = false;
	}
	

	/**
	 * Method used to read separately <b>ModulesTaxonomy</b> and
	 * <b>TypesTaxonomy</b> part of the ontology.
	 * 
	 * @throws OWLOntologyCreationException
	 * 
	 * @return {@code true} is the ontology was read correctly, {@code false}
	 *         otherwise.
	 */
	public boolean readOntology() {

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			File tempOntology = new File(ontologyPath);
			if (tempOntology.exists()) {
				ontology = manager.loadOntologyFromOntologyDocument(tempOntology);
			} else {
				System.err.println("Provided ontology does not exist.");
				return false;
			}
		} catch (OWLOntologyCreationException e) {
			System.err.println("Ontology is not properly provided.");
			return false;
		}
		OWLClass thingClass = manager.getOWLDataFactory().getOWLThing();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		Set<OWLClass> subClasses = reasoner.getSubClasses(thingClass, true).getFlattened();

		OWLClass moduleClass = getModuleClass(subClasses);
		List<OWLClass> typeClasses = getTypeClasses(subClasses);

		if (moduleClass != null) {
			exploreModuleOntologyRec(reasoner, ontology, moduleClass, thingClass, thingClass);
		} else {
			System.err.println("Provided ontology does not contain the "+allModules.getRootID()+" class as a root for operation taxonomy.");
		}

		if (typeClasses != null && !typeClasses.isEmpty()) {
			OWLClass superClass;
			if(typeRootExists) {
				superClass = thingClass;
			} else {
				/* If the main root of the data type taxonomy does not exist, create one artificially. */
				Type root = allTypes.addType("DataTaxonomy", "DataTaxonomy", "DataTaxonomy", NodeType.ROOT);
				allTypes.setRootPredicate(root);
				superClass = new OWLClassImpl(IRI.create("http://www.w3.org#DataTaxonomy"));
			}
			
			for(OWLClass typeClass : typeClasses) {
				exploreTypeOntologyRec(reasoner, ontology, typeClass, superClass, superClass);
			}
		} else {
			System.err.println("Provided ontology does not contain the provided data type taxonomy root class(es).");
		}

		if (moduleClass == null || typeClasses == null || typeClasses.isEmpty()) {
			System.err.println("Ontology was not loaded because of the bad formatting.");
			return false;
		}

		return true;
	}

	/**
	 * Method returns the <b>ModulesTaxonomy</b> class from the set of OWL classes.
	 * 
	 * @param subClasses
	 *            - set of OWL classes
	 * @return <b>ModulesTaxonomy</b> OWL class.
	 */
	private OWLClass getModuleClass(Set<OWLClass> subClasses) {
		OWLClass moduleClass = null;
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).equals(allModules.getRootID())) {
				moduleClass = currClass;
			}
		}
		return moduleClass;
	}

	/**
	 * Method returns the <b>TypesTaxonomy</b> class from the set of OWL classes.
	 * 
	 * @param subClasses
	 *            - set of OWL classes
	 * @return <b>TypesTaxonomy</b> OWL class.
	 */
	private List<OWLClass> getTypeClasses(Set<OWLClass> subClasses) {
		List<OWLClass> typeClasses = new ArrayList<OWLClass>();
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).equals(allTypes.getRootID())) {
				typeClasses.add(currClass);
				typeRootExists = true;
			} else {
				for(String dataTaxonomySubRoot : APEUtils.safe(allTypes.getDataTaxonomyDimensions())) {
					if(getLabel(currClass).equals(dataTaxonomySubRoot)) {
						typeClasses.add(currClass);
					}
				}
			}
		}
		return typeClasses;
	}

	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@ling AbstractModule}) on each step of the way.
	 * 
	 * @param reasoner
	 *            - reasoner used to provide subclasses
	 * @param ontology
	 *            - our current ontology
	 * @param currClass
	 *            - the class (node) currently explored
	 * @param superClass
	 *            - the superclass of the currClass
	 */
	private void exploreModuleOntologyRec(OWLReasoner reasoner, OWLOntology ontology, OWLClass currClass,
			OWLClass superClass, OWLClass rootClass) {
		
		AbstractModule superModule = allModules.get(getLabel(superClass));
		/*
		 * Defining the Node Type based on the node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if(getLabel(currClass).equals(allModules.getRootID())) {
			currNodeType = NodeType.ROOT;
			rootClass = currClass;
		}
		/* Generate the AbstractModule that corresponds to the taxonomy class. */
		AbstractModule currModule = allModules.addModule(new AbstractModule(getLabel(currClass), getLabel(currClass), getLabel(rootClass), currNodeType));
		/* Add the current module as a sub-module of the super module.*/
		if(superModule != null && currModule != null) {
			superModule.addSubPredicate(getLabel(currClass));
		}
		/* Add the super-type for the current type */
		if(currNodeType != NodeType.ROOT) {
			currModule.addSuperPredicate(superModule);
		}
		
		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { 		// in case that the child is not node owl:Nothing
				exploreModuleOntologyRec(reasoner, ontology, child, currClass, rootClass);
			}
		}
	}

	/**
	 * Recursively exploring the hierarchy of the ontology and defining objects
	 * ({@link Type}) on each step of the way.
	 * 
	 * @param reasoner
	 *            - reasoner used to provide subclasses
	 * @param ontology
	 *            - our current ontology
	 * @param currClass
	 *            - the class (node) currently explored
	 * @param superClass
	 *            - the superclass of the currClass
	 */
	private void exploreTypeOntologyRec(OWLReasoner reasoner, OWLOntology ontology, OWLClass currClass,
			OWLClass superClass, OWLClass rootClass) {
		if(currClass == null) {
			return;
		}
		Type superType, subType;
		superType = allTypes.get(getLabel(superClass));
		/*
		 * Check whether the current node is a root or subRoot node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if(getLabel(currClass).equals(allTypes.getRootID())) {
			currNodeType = NodeType.ROOT;
			rootClass = currClass;
		} else {
			for(String dataTaxonomySubRoot : APEUtils.safe(allTypes.getDataTaxonomyDimensions())) {
				if(getLabel(currClass).equals(dataTaxonomySubRoot)) {
					currNodeType = NodeType.SUBROOT;
					rootClass = currClass;
				}
			}
		}
		
		/* Generate the Type that corresponds to the taxonomy class. */
		subType = allTypes.addType(getLabel(currClass), getLabel(currClass), getLabel(rootClass), currNodeType);
		
		/* Add the current type as a sub-type of the super type.*/
		if (superType != null && subType != null) {
			superType.addSubPredicate(getLabel(currClass));
		}
		/* Add the super-type for the current type */
		if(currNodeType != NodeType.ROOT) {
			subType.addSuperPredicate(superType);
		}
		/*
		 * Define the counter to check whether all the subclasses are empty / owl:Nothing
		 */
		int unsatSubClasses = 0, subClasses = reasoner.getSubClasses(currClass, true).getFlattened().size();
		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { 
				/* in case that the child is not node owl:Nothing */
				exploreTypeOntologyRec(reasoner, ontology, child, currClass, rootClass);
			} else {
				unsatSubClasses ++;
			}
		}
		if(unsatSubClasses == subClasses) {
			/* make the type a simple type in case of not having subTypes */
			subType.setToSimplePredicate();;		
		}
	}

	/**
	 * Returning the label of the provided OWL class.
	 * 
	 * @param currClass
	 *            - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getLabel(OWLClass currClass) {
		if(currClass == null) {
			return null;
		}
		String label, classID = currClass.toStringID();
		List<OWLAnnotation> labels = EntitySearcher.getAnnotations(currClass, ontology, factory.getRDFSLabel()).collect(Collectors.toList());
		/* sometimes optional is empty */
		if(labels.size() > 0) {
			label = labels.get(0).toString();
			label = label.substring(label.indexOf("\"")+1, label.lastIndexOf("\""));
			return label;
		} else if(classID.contains("#")) {
			label = classID.substring(classID.indexOf('#') + 1);
			label = label.replace(" ", "_");
			return label;
		} else {
			System.out.println("Class '" + classID + "' has no label.");
			label = classID;
		}
		return classID;
	}
	
	/**
	 * Returning the IRI of the provided OWL class.
	 * 
	 * @param currClass
	 *            - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getIRI(OWLClass currClass) {
		if(currClass == null) {
			return null;
		}
		return currClass.toStringID();
	}

}
