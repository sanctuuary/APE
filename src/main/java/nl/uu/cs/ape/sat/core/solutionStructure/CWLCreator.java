package nl.uu.cs.ape.sat.core.solutionStructure;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.configuration.APECoreConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The {@code CWLCreator} class is used to generate a CWL workflow structure from a given workflow solution.
 *
 * @author Vedran Kasalica
 */
public class CWLCreator {

    /* Textual representation of the CWL file */
    private StringBuilder cwlRepresentation;

    private final static String cwlVersion = "v1.2-dev1";

    /**
     * Instantiates a new Cwl creator.
     *
     * @param solution  the solution
     * @param apeConfig the ape config
     */
    public CWLCreator(SolutionWorkflow solution, APECoreConfig apeConfig) {
        cwlRepresentation = new StringBuilder(
                "class: WorkflowNo_" + solution.getIndex() + "\ncwlVersion: " + cwlVersion + "").append("\n");
        generateCWLRepresentation(solution, apeConfig);
    }

    /**
     * Gets CWL representation.
     *
     * @return the cwl
     */
    public String getCWL() {
        return cwlRepresentation.toString();
    }
    /**
     * TODO
     * @param solution
     * @param apeConfig
     */
    private void generateCWLRepresentation(SolutionWorkflow solution, APECoreConfig apeConfig) {
        cwlRepresentation = cwlRepresentation.append("inputs:").append("\n");
        for (TypeNode typeNode : solution.getWorkflowInputTypeStates()) {
            getNewCWLDataInstance(typeNode, apeConfig, 1);
        }
        cwlRepresentation = cwlRepresentation.append("steps:").append("\n");
        for (ModuleNode moduleNode : solution.getModuleNodes()) {
            defineCWLStep(moduleNode, apeConfig, 1);
        }

        cwlRepresentation = cwlRepresentation.append("outputs:").append("\n");
        for (TypeNode typeNode : solution.getWorkflowOutputTypeStates()) {
            getExistingCWLDataInstance(typeNode, apeConfig, 1, "workflowOut");
        }

    }

    /**
     * TODO
     * @param moduleNode
     * @param apeConfig
     * @param i
     */
    private void defineCWLStep(ModuleNode moduleNode, APECoreConfig apeConfig, int i) {
        cwlRepresentation = cwlRepresentation.append(tabs(i) + moduleNode.getNodeID() + ":");
        /// TODO split the function into smaller functions
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "in:").append("\n");
        int index = 1;
        for (TypeNode typeNode : moduleNode.getInputTypes()) {
            cwlRepresentation = cwlRepresentation
                    .append(tabs(i + 2) + (moduleNode.getNodeID() + (index++)) + "_" + typeNode.getNodeID() + ":");
            if (typeNode.getCreatedByModule() != null) {
                cwlRepresentation = cwlRepresentation.append(typeNode.getCreatedByModule().getNodeID()).append("/");
            }
            cwlRepresentation = cwlRepresentation.append(typeNode.getNodeID()).append("\n");
        }

        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "out: ");
        String outputTypes = "[";
        for (TypeNode typeNode : moduleNode.getOutputTypes()) {
            outputTypes = outputTypes + typeNode.getNodeID() + ",";
        }
        outputTypes = APEUtils.removeLastChar(outputTypes) + "]";
        cwlRepresentation = cwlRepresentation.append(outputTypes).append("\n");

        ///
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "run:").append("\n");

        getOperationRun(moduleNode, apeConfig, i + 2);

    }

    /**
     * TODO
     * @param moduleNode
     * @param apeConfig
     * @param i
     */
    private void getOperationRun(ModuleNode moduleNode, APECoreConfig apeConfig, int i) {
        cwlRepresentation = cwlRepresentation.append(tabs(i) + "class: Operation").append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i) + "inputs:").append("\n");
        int index = 1;
        for (TypeNode typeNode : moduleNode.getInputTypes()) {
            getExistingCWLDataInstance(typeNode, apeConfig, i + 1, moduleNode.getNodeID() + (index++));
        }
        cwlRepresentation = cwlRepresentation.append(tabs(i) + "outputs:").append("\n");
        for (TypeNode typeNode : moduleNode.getOutputTypes()) {
            getNewCWLDataInstance(typeNode, apeConfig, i + 1);
        }
        cwlRepresentation = cwlRepresentation.append(tabs(i) + "hints:").append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "SoftwareRequirement:").append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i + 2) + "packages:").append("\n");
        cwlRepresentation = cwlRepresentation.append(
                tabs(i + 2) + moduleNode.getNodeLabel() + ": [" + moduleNode.getUsedModule().getPredicateID() + "]")
                .append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i) + "intent:")
                .append(toYmlArray(moduleNode.getUsedModule().getSuperPredicates())).append("\n");

    }

    /**
     * Create a label and id for a new data instance.
     * @param typeNode
     * @param apeConfig
     * @param i
     */
    private void getNewCWLDataInstance(TypeNode typeNode, APECoreConfig apeConfig, int i) {
        cwlRepresentation = cwlRepresentation.append(tabs(i) + typeNode.getNodeID() + ":").append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "type: File").append("\n");
        String formatRoot = apeConfig.getCWLFormatRoot();
        List<TaxonomyPredicate> formats = new ArrayList<TaxonomyPredicate>();
        typeNode.getTypes().forEach(type -> {
            if (type.getRootNodeID().equals(formatRoot))
                formats.add(type);
        });
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "format: " + toYmlArray(formats) + "").append("\n");
    }

    /**
     * Create a label and id for a data input that uses an existing data instance.
     * @param typeNode 
     * @param apeConfig
     * @param i
     * @param prefix
     */
    private void getExistingCWLDataInstance(TypeNode typeNode, APECoreConfig apeConfig, int i, String prefix) {
        cwlRepresentation = cwlRepresentation.append(tabs(i) + prefix + "_" + typeNode.getNodeID() + ":").append("\n");
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "type: File").append("\n");
        String formatRoot = apeConfig.getCWLFormatRoot();
        List<TaxonomyPredicate> formats = new ArrayList<TaxonomyPredicate>();
        typeNode.getTypes().forEach(type -> {
            if (type.getRootNodeID().equals(formatRoot))
                formats.add(type);
        });
        cwlRepresentation = cwlRepresentation.append(tabs(i + 1) + "format: " + toYmlArray(formats) + "").append("\n");
    }

    /**
     * Create YML array from the collection of {@link TaxonomyPredicate}s.
     * @param formats collection of {@link TaxonomyPredicate}s
     * @return List of {@link TaxonomyPredicate}s' IDs in YML array format.
     */
    private String toYmlArray(Collection<TaxonomyPredicate> formats) {
        String str = "[";
        for (TaxonomyPredicate format : formats) {
            str = str + format.getPredicateID() + ",";
        }
        return APEUtils.removeLastChar(str) + "]";
    }

    /**
     * Return string that contains the given number of tabs.
     * @param size number of tabs
     * @return String that consists of defined number of tabs.
     */
    private String tabs(int size) {
        String tabs = "";
        for (int i = 0; i < size; i++) {
            tabs = tabs + "\t";
        }
        return tabs;
    }
}
