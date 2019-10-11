package nl.uu.cs.ape.sat.core.solutionStructure;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import guru.nidi.graphviz.attribute.Arrow;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.LinkAttr;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SAT_solution;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.constructs.Literal;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code SolutionWorkflow} class is used to represent a single workflow solution. The workflow consists of {@link SolutionWorkflowNode}s.
 * 
 * @author Vedran Kasalica
 *
 */
public class SolutionWorkflow {

	/** List of module nodes ordered according to their position in the workflow. */
	private List<ModuleNode> moduleNodes;
	/** List of memory type nodes provided as the initial workflow input, ordered according the initial description (ape.config file). */
	private Set<TypeNode> workflowInputTypeStates;
	/** List of used type nodes provided as the final workflow output, ordered according the initial description (ape.config file). */
	private Set<TypeNode> workflowOutputTypeStates;
	/** Map of all {@code ModuleNodes}, where key value is the {@link State} provided by the {@link ModuleAutomaton}. */
	private Map<State, ModuleNode> allModuleNodes;
	/** Map of all {@code MemTypeNode}, where key value is the {@link State} provided by the {@link TypeAutomaton}. */
	private Map<State, TypeNode> allMemoryTypeNodes;
	/** Mapping used to allow us to determine the correlation between the usage of data instances and the actual
	 *  tools that take the instance as input. A mapping is a pair of an Automaton {@link State} that depicts 
	 *  {@link WorkflowElement#USED_TYPE} and a {@link ModuleNode}.<br><br> If the second is NULL, the data is used as WORKFLOW OUTPUT. */
	private Map<State, ModuleNode> usedType2ToolMap;
	/** Non-structured solution obtained directly from the SAT output. */
	private SAT_solution nativeSolution;
	
	/**
	 * Create the structure of the {@link SolutionWorkflow} based on the {@link ModuleAutomaton} and {@link TypeAutomaton} provided.
	 * 
	 * @param toolAutomaton
	 * @param typeAutomaton
	 * @throws Exception exception in case of a mismatch between the type of automaton states and workflow nodes.
	 */
	public SolutionWorkflow(ModuleAutomaton toolAutomaton, TypeAutomaton typeAutomaton) throws Exception {
		this.moduleNodes = new ArrayList<ModuleNode>();
		this.workflowInputTypeStates = new HashSet<TypeNode>();
		this.workflowOutputTypeStates = new HashSet<TypeNode>();
		this.allModuleNodes = new HashMap<State, ModuleNode>();
		this.allMemoryTypeNodes = new HashMap<State, TypeNode>();
		this.usedType2ToolMap = new HashMap<State, ModuleNode>();
		
		ModuleNode prev = null;
		for(State currState : toolAutomaton.getModuleStates()) {
			ModuleNode currNode = new ModuleNode(currState);
			currNode.setPrevModuleNode(prev);
			if(prev!=null) {
				prev.setNextModuleNode(currNode);
			}
			this.moduleNodes.add(currNode);
			this.allModuleNodes.put(currState, currNode);
			prev = currNode;
		}
		
		for(Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			/** typeGenerator represents the tool that created the current block of types as output. If NULL the block is the  initial workflow input. */
			ModuleNode typeGenerator = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber() - 1);
			for(State currState : currBlock.getStates()) {
				TypeNode currTypeNode = new TypeNode(currState);
				currTypeNode.setCreatedByModule(typeGenerator);
				if(typeGenerator != null) {
					typeGenerator.addOutputType(currTypeNode);
				}
				this.allMemoryTypeNodes.put(currState, currTypeNode);
				if(currBlock.getBlockNumber()==0) {
					this.workflowInputTypeStates.add(currTypeNode);
				} else if(currBlock.getBlockNumber() == toolAutomaton.size()) {
//					this.workflowOutputTypeStates.add(currTypeNode); THIS IS WRONG
				}
			}
		}
		/** Use the used type blocks to define INPUT relationship between memory instances and tools (that use it as input). 
		 * The types that are used as final workflow output are input for NULL object.*/ 
		for(Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			ModuleNode inputForTool = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber());
			for(State currState : currBlock.getStates()) {
				this.usedType2ToolMap.put(currState, inputForTool);
			}
		}
	}
	/**
	 * Create a solution workflow, based on the SAT output.
	 * 
	 * @param satSolution - SAT solution, presented as array of integers.
	 * @param synthesisIntance - current synthesis instance
	 * @throws Exception in case of a mismatch between the type of automaton states and workflow nodes.
	 */
	public SolutionWorkflow(int[] satSolution, SAT_SynthesisEngine synthesisIntance) throws Exception {
		/** Call for the default constructor. */
		this(synthesisIntance.getModuleAutomaton(), synthesisIntance.getTypeAutomaton());
		
		this.nativeSolution = new SAT_solution(satSolution, synthesisIntance);
		
		for (int mappedLiteral : satSolution) {
			if (mappedLiteral > synthesisIntance.getMappings().getMaxNumOfMappedAuxVar()) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), synthesisIntance.getMappings());
