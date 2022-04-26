package nl.uu.cs.ape.models;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.NodeType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEDimensionsException;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

import java.util.*;

/**
 * The {@code Module} class represents concrete modules/tools that can be used
 * in our program. It must be annotated using the proper Input/Output pair
 * provided by the tool annotation file.
 *
 * @author Vedran Kasalica
 */
public class Module extends AbstractModule {

    /**
     * List of input types required by the tool.
     */
    private List<Type> moduleInput;

    /**
     * List of output types generated by the tool.
     */
    private List<Type> moduleOutput;

    /**
     * Tool execution command.
     */
    private String executionCommand;

    /**
     * CWL inputs.
     * Optional because CWL annotations are not required.
     */
    private ArrayList<LinkedHashMap<String, String>> cwlInputs;
    /**
     * CWL implementation.
     * Optional because CWL annotations are not required, and the implementation in CWL annotations is not required either.
     */
    private Map<String, Object> cwlImplementation;

    /**
     * Constructs a new Module with already defined lists of input and output types.
     *
     * @param moduleName      Name of the module.
     * @param moduleID        ID of the module.
     * @param rootNode        ID of the Taxonomy Root associated with the Module.
     * @param moduleInput     List of the INPUT types/formats.
     * @param moduleOutput    List of the OUTPUT types/formats.
     * @param moduleExecution Command that is used to execute the tool from the command line.
     */
    public Module(String moduleName, String moduleID, String rootNode, List<Type> moduleInput,
                  List<Type> moduleOutput, String moduleExecution) {
        super(moduleName, moduleID, rootNode, NodeType.LEAF);
        this.moduleInput = new ArrayList<Type>(moduleInput);
        this.moduleOutput = new ArrayList<Type>(moduleOutput);
        this.executionCommand = moduleExecution;
    }

    /**
     * Constructs a new Module with empty lists of input and output types/formats.
     *
     * @param moduleName      Name of the module.
     * @param moduleID        Unique module identifier.
     * @param rootNode        ID of the Taxonomy Root node corresponding to the Module.
     * @param moduleExecution Command that is used to execute the tool from the command line.
     */
    public Module(String moduleName, String moduleID, String rootNode, String moduleExecution) {
        super(moduleName, moduleID, rootNode, NodeType.LEAF);
        this.moduleInput = new ArrayList<Type>();
        this.moduleOutput = new ArrayList<Type>();
        this.executionCommand = moduleExecution;
    }

    /**
     * Constructor used to override an existing AbstractModule with a new Module.
     *
     * @param module         New created Module.
     * @param abstractModule Existing AbstractModule that is to be extended with all the fields from the Module.
     */
    public Module(Module module, TaxonomyPredicate abstractModule) {
        super(abstractModule, NodeType.LEAF);
        this.moduleInput = module.getModuleInput();
        this.moduleOutput = module.getModuleOutput();
        this.executionCommand = module.getExecutionCode();
    }

    /**
     * Returns the list (possibly empty) of required input types for the module.
     * Returns null in the case of abstract classes, as they do not have input types.
     *
     * @return List of input types (tool modules) or null (non-tool/abstract modules).
     */
    @Override
    public List<Type> getModuleInput() {
        return moduleInput;
    }

    /**
     * Sets module input.
     *
     * @param moduleInputs the module inputs
     */
    public void setModuleInput(List<Type> moduleInputs) {
        this.moduleInput = moduleInputs;
    }

    /**
     * Appends the specified element to the end of the input list (optional operation).
     *
     * @param moduleInput Element to be appended to this list.
     */
    public void addModuleInput(Type moduleInput) {
        this.moduleInput.add(moduleInput);
    }

    /**
     * Returns the list (possibly empty) of required output types for the module.
     * Returns null in the case of abstract classes, as they do not have output types.
     *
     * @return List of output types (tool modules) or null (non-tool/abstract modules).
     */
    @Override
    public List<Type> getModuleOutput() {
        return moduleOutput;
    }

    /**
     * Sets module output.
     *
     * @param moduleOutput the module output
     */
    public void setModuleOutput(List<Type> moduleOutput) {
        this.moduleOutput = moduleOutput;
    }

    /**
     * Appends the specified element to the end of the output list (optional operation).
     *
     * @param moduleOutput Element to be appended to this list.
     */
    public void addModuleOutput(Type moduleOutput) {
        this.moduleOutput.add(moduleOutput);
    }

    /**
     * Return the object that implements the execution of the Module.
     * null in case of it not having an implementation.
     *
     * @return {@code ModuleExecution} object or null if the module does not have an implementation.
     */
    public String getExecutionCode() {
        return this.executionCommand;
    }

