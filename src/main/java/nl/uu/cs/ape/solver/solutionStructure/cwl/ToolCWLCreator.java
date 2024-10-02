package nl.uu.cs.ape.solver.solutionStructure.cwl;

import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

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
        generateWorkflowInOut("inputs", super.getInputs());
        generateWorkflowInOut("outputs", super.getOutputs());
    }

    /**
     * Generate the inputs and outputs of the tool.
     * 
     * @param header The header of the section (inputs/outputs).
     * @param map    The types of the inputs/outputs.
     */
    private void generateWorkflowInOut(String header, Map<String, List<TaxonomyPredicate>> map) {
        cwlRepresentation.append(header).append(":\n");
        // Inputs
        map.entrySet().forEach(input ->

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
                .append(formatType(input.getValue()))
                .append("\n"));
    }

    /**
     * Get the format of the current workflow node and a label for the CWL file.
     * 
     * @param list
     */
    public String formatType(List<TaxonomyPredicate> list) {
        StringBuilder printString = new StringBuilder();

        if (!list.isEmpty()) {
            printString.append("\"").append(list.get(0).getPredicateLongLabel()).append("\" # ").append(list.get(0).getPredicateLabel()).append("\n");
        }
        if (list.size() > 1) {
            log.warn("Multiple types for the same input/output are not supported.");
            printString.append(ind(2)).append("doc: The I/O is annotated with multiple data formats in bio.tools. We specified here the first one, while the rest are part of tool.json file. Please check the instructions on how to handle such cases: https://workflomics.readthedocs.io/en/domain-creation/developer-guide/domain-development.html#id2 ");
        }

        return printString.toString();
    }

}
