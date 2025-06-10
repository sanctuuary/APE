package nl.uu.cs.ape.solver.solutionStructure.cwl;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Class to generate a CWL structure for a tool.
 */
@Slf4j
public class ToolCWLCreator extends CWLToolBase {
    /**
     * Instantiates a new CWL creator.
     * 
     * @param module The solution to represent in CWL.
     */
    public ToolCWLCreator(Module module) {
        super(module);
    }

    /**
     * Generates the CWL representation.
     */
    @Override
    protected void generateCWLRepresentation() {
        // Tool annotations
        generateToolOperations();
        generateWorkflowInOut("inputs", super.getFormats(tool.getModuleInput(), "in"),
                super.getTypes(tool.getModuleInput(), "in"));
        generateWorkflowInOut("outputs", super.getFormats(tool.getModuleOutput(), "out"),
                super.getTypes(tool.getModuleOutput(), "out"));
    }

    private void generateToolOperations() {
        cwlRepresentation.append("intent:\n");
        for (TaxonomyPredicate operation : super.tool.getParentPredicates()) {
            cwlRepresentation
                    .append(("  - "))
                    .append(operation.getPredicateLongLabel())
                    .append("\n");
        }
        cwlRepresentation.append("\n");

    }

    /**
     * Generate the inputs and outputs of the tool.
     * 
     * @param header     The header of the section (inputs/outputs).
     * @param mapFormats The formats of the inputs/outputs.
     * @param mapTypes   The data types of the inputs/outputs.
     */
    private void generateWorkflowInOut(String header, Map<String, List<TaxonomyPredicate>> mapFormats,
            Map<String, List<TaxonomyPredicate>> mapTypes) {
        cwlRepresentation.append(header).append(":\n");
        // Inputs
        mapFormats.entrySet().forEach(input ->

        cwlRepresentation
                // Name
                .append(ind(1))
                .append(input.getKey())
                .append(":\n")
                // Data type
                .append(ind(2))
                .append("type: File")
                .append("\n")
                // Format
                .append(ind(2))
                .append("format: ")
                .append(formatType(input.getValue(), "format"))
                .append("\n")
                // Type
                .append(ind(2))
                .append("edam:data_0006: ")
                .append(formatType(mapTypes.get(input.getKey()), "data type"))
                .append("\n"));
    }

    /**
     * Get the format of the current workflow node and a label for the CWL file.
     * 
     * @param list
     *                 List of {@link TaxonomyPredicate} representing the formats or
     *                 types.
     * @param typeName
     *                 The name of the type (e.g., "format" or "type").
     */
    public String formatType(List<TaxonomyPredicate> list, String typeName) {
        StringBuilder printString = new StringBuilder();

        if (!list.isEmpty()) {
            printString.append("\"").append(list.get(0).getPredicateLongLabel()).append("\"  # ")
                    .append(list.get(0).getPredicateLabel()).append("\n");
        }
        if (list.size() > 1) {
            log.info("Multiple " + typeName + "s for the same input/output are not supported.");
            printString.append(ind(2)).append(
                    "doc: The I/O is annotated with multiple " + typeName
                            + "s in bio.tools. We specified here the first one. To implement other combinations you would have to create more CWL files. Please check the instructions on how to handle such cases: https://workflomics.readthedocs.io/en/latest/domain-expert-guide/domain-development.html");
        }

        return printString.toString();
    }

}
