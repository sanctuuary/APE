package nl.uu.cs.ape.sat.core.solutionStructure;

import static guru.nidi.graphviz.model.Factory.node;

import java.util.HashSet;
import java.util.Set;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * The {@code ModuleNode} class is used to represent module step in the actual solution workflow. Each {@code ModuleNode} represents an action/tool in the solution workflow.
 * 
 * @author Vedran Kasalica
 *
 */
public class ModuleNode extends SolutionWorkflowNode {

	/** Tool that is used in the workflow step. */
	private Module usedModule;
	/** Abstract Modules that describe the workflow step. */
	private Set<AbstractModule> abstractModules;
	/** Next module step in the workflow. */
	private ModuleNode nextModuleNode;
	/** Previous module step in the workflow. */
	private ModuleNode prevModuleNode;
	/** List of the data instances that are used as input for the tool. */
	private Set<TypeNode> inputTypes;
	/** List of the data instances that are generated as output of the tool. */
	private Set<TypeNode> outputTypes;
	
	
	
	public ModuleNode(State automatonState) throws Exception {
		super(automatonState);
		this.usedModule = null;
		this.abstractModules = new HashSet<AbstractModule>();
		this.prevModuleNode = null;
		this.nextModuleNode = null;
		if(automatonState.getWorkflowStateType() != WorkflowElement.MODULE) {
			throw new Exception("Class ModuleNode can only be instantiated using State that is of type WorkflowElement.MODULE, as a parameter.");
		}
		inputTypes = new HashSet<TypeNode>();
		outputTypes = new HashSet<TypeNode>();
	}
	
	/** 
	 * Set module/tool that defines this step in the workflow.
	 * @param module - tool provided by the tool/module annotations.
	 */
	public void setUsedModule(Module module) {
		this.usedModule = module;
	}
	
	/**
	 * Add the abstract module to the list of modules that describes the tool instance.
	 * @param abstractModule - abstract type that describes the instance.
	 */
	public void addAbstractDescriptionOfUsedType(AbstractModule abstractModule) {
		if(!abstractModule.isTool()) {
			this.abstractModules.add(abstractModule);
		} else {
			System.err.println("Actual tool cannot be uset to describe a module in an abstract way.");
		}
	}
	
	public void addInputType(TypeNode inputTypeNode) {
		inputTypes.add(inputTypeNode);
	}
	
	public void addOutputType(TypeNode outputTypeNode) {
		outputTypes.add(outputTypeNode);
	}

	public void setNextModuleNode(ModuleNode nextModuleNode) {
		this.nextModuleNode = nextModuleNode;
	}

	public void setPrevModuleNode(ModuleNode prevModuleNode) {
		this.prevModuleNode = prevModuleNode;
	}

	public Module getUsedModule() {
		return usedModule;
	}

	public Set<AbstractModule> getAbstractModules() {
		return abstractModules;
	}

	public ModuleNode getNextModuleNode() {
		return nextModuleNode;
	}

	public ModuleNode getPrevModuleNode() {
		return prevModuleNode;
	}

	public Set<TypeNode> getInputTypes() {
		return inputTypes;
	}

	public Set<TypeNode> getOutputTypes() {
		return outputTypes;
	}
	
	public boolean isEmpty() {
		return usedModule == null;
	}
	
	/**
	 * Get string representation of the ModuleNode.
	 * 
	 * @return Printable string that can be used for the presentation of this workflow node.
	 */
	public String toString() {
		StringBuilder printString = new StringBuilder();
		printString = printString.append("[").append(this.usedModule.getPredicateID()).append("]");
		
		return printString.toString();
	}
	
	public String getDotDefinition() {
		return getDotID() + " [label=\"" + getDotLabel() + "\", shape=box];\n";
	}
	public Graph addModuleToGraph(Graph workflowGraph) {
		return workflowGraph = workflowGraph.with(node(getDotID()).with(Label.of(getDotLabel())));
	}
	
	public String getDotID() {
		StringBuilder printString = new StringBuilder();
		printString = printString.append("\"").append(this.usedModule.getPredicateID());
		printString = printString.append("_").append(super.getAutomatonState().getPredicateID()).append("\"");
		
		return printString.toString();
	}
	
	public String getDotLabel() {
		StringBuilder printString = new StringBuilder();
		printString = printString.append(this.usedModule.getPredicateLabel());
		
		return printString.toString();
	}
	
}
