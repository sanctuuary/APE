package SAT;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.Module;
import SAT.models.Type;
import de.jabc.plugin.ontEDAPI.OntEDAPI;
import de.jabc.plugin.ontEDAPI.OntEDFactory;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDException;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDMissingImportException;
import de.jabc.plugin.ontEDAPI.defaultImpl.DefaultFactory;
import de.jabc.plugin.ontEDAPI.ontEDInterfaces.OntEDClass;
import de.jabc.plugin.ontEDAPI.ontEDInterfaces.OntEDDataProperty;

public class OWLExplorer {

	// this URL points to the ontology in the file system
	private static String gmtURL;

	// fields to provide easy access to GMT for all methods in this class
	private static OWLOntology gmtOntology;
	private static OWLOntologyManager ontManager;
	private static OWLDataFactory ontDataFactory;
	private static AllModules allModules;
	private static AllTypes allTypes;

	/**
	 * Updates all the modules and types in @modules and @types, based on the
	 * taxonomy from @file. Taxonomy had to contain 2 separate substructures:
	 * ModulesTaxonomy and TypesTaxonomy.
	 * 
	 * @param file
	 *            - path to the module and type taxonomy
	 * @param modules
	 *            - set of all modules that will get updated
	 * @param types
	 *            - set of all types that will get updated
	 */
	public static void getObjectsFromTaxonomy(String file, AllModules modules, AllTypes types)
			throws OntEDException, IOException, OntEDMissingImportException {

		gmtURL = file;
		allModules = modules;
		allTypes = types;

		// load GMT
		IRI gmtIRI = IRI.create(gmtURL);
		ontManager = OWLManager.createOWLOntologyManager();
		try {
			gmtOntology = ontManager.loadOntologyFromOntologyDocument(gmtIRI);
			// System.out.println("Loaded GMT ontology.");
		} catch (OWLOntologyCreationException e) {
			System.err.println("Error loading GMT ontology.");
			e.printStackTrace();
			return;
		}
		ontDataFactory = ontManager.getOWLDataFactory();

		/*
		 * translation of each ontology starts now
		 */

		String log4jConfPath = "/home/vedran/eclipse-workspace/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		/* CREATE PROPHETS-COMPATIBLE MODULE TAXONOMY (using OntED) */

		// create empty ontology
		OntEDFactory factory_modules = new DefaultFactory();
		OntEDAPI api_modules;

		api_modules = new OntEDAPI(factory_modules);
		api_modules.createNewOntology(URI.create("http://de/jabc/prophets/modules"));

		// add data properties for sibUID and branchName
		OntEDDataProperty sibUID = factory_modules
				.instantiateOntEDDataProperty(URI.create("http://de/jabc/prophets/modules/properties/sibUID"));
		api_modules.createDataProperty(sibUID);
		OntEDDataProperty branchName = factory_modules
				.instantiateOntEDDataProperty(URI.create("http://de/jabc/prophets/modules/properties/branchName"));
		api_modules.createDataProperty(branchName);

		// get "operation" class
		OWLClass operationClass = ontDataFactory.getOWLClass(
				IRI.create("http://www.semanticweb.org/vedran/ontologies/2018/2/GMTTaxonomy#ModulesTaxonomy"));

		// add operation terms from GMT to service taxonomy
		addClassTree2OntedOntology(operationClass, api_modules.getOwlThing(), factory_modules, api_modules, gmtOntology,
				1);
		
		 /* CREATE PROPHETS-COMPATIBLE TYPE TAXONOMY (using OntED) */
		
		 // create empty ontology
		 factory_modules = new DefaultFactory();
		 api_modules = new OntEDAPI(factory_modules);
		 api_modules.createNewOntology(URI.create("http://de/jabc/prophets/types"));
		
		 // add data properties for sibUID and branchName
		 sibUID = factory_modules
		 .instantiateOntEDDataProperty(URI.create("http://de/jabc/prophets/types/properties/sibUID"));
		 api_modules.createDataProperty(sibUID);
		 branchName = factory_modules
		 .instantiateOntEDDataProperty(URI.create("http://de/jabc/prophets/types/properties/branchName"));
		 api_modules.createDataProperty(branchName);
		
		 // get "data" class, http://edamontology.org/data_0006
		 OWLClass dataClass = ontDataFactory.getOWLClass(
		 IRI.create("http://www.semanticweb.org/vedran/ontologies/2018/2/GMTTaxonomy#TypesTaxonomy"));
		
		 // add data terms from GMT to type taxonomy
		 addClassTree2OntedOntology(dataClass, api_modules.getOwlThing(),
		 factory_modules, api_modules, gmtOntology, 2);

	}

