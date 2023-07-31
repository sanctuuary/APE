package nl.uu.cs.ape.core.solutionStructure;

import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to generate a CWL workflow structure from a given workflow solution.
 */
@Slf4j
public class DefaultCWLCreator extends CWLCreatorBase {
    /**
     * Maintain a list of the CWL parameter names which represent {@link TypeNode}s.
     * I.e. this hashmap has TypeNode IDs as keys and their names in the CWL file as
     * values.
     */
    private final HashMap<String, String> workflowParameters = new HashMap<>();

    /**
     * Instantiates a new CWL creator.
     * 
     * @param solution The solution to represent in CWL.
     */
    public DefaultCWLCreator(SolutionWorkflow solution) {
        super(solution);
    }

    @Override
    public String getCWLVersion() {
        return "v1.2";
    }

    /**
     * Generates the CWL representation.
     */
    @Override
    protected void generateCWLRepresentation() {
        // Workflow
        generateWorkflowInputs();
        generateWorkflowSteps();
        generateWorkflowOutputs();
    }

    /**
     * Generate the top-level inputs definition of the workflow.
     */
    private void generateWorkflowInputs() {
        cwlRepresentation.append("inputs:").append("\n");
        cwlRepresentation.append(getInputsInCWL(false));
    }

    /**
     * Generate the top-level outputs definition of the workflow.
     * 
     * @param formatForCwlInputsYmlFile Whether the inputs are used to generate the
     *                                  CWL inputs.yml file.
     *
     * @return The CWL representation of the workflow outputs.
     */
    private String getInputsInCWL(boolean formatForCwlInputsYmlFile) {
        StringBuilder inputsInCWL = new StringBuilder();

        int indentLevel = 1;
        String typeLabel = "type";
        if (formatForCwlInputsYmlFile) {
            indentLevel = 0;
            typeLabel = "class";
        }
        int i = 1;
        for (TypeNode typeNode : solution.getWorkflowInputTypeStates()) {
            String inputName = String.format("input%o", i++);
            addNewParameterToMap(typeNode, inputName);
            inputsInCWL
                    // Name
                    .append(ind(indentLevel))
                    .append(inputName)
                    .append(":\n")
                    // Data type
                    .append(ind(indentLevel + 1))
                    .append(typeLabel + ": File")
                    .append("\n")
                    // Format
                    .append(ind(indentLevel + 1))
                    .append("format: ")
                    .append(typeNode.getFormat())
                    .append("\n");
            if (formatForCwlInputsYmlFile) {
                inputsInCWL
                        .append(ind(indentLevel + 1))
                        .append("path: ")
                        .append("set_full_path_to_the_file_with_extension_here")
                        .append("\n");
            }
        }
        return inputsInCWL.toString();
    }

    /**
     * Generate the top-level outputs definition of the workflow.
     */
    private void generateWorkflowOutputs() {
        cwlRepresentation.append("outputs:\n");
        // Outputs
        int i = 1;
        for (TypeNode typeNode : solution.getWorkflowOutputTypeStates()) {
            cwlRepresentation
                    // Name
                    .append(ind(1))
                    .append(String.format("output%o", i))
                    .append(":\n")
                    // Data type
                    .append(ind(2))
                    .append("type: File")
                    .append("\n")
                    // Format
                    .append(ind(2))
                    .append("format: ")
                    .append(typeNode.getFormat())
                    .append("\n")
                    // outputSource
                    .append(ind(2))
                    .append("outputSource: ")
                    .append(stepName(typeNode.getCreatedByModule()))
                    .append("/");
            // Get the id of the step run's output bound to this workflow output
            // (step_name/output_name_ID)
            int outId = typeNode.getCreatedByModule().getOutputTypes().get(i - 1).getAutomatonState()
                    .getLocalStateNumber();
            cwlRepresentation
                    .append(inOutName(typeNode.getCreatedByModule(), "out", outId + 1))
                    .append("\n");
            i++;
        }
    }

    /**
     * Generate the top-level steps of the workflow.
     */
    private void generateWorkflowSteps() {
        cwlRepresentation.append("steps:").append("\n");
        for (ModuleNode moduleNode : solution.getModuleNodes()) {
            generateStep(moduleNode);
        }
    }

    /**
     * Generate one workflow step.
     * 
     * @param moduleNode The {@link ModuleNode} related to the step.
     */
    private void generateStep(ModuleNode moduleNode) {
        final int baseInd = 1;
        // Name
        cwlRepresentation
                .append(ind(baseInd))
                .append(stepName(moduleNode))
                .append(":\n");
        generateDefaultStepRun(moduleNode);
        generateStepIn(moduleNode);
        generateStepOut(moduleNode);
    }

