package SAT.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SAT.models.constructs.Predicate;
/**
 *  The {@code AbstractModule} class represents modules/tools that can be used. {@code AbstractModules} can be actual tools or their abstraction classes.
 *  
 * @author Vedran Kasalica
 *
 */
public class AbstractModule implements Predicate {

	private String moduleName;
	private String moduleID;
	// set of all the modules that are subsumed by the abstract module (null if the
	// module is a tool)
	private Set<String> subModules;
	// represents whether the module is a tool/leaf or simply an abstract module
	private boolean isTool;

	/**
	 * Creates an abstract module from @moduleName and @moduleID. If @isTool is
	 * true, module is an actual tool, otherwise it's an abstract/non-tool module.
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param isTool
	 *            - determines whether the module represents a tool
	 */
	public AbstractModule(String moduleName, String moduleID, boolean isTool) {
		super();
		this.moduleName = moduleName;
		this.moduleID = moduleID;
		this.isTool = isTool;
		if (!isTool)
			this.subModules = new HashSet<String>();
	}

	/**
	 * Generate an AbstractModule from an existing one. In order to provide means
	 * for combining Module and AbstractModule objects
	 * 
	 * @param abstractModule
	 *            - abstract module that is being coppied
	 * @param isTool
	 *            - determines whether the module represents a tool
	 */
	public AbstractModule(AbstractModule abstractModule, boolean isTool) {
		super();
		this.moduleName = abstractModule.getModuleName();
		this.moduleID = abstractModule.getModuleID();
		this.isTool = isTool;
		if (!isTool) {
			this.subModules = abstractModule.getSubModules();
			if (this.subModules == null) {
				this.subModules = new HashSet<String>();
			}
		}
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
	 * 
	 * @return true if the (abstract) module represent an actual tool
	 */
	public boolean isTool() {
		return isTool;
	}

	public void setIsTool(boolean isTool) {
		this.isTool = isTool;
	}

	/**
	 * Returns null. Abstract classes do not have input types.
	 * 
	 * @return null
	 */
	public List<Type> getModuleInput() {
		return null;
	}

	/**
	 * Returns null. Abstract classes do not have output types.
	 * 
	 * @return null
	 */
	public List<Type> getModuleOutput() {
		return null;
	}

	/**
	 * Return a printable String version of the abstract module.
	 * 
	 * @return abstract module as printable String
	 */
	public String print() {

		return getModuleID() + ", " + getModuleName();
	}

	/**
	 * Print the ID of the AbstractModule
	 * 
	 * @return module ID as a {@link String}
	 */
	public String printShort() {
		return moduleID;
	}

	@Override
	public String getPredicate() {
		return moduleID;
	}

	@Override
	public String getType() {
		return "abstract module";
	}

	/**
	 * Adds a submodule to an abstract/non-tool module, if it was not added present
	 * already.
	 * 
	 * @param module
	 *            - module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(AbstractModule module) {
		if (!isTool) {
			subModules.add(module.getModuleID());
			return true;
		} else {
			System.err.println("Cannot add submodules to a tool/leaf module!");
			return false;
		}
	}

	/**
	 * Adds a submodule to an abstract/non-tool module, if not present already.
	 * 
	 * @param moduleID
	 *            - ID of the module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(String moduleID) {
		if (!isTool) {
			return subModules.add(moduleID);
		} else {
			System.err.println("Cannot add submodules to a tool/leaf module!");
			return false;
		}
	}

	/**
	 * Returns the list of the modules that are directly subsumed by the modules.
	 * 
	 * @return List of the submodules or null in case of a tool/leaf module
	 */
	public Set<String> getSubModules() {
		return subModules;
	}

	@Override
	public boolean equals(Object obj) {
		AbstractModule other = (AbstractModule) obj;
		return this.moduleID.matches(other.moduleID);
	}

	@Override
	public int hashCode() {
		return moduleID.hashCode();
	}

	/**
	 * The class is used to check weather the module with @moduleID was already
	 * introduced earlier on in @allModules. In case it was it returns the item,
	 * otherwise the new element is generated and returned.
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param isTool
	 *            - determines whether the module represents a tool
	 * @param allModules
	 *            - set of all the modules created so far
	 * @return the Abstract Module representing the item.
	 */
	public static AbstractModule generateModule(String moduleName, String moduleID, boolean isTool,
			AllModules allModules) {
		return allModules.addModule(new AbstractModule(moduleName, moduleID, isTool));
	}

	/**
	 * Print the tree shaped representation of the module taxonomy.
	 * 
	 * @param str - string that is helping the recursive function to distinguish between the tree levels
	 * @param allModules - set of all the modules
	 */
	public void printTree(String str, AllModules allModules) {
		System.out.println(str + printShort());
		if (subModules != null)
			for (String moduleID : subModules) {
				allModules.get(moduleID).printTree(str + " > ", allModules);
			}
	}

}
