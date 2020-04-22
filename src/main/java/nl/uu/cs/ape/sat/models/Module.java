package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code Module} class represents concrete modules/tools that can be used
 * in our program. It must be annotated using the proper Input/Output pair
 * provided by the tool annotation file.
 * 
 * @author Vedran Kasalica
 *
 */
public class Module extends AbstractModule {

	/** List of input types required by the tool. */
	private List<DataInstance> moduleInput;
	/** List of output types generated by the tool. */
	private List<DataInstance> moduleOutput;
	/** Tool execution engine - TODO */
	private ModuleExecution moduleExecution;

	/**
	 * Constructs a new Module with already defined lists of input and output types.
	 * 
	 * @param moduleName   - Module name
	 * @param moduleID     - Module ID
	 * @param rootNode     - ID of the Taxonomy Root associated with the Module
	 * @param moduleInput  - list of the INPUT types/formats
	 * @param moduleOutput - list of the OUTPUT types/formats
	 */
	public Module(String moduleName, String moduleID, String rootNode, List<DataInstance> moduleInput,
			List<DataInstance> moduleOutput, ModuleExecution moduleExecution) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<DataInstance>(moduleInput);
		this.moduleOutput = new ArrayList<DataInstance>(moduleOutput);
		this.moduleExecution = moduleExecution;
	}

	/**
	 * Constructs a new Module with empty lists of input and output types/formats.
	 * 
	 * @param moduleName - module name
	 * @param moduleID   - unique module identifier
	 * @param rootNode   - ID of the Taxonomy Root node corresponding to the Module.
	 */
	public Module(String moduleName, String moduleID, String rootNode, ModuleExecution moduleExecution) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<DataInstance>();
		this.moduleOutput = new ArrayList<DataInstance>();
		this.moduleExecution = moduleExecution;
	}

	/**
	 * Constructor used to override an existing AbstractModule with a new Module.
	 * 
	 * @param module         - New created Module.
	 * @param abstractModule - Existing AbstractModule that is to be extended with
	 *                       all the fields from the Module.
	 */
	public Module(Module module, TaxonomyPredicate abstractModule) {
		super(abstractModule, NodeType.LEAF);
		this.moduleInput = module.getModuleInput();
		this.moduleOutput = module.getModuleOutput();
		this.moduleExecution = module.getModuleExecution();
	}

	/**
	 * Returns the list (possibly empty) of required input types for the module.
	 * Returns null in the case of abstract classes, as they do not have input
	 * types.
	 * 
	 * @return List of input types (tool modules) or null (non-tool/abstract
	 *         modules)
	 */
	@Override
	public List<DataInstance> getModuleInput() {
		return moduleInput;
	}

	public void setModuleInput(List<DataInstance> moduleInputs) {
		this.moduleInput = moduleInputs;
	}

	/**
	 * Appends the specified element to the end of the input list (optional
	 * operation).
	 * 
	 * @param moduleInput - element to be appended to this list
	 */
	public void addModuleInput(DataInstance moduleInput) {
		this.moduleInput.add(moduleInput);
	}

	/**
	 * Returns the list (possibly empty) of required output types for the module.
	 * Returns null in the case of abstract classes, as they do not have output
	 * types.
	 * 
	 * @return List of output types (tool modules) or null (non-tool/abstract
	 *         modules)
	 */
	@Override
	public List<DataInstance> getModuleOutput() {
		return moduleOutput;
	}

	public void setModuleOutput(List<DataInstance> moduleOutput) {
		this.moduleOutput = moduleOutput;
	}

	/**
	 * Appends the specified element to the end of the output list (optional
	 * operation).
	 * 
	 * @param moduleInput - element to be appended to this list
	 */
	public void addModuleOutput(DataInstance moduleOutput) {
		this.moduleOutput.add(moduleOutput);
	}

	/**
	 * Return the object that implements the execution of the Module. {@code null}
	 * in case of it not having an implementation.
	 * 
	 * @return {@code ModuleExecution} object or {@code null} if the module does
	 *         not have an implementation.
	 */
	public ModuleExecution getModuleExecution() {
		return this.moduleExecution;
	}

	static int id = 1;


	/**
	 * Creates and returns a module from a tool annotation instance from a Json file.
	 * 
	 * @param jsonModule - JSON representation of a module
	 * @param domainSetup - domain information, including all the existing tools and types
	 * @return New Module object.
	 */
	public static Module moduleFromJson(JSONObject jsonModule, APEDomainSetup domainSetup)
			throws JSONException {
		String ontologyPrefixURI = domainSetup.getOntologyPrefixURI();
		AllModules allModules = domainSetup.getAllModules();
		String moduleURI = APEUtils.createClassURI(jsonModule.getString(APEConfig.getJsonTags("id")), ontologyPrefixURI);
		if(allModules.get(moduleURI) != null) {
			moduleURI = moduleURI + "[tool]";
		}
		String moduleLabel = jsonModule.getString(APEConfig.getJsonTags("label"));
		Set<String> taxonomyModules = new HashSet<String>(APEUtils.getListFromJson(jsonModule, APEConfig.getJsonTags("taxonomyOperations"), String.class));
		taxonomyModules = APEUtils.createURIsFromLabels(taxonomyModules, ontologyPrefixURI);
		/** Check if the referenced module taxonomy classes exist. */
		List<String> toRemove = new ArrayList<String>();
		for(String taxonomyModule : taxonomyModules) {
			String taxonomyModuleURI =  APEUtils.createClassURI(taxonomyModule, ontologyPrefixURI);
			if(allModules.get(taxonomyModuleURI) == null) {
				System.err.println("Tool '" + moduleURI + "' annotation issue. "
						+ "Referenced '"+APEConfig.getJsonTags("taxonomyOperations")+"': '" + taxonomyModuleURI + "' cannot be found in the Tool Taxonomy.");
				toRemove.add(taxonomyModuleURI);
			}
		}
		taxonomyModules.removeAll(toRemove);
		
		/* If the taxonomy terms were not properly specified the tool taxonomy root is used as superclass of the tool. */
		if(taxonomyModules.isEmpty()) {
				System.err.println("Tool '" + moduleURI + "' annotation issue. "
						+ "None of the referenced '"+APEConfig.getJsonTags("taxonomyOperations")+"' can be found in the Tool Taxonomy.");
				taxonomyModules.add(allModules.getRootID());
		}
		
		String executionCode = null;
		try {
			executionCode = jsonModule.getJSONObject(APEConfig.getJsonTags("implementation"))
					.getString(APEConfig.getJsonTags("code"));
		} catch (JSONException e) {
			/* Skip the execution code */}
		
		List<JSONObject> jsonModuleInput = APEUtils.getListFromJson(jsonModule, APEConfig.getJsonTags("inputs"),
				JSONObject.class);
		List<JSONObject> jsonModuleOutput = APEUtils.getListFromJson(jsonModule, APEConfig.getJsonTags("outputs"),
				JSONObject.class);

		List<DataInstance> inputs = new ArrayList<DataInstance>();
		List<DataInstance> outputs = new ArrayList<DataInstance>();

		/* For each input and output, allocate the corresponding abstract types. */
		for (JSONObject jsonInput : jsonModuleInput) {
			if (!jsonInput.isEmpty()) {
				inputs.add(createInstance(domainSetup, jsonInput));
			}
		}
		for (JSONObject jsonOutput : jsonModuleOutput) {
			if (!jsonOutput.isEmpty()) {
				outputs.add(createInstance(domainSetup, jsonOutput));
			}
		}

		ModuleExecution moduleExecutionImpl = null;
		if (executionCode != null && !executionCode.equals("")) {
			moduleExecutionImpl = new ModuleExecutionCode(executionCode);
		}

		/*
		 * Add the module and make it sub module of the currSuperModule (if it was not
		 * previously defined)
		 */
		Module currModule =  (Module) allModules.addPredicate(new Module(moduleLabel, moduleURI, allModules.getRootID(), moduleExecutionImpl));
		
		/*	For each supermodule add the current module as a subset and vice versa. */
		for(String superModuleID : taxonomyModules) {
			AbstractModule superModule = allModules.get(superModuleID);
			if(superModule != null) {
				superModule.addSubPredicate(currModule);
				currModule.addSuperPredicate(superModule);
			}
		}
		
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);
		currModule.setAsRelevantTaxonomyTerm(allModules);
		
		return currModule;
	}
	
	/** 
	 * Helper function used to generate a data instance from a json annotation
	  * @param domainSetup - domain information, including all the existing tools and types
	 * @param jsonDataInstance - json encoding of the data instance
	 * @return
	 */
	private static DataInstance createInstance(APEDomainSetup domainSetup, JSONObject jsonDataInstance) {
		DataInstance dataInstance = new DataInstance();
		for (String typeSuperClassLabel : jsonDataInstance.keySet()) {
			String typeSuperClassURI =  APEUtils.createClassURI(typeSuperClassLabel, domainSetup.getOntologyPrefixURI());
			/* Logical connective that determines the semantics of the list notion, i.e. whether all the types in the list have to be satisfied or at least one of them. */
			LogicOperation logConn = LogicOperation.AND;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<TaxonomyPredicate>();
			if(typeSuperClassURI.endsWith("$OR$")) {
				logConn = LogicOperation.OR;
			} else if(typeSuperClassURI.endsWith("$AND$")) {
				logConn = LogicOperation.AND;
			} 
			for (String currTypeLabel : APEUtils.getListFromJson(jsonDataInstance, typeSuperClassLabel, String.class)) {
				String currTypeURI = APEUtils.createClassURI(currTypeLabel, domainSetup.getOntologyPrefixURI());
				if(typeSuperClassURI.endsWith("$OR$")) {
					typeSuperClassURI = typeSuperClassURI.replace("$OR$", "");
				} else if(typeSuperClassURI.endsWith("$AND$")) {
					typeSuperClassURI = typeSuperClassURI.replace("$AND$", "");
				}
				if (domainSetup.getAllTypes().get(currTypeURI) == null) {
					System.err.println("Data type \"" + currTypeURI.toString()
							+ "\" used in the tool annotations does not exist in the " + typeSuperClassLabel + " taxonomy. This might influence the validity of the solutions.");
				}
				if (domainSetup.getAllTypes().getDataTaxonomyDimensionIDs().contains(typeSuperClassURI)) {
					Type currType = domainSetup.getAllTypes().addPredicate(new Type(currTypeLabel, currTypeURI, typeSuperClassURI,
							NodeType.UNKNOWN));
					if (currType != null) {
						/* if the type exists, make it relevant from the taxonomy perspective and add it to the outputs */
						currType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
						logConnectedPredicates.add(currType);
					}
				} else {
					throw new JSONException(
							"Error in the tool annotation file. The data subtaxonomy '" + typeSuperClassURI
									+ "' was not defined, but it was used as annotation for input/output type '" + currTypeURI + "'.");
				}
			}
			/* Create a new type, that represents a disjunction/conjunction of the types, that can be used to abstract over each of the tools individually. */
			Type newAbsType = (Type) domainSetup.generateAuxiliaryPredicate(logConnectedPredicates, logConn);
			if(newAbsType != null) {
				newAbsType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
				dataInstance.addType(newAbsType);
			}
		}
		return dataInstance;
	}

	/**
	 * Return a printable String version of the module.
	 * 
	 * @return module as printable String
	 */
	@Override
	public String toString() {
		StringBuilder inputs = new StringBuilder();
		StringBuilder outputs = new StringBuilder();

		for (DataInstance types : moduleInput) {
			inputs = inputs.append("{ ");
			for (Type type : types.getTypes()) {
				inputs = inputs.append("'").append(type.getPredicateLabel()).append("' ");
			}
			inputs = inputs.append("} ");
		}
		for (DataInstance types : moduleOutput) {
			outputs = outputs.append("{ ");
			for (Type type : types.getTypes()) {
				outputs = outputs.append("'").append(type.getPredicateLabel()).append("' ");
			}
			outputs = outputs.append("} ");
		}

		return "______________________________________\n|" + super.toString() + ",\n|IN:\t" + inputs + ",\n|OUT\t"
				+ outputs + "\n|______________________________________";
	}

	/**
	 * Print the ID of the Module with a label [T] in the end (denoting Tool)
	 * 
	 * @return Module ID
	 */
	@Override
	public String toShortString() {
		return super.toShortString(); //+ "[T]";
	}

	@Override
	public String getType() {
		return "module";
	}
}