    /**
     * Generate the "in" section of a workflow step.
     * 
     * @param moduleNode The {@link ModuleNode} that is the step.
     */
    private void generateStepIn(ModuleNode moduleNode) {
        final int baseInd = 2;
        // "in" key
        cwlRepresentation.append(ind(baseInd)).append("in").append(":\n");
        // If there are no inputs, give an empty array as input
        if (!moduleNode.hasInputTypes()) {
            // Remove the last newline so the array is on the same line as "in:"
            deleteLastNCharactersFromCWL(1);
            cwlRepresentation
                    .append(" ")
                    .append("[]")
                    .append("\n");
        } else {
            List<TypeNode> inputs = moduleNode.getInputTypes();
            IntStream.range(0, inputs.size()).filter(i -> !inputs.get(i).isEmpty())
                    .forEach(i -> cwlRepresentation
                            .append(ind(baseInd + 1))
                            .append(inOutName(moduleNode, "in", i + 1))
                            .append(": ")
                            .append(workflowParameters.get(inputs.get(i).getNodeID()))
                            .append("\n"));
        }
    }

    /**
     * Generate the "out" section of a workflow step.
     * 
     * @param moduleNode The {@link ModuleNode} that is the step.
     */
    private void generateStepOut(ModuleNode moduleNode) {
        final int baseInd = 2;
        cwlRepresentation
                // "out" key
                .append(ind(baseInd))
                .append("out: ")
                // The outputs array
                .append("[");

        List<TypeNode> outputs = moduleNode.getOutputTypes();
        IntStream.range(0, outputs.size()).filter(i -> !outputs.get(i).isEmpty())
                .forEach(i -> {
                    String name = inOutName(moduleNode, "out", i + 1);
                    addNewParameterToMap(outputs.get(i), String.format("%s/%s", stepName(moduleNode), name));
                    cwlRepresentation
                            .append(name)
                            .append(", ");
                });
        if (moduleNode.hasOutputTypes()) {
            // Remove the last comma
            deleteLastNCharactersFromCWL(2);
        }
        cwlRepresentation.append("]").append("\n");
    }

    /**
     * Generate the run field of a workflow step.
     * 
     * @param moduleNode The {@link ModuleNode} related to the step.
     */
    private void generateDefaultStepRun(ModuleNode moduleNode) {
        final int baseInd = 2;
        String moduleName = moduleNode.getUsedModule().getPredicateLabel();
        cwlRepresentation
                // Main key
                .append(ind(baseInd))
                .append("run: https://raw.githubusercontent.com/Workflomics/containers/main/cwl/tools/" + moduleName
                        + "/" + moduleName + ".cwl")
                .append("\n");
        /*
         * // Hints and intent
         * generateStepHints(moduleNode, baseInd + 1);
         * generateStepIntent(moduleNode, baseInd + 1);
         */
    }

    /**
     * Generate the run field of a workflow step.
     * 
     * @param moduleNode The {@link ModuleNode} related to the step.
     */
    private void generateStepRun(ModuleNode moduleNode) {
        final int baseInd = 2;
        cwlRepresentation
                // Main key
                .append(ind(baseInd))
                .append("run:")
                .append("\n")
                // Class
                .append(ind(baseInd + 1))
                .append("class: Operation")
                .append("\n");
        // Inputs
        cwlRepresentation
                .append(ind(baseInd + 1))
                .append("inputs:")
                .append("\n");
        generateTypeNodes(moduleNode, moduleNode.getInputTypes(), true, baseInd + 2);
        // Outputs
        cwlRepresentation
                .append(ind(baseInd + 1))
                .append("outputs:")
                .append("\n");
        generateTypeNodes(moduleNode, moduleNode.getOutputTypes(), false, baseInd + 2);
        // Hints and intent
        generateStepHints(moduleNode, baseInd + 1);
        generateStepIntent(moduleNode, baseInd + 1);
    }

