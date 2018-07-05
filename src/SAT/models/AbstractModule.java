package SAT.models;

import java.util.ArrayList;
import java.util.List;

public class AbstractModule implements Atom{

	protected String moduleName;
	private String moduleID;
//	list of all the modules that are subsumed by the abstract module (null if the module is a tool)
	private List<Module> subModules;
//	determines whether the module is a tool or simply an abstract module
	private boolean isTool;

	/**
	 *  Creates an abstract module from @moduleName and @moduleID. If @isTool is true, module is an actual tool, otherwise it's an abstract/non-tool module.
	 * @param moduleName
	 * @param moduleID
	 * @param isTool
	 */
	public AbstractModule(String moduleName, String moduleID, boolean isTool) {
		super();
		this.moduleName = moduleName;
		this.moduleID = moduleID;
		this.isTool = isTool;
		if(!isTool)
			subModules = new ArrayList<>();
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	/**
	 * True if the module is a tool, false otherwise.
	 * @return true if the (abstract) module represent an actual tool
	 */
	public boolean isTool() {
		return isTool;
	}
	
	/**
	 *	The class is supposed to be used to check weather the module was already introduced earlier
	 *	on. In case it was it should return the item, otherwise the new element needs to
	 *	be generated and returned.
	 * 
	 * @param moduleName
	 * @param moduleID
	 * @return
	 */
	public static AbstractModule generateModule(String moduleName, String moduleID){
//		TODO
//		AbstractModule tmpModule;
//		//check in memory if it exists
////		if((tmpType = collection.exists(moduleID)) == null)
//			tmpModule = new AbstractModule(moduleName, moduleID);
		
		return null;
			
	}
	
	/**
	 * Returns null. Abstract classes do not have input types.
	 * @return null
	 */
	public List<Type> getModuleInput() {
		return null;
	}
	/**
	 * Returns null. Abstract classes do not have output types.
	 * @return null
	 */
	public List<Type> getModuleOutput() {
		return null;
	}
	
	public String print(){
		
		return getModuleID() + ", " + getModuleName();
	}

	@Override
	public String getAtom() {
		return moduleID;
	}

	@Override
	public String getType() {
		return "abstract module";
	}
	
	public void setSubModule(Module module){
		subModules.add(module);
	}
	
	public List<Module> getSubTypes(){
		return subModules;
	}

	@Override
	 public boolean equals(Object obj) {
	   AbstractModule other=(AbstractModule) obj;
	   return this.moduleID.matches(other.moduleID);
	 }

	 @Override
	 public int hashCode() {
	    return moduleID.hashCode();
	 }
	 
}
