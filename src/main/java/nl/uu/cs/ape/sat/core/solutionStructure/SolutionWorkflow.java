package nl.uu.cs.ape.sat.core.solutionStructure;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.LinkAttr;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SAT_solution;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AuxTaxonomyPredicate;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Literal;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code SolutionWorkflow} class is used to represent a single workflow
 * solution. The workflow consists of {@link SolutionWorkflowNode}s.
 * 
 * @author Vedran Kasalica
 *
 */
public class SolutionWorkflow {
	/** List of module nodes ordered according to their position in the workflow. */
	private List<ModuleNode> moduleNodes;
	/**
	 * List of memory type nodes provided as the initial workflow input, ordered
	 * according the initial description (ape.config file).
	 */
	private List<TypeNode> workflowInputTypeStates;
	/**
	 * List of used type nodes provided as the final workflow output, ordered
	 * according the initial description (ape.config file).
	 */
	private List<TypeNode> workflowOutputTypeStates;
	/**
	 * Map of all {@code ModuleNodes}, where key value is the {@link State} provided
	 * by the {@link ModuleAutomaton}.
	 */
	private Map<State, ModuleNode> mappedModuleNodes;
	/**
	 * Map of all {@code MemTypeNode}, where key value is the {@link State} provided
	 * by the {@link TypeAutomaton}.
	 */
	private Map<State, TypeNode> mappedMemoryTypeNodes;
	/**
	 * Mapping used to allow us to determine the correlation between the usage of
	 * data instances and the actual tools that take the instance as input. A
	 * mapping is a pair of an Automaton {@link State} that depicts
	 * {@link WorkflowElement#USED_TYPE} and a {@link ModuleNode}.<br>
	 * <br>
	 * If the second is NULL, the data is used as WORKFLOW OUTPUT.
	 */
	private Map<State, ModuleNode> usedType2ToolMap;
	/** Non-structured solution obtained directly from the SAT output. */
	private SAT_solution nativeSolution;
	/** Graph representation of the data-flow workflow solution. */
	private SolutionGraph dataflowGraph;
	/** Graph representation of the control-flow workflow solution. */
	private SolutionGraph controlflowGraph;
	/** Index of the solution. */
	private int index;

