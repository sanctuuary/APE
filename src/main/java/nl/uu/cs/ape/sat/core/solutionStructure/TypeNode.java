package nl.uu.cs.ape.sat.core.solutionStructure;

import static guru.nidi.graphviz.model.Factory.node;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * The {@code TypeNode} class is used to represent a data instance in the
 * memory, in the actual solution workflow. Each {@code TypeNode} represents
 * an output of a tool in the workflow execution.<br><br>
 * 
 * When compared to the {@link TypeAutomaton} representation of the problem, a Workflow {@code TypeNode} correspond to a {@link State} of a {@link WorkflowElement#MEMORY_TYPE} element.
 * 
 * @author Vedran Kasalica
 *
 */
public class TypeNode extends SolutionWorkflowNode {

	/** Types that define the data instance. The set cannot contain {@code EmptyType}. */
	private SortedSet<Type> usedTypes;
	/** Abstract Types that describe the data instance. */
	private SortedSet<Type> abstractTypes;
	/**
	 * Module/step in the workflow that generates the data instance as input. If
	 * {@code null} data instance corresponds to the initial workflow input.
	 */
	private ModuleNode createdByModule;
	/**
	 * Modules/steps in the workflow that use this data instance as input.
	 * {@code NULL} represents workflow output.
	 */
	private List<ModuleNode> usedByModules;

	/**
	 * Creating Workflow Node that corresponds to a type instance in memory.
	 * @param automatonState - state in the {@link TypeAutomaton} that corresponds to the workflow node.
	 * @throws Exception Exception when the Type Workflow Node is instantiated using a State in TypeAutomaton that does not correspond to a {@code WorkflowElement#MEMORY_TYPE}.
	 */
	public TypeNode(State automatonState) throws ExceptionInInitializerError {
		super(automatonState);
		this.usedTypes = new TreeSet<Type>();
		this.abstractTypes = new TreeSet<Type>();
		this.createdByModule = null;
		usedByModules = new ArrayList<ModuleNode>();
		if (automatonState.getWorkflowStateType() != WorkflowElement.MEMORY_TYPE) {
			throw new ExceptionInInitializerError(
					"Class MemTypeNode can only be instantiated using State that is of type WorkflowElement.MEMORY_TYPE, as a parameter.");
		}
	}

	/**
	 * Add the primitive type to the list of types that define the instance.
	 * 
	 * @param simpleType - instance type that defines the instance.
	 */
	public void addUsedType(Type simpleType) {
		/* Check if the type is simple */
		if (simpleType.isSimplePredicate()) {
			this.usedTypes.add(simpleType);
		} else {
			System.err.println("Abstract and Empty Type cannot be used to define an instance.");
		}
	}

	/**
	 * Add the abstract type to the list of types that describes the instance.
	 * 
	 * @param abstractType - abstract type that describes the instance.
	 */
	public void addAbstractDescriptionOfUsedType(Type abstractType) {
		if (!abstractType.isSimplePredicate()) {
			this.abstractTypes.add(abstractType);
		} else {
			System.err.println("Simple Type cannot be uset to describe an instance.");
		}
	}

	/**
	 * Add a tool, that uses this data instance as input, to the List
	 * {@link TypeNode#usedByModules}.
	 * 
	 * @param usedByTool - tool/module node in the workflow, that uses this data
	 *                   instance as input
	 */
	public void addUsedByTool(ModuleNode usedByTool) {
			usedByModules.add(usedByTool);
	}

	/** Set the tool/workflow step that creates this data instance. */
	public void setCreatedByModule(ModuleNode createdByModule) {
		this.createdByModule = createdByModule;
	}

	/**
	 * Get simple/primitive data types that define the instance.
	 * 
	 * @return List of simple/primitive data types that define the instance.
	 */
	public SortedSet<Type> getTypes() {
		return usedTypes;
	}

	/**
	 * Get abstract data types that describe the instance. They are used in order to
	 * abstract from the simple/primitive data types.
	 * 
	 * @return List of abstract data types that describe the instance.
	 */
	public SortedSet<Type> getAbstractTypes() {
		return abstractTypes;
	}

	/**
	 * Get tool step in the workflow that creates this data instance as output.
	 * 
	 * @return {@link ModuleNode} that uses this data type. If {@code null} data
	 *         instance corresponds to the initial workflow input.
	 */
	public ModuleNode getCreatedByModule() {
		return createdByModule;
	}

	/**
	 * Get all module nodes that use the data instance in input. If {@code NULL} is
	 * in the list, the type is used as workflow output.
	 * 
	 * @return List of modules that used the type instance (a {@code NULL} value represent the workflow output).
	 */
	public List<ModuleNode> getUsedByModules() {
		return usedByModules;
	}

	/**
	 * Returns {@code true} if the node does not contain any type instances.
	 * 
	 * @return {@code true} if the type node is empty.
	 */
	public boolean isEmpty() {
		return usedTypes.isEmpty();
	}

	/**
	 * Get string representation of the TypeNode.
	 * 
	 * @return Printable string that can be used for the presentation of this
	 *         workflow node.
	 */
	public String toString() {
		StringBuilder printString = new StringBuilder();

		printString = printString.append("[");
		int i = 0;
		for (Type type : this.usedTypes) {
			printString = printString.append(type.getPredicateID());
			if (++i < this.usedTypes.size()) {
				printString = printString.append(", ");
			}
		}
		printString = printString.append(" (" + super.getAutomatonState().getPredicateID() + ")]");

		return printString.toString();
	}

	public String getDotDefinition() {
		return getNodeID() + " [label=\"" + getNodeLabel() + "\", color=blue];\n";
	}

	/**
	 * Add the current type node to the workflow graph, and return the graph.
	 * 
	 * @param workflowGraph - graph that is to be extended
	 * @return {@link Graph} extended with the current {@link TypeNode}
	 */
	public Graph addTypeToGraph(Graph workflowGraph) {
		return workflowGraph = workflowGraph.with(node(getNodeID()).with(Label.of(getNodeLabel() + "   ")));
	}

	/** Get label of the current workflow node in .dot representation. */
	public String getNodeLabel() {
		StringBuilder printString = new StringBuilder();
		int i = 0;
		for (Type type : this.usedTypes) {
			printString = printString.append(type.getPredicateLabel());
			if (++i < this.usedTypes.size()) {
				printString = printString.append(", ");
			}
		}
		return printString.toString();
	}

	/** Get id of the current workflow node in .dot representation. */
	public String getNodeID() {
		StringBuilder printString = new StringBuilder();

		int i = 0;
		for (Type type : this.usedTypes) {
			printString = printString.append(type.getPredicateID());
			if (++i < this.usedTypes.size()) {
				printString = printString.append(",");
			}
		}
		printString = printString.append("_").append(super.getAutomatonState().getPredicateID());

		return printString.toString();
	}
	
	/**
	 * Get a short non descriptive node ID, that can be used as a variable name.
	 * @return
	 */
	public String getShortNodeID() {
		return "node" + Math.abs(getNodeID().hashCode());
	}
}
