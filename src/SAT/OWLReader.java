package SAT;

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

import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.Type;

/**
 * The {@code OWLReader} class is used to extract the classification information
 * regarding the modules and data types from the OWL ontology.
 * 
 * @author Vedran Kasalica
 *
 */
public class OWLReader {

	private static String ONTOLOGY_PATH;
	private final AllModules allModules;
	private AllTypes allTypes;

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
	public OWLReader(String ontologyPath, AllModules allModules, AllTypes allTypes) {
		this.ONTOLOGY_PATH = ontologyPath;
		this.allModules = allModules;
		this.allTypes = allTypes;
	}

	public static void main(String[] args) {
		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();
		String path = "/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/GMT_UseCase_taxonomy.owl";
		OWLReader curr = new OWLReader(path, allModules, allTypes);
		try {
			curr.readOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	public boolean readOntology() throws OWLOntologyCreationException {

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ONTOLOGY_PATH));
		OWLClass thingClass = manager.getOWLDataFactory().getOWLThing();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ontology);

		Set<OWLClass> subClasses = reasoner.getSubClasses(thingClass, true).getFlattened();

		OWLClass moduleClass = getModuleClass(subClasses);
		OWLClass typeClass = getTypeClass(subClasses);

		
		if (moduleClass != null) {
			exploreModuleOntologyRec(reasoner, ontology, moduleClass, thingClass);
		} else {
			System.err.println("Provided ontology does not contain the ModulesTaxonomy class.");
		}

		if (typeClass != null) {
			exploreTypeOntologyRec(reasoner, ontology, typeClass, thingClass);
		} else {
			System.err.println("Provided ontology does not contain the TypesTaxonomy class.");
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
	private static OWLClass getModuleClass(Set<OWLClass> subClasses) {
		OWLClass moduleClass = null;
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).matches("ModulesTaxonomy")) {
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
	private static OWLClass getTypeClass(Set<OWLClass> subClasses) {
		OWLClass typeClass = null;
		for (OWLClass currClass : subClasses) {
			if (getLabel(currClass).matches("TypesTaxonomy")) {
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
			OWLClass superClass) {
		AbstractModule superModule, subModule;
		superModule = allModules.get(getLabel(superClass));
		subModule = AbstractModule.generateModule(getLabel(currClass), getLabel(currClass), false, allModules);
		if (superModule != null) {
			superModule.addSubModule(subModule);
		}

		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { // in case that the child is not node owl:Nothing
				exploreModuleOntologyRec(reasoner, ontology, child, currClass);
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
			OWLClass superClass) {
		Type superType, subType;
		superType = allTypes.get(getLabel(superClass));
		subType = Type.generateType(getLabel(currClass), getLabel(currClass), false, allTypes);
		if (superType != null) {
			superType.addSubType(subType);
		}

		for (OWLClass child : reasoner.getSubClasses(currClass, true).getFlattened()) {
			if (reasoner.isSatisfiable(child)) { // in case that the child is not node owl:Nothing
				exploreTypeOntologyRec(reasoner, ontology, child, currClass);
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
	private static String getLabel(OWLClass currClass) {
		String classID = currClass.toStringID();
		String label = classID.substring(classID.indexOf('#') + 1);
		label = label.replace(" ", "_");
		return label;
	}

}
