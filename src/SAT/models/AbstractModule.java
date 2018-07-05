package SAT.models;

import java.util.ArrayList;
import java.util.List;

public class AbstractModule implements Atom{

	protected String moduleName;
	private String moduleID;
	private List<Module> subModules;

	public AbstractModule(String moduleName, String moduleID) {
		super();
		this.moduleName = moduleName;
		this.moduleID = moduleID;
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
	 *	The clas is supposed to be used to check weather the module was already introduced earlier
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