    /**
     * Generate the hints related to this step's run.
     * 
     * @param moduleNode The {@link ModuleNode} that is the step.
     * @param baseInd    The indentation at which the hints should start.
     */
    private void generateStepHints(ModuleNode moduleNode, int baseInd) {
        cwlRepresentation
                // "hints" key
                .append(ind(baseInd))
                .append("hints:")
                .append("\n")
                // "SoftwareRequirement" key
                .append(ind(baseInd + 1))
                .append("SoftWareRequirement:")
                .append("\n")
                // "packages" key
                .append(ind(baseInd + 2))
                .append("packages:")
                .append("\n")
                // The required package
                .append(ind(baseInd + 3))
                .append(
                        String.format("%s: [\"%s\"]",
                                moduleNode.getNodeLabel(),
                                moduleNode.getUsedModule().getPredicateID()))
                .append("\n");
    }

    /**
     * Generate the intent for a workflow step's run.
     * 
     * @param moduleNode The {@link ModuleNode} that is the workflow step.
     * @param baseInd    The indentation level at which the intent should start.
     */
    private void generateStepIntent(ModuleNode moduleNode, int baseInd) {
        cwlRepresentation
                .append(ind(baseInd))
                .append("intent: ")
                .append("[");
        for (TaxonomyPredicate predicate : moduleNode.getUsedModule().getSuperPredicates()) {
            cwlRepresentation
                    .append("\"")
                    .append(predicate.getPredicateID())
                    .append("\"")
                    .append(", ");
        }
        deleteLastNCharactersFromCWL(2);
        cwlRepresentation.append("]").append("\n");
    }

    /**
     * Generate the inputs or outputs of a step's run.
     * 
     * @param moduleNode   The {@link ModuleNode} that is the workflow step.
     * @param typeNodeList The {@link TypeNode}s that are either the input or output
     *                     nodes.
     * @param input        Whether the type nodes are inputs. If false, they are
     *                     consider outputs.
     * @param baseInd      The indentation level of the "inputs:" or "outputs:" line
     *                     preceding this function.
     */
    private void generateTypeNodes(ModuleNode moduleNode, List<TypeNode> typeNodeList, boolean input, int baseInd) {
        // If the input or output nodes are empty, give an empty array as input or
        // output
        if (typeNodeList.isEmpty()) {
            // Remove the last newline so the array is on the same line as "inputs:"
            deleteLastNCharactersFromCWL(1);
            cwlRepresentation
                    .append(" ")
                    .append("[]")
                    .append("\n");
            return;
        }

        int i = 0;
        for (TypeNode typeNode : typeNodeList) {
            // Don't include empty nodes
            if (typeNode.isEmpty()) {
                return;
            }

            i++;
            cwlRepresentation
                    // Name
                    .append(ind(baseInd))
                    .append(inOutName(moduleNode, input ? "in" : "out", i))
                    .append(":\n")
                    // Data type
                    .append(ind(baseInd + 1))
                    .append("type: File")
                    .append("\n")
                    // Format
                    .append(ind(baseInd + 1))
                    .append("format: ")
                    .append(typeNode.getFormat())
                    .append("\n");
        }
    }

    /**
     * Generate the name of the input or output of a step's run input or output.
     * I.e. "moduleName_indicator_n".
     * 
     * @param moduleNode The {@link ModuleNode} that is the workflow step.
     * @param indicator  Indicator whether it is an input or an output.
     * @param n          The n-th input or output this is.
     * @return The name of the input or output.
     */
    private String inOutName(ModuleNode moduleNode, String indicator, int n) {
        return String.format("%s_%s_%o",
                moduleNode.getNodeLabel(),
                indicator,
                n);
    }

    /**
     * Add a parameter to the parameter hashmap.
     * Will log an error to the error output if {@link TypeNode} is already known.
     * 
     * @param typeNode The {@link TypeNode} that is the input parameter.
     * @param name     The name of the parameter.
     * @return The ID of the parameter.
     */
    private String addNewParameterToMap(TypeNode typeNode, String name) {
        if (workflowParameters.putIfAbsent(typeNode.getNodeID(), name) != null) {
            log.warn("Duplicate key \"{}\" in workflow inputs!", typeNode.getNodeID());
        }
        return typeNode.getNodeID();
    }

    /**
     * Generate the name for a step in the workflow.
     * 
     * @param moduleNode The {@link ModuleNode} that is the workflow step.
     * @return The name of the workflow step.
     */
    private String stepName(ModuleNode moduleNode) {
        return moduleNode.getUsedModule().getPredicateLabel() +
                moduleNode.getAutomatonState().getLocalStateNumber();
    }

    /**
     * Generates the CWL inputs as a YML file content.
     * 
     * @return Content of a YML file that describes the CWL workflow inputs.
     */
    public String generateCWLWorkflowInputs() {
        return getInputsInCWL(true);
    }
}
