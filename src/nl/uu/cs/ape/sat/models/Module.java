package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;

/**
 * The {@code Module} class represents concrete modules/tools that can be used in our program.
 * 
 * @author Vedran Kasalica
 *
 */
public class Module extends AbstractModule {

	private List<Types> moduleInput;
	private List<Types> moduleOutput;

	public Module(String moduleName, String moduleID, String rootNode, List<Types> moduleInput, List<Types> moduleOutput) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<Types>(moduleInput);
		this.moduleOutput = new ArrayList<Types>(moduleOutput);
	}

	/**
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 */
	public Module(String moduleName, String moduleID, String rootNode) {
		super(moduleName, moduleID, rootNode, NodeType.LEAF);
		this.moduleInput = new ArrayList<Types>();
		this.moduleOutput = new ArrayList<Types>();
	}

	/**
	 * Constructor used to create the Module base on an AbstractModule
	 * 
	 * @param module
	 * @param abstractModule
	 */
	public Module(Module module, AbstractModule abstractModule) {
		super(abstractModule, NodeType.LEAF);
		this.moduleInput = module.getModuleInput();
		this.moduleOutput = module.getModuleOutput();
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
	 * Creates a module from a row provided by the tool annotations CSV. This annotation allows only a single type/format per input/output instance.
	 * @param stringModule
	 * @param allModules
	 * @param allTypes
	 * @return
	 */
	public static Module moduleFromString(String[] stringModule, AllModules allModules, AllTypes allTypes) {

		String superModuleID = stringModule[0];
		String moduleName = stringModule[1];
		String moduleID = stringModule[1];
		String[] stringModuleInputTypes = stringModule[2].split("#");
		String[] stringModuleOutputTypes = stringModule[3].split("#");

		List<Types> inputs = new ArrayList<Types>();
		List<Types> outputs = new ArrayList<Types>();

		for (String input : stringModuleInputTypes) {
			if (!input.matches("")) {
				Types singleInput = new Types();
				singleInput.addType(Type.generateType(input, input, APEConfig.getConfig().getDATA_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes));
				inputs.add(singleInput);
			}
		}

		for (String output : stringModuleOutputTypes) {
			if (!output.matches("")) {
				Types singleOutput = new Types();
				singleOutput.addType(Type.generateType(output, output, APEConfig.getConfig().getDATA_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes));
				outputs.add(singleOutput);
			}
		}

		
		Module currModule = Module.generateModule(moduleName, moduleID,APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), allModules);
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);

		// in case of the tool being instance of the superModule, add it as a sub module (enrich the ontology)
		if (!superModuleID.matches(moduleID)) {
			AbstractModule superModule = AbstractModule.generateModule(superModuleID, superModuleID, APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), NodeType.ABSTRACT, allModules);
			// if the super module is represented as a tool, convert it to abstract module
			if (superModule instanceof Module) {
				AbstractModule newSuperModule = new AbstractModule(superModule, NodeType.ABSTRACT);
				newSuperModule.addSubModule(currModule);
				allModules.swapAbstractModule2Module(newSuperModule, superModule);
			} else {
				superModule.setNodeType(NodeType.ABSTRACT);
				superModule.addSubModule(currModule);
			}
		}

		return currModule;
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
		String moduleName = xmlModule.selectSingleNode("displayName").getText();
		String moduleID = xmlModule.selectSingleNode("displayName").getText();
		List<Node> xmlModuleInput = xmlModule.selectNodes("inputs/input");
		List<Node> xmlModuleOutput = xmlModule.selectNodes("outputs/output");

		List<Types> inputs = new ArrayList<Types>();
		List<Types> outputs = new ArrayList<Types>();

		for (Node xmlInput : xmlModuleInput) {
			if (xmlInput.hasContent()) {
				Types input = new Types();
				for(Node xmlType : xmlInput.selectNodes("*")) {
					input.addType(Type.generateType(xmlType.getText(), xmlType.getText(), APEConfig.getConfig().getDATA_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes));
				}
				inputs.add(input);
			}
		}

		for (Node xmlOutput : xmlModuleOutput) {
			if (xmlOutput.hasContent()) {
				Types output = new Types();
				for(Node xmlType : xmlOutput.selectNodes("*")) {
					output.addType(Type.generateType(xmlType.getText(), xmlType.getText(), APEConfig.getConfig().getDATA_TAXONOMY_ROOT(), NodeType.UNKNOWN, allTypes));
				}
				outputs.add(output);
			}
		}

		
		Module currModule = Module.generateModule(moduleName, moduleID, APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), allModules);
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);

		// in case of the tool being instance of the superModule, add it as a sub module (enrich the ontology)
		if (!superModuleID.matches(moduleID)) {
			AbstractModule superModule = AbstractModule.generateModule(superModuleID, superModuleID, APEConfig.getConfig().getMODULE_TAXONOMY_ROOT(), NodeType.ABSTRACT, allModules);
			// if the super module is represented as a tool, convert it to abstract module
			if (superModule instanceof Module) {
				AbstractModule newSuperModule = new AbstractModule(superModule, NodeType.ABSTRACT);
				newSuperModule.addSubModule(currModule);
				allModules.swapAbstractModule2Module(newSuperModule, superModule);
			} else {
				superModule.setNodeType(NodeType.ABSTRACT);
				superModule.addSubModule(currModule);
			}
		}

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
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param allModules
	 *            - set of all the modules created so far
	 * @return the Module representing the item.
	 */
	public static Module generateModule(String moduleName, String moduleID, String rootNode, AllModules allModules) {

		return (Module) allModules.addModule(new Module(moduleName, moduleID, rootNode));
	}

}
