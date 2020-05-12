/**
 * 
 */
package nl.uu.cs.ape.sat.core.solutionStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code CWLCreator} class is used to
 *
 * @author Vedran Kasalica
 *
 */
public class CWLCreator {
	/* Textual representation of the CWL file */
	private StringBuilder cwlRepresentation;
	private final String cwlVersion = "v1.2-dev1";

	public CWLCreator(SolutionWorkflow solution, APEConfig apeConfig) {
		cwlRepresentation = new StringBuilder(
				"class: WorkflowNo_" + solution.getIndex() + "\ncwlVersion: " + cwlVersion + "").append("\n");
		generateCWLRepresentation(solution, apeConfig);
	}

	public String getCWL() {
		return cwlRepresentation.toString();
	}

	private void generateCWLRepresentation(SolutionWorkflow solution, APEConfig apeConfig) {
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
	 * @param moduleNode
	 * @param apeConfig
	 * @param tabs
	 */
	private void defineCWLStep(ModuleNode moduleNode, APEConfig apeConfig, int i) {
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

		///
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
	 * @param moduleNode
	 * @param apeConfig
	 * @param i
	 */
	private void getOperationRun(ModuleNode moduleNode, APEConfig apeConfig, int i) {
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
	 * 
	 * @param typeNode
	 * @param apeConfig
	 * @param string
	 */
	private void getNewCWLDataInstance(TypeNode typeNode, APEConfig apeConfig, int i) {
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
	 * 
	 * @param typeNode
	 * @param apeConfig
	 * @param string
	 */
	private void getExistingCWLDataInstance(TypeNode typeNode, APEConfig apeConfig, int i, String prefix) {
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

	private String toYmlArray(Collection<TaxonomyPredicate> formats) {
		String str = "[";
		for (TaxonomyPredicate format : formats) {
			str = str + format.getPredicateID() + ",";
		}
		return APEUtils.removeLastChar(str) + "]";
	}

	private String tabs(int size) {
		String tabs = "";
		for (int i = 0; i < size; i++) {
			tabs = tabs + "\t";
		}
		return tabs;
	}
}