//				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					if (currLiteral.isWorkflowElementType(WorkflowElement.MODULE)) {
						ModuleNode currNode = this.allModuleNodes.get(currLiteral.getUsedInStateArgument());
						if(currLiteral.getPredicate() instanceof Module) {
							currNode.setUsedModule((Module) currLiteral.getPredicate());
						} else {
							currNode.addAbstractDescriptionOfUsedType((AbstractModule) currLiteral.getPredicate());
						}
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.MEMORY_TYPE)) {
						TypeNode currNode = this.allMemoryTypeNodes.get(currLiteral.getUsedInStateArgument());
						if(((Type) currLiteral.getPredicate()).isSimpleType()) {
							currNode.addUsedType((Type) currLiteral.getPredicate());
						} else {
							currNode.addAbstractDescriptionOfUsedType((Type) currLiteral.getPredicate());
						}
					} else if (currLiteral.isWorkflowElementType(WorkflowElement.USED_TYPE)
							&& ((Type) currLiteral.getPredicate()).isSimpleType()) {
						continue;
					} else if(currLiteral.isWorkflowElementType(WorkflowElement.MEM_TYPE_REFERENCE) &&
							((State) (currLiteral.getPredicate())).getAbsoluteStateNumber() != -1) {
						/* Add all positive literals that describe memory type references that are not pointing to null state (NULL state has AbsoluteStateNumber == -1), i.e. that are valid. */
						ModuleNode usedTypeNode = this.usedType2ToolMap.get(currLiteral.getUsedInStateArgument());
						TypeNode memoryTypeNode = this.allMemoryTypeNodes.get(currLiteral.getPredicate());
						if(usedTypeNode != null) {
							usedTypeNode.addInputType(memoryTypeNode);
						} else {
							this.workflowOutputTypeStates.add(memoryTypeNode);
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

	public List<ModuleNode> getModuleNodes() {
		return this.moduleNodes;
	}

	public Set<TypeNode> getWorkflowInputTypeStates() {
		return this.workflowInputTypeStates;
	}

	public Set<TypeNode> getWorkflowOutputTypeStates() {
		return this.workflowOutputTypeStates;
	}
	
	/**
	 * Get non-structured solution obtained directly from the SAT output.
	 * @return Object of class {@link SAT_solution}
	 */
	public SAT_solution getnativeSATsolution() {
		return this.nativeSolution;
	}
	
	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray() {
		return this.nativeSolution.getNegatedMappedSolutionArray();
	}
	
	/**
	 * Get a readable version of the workflow solution.
	 * @return Printable String that represents the solution workflow.
	 */
	public String getReadableSolution() {
		StringBuilder solution = new StringBuilder();
		
		solution = solution.append("WORKFLOW_IN:{");
		int i = 0;
		for(TypeNode workflowInput : this.workflowInputTypeStates) {
			solution = solution.append(workflowInput.toString());
			if(++i < this.workflowInputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("} |");
		
		for(ModuleNode currTool : this.moduleNodes) {
			solution = solution.append(" IN:{");
			i = 0;
			for(TypeNode toolInput : currTool.getInputTypes()) {
				if(!toolInput.isEmpty()) {
					if(i++ > 1) {
						solution = solution.append(", ");
					}
					solution = solution.append(toolInput.toString());
				}
			}
			solution = solution.append("} ").append(currTool.toString());
			solution = solution.append(" OUT:{");
			i = 0;
			for(TypeNode toolOutput : currTool.getOutputTypes()) {
				if(!toolOutput.isEmpty()) {
					if(i++ > 1) {
						solution = solution.append(", ");
					}
					solution = solution.append(toolOutput.toString());
				}
			}
			solution = solution.append("} |");
		}
		i = 0;
		solution = solution.append("WORKFLOW_OUT:{");
		for(TypeNode workflowOutput : this.workflowOutputTypeStates) {
			solution = solution.append(workflowOutput.toString());
			if(++i < this.workflowOutputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("}");
		
		return solution.toString();
	}
	
	/**
	 * Get a graph that represent the solution in .dot format (see http://www.graphviz.org/).
	 * @param title - title of the graph
	 * @return String that represents the solution workflow in .dot graph format.
	 */
	public String getSolutionDotFormat() {
		StringBuilder solution = new StringBuilder();
		
		String input = "\"Workflow INPUT\"";
		String output = "\"Workflow OUTPUT\"";
		boolean inputDefined = false, outputDefined = false;
		
		for(TypeNode workflowInput : this.workflowInputTypeStates) {
			if(!inputDefined) {
				System.out.println(input + " [shape=box, color = red];\n");
				inputDefined = true;
			}
			solution = solution.append(input+ "->" + workflowInput.getDotID() + ";\n");
			solution = solution.append(workflowInput.getDotDefinition());
		}
		
		for(ModuleNode currTool : this.moduleNodes) {
			solution = solution.append(currTool.getDotDefinition());
			for(TypeNode toolInput : currTool.getInputTypes()) {
				if(!toolInput.isEmpty()) {
					solution = solution.append(toolInput.getDotID() + "->" + currTool.getDotID() + "[label = in, fontsize = 10];\n");
				}
			}
			for(TypeNode toolOutput : currTool.getOutputTypes()) {
				if(!toolOutput.isEmpty()) {
					solution = solution.append(toolOutput.getDotDefinition());
					solution = solution.append(currTool.getDotID() + "->" + toolOutput.getDotID() + " [label = out, fontsize = 10];\n");
				}
			}
		}
		for(TypeNode workflowOutput : this.workflowOutputTypeStates) {
			if(!outputDefined) {
				solution = solution.append(output + " [shape=box, color = red];\n");
				outputDefined = true;
			}
			solution = solution.append(workflowOutput.getDotDefinition());
			solution = solution.append(workflowOutput.getDotID() + "->" + output + ";\n");
		}
		
		return solution.toString();
	}
	
	/**
	 * Get a graph that represent the solution.
	 * @param title - title of the graph
	 * @return {@link Graph} object that represents the solution workflow.
	 */
	public Graph getSolutionGraph(String title) {
		
		Graph workflowGraph = graph(title).directed()
		        .graphAttr().with(RankDir.TOP_TO_BOTTOM);
		
		String input = "Workflow INPUT" + "     ";
		String output = "Workflow OUTPUT" + "     ";
		boolean inputDefined = false, outputDefined = false;
		int index = 0;
		for(TypeNode workflowInput : this.workflowInputTypeStates) {
			index++;
			if(!inputDefined) {
				workflowGraph = workflowGraph.with(node(input).with(Color.RED, Shape.RECTANGLE, Style.BOLD));
				inputDefined = true;
			}
			workflowGraph = workflowInput.addTypeToGraph(workflowGraph);
			workflowGraph = workflowGraph.with(node(input).link(to(node(workflowInput.getDotID())).with(LinkAttr.weight(index), Style.DOTTED)));
		}
		
		for(ModuleNode currTool : this.moduleNodes) {
			workflowGraph = currTool.addModuleToGraph(workflowGraph);
//			index = 0;
			for(TypeNode toolInput : currTool.getInputTypes()) {
				if(!toolInput.isEmpty()) {
					index++;
					workflowGraph = workflowGraph.with(node(toolInput.getDotID()).link(to(node(currTool.getDotID())).with(Label.of("in   "), Color.ORANGE, LinkAttr.weight(index))));
				}
			}
//			index = 0;
			for(TypeNode toolOutput : currTool.getOutputTypes()) {
				if(!toolOutput.isEmpty()) {
					index++;
					workflowGraph = toolOutput.addTypeToGraph(workflowGraph);
					workflowGraph = workflowGraph.with(node(currTool.getDotID()).link(to(node(toolOutput.getDotID())).with(Label.of("out   "), Color.BLACK, LinkAttr.weight(index))));
				}
			}
		}
//		index = 0;
		for(TypeNode workflowOutput : this.workflowOutputTypeStates) {
			index++;
			if(!outputDefined) {
				workflowGraph = workflowGraph.with(node(output).with(Color.RED, Shape.RECTANGLE, Style.BOLD));
				outputDefined = true;
			}
			workflowGraph = workflowOutput.addTypeToGraph(workflowGraph);
			workflowGraph = workflowGraph.with(node(workflowOutput.getDotID()).link(to(node(output)).with(LinkAttr.weight(index), Style.DOTTED)));
		}
		
		return workflowGraph;
	}
	
	public int getSolutionlength() {
		return this.moduleNodes.size();
	}
}
