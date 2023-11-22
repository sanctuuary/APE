package nl.uu.cs.ape.solver.solutionStructure;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.attribute.Label.Justification;
import guru.nidi.graphviz.model.Graph;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static guru.nidi.graphviz.model.Factory.node;

/**
 * The {@code TypeNode} class is used to represent a data instance in the
 * memory, in the actual solution workflow. Each {@code TypeNode} represents
 * an output of a tool in the workflow execution.
 * <p>
 * When compared to the {@link TypeAutomaton} representation of the problem,
 * a Workflow {@code TypeNode} correspond to a {@link State} in memory.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class TypeNode extends SolutionWorkflowNode {

    /**
     * Types that define the data instance. The set cannot contain
     * {@code EmptyType}.
     */
    private SortedSet<Type> usedTypes;

    /**
     * Abstract Types that describe the data instance.
     */
    private SortedSet<Type> abstractTypes;

    /**
     * Module/step in the workflow that generates the data instance as input.
     * If null data instance corresponds to the initial workflow input.
     */
    private ModuleNode createdByModule;

    /**
     * Modules/steps in the workflow that use this data instance as input.
     * null represents workflow output.
     */
    private List<ModuleNode> usedByModules;

    /**
     * Creating Workflow Node that corresponds to a type instance in memory.
     *
     * @param automatonState State in the {@link TypeAutomaton} that corresponds to
     *                       the workflow node.
     * @throws ExceptionInInitializerError Exception when the Type Workflow Node is
     *                                     instantiated using a State in
     *                                     TypeAutomaton that does not correspond to
     *                                     a {@code AtomType#MEMORY_TYPE}.
     */
    public TypeNode(State automatonState) throws ExceptionInInitializerError {
        super(automatonState);
        this.usedTypes = new TreeSet<Type>();
        this.abstractTypes = new TreeSet<Type>();
        this.createdByModule = null;
        usedByModules = new ArrayList<ModuleNode>();
        if (automatonState.getWorkflowStateType() != AtomType.MEMORY_TYPE) {
            throw new ExceptionInInitializerError(
                    "Class MemTypeNode can only be instantiated using State that is of type AtomType.MEMORY_TYPE, as a parameter.");
        }
    }

    /**
     * Add the primitive type to the list of types that define the instance.
     *
     * @param simpleType instance type that defines the instance.
     */
    public void addUsedType(Type simpleType) {
        /* Check if the type is simple */
        if (simpleType.isSimplePredicate()) {
            this.usedTypes.add(simpleType);
        } else {
            log.warn("Abstract and Empty Type cannot be used to define an instance.");
        }
    }

    /**
     * Add the abstract type to the list of types that describes the instance.
     *
     * @param abstractType abstract type that describes the instance.
     */
    public void addAbstractDescriptionOfUsedType(Type abstractType) {
        if (!abstractType.isSimplePredicate()) {
            this.abstractTypes.add(abstractType);
        } else {
            log.warn("Simple Type cannot be used to describe an instance.");
        }
    }

    /**
     * Add a tool, that uses this data instance as input, to the List
     * {@link TypeNode#usedByModules}.
     *
     * @param usedByTool Tool/module node in the workflow, that uses this data
     *                   instance as input.
     */
    public void addUsedByTool(ModuleNode usedByTool) {
        usedByModules.add(usedByTool);
    }

    /**
     * Sets created by module.
     *
     * @param createdByModule Set the tool/workflow step that creates this data
     *                        instance.
     */
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
     * @return The {@link ModuleNode} that uses this data type. If null data
     *         instance corresponds to the initial workflow input.
     */
    public ModuleNode getCreatedByModule() {
        return createdByModule;
    }

    /**
     * Get all module nodes that use the data instance in input. If null is
     * in the list, the type is used as workflow output.
     *
     * @return List of modules that used the type instance (a null value represent
     *         the workflow output).
     */
    public List<ModuleNode> getUsedByModules() {
        return usedByModules;
    }

    /**
     * Returns true if the node does not contain any type instances.
     *
     * @return true if the type node is empty.
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

        printString.append("[");
        int i = 0;
        for (Type type : this.usedTypes) {
            printString.append(type.getPredicateID());
            if (++i < this.usedTypes.size()) {
                printString.append(", ");
            }
        }
        printString.append(" (" + super.getAutomatonState().getPredicateID() + ")]");

        return printString.toString();
    }

    /**
     * Gets dot definition.
     *
     * @return the dot definition
     */
    public String getDotDefinition() {
        return getNodeID() + " [label=\"" + getNodeLabel() + "\", color=blue];\n";
    }

    /**
     * Add the current type node to the workflow graph, and return the graph.
     *
     * @param workflowGraph Graph that is to be extended.
     * @return The {@link Graph} extended with the current {@link TypeNode}.
     */
    public Graph addTypeToGraph(Graph workflowGraph) {
        return workflowGraph.with(node(getNodeID()).with(Label.of(getNodeLabel() + "   ")));
    }

    public Graph addTavernaStyleTypeToGraph(Graph workflowGraph) {
        return workflowGraph
                .with(node(getNodeID()).with(Style.FILLED, Color.rgb("6CF1FF").fill(),
                        Label.html(getNodeGraphLabels()), Shape.RECTANGLE));
    }

    /**
     * Get label of the current workflow node in .dot representation.
     */
    public String getNodeLabel() {
        StringBuilder printString = new StringBuilder();
        int i = 0;
        for (Type type : this.usedTypes) {
            String typeLabel = type.getPredicateLabel();
            if (typeLabel.endsWith("_p")) {
                // remove "_plain" suffix
                typeLabel = APEUtils.removeNLastChar(typeLabel, 2);
            }
            printString.append(typeLabel);
            if (++i < this.usedTypes.size()) {
                printString.append(", ");
            }
        }
        return printString.toString();
    }

    /**
     * Get label of the current workflow node as an HTML element.
     */
    public String getNodeGraphLabels() {
        StringBuilder labelBuilder = new StringBuilder();
        int i = 0;
        for (Type type : this.usedTypes) {
            String typeLabel = type.getPredicateLabel();
            if (typeLabel.endsWith("_p")) {
                // remove "_plain" suffix
                typeLabel = APEUtils.removeNLastChar(typeLabel, 2);
            }
            String formatting = "%s";

            switch (i++ % 4) {
                case 1:
                    formatting = "<b><i>%s</i></b>";
                    break;
                case 2:
                    formatting = "<i>%s</i>";
                    break;
                case 3:
                    formatting = "<b>%s</b>";
                    break;
                default:
                    formatting = "%s";

            }
            labelBuilder.append(String.format(formatting, typeLabel)).append("<br/>");
        }
        labelBuilder.delete(labelBuilder.length() - 5, labelBuilder.length());
        return labelBuilder.toString();
    }

    /**
     * Get the unique ID of the current workflow node in .dot representation.
     */
    public String getNodeID() {
        StringBuilder printString = new StringBuilder("\"");

        int i = 0;
        for (Type type : this.usedTypes) {
            printString.append(type.getPredicateID());
            if (++i < this.usedTypes.size()) {
                printString.append(",");
            }
        }
        printString.append("[").append(super.getAutomatonState().getPredicateID()).append("]").append("\"");

        return printString.toString();
    }

    /**
     * Get the format of the current workflow node and a label for the CWL file.
     */
    public String getFormat() {
        StringBuilder printString = new StringBuilder();

        for (Type type : this.usedTypes) {
            if (type.getRootNodeID().equals("http://edamontology.org/format_1915")) {
                printString.append("\"").append(type.getPredicateLongLabel()).append("\" # ")
                        .append(type.getPredicateLabel());
            }
        }
        if (printString.length() == 0) {
            printString.append("\"unknown\"");
        }

        return printString.toString();
    }

    /**
     * Gets node descriptive label, containing type IDs.
     */
    public String getNodeLongLabel() {
        StringBuilder printString = new StringBuilder();
        int i = 0;
        for (Type type : this.usedTypes) {
            String typeLabel = type.getPredicateLongLabel();
            printString.append(typeLabel);
            if (++i < this.usedTypes.size()) {
                printString.append(", ");
            }
        }
        return printString.toString();
    }

    /**
     * Gets short node id.
     *
     * @return A short non descriptive node ID, that can be used as a variable name.
     */
    public String getShortNodeID() {
        return "node" + getNodeID().hashCode();
    }
}
