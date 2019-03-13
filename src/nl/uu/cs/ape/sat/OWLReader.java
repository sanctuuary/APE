package nl.uu.cs.ape.sat;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import nl.uu.cs.ape.sat.models.APEConfig;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.NodeType;
import nl.uu.cs.ape.sat.models.Type;

/**
 * The {@code OWLReader} class is used to extract the classification information
 * regarding the modules and data types from the OWL ontology.
 * 
 * @author Vedran Kasalica
 *
 */
public class OWLReader {

	private final String ONTOLOGY_PATH;
	private final AllModules allModules;
	private final AllTypes allTypes;
	private final String moduleTaxonomyRoot;
	private final String dataTaxonomyRoot;

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
	public OWLReader(AllModules allModules, AllTypes allTypes) {
		this.ONTOLOGY_PATH = APEConfig.getConfig().getONTOLOGY_PATH();
		this.moduleTaxonomyRoot = APEConfig.getConfig().getMODULE_TAXONOMY_ROOT();
		this.dataTaxonomyRoot = APEConfig.getConfig().getTYPE_TAXONOMY_ROOT();
		this.allModules = allModules;
		this.allTypes = allTypes;
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
		OWLOntology ontology = null;
		try {
			File tempOntology = new File(ONTOLOGY_PATH);
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
		OWLClass typeClass = getTypeClass(subClasses);

		if (moduleClass != null) {
			exploreModuleOntologyRec(reasoner, ontology, moduleClass, thingClass, thingClass);
		} else {
			System.err.println("Provided ontology does not contain the "+moduleTaxonomyRoot+" class.");
		}

		if (typeClass != null) {
			exploreTypeOntologyRec(reasoner, ontology, typeClass, thingClass, thingClass);
		} else {
			System.err.println("Provided ontology does not contain the "+dataTaxonomyRoot+" class.");
		}

		if (moduleClass == null || typeClass == null) {
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
			if (getLabel(currClass).matches(moduleTaxonomyRoot)) {
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
	private OWLClass getTypeClass(Set<OWLClass> subClasses) {
		OWLClass typeClass = null;
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).matches(dataTaxonomyRoot)) {
				typeClass = currClass;
			}
		}
		return typeClass;
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
		
		AbstractModule superModule, subModule;
		superModule = allModules.get(getLabel(superClass));
		/*
		 * Defining the Node Type based on the node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if(getLabel(currClass).matches(APEConfig.getConfig().getMODULE_TAXONOMY_ROOT())) {
			currNodeType = NodeType.ROOT;
			rootClass = currClass;
		}
		subModule = AbstractModule.generateModule(getLabel(currClass), getLabel(currClass), getLabel(rootClass), currNodeType, allModules,superModule);

		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { 		// in case that the child is not node owl:Nothing
				exploreModuleOntologyRec(reasoner, ontology, child, currClass, rootClass);
			} else { 									// make the module a tool in case of not having subModules
				subModule.setToTool();	
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
		Type superType, subType;
		superType = allTypes.get(getLabel(superClass));
		/*
		 * Check whether the current node is a root or subRoot node.
		 */
		NodeType currNodeType = NodeType.ABSTRACT;
		if(getLabel(currClass).matches(APEConfig.getConfig().getTYPE_TAXONOMY_ROOT())) {
			currNodeType = NodeType.ROOT;
			rootClass = currClass;
		} else {
			for(String dataTaxonomySubRoot : APEConfig.getConfig().getData_Taxonomy_SubRoots()) {
				if(getLabel(currClass).matches(dataTaxonomySubRoot)) {
					currNodeType = NodeType.SUBROOT;
					rootClass = currClass;
				}
			}
		}
		
		subType = Type.generateType(getLabel(currClass), getLabel(currClass), getLabel(rootClass), currNodeType, allTypes,superType);

		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { // in case that the child is not node owl:Nothing
				exploreTypeOntologyRec(reasoner, ontology, child, currClass, rootClass);
			} else {
				subType.setToSimpleType();		// make the type a simple type in case of not having subTypes
			}
		}
	}

	/**
	 * Printing the label of the provided OWL class.
	 * 
	 * @param currClass
	 *            - provided OWL class
	 * @return String representation of the class name.
	 */
	private String getLabel(OWLClass currClass) {
		if(currClass == null) {
			return null;
		}
		String classID = currClass.toStringID();
		String label = classID.substring(classID.indexOf('#') + 1);
		label = label.replace(" ", "_");
		return label;
	}

}
