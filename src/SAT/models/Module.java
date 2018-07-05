package SAT.models;

import java.util.ArrayList;
import java.util.List;

public class Module extends AbstractModule {

	private List<Type> moduleInput;
	private List<Type> moduleOutput;

	public Module(String moduleName, String moduleID, List<Type> moduleInput, List<Type> moduleOutput) {
		super(moduleName, moduleID);
		this.moduleInput = new ArrayList<Type>(moduleInput);
		this.moduleOutput = new ArrayList<Type>(moduleOutput);
	}

	public List<Type> getModuleInput() {
		return moduleInput;
	}

	public void setModuleInput(List<Type> moduleInput) {
		this.moduleInput = moduleInput;
	}

	public List<Type> getModuleOutput() {
		return moduleOutput;
	}

	public void setModuleOutput(List<Type> moduleOutput) {
		this.moduleOutput = moduleOutput;
	}
	
	public static AbstractModule generateModule(String moduleName, String moduleID){
//			TODO The class should use already generated method in the super class in order
//				to check weather a new item is required to be generated or there is an existing
//				one in the memory.
		return null;
			
	}

	public static Module moduleFromString(String[] stringModule) {

		String moduleName = stringModule[0];
		String moduleID = stringModule[1];
		String[] stringModuleInputTypes = stringModule[2].split("#");
		String[] stringModuleOutputTypes = stringModule[3].split("#");

		List<Type> inputs = new ArrayList<Type>();
		List<Type> outputs = new ArrayList<Type>();

		for (String input : stringModuleInputTypes) {
			if(!input.matches("")){
				inputs.add(Type.generateType(input, input));
			}
		}

		for (String output : stringModuleOutputTypes) {
			if(!output.matches("")){
				outputs.add(Type.generateType(output, output));
			}
		}

		return new Module(moduleName, moduleID, inputs, outputs);
	}

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
	
	@Override
	public String getType() {
		return "module";
	}

	@Override
	 public boolean equals(Object obj) {
		Module other=(Module) obj;
	   return this.getModuleID().matches(other.getModuleID());
	 }

	 @Override
	 public int hashCode() {
	    return getModuleID().hashCode();
	 }
	 
}