    /**
     * Return the CWL inputs.
     * Empty when the CWL annotations were not provided (or not yet set).
     *
     * @return An ArrayList containing the CWL inputs types.
     */
    public ArrayList<LinkedHashMap<String, String>> getCwlInputs() {
        return this.cwlInputs;
    }

    /**
     * Set the CWL inputs.
     * @param cwlInputs The inputs to set.
     */
    public void setCwlInputs(ArrayList<LinkedHashMap<String, String>> cwlInputs) {
        this.cwlInputs = cwlInputs;
    }

    /**
     * Return the CWL implementation.
     * Empty when the CWL annotations were not provided (or not yet set), or the implementation for this module does not exist.
     *
     * @return A map representing the implementation.
     */
    public Map<String, Object> getCwlImplementation() {
        return cwlImplementation;
    }

    /**
     * Set the CWL implementation.
     * @param cwlImplementation The implementation to set.
     */
    public void setCwlImplementation(Map<String, Object> cwlImplementation) {
        this.cwlImplementation = cwlImplementation;
    }

    /**
	 * Generate a taxonomy tool instance that is referenced in the json.
	 * 
	 * @param jsonParam
	 * @param domainSetup
	 * @return A AbstractModule object that represent the data instance given as the parameter.
	 * @throws JSONException if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced modules are not well defined
	 */
    public static AbstractModule taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup)
			throws JSONException {
		/* Set of predicates where each describes a type dimension */
		SortedSet<TaxonomyPredicate> parameterDimensions = new TreeSet<TaxonomyPredicate>();
		AllModules allModules = domainSetup.getAllModules();
		/* Iterate through each of the dimensions */
		for (String currRootLabel : jsonParam.keySet()) {
			String curRootURI = currRootLabel;
			if(!allModules.existsRoot(curRootURI)) {
				curRootURI = APEUtils.createClassURI(currRootLabel, domainSetup.getOntologyPrefixIRI());
			}
			if(!allModules.existsRoot(curRootURI)) {
				throw APEDimensionsException.notExistingDimension("Data type was defined over a non existing data dimension: '" + curRootURI + "', in JSON: '" + jsonParam + "'");
			}
			LogicOperation logConn = LogicOperation.OR;
			SortedSet<TaxonomyPredicate> logConnectedPredicates = new TreeSet<TaxonomyPredicate>();
			/* for each dimensions a disjoint array of types/tools is given */
			for (String currModuleLabel : APEUtils.getListFromJson(jsonParam, currRootLabel, String.class)) {
				String currModuleURI = APEUtils.createClassURI(currModuleLabel, domainSetup.getOntologyPrefixIRI());
				
				AbstractModule currModule = allModules.get(currModuleURI);
				if (currModule == null) {
					currModule = allModules.get(currModuleLabel);
				}
				
				if (currModule != null) {
					/*
					 * if the type exists, make it relevant from the taxonomy perspective and add it
					 * to the outputs
					 */
					currModule.setAsRelevantTaxonomyTerm(allModules);
					logConnectedPredicates.add(currModule);
				} else {
					throw APEDimensionsException.dimensionDoesNotContainClass(String.format("Error in a JSON input. The tool '%s' was not defined or does not belong to the tool dimension '%s'.", currModuleURI, curRootURI));
				}
			}

			/*
			 * Create a new type, that represents a disjunction of the types, that can be
			 * used to abstract over each of the types individually and represents specificaion over one dimension.
			 */
			AbstractModule abstractDimensionType = AuxModulePredicate.generateAuxiliaryPredicate(logConnectedPredicates, logConn,domainSetup);
			if (abstractDimensionType != null) {
	            parameterDimensions.add(abstractDimensionType);
			}

		}
		AbstractModule taxonomyInstance = AuxModulePredicate.generateAuxiliaryPredicate(parameterDimensions, LogicOperation.AND, domainSetup);

		return taxonomyInstance;
	}
    

    /**
     * Return a printable String version of the module.
     *
     * @return Module as printable String.
     */
    @Override
    public String toString() {
        StringBuilder inputs = new StringBuilder();
        StringBuilder outputs = new StringBuilder();

        for (Type inType : moduleInput) {
        	inputs.append("{").append(inType.toShortString()).append("} ");
        }
        for (Type outType : moduleOutput) {
            outputs.append("{").append(outType.toShortString()).append("} ");
        }

        return "______________________________________\n|" 
        		+ super.toString() + 
        		",\n|IN:\t" + inputs + 
        		",\n|OUT\t" + outputs + 
        		"\n|______________________________________";
    }

    /**
     * Print the ID of the Module with a label [T] in the end (denoting Tool).
     *
     * @return ID of the Module.
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
