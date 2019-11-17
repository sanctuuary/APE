package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;
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
	private Module_Execution moduleExecution;

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
			List<DataInstance> moduleOutput, Module_Execution moduleExecution) {
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
	public Module(String moduleName, String moduleID, String rootNode, Module_Execution moduleExecution) {
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

	public int hashCode() {
		return super.hashCode();
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
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
	 * @return {@code Module_Execution} object or {@code null} if the module does
	 *         not have an implementation.
	 */
	public Module_Execution getModuleExecution() {
		return this.moduleExecution;
	}

	static int id = 1;


	/**
	 * Creates and returns a module from a tool annotation instance from a Json file.
	 * 
	 * @param jsonModule - JSON representation of a module
	 * @param allModules - list of all the modules
	 * @param allTypes   - list of all the types
	 * @return New Module object.
	 */
	public static Module moduleFromJson(JSONObject jsonModule, AllModules allModules, AllTypes allTypes)
			throws JSONException {

		String moduleID = jsonModule.getString(APEConfig.getJsonTags("id"));
		String moduleLabel = jsonModule.getString(APEConfig.getJsonTags("label"));
		Set<String> taxonomyModules = new HashSet<String>(APEUtils.getListFromJson(jsonModule, APEConfig.getJsonTags("taxonomyTerms"), String.class));
		
		/** Check if the referenced module taxonomy classes exist. */
		List<String> toRemove = new ArrayList<String>();
		for(String taxonomyModule : taxonomyModules) {
			if(allModules.get(taxonomyModule) == null) {
				toRemove.add(taxonomyModule);
			}
		}
		taxonomyModules.removeAll(toRemove);
		
		/* If the taxonomy terms were not properly specified the tool taxonomy root is used as superclass of the tool. */
		if(taxonomyModules.isEmpty() && (allModules.get(moduleID) == null)) {
				System.err.println("Annotated tool \"" + moduleID
						+ "\" cannot be found in the Tool Taxonomy. It is added as a direct subclass of the root in the Tool Taxonomy.");
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

		for (JSONObject jsonInput : jsonModuleInput) {
			if (!jsonInput.isEmpty()) {
				DataInstance input = new DataInstance();
				for (String typeSubntology : jsonInput.keySet()) {
					for (String currTypeID : APEUtils.getListFromJson(jsonInput, typeSubntology, String.class)) {
						if (allTypes.get(currTypeID) == null) {
							System.err.println("Data format \"" + currTypeID
									+ "\" used in the tool annotations does not exist in the data taxonomy. This might influence the validity of the solutions.");
						}
						if (allTypes.getDataTaxonomyDimensions().contains(typeSubntology)) {
							Type currType = allTypes.addType(currTypeID, currTypeID, typeSubntology, NodeType.UNKNOWN);
							if (currType != null) {
								/* if the type exists, make it relevant from the taxonomy perspective and add it to the inputs */
								currType.setAsRelevantTaxonomyTerm(allTypes);
								input.addType(currType);
							}
						} else {
							new JSONException(
									"Error in the tool annotation file . The data subtaxonomy '" + typeSubntology
											+ "' was not defined, but it was used as annotation for input type '" + currTypeID + "'.");
						}
					}
				}
				inputs.add(input);
			}
		}

		for (JSONObject jsonOutput : jsonModuleOutput) {
			if (!jsonOutput.isEmpty()) {
				DataInstance output = new DataInstance();
				for (String typeSubntology : jsonOutput.keySet()) {
					for (String currTypeID : APEUtils.getListFromJson(jsonOutput, typeSubntology, String.class)) {
						if (allTypes.get(currTypeID) == null) {
							System.err.println("Data format \"" + currTypeID.toString()
									+ "\" used in the tool annotations does not exist in the data taxonomy. This might influence the validity of the solutions.");
						}
						if (allTypes.getDataTaxonomyDimensions().contains(typeSubntology)) {
							Type currType = allTypes.addType(currTypeID, currTypeID, typeSubntology,
									NodeType.UNKNOWN);
							if (currType != null) {
								/* if the type exists, make it relevant from the taxonomy perspective and add it to the outputs */
								currType.setAsRelevantTaxonomyTerm(allTypes);
								output.addType(currType);
							}
						} else {
							new JSONException(
									"Error in the tool annotation file . The data subtaxonomy '" + typeSubntology
											+ "' was not defined, but it was used as annotation for output type '" + currTypeID + "'.");
						}
					}
				}
				outputs.add(output);
			}
		}

		Module_Execution moduleExecutionImpl = null;
		if (executionCode != null && !executionCode.equals("")) {
			moduleExecutionImpl = new Module_Execution_Code(executionCode);
		}

		/*
		 * Add the module and make it sub module of the currSuperModule (if it was not
		 * previously defined)
		 */
		Module currModule =  (Module) allModules.addModule(new Module(moduleLabel, moduleID, allModules.getRootID(), moduleExecutionImpl));
		
		/*	For each supermodule add the current module as a subset and vice versa. */
		for(String superModuleID : taxonomyModules) {
			AbstractModule superModule = allModules.get(superModuleID);
			if(superModule != null) {
				superModule.addSubPredicate(moduleID);
				currModule.addSuperPredicate(superModuleID);
			}
		}
		
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);
		currModule.setAsRelevantTaxonomyTerm(allModules);
		
		return currModule;
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
		return super.toShortString() + "[T]";
	}

	@Override
	public String getType() {
		return "module";
	}
}
