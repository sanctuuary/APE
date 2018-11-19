package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;
/**
 * The {@code Module} class represents concrete modules/tools that can be used in our program.
 * 
 * @author Vedran Kasalica
 *
 */
public class Module extends AbstractModule {

	private List<Type> moduleInput;
	private List<Type> moduleOutput;

	public Module(String moduleName, String moduleID, List<Type> moduleInput, List<Type> moduleOutput) {
		super(moduleName, moduleID, true);
		this.moduleInput = new ArrayList<Type>(moduleInput);
		this.moduleOutput = new ArrayList<Type>(moduleOutput);
	}

	/**
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 */
	public Module(String moduleName, String moduleID) {
		super(moduleName, moduleID, true);
		this.moduleInput = new ArrayList<Type>();
		this.moduleOutput = new ArrayList<Type>();
	}

	/**
	 * Constructor used to create the Module base on an AbstractModule
	 * 
	 * @param module
	 * @param abstractModule
	 */
	public Module(Module module, AbstractModule abstractModule) {
		super(abstractModule, true);
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
	public List<Type> getModuleInput() {
		return moduleInput;
	}

	public void setModuleInput(List<Type> moduleInputs) {
		this.moduleInput = moduleInputs;
	}

	/**
	 * Appends the specified element to the end of the input list (optional
	 * operation).
	 * 
	 * @param moduleInput
	 *            - element to be appended to this list
	 */
	public void addModuleInput(Type moduleInput) {
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
	public List<Type> getModuleOutput() {
		return moduleOutput;
	}

	public void setModuleOutput(List<Type> moduleOutput) {
		this.moduleOutput = moduleOutput;
	}

	/**
	 * Appends the specified element to the end of the output list (optional
	 * operation).
	 * 
	 * @param moduleInput
	 *            - element to be appended to this list
	 */
	public void addModuleOutput(Type moduleOutput) {
		this.moduleOutput.add(moduleOutput);
	}

	public static Module moduleFromString(String[] stringModule, AllModules allModules, AllTypes allTypes) {

		String superModuleID = stringModule[0];
		String moduleName = stringModule[1];
		String moduleID = stringModule[1];
		String[] stringModuleInputTypes = stringModule[2].split("#");
		String[] stringModuleOutputTypes = stringModule[3].split("#");

		List<Type> inputs = new ArrayList<Type>();
		List<Type> outputs = new ArrayList<Type>();

		for (String input : stringModuleInputTypes) {
			if (!input.matches("")) {
				inputs.add(Type.generateType(input, input, true, allTypes));
			}
		}

		for (String output : stringModuleOutputTypes) {
			if (!output.matches("")) {
				outputs.add(Type.generateType(output, output, true, allTypes));
			}
		}

		
		Module currModule = Module.generateModule(moduleName, moduleID, allModules);
		currModule.setModuleInput(inputs);
		currModule.setModuleOutput(outputs);

		// in case of the tool being instance of the superModule, add it as a sub module (enrich the ontology)
		if (!superModuleID.matches(moduleID)) {
			AbstractModule superModule = AbstractModule.generateModule(superModuleID, superModuleID, false, allModules);
			// if the super module is represented as a tool, convert it to abstract module
			if (superModule instanceof Module) {
				System.out.println("Module type is: " + superModuleID);
				AbstractModule newSuperModule = new AbstractModule(superModule, false);
				newSuperModule.addSubModule(currModule);
				allModules.swapAbstractModule2Module(newSuperModule, superModule);
			} else {
				superModule.setIsTool(false);
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

		for (Type type : moduleInput)
			inputs += type.getTypeName() + "_";

		for (Type type : moduleOutput)
			outputs += type.getTypeName() + "_";

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
	public static Module generateModule(String moduleName, String moduleID, AllModules allModules) {

		return (Module) allModules.addModule(new Module(moduleName, moduleID));
	}

}
