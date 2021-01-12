package nl.uu.cs.ape.sat.core.solutionStructure;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.node;

/**
 * The {@code ModuleNode} class is used to represent module step in the actual solution workflow.
 * Each {@code ModuleNode} represents an action/tool in the solution workflow.
 * <p>
 * When compared to the {@link ModuleAutomaton} representation of the problem, a Workflow {@code TypeNode}
 * correspond to a {@link State} of a {@link WorkflowElement#MODULE} element.
 *
 * @author Vedran Kasalica
 */
public class ModuleNode extends SolutionWorkflowNode {

    /**
     * Tool that is used in the workflow step.
     */
    private Module usedModule;

    /**
     * Abstract Modules that describe the workflow step.
     */
    private Set<AbstractModule> abstractModules;

    /**
     * Next module step in the workflow.
     */
    private ModuleNode nextModuleNode;

    /**
     * Previous module step in the workflow.
     */
    private ModuleNode prevModuleNode;

    /**
     * List of the data instances that are used as input for the tool.
     */
    private List<TypeNode> inputTypes;

    /**
     * List of the data instances that are generated as output of the tool.
     */
    private List<TypeNode> outputTypes;

    /**
     * Creating Workflow Node that corresponds to a tool usage.
     *
     * @param automatonState state in the {@link ModuleAutomaton} that corresponds to the workflow node.
     * @throws ExceptionInInitializerError Exception when the Tool Workflow Node is instantiated using                                     a State in ModuleAutomaton that does not correspond to a                                     {@code WorkflowElement#MODULE}..
     */
    public ModuleNode(State automatonState) throws ExceptionInInitializerError {
        super(automatonState);
        this.usedModule = null;
        this.abstractModules = new HashSet<AbstractModule>();
        this.prevModuleNode = null;
        this.nextModuleNode = null;
        if (automatonState.getWorkflowStateType() != WorkflowElement.MODULE) {
            throw new ExceptionInInitializerError(
                    "Class ModuleNode can only be instantiated using State that is of type WorkflowElement.MODULE, as a parameter.");
        }
        inputTypes = new ArrayList<TypeNode>();
        outputTypes = new ArrayList<TypeNode>();
    }

    /**
     * Set module/tool that defines this step in the workflow.
     *
     * @param module - tool provided by the tool/module annotations.
     */
    public void setUsedModule(Module module) {
        this.usedModule = module;
    }

    /**
     * Add the abstract module to the list of modules that describes the tool instance.
     *
     * @param abstractModule - abstract type that describes the instance.
     */
    public void addAbstractDescriptionOfUsedType(AbstractModule abstractModule) {
        if (!abstractModule.isSimplePredicate()) {
            this.abstractModules.add(abstractModule);
        } else {
            System.err.println("Actual tool cannot be uset to describe a module in an abstract way.");
        }
    }

    /**
     * Add input type.
     *
     * @param inputTypeNode the input type node
     */
    public void addInputType(TypeNode inputTypeNode) {
        inputTypes.add(inputTypeNode);
    }

    /**
     * Sets input type.
     *
     * @param inputIndex     A specific input slot.
     * @param memoryTypeNode Input type to be set.
     */
    public void setInputType(int inputIndex, TypeNode memoryTypeNode) {
        APEUtils.safeSet(this.inputTypes, inputIndex, memoryTypeNode);
    }

    /**
     * Add output type.
     *
     * @param outputTypeNode the output type node
     */
    public void addOutputType(TypeNode outputTypeNode) {
        outputTypes.add(outputTypeNode);
    }

    /**
     * Sets next module node.
     *
     * @param nextModuleNode the next module node
     */
    public void setNextModuleNode(ModuleNode nextModuleNode) {
        this.nextModuleNode = nextModuleNode;
    }

    /**
     * Sets prev module node.
     *
     * @param prevModuleNode the prev module node
     */
    public void setPrevModuleNode(ModuleNode prevModuleNode) {
        this.prevModuleNode = prevModuleNode;
    }

    /**
     * Gets used module.
     *
     * @return the used module
     */
    public Module getUsedModule() {
        return usedModule;
    }

    /**
     * Gets abstract modules.
     *
     * @return the abstract modules
     */
    public Set<AbstractModule> getAbstractModules() {
        return abstractModules;
    }

    /**
     * Gets next module node.
     *
     * @return The next operation that should be per in the workflow structure.
     */
    public ModuleNode getNextModuleNode() {
        return nextModuleNode;
    }

    /**
     * Has next module boolean.
     *
     * @return True if the current operation is not the last one in the workflow, false otherwise.
     */
    public boolean hasNextModule() {
        return nextModuleNode != null;
    }

    /**
     * Has prev module boolean.
     *
     * @return True if the current operation is not the first one in the workflow, false otherwise.
     */
    public boolean hasPrevModule() {
        return prevModuleNode != null;
    }

    /**
     * Gets prev module node.
     *
     * @return the prev module node
     */
    public ModuleNode getPrevModuleNode() {
        return prevModuleNode;
    }

    /**
     * Gets input types.
     *
     * @return the input types
     */
    public List<TypeNode> getInputTypes() {
        return inputTypes;
    }

    /**
     * Gets output types.
     *
     * @return the output types
     */
    public List<TypeNode> getOutputTypes() {
        return outputTypes;
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return usedModule == null;
    }

    /**
     * Get string representation of the ModuleNode.
     *
     * @return Printable string that can be used for the presentation of this workflow node.
     */
    public String toString() {
        if (usedModule == null) {
            return "[]";
        }
        StringBuilder printString = new StringBuilder();
        printString = printString.append("[").append(this.usedModule.getPredicateID()).append("]");

        return printString.toString();
    }

    /**
     * Gets dot definition.
     *
     * @return the dot definition
     */
    public String getDotDefinition() {
        return getNodeID() + " [label=\"" + getNodeLabel() + "\", shape=box];\n";
    }

    /**
     * Add the current tool node to the workflow graph, and return the graph.
     *
     * @param workflowGraph Graph that is to be extended.
     * @return The {@link Graph} extended with the current {@link ModuleNode}.
     */
    public Graph addModuleToGraph(Graph workflowGraph) {
        return workflowGraph = workflowGraph
                .with(node(getNodeID()).with(Label.of(getNodeLabel() + "              "), Shape.RECTANGLE, Color.BLUE, Style.BOLD));
    }

    /**
     * Get id of the current workflow node.
     */
    public String getNodeID() {
        StringBuilder printString = new StringBuilder();
        printString = printString.append("\"").append(this.usedModule.getPredicateID());
        printString = printString.append("_").append(super.getAutomatonState().getPredicateID()).append("\"");

        return printString.toString();
    }

    /**
     * Get label of the current workflow node.
     */
    public String getNodeLabel() {
        return this.usedModule.getPredicateLabel();
    }
    
    /**
     *  Gets node descriptive label, containing module IDs.
     */
    public String getNodeFullLabel() {
    	return this.usedModule.getPredicateID();
    }
}
