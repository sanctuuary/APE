package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;

/**
 * The {@code Module} class represents concrete modules/tools that can be used in our program. It must be annotated using the proper Input/Output pair provided by the 
 * tool annotation file.
 * 
 * @author Vedran Kasalica
 *
 */
public class Module extends AbstractModule {

	private List<Types> moduleInput;
	private List<Types> moduleOutput;
	private Module_Execution moduleExecution;

	/**
	 * Constructs a new Module with already defined lists of input and output types.
	 * @param moduleName 	- Module name
	 * @param moduleID		- Module ID
	 * @param rootNode		- ID of the Taxonomy Root associated with the Module
	 * @param moduleInput	- list of the INPUT types/formats
	 * @param moduleOutput	- list of the OUTPUT types/formats
	 */
	public Module(String moduleName, String moduleID, String rootNode, List<Types> moduleInput, List<Types> moduleOutput, Module_Execution moduleExecution) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<Types>(moduleInput);
		this.moduleOutput = new ArrayList<Types>(moduleOutput);
		this.moduleExecution = moduleExecution;
	}

	/**
	 * Constructs a new Module with empty lists of input and output types/formats.
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param rootNode
	 *            - ID of the Taxonomy Root node corresponding to the Module.
	 */
	public Module(String moduleName, String moduleID, String rootNode, Module_Execution moduleExecution) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<Types>();
		this.moduleOutput = new ArrayList<Types>();
		this.moduleExecution = moduleExecution;
	}

	/**
	 * Constructor used to override an existing AbstractModule with a new Module. 
	 * 
	 * @param module - New created Module.
	 * @param abstractModule - Existing AbstractModule that is to be extended with all the fields from the Module.
	 */
	public Module(Module module, AbstractModule abstractModule) {
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
	public List<Types> getModuleInput() {
		return moduleInput;
	}

	public void setModuleInput(List<Types> moduleInputs) {
		this.moduleInput = moduleInputs;
	}

	/**
	 * Appends the specified element to the end of the input list (optional
	 * operation).
	 * 
	 * @param moduleInput
	 *            - element to be appended to this list
	 */
	public void addModuleInput(Types moduleInput) {
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
	public List<Types> getModuleOutput() {
		return moduleOutput;
	}

	public void setModuleOutput(List<Types> moduleOutput) {
		this.moduleOutput = moduleOutput;
	}

	/**
	 * Appends the specified element to the end of the output list (optional
	 * operation).
	 * 
	 * @param moduleInput
	 *            - element to be appended to this list
	 */
	public void addModuleOutput(Types moduleOutput) {
		this.moduleOutput.add(moduleOutput);
	}
	
	/**
	 * Return the object that implements the execution of the Module. {@code null} in case of it not having an implementation.
	 * @return {@code Module_Execution} object or {@code null} if the module does not have an implementation.
	 */
	public Module_Execution getModuleExecution() {
		return this.moduleExecution;
	}

	/**
	 * Creates a module from a row provided by the tool annotations CSV.
	 * @param stringModule
	 * @param allModules
	 * @param allTypes
	 * @return
	 */
	public static Module moduleFromXML(Node xmlModule, AllModules allModules, AllTypes allTypes) {

		String superModuleID = xmlModule.selectSingleNode("operation").getText();
		String moduleName = xmlModule.valueOf("@name");
		String moduleID = xmlModule.valueOf("@name");
		String executionCode = xmlModule.selectSingleNode("implementation").selectSingleNode("code").getText();
		
//		BIO tools 
//		String moduleName = xmlModule.selectSingleNode("displayName").getText();
//		String moduleID = xmlModule.selectSingleNode("displayName").getText();
		List<Node> xmlModuleInput = xmlModule.selectNodes("inputs/input");
		List<Node> xmlModuleOutput = xmlModule.selectNodes("outputs/output");

		List<Types> inputs = new ArrayList<Types>();
		List<Types> outputs = new ArrayList<Types>();

		for (Node xmlInput : xmlModuleInput) {
			if (xmlInput.hasContent()) {
				Types input = new Types();
				for(Node xmlType : xmlInput.selectNodes("*")) {
					input.addType(Type.generateType(xmlType.getText(), xmlType.getText(), APEConfig.getConfig().getTYPE_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes, allTypes.getRootType()));
				}
				inputs.add(input);
			}
		}

		for (Node xmlOutput : xmlModuleOutput) {
			if (xmlOutput.hasContent()) {
				Types output = new Types();
				for(Node xmlType : xmlOutput.selectNodes("*")) {
					output.addType(Type.generateType(xmlType.getText(), xmlType.getText(), APEConfig.getConfig().getTYPE_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes, allTypes.getRootType()));
				}
				outputs.add(output);
			}
		}
		
		Module_Execution moduleExecutionImpl = null;
		if(executionCode != null && !executionCode.matches("")) {
			moduleExecutionImpl = new Module_Execution_Code(executionCode);
		} 

		AbstractModule currSuperModule;
		/*
		 * In case of the tool being instance of the abstract superModule, add it as a sub module (enrich the ontology).
		 * Depending of the superModuleID currSuperModule is an AbstractModule or the Module Taxonomy root.
		 */
		if (!superModuleID.matches(moduleID)) {
			AbstractModule superModule = AbstractModule.generateModule(superModuleID, superModuleID, APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), NodeType.ABSTRACT, allModules, allModules.getRootModule());
			/*
			 *  If the super module is represented as a tool, convert it to abstract module
			 */
			if (superModule instanceof Module) {
				AbstractModule newSuperModule = new AbstractModule(superModule, NodeType.ABSTRACT);
				allModules.swapAbstractModule2Module(newSuperModule, superModule);
				currSuperModule = newSuperModule;
			} else {
				superModule.setNodeType(NodeType.ABSTRACT);
				currSuperModule = superModule;
			}
		} else {
			currSuperModule = allModules.getRootModule();
		}

		/*
		 * Add the module and make it sub module of the currSuperModule (if it was not previously defined)
		 */
		Module currModule = Module.generateModule(moduleName, moduleID, APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), allModules, currSuperModule, moduleExecutionImpl);
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);
		
		return currModule;
	}

	/**
	 * Return a printable String version of the module.
	 * 
	 * @return module as printable String
	 */
	@Override
	public String print() {
		String inputs = "";
		String outputs = "";

		for (Types types : moduleInput) {
			inputs += "{";
			for(Type type: types.getTypes()) {
			inputs += type.getTypeName() + "_";
			}
			inputs += "}";
		}
		for (Types types : moduleOutput) {
			outputs += "{";
			for(Type type: types.getTypes()) {
				outputs += type.getTypeName() + "_";
			}
			outputs += "}";
		}

		return "\n________________________\n|" + super.print() + ",\n|" + inputs + ",\n|" + outputs
				+ "\n|________________________|";
	}

	/**
	 * Print the ID of the Module with a label [T] in the end (denoting Tool)
	 * 
	 * @return Module ID
	 */
	@Override
	public String printShort() {
		return super.printShort() + "[T]";
	}

	@Override
	public String getType() {
		return "module";
	}

	@Override
	public boolean equals(Object obj) {
		Module other = (Module) obj;
		return this.getModuleID().matches(other.getModuleID());
	}

	@Override
	public int hashCode() {
		return getModuleID().hashCode();
	}

	/**
	 * The class is used to check weather the Module with @moduleID was already
	 * introduced earlier on in @allModules. In case it was defined as
	 * {@literalModule} it returns the item, in case of it being introduced as an
	 * AbstractModule, it is extended to a Module and returned, otherwise the new
	 * element is generated and returned.
	 * <br>
	 * <br>
	 * In case of generating a new Module, the object is added to the set of all the Modules and added as a subModule to the parent Module.
	 * 
	 * @param moduleName
	 *            - Module name.
	 * @param moduleID
	 *            - Unique module identifier.
	 * @param rootNode
	 *            - ID of the Taxonomy Root node corresponding to the Module.
	 * @param allModules
	 *            - Set of all the modules created so far.
	 * @return The Module representing the item.
	 */
	public static Module generateModule(String moduleName, String moduleID, String rootNode, AllModules allModules, AbstractModule superModule, Module_Execution moduleExecution) {

		AbstractModule currModule = allModules.addModule(new Module(moduleName, moduleID, rootNode, moduleExecution));
		if(superModule != null) {
			superModule.addSubModule(moduleID);
		}
		return (Module) currModule;
	}

}