	/**
	 * private Renderer renderedGraph;
	 * 
	 * /** Create the structure of the {@link SolutionWorkflow} based on the
	 * {@link ModuleAutomaton} and {@link TypeAutomaton} provided.
	 * 
	 * @param toolAutomaton
	 * @param typeAutomaton
	 * @throws Exception exception in case of a mismatch between the type of
	 *                   automaton states and workflow nodes.
	 */
	private SolutionWorkflow(ModuleAutomaton toolAutomaton, TypeAutomaton typeAutomaton)
			throws ExceptionInInitializerError {
		this.moduleNodes = new ArrayList<ModuleNode>();
		this.workflowInputTypeStates = new ArrayList<TypeNode>();
		this.workflowOutputTypeStates = new ArrayList<TypeNode>();
		this.mappedModuleNodes = new HashMap<State, ModuleNode>();
		this.mappedMemoryTypeNodes = new HashMap<State, TypeNode>();
		this.usedType2ToolMap = new HashMap<State, ModuleNode>();

		ModuleNode prev = null;
		for (State currState : toolAutomaton.getModuleStates()) {
			ModuleNode currNode = new ModuleNode(currState);
			currNode.setPrevModuleNode(prev);
			if (prev != null) {
				prev.setNextModuleNode(currNode);
			}
			this.moduleNodes.add(currNode);
			this.mappedModuleNodes.put(currState, currNode);
			prev = currNode;
		}

		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			/**
			 * typeGenerator represents the tool that created the current block of types as
			 * output. If NULL the block is the initial workflow input.
			 */
			ModuleNode typeGenerator = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber() - 1);
			for (State currState : currBlock.getStates()) {
				TypeNode currTypeNode = new TypeNode(currState);
				currTypeNode.setCreatedByModule(typeGenerator);
				if (typeGenerator != null) {
					typeGenerator.addOutputType(currTypeNode);
				}
				this.mappedMemoryTypeNodes.put(currState, currTypeNode);
				if (currBlock.getBlockNumber() == 0) {
					this.workflowInputTypeStates.add(currTypeNode);
				} else if (currBlock.getBlockNumber() == toolAutomaton.size()) {
//					this.workflowOutputTypeStates.add(currTypeNode); THIS IS WRONG
				}
			}
		}
		/**
		 * Use the used type blocks to define INPUT relationship between memory
		 * instances and tools (that use it as input). The types that are used as final
		 * workflow output are input for NULL object.
		 */
		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			ModuleNode inputForTool = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber());
			for (State currState : currBlock.getStates()) {
				this.usedType2ToolMap.put(currState, inputForTool);
			}
		}
	}

	/**
	 * Create a solution workflow, based on the SAT output.
	 * 
	 * @param satSolution      - SAT solution, presented as array of integers.
	 * @param synthesisIntance - current synthesis instance
	 * @throws Exception in case of a mismatch between the type of automaton states
	 *                   and workflow nodes.
	 */
	public SolutionWorkflow(int[] satSolution, SAT_SynthesisEngine synthesisIntance) {
		/** Call for the default constructor. */
		this(synthesisIntance.getModuleAutomaton(), synthesisIntance.getTypeAutomaton());

		this.nativeSolution = new SAT_solution(satSolution, synthesisIntance);

		for (int mappedLiteral : satSolution) {
			if (mappedLiteral > synthesisIntance.getMappings().getMaxNumOfMappedAuxVar()) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), synthesisIntance.getMappings());
				if (!currLiteral.isNegated()) {
					if (currLiteral.getPredicate() instanceof AuxTaxonomyPredicate) {
						continue;
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.MODULE)) {
						ModuleNode currNode = this.mappedModuleNodes.get(currLiteral.getUsedInStateArgument());
						if (currLiteral.getPredicate() instanceof Module) {
							currNode.setUsedModule((Module) currLiteral.getPredicate());
						} else {
							currNode.addAbstractDescriptionOfUsedType((AbstractModule) currLiteral.getPredicate());
						}
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.MEMORY_TYPE)) {
						TypeNode currNode = this.mappedMemoryTypeNodes.get(currLiteral.getUsedInStateArgument());
						if (currLiteral.getPredicate() instanceof Type
								&& ((Type) currLiteral.getPredicate()).isSimplePredicate()) {
							currNode.addUsedType((Type) currLiteral.getPredicate());
						} else if (currLiteral.getPredicate() instanceof Type) {
							currNode.addAbstractDescriptionOfUsedType((Type) currLiteral.getPredicate());
						} else {
							/* Memory type cannot be anything else except a Type. */
						}
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.USED_TYPE)
							&& ((Type) currLiteral.getPredicate()).isSimplePredicate()) {
						continue;
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.MEM_TYPE_REFERENCE)
							&& ((State) (currLiteral.getPredicate())).getAbsoluteStateNumber() != -1) {
						/*
						 * Add all positive literals that describe memory type references that are not
						 * pointing to null state (NULL state has AbsoluteStateNumber == -1), i.e. that
						 * are valid.
						 */
						ModuleNode usedTypeNode = this.usedType2ToolMap.get(currLiteral.getUsedInStateArgument());
						TypeNode memoryTypeNode = this.mappedMemoryTypeNodes.get(currLiteral.getPredicate());
						int inputIndex = currLiteral.getUsedInStateArgument().getStateNumber();
						/** Keep the order of inputs as they were defined in the solution file. */
						if (usedTypeNode != null) {
							usedTypeNode.setInputType(inputIndex, memoryTypeNode);
						} else {
							APEUtils.safeSet(this.workflowOutputTypeStates, inputIndex, memoryTypeNode);
						}
						memoryTypeNode.addUsedByTool(usedTypeNode);
					}
				}
			}
		}

		/** Remove empty elements of the sets. */
		this.workflowInputTypeStates.removeIf(node -> node.isEmpty());

		this.workflowOutputTypeStates.removeIf(node -> node.isEmpty());

	}

	/**
	 * Method returns the list of nodes that represent operations in the workflow,
	 * in order in which they should be executed.
	 * 
	 * @return List of {@link ModuleNode} objects, in order in which they should be
	 *         executed.
	 */
	public List<ModuleNode> getModuleNodes() {
		return this.moduleNodes;
	}

	/**
	 * Method returns the set of initial data types that are given as an input to
	 * the workflow.
	 * 
	 * @return List of {@link TypeNode} objects, where each node describes a
	 *         specific data instance.
	 */
	public List<TypeNode> getWorkflowInputTypeStates() {
		return this.workflowInputTypeStates;
	}

	/**
	 * Method returns the set of final data types that are given as an output of the
	 * workflow.
	 * 
	 * @return List of {@link TypeNode} objects, where each node describes a
	 *         specific data instance.
	 */
	public List<TypeNode> getWorkflowOutputTypeStates() {
		return this.workflowOutputTypeStates;
	}

	/**
	 * Get non-structured solution obtained directly from the SAT output.
	 * 
	 * @return A {@link SAT_solution} object, that contains information about the
	 *         native SAT encoding, and how it translates into human
	 */
	public SAT_solution getNativeSATsolution() {
		return this.nativeSolution;
	}

	/**
	 * Get the graphical representation of the data-flow diagram in default
	 * {@link RankDir#TOP_TO_BOTTOM} direction.
	 * 
	 * @return the field {@link dataflowGraph}.
	 */
	public SolutionGraph getDataflowGraph() {
		if (this.dataflowGraph != null) {
			return this.dataflowGraph;
		} else {
			return generateFieldDataflowGraph("", RankDir.TOP_TO_BOTTOM);
		}
	}

	/**
	 * Get the graphical representation of the data-flow diagram with the required
	 * title and in the defined orientation.
	 * 
	 * @param title       - the title of the SolutionGraph
	 * @param orientation - orientation of the solution graph (e.g.
	 *                    {@link RankDir#TOP_TO_BOTTOM}
	 * @return the solution graph
	 */
	public SolutionGraph getDataflowGraph(String title, RankDir orientation) {
		if (this.dataflowGraph != null) {
			return this.dataflowGraph;
		} else {
			return generateFieldDataflowGraph(title, orientation);
		}
	}

	/**
	 * Get the graphical representation of the control-flow diagram in default
	 * {@link RankDir#TOP_TO_BOTTOM} direction.
	 * 
	 * @return the field {@link controlflowGraph}.
	 */
	public SolutionGraph getControlflowGraph() {
		if (this.controlflowGraph != null) {
			return this.controlflowGraph;
		} else {
			return generateFieldControlflowGraph("", RankDir.TOP_TO_BOTTOM);
		}
	}

	/**
	 * /** Get the graphical representation of the control-flow diagram with the
	 * required title and in the defined orientation.
	 * 
	 * @param title       - the title of the SolutionGraph
	 * @param orientation - orientation of the solution graph (e.g.
	 *                    {@link RankDir#TOP_TO_BOTTOM}
	 * @return the solution graph
	 */
	public SolutionGraph getControlflowGraph(String title, RankDir orientation) {
		if (this.controlflowGraph != null) {
			return this.controlflowGraph;
		} else {
			return generateFieldControlflowGraph(title, orientation);
		}
	}

	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray(boolean toolSeqRepeat) {
		return this.nativeSolution.getNegatedMappedSolutionArray(toolSeqRepeat);
	}

	/**
	 * Get a readable version of the workflow solution.
	 * 
	 * @return Printable String that represents the solution workflow.
	 */
	public String getReadableSolution() {
		StringBuilder solution = new StringBuilder();

		solution = solution.append("WORKFLOW_IN:{");
		int i = 0;
		for (TypeNode workflowInput : this.workflowInputTypeStates) {
			solution = solution.append(workflowInput.toString());
			if (++i < this.workflowInputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("} |");

		for (ModuleNode currTool : this.moduleNodes) {
			solution = solution.append(" IN:{");
			i = 0;
			for (TypeNode toolInput : currTool.getInputTypes()) {
				if (!toolInput.isEmpty()) {
					if (i++ > 1) {
						solution = solution.append(", ");
					}
					solution = solution.append(toolInput.toString());
				}
			}
			solution = solution.append("} ").append(currTool.toString());
			solution = solution.append(" OUT:{");
			i = 0;
			for (TypeNode toolOutput : currTool.getOutputTypes()) {
				if (!toolOutput.isEmpty()) {
					if (i++ > 1) {
						solution = solution.append(", ");
					}
					solution = solution.append(toolOutput.toString());
				}
			}
			solution = solution.append("} |");
		}
		i = 0;
		solution = solution.append("WORKFLOW_OUT:{");
		for (TypeNode workflowOutput : this.workflowOutputTypeStates) {
			solution = solution.append(workflowOutput.toString());
			if (++i < this.workflowOutputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("}");

		return solution.toString();
	}

	/**
	 * Get a graph that represent the solution in .dot format (see
	 * http://www.graphviz.org/).
	 * 
	 * @param title - title of the graph
	 * @return String that represents the solution workflow in .dot graph format.
	 */
	public String getSolutionDotFormat() {
		StringBuilder solution = new StringBuilder();

		String input = "\"Workflow INPUT\"";
		String output = "\"Workflow OUTPUT\"";
		boolean inputDefined = false, outputDefined = false;

		for (TypeNode workflowInput : this.workflowInputTypeStates) {
			if (!inputDefined) {
				System.out.println(input + " [shape=box, color = red];\n");
				inputDefined = true;
			}
			solution = solution.append(input + "->" + workflowInput.getNodeID() + ";\n");
			solution = solution.append(workflowInput.getDotDefinition());
		}

		for (ModuleNode currTool : this.moduleNodes) {
			solution = solution.append(currTool.getDotDefinition());
			for (TypeNode toolInput : currTool.getInputTypes()) {
				if (!toolInput.isEmpty()) {
					solution = solution.append(
							toolInput.getNodeID() + "->" + currTool.getNodeID() + "[label = in, fontsize = 10];\n");
				}
			}
			for (TypeNode toolOutput : currTool.getOutputTypes()) {
				if (!toolOutput.isEmpty()) {
					solution = solution.append(toolOutput.getDotDefinition());
					solution = solution.append(
							currTool.getNodeID() + "->" + toolOutput.getNodeID() + " [label = out, fontsize = 10];\n");
				}
			}
		}
		for (TypeNode workflowOutput : this.workflowOutputTypeStates) {
			if (!outputDefined) {
				solution = solution.append(output + " [shape=box, color = red];\n");
				outputDefined = true;
			}
			solution = solution.append(workflowOutput.getDotDefinition());
			solution = solution.append(workflowOutput.getNodeID() + "->" + output + ";\n");
		}

		return solution.toString();
	}

	/**
	 * Generate a graph that represent the data-flow solution and set is as the
	 * field {@link #dataflowGraph} of the current object .
	 * 
	 * @param title - title of the graph
	 * @return {@link Graph} object that represents the solution workflow.
	 */
	private SolutionGraph generateFieldDataflowGraph(String title, RankDir orientation) {
		Graph workflowGraph = graph(title).directed().graphAttr().with(orientation);

		String input = "Workflow INPUT" + "     ";
		String output = "Workflow OUTPUT" + "     ";
		boolean inputDefined = false, outputDefined = false;
		int index = 0;
		int workflowInNo = 1;
		for (TypeNode workflowInput : this.workflowInputTypeStates) {
			if (!inputDefined) {
				workflowGraph = workflowGraph.with(node(input).with(Color.RED, Shape.RECTANGLE, Style.BOLD));
				inputDefined = true;
			}
			workflowGraph = workflowInput.addTypeToGraph(workflowGraph);
			workflowGraph = workflowGraph.with(node(input).link(to(node(workflowInput.getNodeID()))
					.with(Label.of((workflowInNo++) + "  "), LinkAttr.weight(index++), Style.DOTTED)));
		}

		for (ModuleNode currTool : this.moduleNodes) {
			workflowGraph = currTool.addModuleToGraph(workflowGraph);
			int inputNo = 1;
			for (TypeNode toolInput : currTool.getInputTypes()) {
				if (!toolInput.isEmpty()) {
					workflowGraph = workflowGraph.with(node(toolInput.getNodeID()).link(to(node(currTool.getNodeID()))
							.with(Label.of("in " + (inputNo++) + "  "), Color.ORANGE, LinkAttr.weight(index++))));
				}
			}
			int outputNo = 1;
			for (TypeNode toolOutput : currTool.getOutputTypes()) {
				if (!toolOutput.isEmpty()) {
					workflowGraph = toolOutput.addTypeToGraph(workflowGraph);
					workflowGraph = workflowGraph.with(node(currTool.getNodeID()).link(to(node(toolOutput.getNodeID()))
							.with(Label.of("out " + (outputNo++) + "  "), Color.BLACK, LinkAttr.weight(index++))));
				}
			}
		}
		int workflowOutNo = 1;
		for (TypeNode workflowOutput : this.workflowOutputTypeStates) {
			if (!outputDefined) {
				workflowGraph = workflowGraph.with(node(output).with(Color.RED, Shape.RECTANGLE, Style.BOLD));
				outputDefined = true;
			}
			workflowGraph = workflowOutput.addTypeToGraph(workflowGraph);
			workflowGraph = workflowGraph.with(node(workflowOutput.getNodeID()).link(
					to(node(output)).with(Label.of((workflowOutNo++) + "  "), LinkAttr.weight(index++), Style.DOTTED)));
		}
		this.dataflowGraph = new SolutionGraph(workflowGraph);
		return this.dataflowGraph;
	}

	/**
	 * Generate a graph that represent the control-flow solution and set is as the
	 * field {@link #controlflowGraph} of the current object .
	 * 
	 * @param title - title of the graph
	 * @return {@link Graph} object that represents the solution workflow.
	 */
	private SolutionGraph generateFieldControlflowGraph(String title, RankDir orientation) {
		Graph workflowGraph = graph(title).directed().graphAttr().with(orientation);

		String input = "START" + "     ";
		String output = "END" + "     ";
		workflowGraph = workflowGraph.with(node(input).with(Color.BLACK, Style.BOLD));
		String prevNode = input;
		for (ModuleNode currTool : this.moduleNodes) {
			workflowGraph = currTool.addModuleToGraph(workflowGraph);
			workflowGraph = workflowGraph
					.with(node(prevNode).link(to(node(currTool.getNodeID())).with(Label.of("next   "), Color.RED)));
			prevNode = currTool.getNodeID();
		}
		workflowGraph = workflowGraph.with(node(output).with(Color.BLACK, Style.BOLD));
		workflowGraph = workflowGraph.with(node(prevNode).link(to(node(output)).with(Label.of("next   "), Color.RED)));

		this.controlflowGraph = new SolutionGraph(workflowGraph);
		return this.controlflowGraph;
	}

	public int getSolutionlength() {
		return this.moduleNodes.size();
	}

	/**
	 * Sets the index of the solution in all the solutions.
	 * 
	 * @param i
	 */
	public void setIndex(int i) {
		this.index = i;

	}

	/**
	 * Returns the index of the solution in all the solutions.
	 */
	public int getIndex() {
		return this.index;

	}

	/**
	 * Return the executable shell script, that corresponds to the given workflow.
	 * 
	 * @return String that represents the shell script for executing the given
	 *         workflow.
	 */
	public String getScriptExecution() {
		StringBuffer script = new StringBuffer("#!/bin/bash\n");
		script = script.append("if [ $# -ne " + workflowInputTypeStates.size() + " ]\n\tthen\n");
		script = script
				.append("\t\techo \"" + workflowInputTypeStates.size() + " argument(s) expected.\"\n\t\texit\nfi\n");
		int in = 1;
		for (TypeNode input : workflowInputTypeStates) {
			script = script.append(input.getShortNodeID() + "=$" + (in++) + "\n");
		}
		script = script.append("\n");
		for (ModuleNode operation : moduleNodes) {
			String code = operation.getUsedModule().getExecutionCode();
			if (code == null || code.equals("")) {
				script = script.append("\"Error. Tool '" + operation.getNodeLabel() + "' is missing the execution code.\"")
						.append("\n");
			} else {
				for (int i = 0; i < operation.getInputTypes().size(); i++) {
					code = code.replace("@input[" + i + "]", operation.getInputTypes().get(i).getShortNodeID());
				}
				for (int i = 0; i < operation.getOutputTypes().size(); i++) {
					code = code.replace("@output[" + i + "]", operation.getOutputTypes().get(i).getShortNodeID());
				}
				script = script.append(code).append("\n");
			}
		}
		int out = 1;
		for (TypeNode output : workflowOutputTypeStates) {
			script = script.append("echo \"" + (out++) + ". output is: $" + output.getShortNodeID() + "\"");
		}

		return script.toString();
	}

}
