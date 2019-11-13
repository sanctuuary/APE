package nl.uu.cs.ape.sat.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code AbstractModule} class represents modules/tools provided by the
 * Module Taxonomy as well as their abstraction classes. Instances of
 * {@link AbstractModule} can be actual tools or their abstraction classes,
 * while all instances of the actual tools are extended to {@link Module}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AbstractModule extends TaxonomyPredicate {

	private final String moduleName;
	private final String moduleID;
	/**
	 * Set of all the modules that are subsumed by the abstract module (null if the
	 * module is a tool)
	 */
	private Set<String> subModules;

	/**
	 * Creates an abstract module from @moduleName and @moduleID. If @isTool is
	 * true, module is an actual tool, otherwise it's an abstract/non-tool module.
	 * 
	 * @param moduleName - module name
	 * @param moduleID   - unique module identifier
	 * @param rootNode   - ID of the Taxonomy Root node corresponding to the Module.
	 * @param nodeType   - {@link NodeType} object describing the type w.r.t. the
	 *                   Module Taxonomy.
	 */
	public AbstractModule(String moduleName, String moduleID, String rootNode, NodeType nodeType) {
		super(rootNode, nodeType);
		this.moduleName = moduleName;
		this.moduleID = moduleID;
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subModules = new HashSet<String>();
		}
	}

	/**
	 * Generate an AbstractModule from an existing one. In order to provide means
	 * for combining Module and AbstractModule objects
	 * 
	 * @param abstractModule - abstract module that is being copied
	 * @param nodeType       - {@link NodeType} object describing the type w.r.t.
	 *                       the Module Taxonomy.
	 */
	public AbstractModule(AbstractModule abstractModule, NodeType nodeType) {
		super(abstractModule.getRootNode(), (nodeType != null) ? nodeType : abstractModule.getNodeType());
		this.moduleName = abstractModule.getPredicateLabel();
		this.moduleID = abstractModule.getPredicateID();

		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subModules = abstractModule.getSubModules();
			if (this.subModules == null) {
				this.subModules = new HashSet<String>();
			}
		}
	}

	@Override
	public int hashCode() {
		return moduleID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		AbstractModule other = (AbstractModule) obj;
		return this.moduleID.equals(other.getPredicateID());
	}

	@Override
	public String getPredicateID() {
		return moduleID;
	}

	@Override
	public String getPredicateLabel() {
		return moduleName;
	}

	/**
	 * True if the module is a tool, false otherwise.
	 * 
	 * @return true if the (abstract) module represent an actual tool
	 */
	public boolean isTool() {
		return this.nodeType == NodeType.LEAF;
	}

	/**
	 * True if the module is the root module, false otherwise.
	 * 
	 * @return true if the (abstract) module represent the root module
	 */
	public boolean isRoot() {
		return this.nodeType == NodeType.ROOT;
	}

	/**
	 * 
	 * Set the module to be a tool (LEAF in the Module Taxonomy Tree)
	 * 
	 */
	public void setToTool() {
		this.nodeType = NodeType.LEAF;
	}

	/**
	 * Returns null. Abstract classes do not have input types.
	 * 
	 * @return null
	 */
	public List<DataInstance> getModuleInput() {
		return null;
	}

	/**
	 * Returns null. Abstract classes do not have output types.
	 * 
	 * @return null
	 */
	public List<DataInstance> getModuleOutput() {
		return null;
	}

	/**
	 * Return a printable String version of the abstract module.
	 * 
	 * @return abstract module as printable String
	 */
	public String print() {

		return "ID: " + getPredicateID() + ", Label:" + getPredicateLabel();
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
	public String getType() {
		return "abstract module";
	}

	/**
	 * Adds a submodule to an abstract/non-tool module, if it was not added present
	 * already.
	 * 
	 * @param module - module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(AbstractModule module) {
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			subModules.add(module.getPredicateID());
			return true;
		} else {
			System.err.println("Cannot add submodules to a tool/leaf or empty module!");
			return false;
		}
	}

	/**
	 * Adds a submodule to an abstract/non-tool module, if not present already.
	 * 
	 * @param moduleID - ID of the module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(String moduleID) {
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			return subModules.add(moduleID);
		} else {
			System.err.println("Cannot add submodules to a tool/leaf or empty module!");
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

	/**
	 * Print the tree shaped representation of the module taxonomy.
	 * 
	 * @param str        - string that is helping the recursive function to
	 *                   distinguish between the tree levels
	 * @param allModules - set of all the modules
	 */
	public void printTree(String str, AllModules allModules) {
		System.out.println(str + printShort() + "[" + getNodeType() + "]");
		for (String moduleID : APEUtils.safe(subModules)) {
			allModules.get(moduleID).printTree(str + ". ", allModules);
		}
	}

}