	/**
	 * recursive method for walking through a class tree and turn it into an OntED
	 * ontology (for use in PROPHETS)
	 *
	 * @param edamClass
	 *            - current class
	 * @param ontedsuperclass
	 *            - superclass
	 * @param ontedfactory
	 * @param ontedapi
	 * @param edamDataFactory
	 * @param edamOntology
	 * @throws OntEDException
	 */
	private static void addClassTree2OntedOntology(OWLClass edamClass, OntEDClass ontedsuperclass,
			OntEDFactory ontedfactory, OntEDAPI ontedapi, OWLOntology edamOntology, int i) throws OntEDException {

		AbstractModule superModule, subModule;
		Type superType, subType;
		if (getLabel(edamClass).matches("owl:Nothing")) {
			if (i == 1) {
				superModule = allModules.get(getLabel(ontedsuperclass));
				superModule.setIsTool(true);
				allModules.addModule(new Module(superModule.getModuleName(), superModule.getModuleID()));
			} else {
				superType = allTypes.get(getLabel(ontedsuperclass));
				superType.setSimpleType(true);
			}
		} else {
			if (getLabel(ontedsuperclass).matches("Thing")) {
				if (i == 1) {
					subModule = AbstractModule.generateModule(getLabel(edamClass), getLabel(edamClass), false,
							allModules);
				} else {
					subType = Type.generateType(getLabel(edamClass), getLabel(edamClass), false, allTypes);
				}
			} else {
				if (i == 1) {
					superModule = allModules.get(getLabel(ontedsuperclass));
					subModule = AbstractModule.generateModule(getLabel(edamClass), getLabel(edamClass), false,
							allModules);
					superModule.addSubModule(subModule);

				} else {
					superType = allTypes.get(getLabel(ontedsuperclass));
					subType = Type.generateType(getLabel(edamClass), getLabel(edamClass), false, allTypes);
					superType.addSubType(subType);
				}
			}
			// add the (new) class to the OntED ontology
			URI classURI = URI.create(ontedapi.getOntologyURI() + "#" + getLabel(edamClass));
			OntEDClass ontedclass = null;

			try {
				// This seems to be the only way of checking if the
				// individual exists.
				ontedclass = ontedapi.getOntEDClass(classURI);
			} catch (OntEDException e) {
				// TODO: do I really want to check for the string message?
				// but if not,
				// what happens when some other onted exception occurs?
				if (e.getMessage().equals("OntEDClass object " + classURI.toASCIIString() + " does not exist!")) {
					ontedclass = ontedfactory.instantiateOntEDClass(classURI);
					ontedapi.createClass(ontedclass);
				}
			}

			// add a subclass relationship between the class and its superclass
			ontedapi.addSubClassOfAxiom(ontedsuperclass, ontedclass);

			// add the classe's subclasses recursively
			OWLReasoner r = new StructuralReasonerFactory().createReasoner(edamOntology);
			NodeSet<OWLClass> subclasses = r.getSubClasses(edamClass, true);

			for (Node<OWLClass> subClass : subclasses) {
				// if (!(subClass.getRepresentativeElement().isOWLNothing())) {
				addClassTree2OntedOntology(subClass.getRepresentativeElement(), ontedclass, ontedfactory, ontedapi,
						edamOntology, i);
				// }
			}
		}

	}

	private static String getLabel(OntEDClass edamclass) {

		String label = edamclass.toString().substring(edamclass.toString().indexOf('#') + 1);
		label = label.replace(" ", "_");
		return label;
	}

	/**
	 * returns the label of an OWL/GMT class, replaces whitespaces (which OntEd can
	 * not handle) with "_"
	 * 
	 * @param owlclass
	 * @param owlontology
	 * @param owldatafactory
	 * @return
	 */
	private static String getLabel(OWLClass edamclass) {

		if (!(edamclass.isOWLNothing())) {
			String label = edamclass.toStringID().substring(edamclass.toStringID().indexOf('#') + 1);
			label = label.replace(" ", "_");
			return label;
		} else {
			return edamclass.toString();
		}
	}

	/**
	 * small helper method that returns the (first) index of a string in a string
	 * array
	 * 
	 * @param array
	 * @param s
	 * @return
	 */
	private static int getIndexOf(String[] array, String s) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}
}
