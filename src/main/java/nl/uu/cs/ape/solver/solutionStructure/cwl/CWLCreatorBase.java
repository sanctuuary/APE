package nl.uu.cs.ape.solver.solutionStructure.cwl;

import nl.uu.cs.ape.solver.solutionStructure.ModuleNode;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;

/**
 * Base class with shared behavior for CWL export classes.
 */
public abstract class CWLCreatorBase {
    /**
     * Cwl representation.
     */
    protected final StringBuilder cwlRepresentation;
    /**
     * Solution.
     */
    protected SolutionWorkflow solution;
    /**
     * Indent style used.
     */
    private IndentStyle indentStyle;

    /**
     * Generate the creator base from the workflow solution.
     * 
     * @param solution - APE workflow solution
     */
    protected CWLCreatorBase(SolutionWorkflow solution) {
        this.solution = solution;
        this.cwlRepresentation = new StringBuilder();
        this.indentStyle = IndentStyle.SPACES2;
    }

    /**
     * Override the default indentation style.
     * 
     * @param indentStyle The indentation style to use.
     * @return A reference to this CWLCreator.
     */
    public CWLCreatorBase setIndentStyle(IndentStyle indentStyle) {
        this.indentStyle = indentStyle;
        return this;
    }

    /**
     * Get the CWL version required for the CWL file.
     * 
     * @return The required CWL version.
     */
    public abstract String getCWLVersion();

    /**
     * Generates the CWL representation.
     * 
     * @return The CWL representation.
     */
    public String generate() {
        // Top of file comment
        generateTopComment();

        cwlRepresentation.append(String.format("cwlVersion: %s%n", getCWLVersion()));
        cwlRepresentation.append("class: Workflow").append("\n");

        // Label and doc
        cwlRepresentation.append("\n");
        cwlRepresentation.append("label: ").append(getWorkflowName()).append("\n");
        generateDoc();

        generateCWLRepresentation();
        return cwlRepresentation.toString();
    }

    /**
     * Adds the comment at the top of the file.
     */
    protected void generateTopComment() {
        cwlRepresentation.append(String.format("# %s%n", getWorkflowName()));
        cwlRepresentation.append("# This workflow is generated by APE (https://github.com/sanctuuary/APE).\n");
    }

    /**
     * Get the name of the workflow.
     * 
     * @return The name of the workflow.
     */
    private String getWorkflowName() {
        return String.format("WorkflowNo_%o", solution.getIndex());
    }

    /**
     * Generate the workflow description.
     */
    private void generateDoc() {
        cwlRepresentation.append("doc: ");
        cwlRepresentation.append("A workflow including the tool(s) ");
        // List all used tools
        for (ModuleNode moduleNode : solution.getModuleNodes()) {
            cwlRepresentation
                    .append(moduleNode.getNodeLabel())
                    .append(", ");
        }
        deleteLastNCharactersFromCWL(2);
        cwlRepresentation.append(".").append("\n").append("\n");
    }

    /**
     * Generate the main part of the CWL representation.
     */
    protected abstract void generateCWLRepresentation();

    /**
     * Gets the CWL representation.
     * 
     * @return The CWL representation.
     */
    public String getCWL() {
        return cwlRepresentation.toString();
    }

    /**
     * Delete a number of characters at the end of the CWL file.
     * 
     * @param numberOfCharToDel The number of characters to remove.
     */
    protected void deleteLastNCharactersFromCWL(int numberOfCharToDel) {
        cwlRepresentation.delete(cwlRepresentation.length() - numberOfCharToDel, cwlRepresentation.length());
    }

    /**
     * Generate the indentation at the start of a line.
     * 
     * @param level The level of indentation.
     * @return The indentation of the given level.
     */
    protected String ind(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append(this.indentStyle);
        }
        return builder.toString();
    }

    /**
     * The available indentation styles.
     */
    public enum IndentStyle {
        /** Two spaces for indentation. */
        SPACES2("  "),
        /** Four spaces for indentation. */
        SPACES4("    ");

        private final String text;

        IndentStyle(String s) {
            this.text = s;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }
}