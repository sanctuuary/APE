package nl.uu.cs.ape.sat.core.solutionStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.misc.OrderedHashSet;

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
	/** Mapping used to allow us to determine memory instances that are used as tool inputs. */
	private Map<State, ModuleNode> inputMappingFunction;
	
	private SAT_solution nativeSolution;
	
	
	public SolutionWorkflow(ModuleAutomaton toolAutomaton, TypeAutomaton typeAutomaton) throws Exception {
		this.moduleNodes = new ArrayList<ModuleNode>();
		this.workflowInputTypeStates = new HashSet<TypeNode>();
		this.workflowOutputTypeStates = new HashSet<TypeNode>();
		this.allModuleNodes = new HashMap<State, ModuleNode>();
		this.allMemoryTypeNodes = new HashMap<State, TypeNode>();
		this.inputMappingFunction = new HashMap<State, ModuleNode>();
		
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
			ModuleNode typeCreatedByTool = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber() - 1);
			for(State currState : currBlock.getStates()) {
				TypeNode currTypeNode = new TypeNode(currState);
				currTypeNode.setCreatedByModule(typeCreatedByTool);
				if(typeCreatedByTool != null) {
					typeCreatedByTool.addOutputType(currTypeNode);
				}
				this.allMemoryTypeNodes.put(currState, currTypeNode);
				if(currBlock.getBlockNumber()==0) {
					workflowInputTypeStates.add(currTypeNode);
				} else if(currBlock.getBlockNumber() == toolAutomaton.size()) {
					workflowOutputTypeStates.add(currTypeNode);
				}
			}
		}
		/* Use used type blocks to define INPUT relationship between memory instances and tools. */ 
		for(Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			ModuleNode inputForTool = APEUtils.safeGet(moduleNodes, currBlock.getBlockNumber());
			for(State currState : currBlock.getStates()) {
				this.inputMappingFunction.put(currState, inputForTool);
			}
		}
	}
	
	public SolutionWorkflow(int[] satSolution, SAT_SynthesisEngine synthesisIntance) throws Exception {
		this(synthesisIntance.getModuleAutomaton(), synthesisIntance.getTypeAutomaton());
		nativeSolution = new SAT_solution(satSolution, synthesisIntance);
		
		for (int mappedLiteral : satSolution) {
			if (mappedLiteral > synthesisIntance.getMappings().getMaxNumOfMappedAuxVar()) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), synthesisIntance.getMappings());
//				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					if (currLiteral.getWorkflowElementType() == WorkflowElement.MODULE) {
						ModuleNode currNode = this.allModuleNodes.get(currLiteral.getUsedInStateArgument());
						if(currLiteral.getPredicate() instanceof Module) {
							currNode.setUsedModule((Module) currLiteral.getPredicate());
						} else {
							currNode.addAbstractDescriptionOfUsedType((AbstractModule) currLiteral.getPredicate());
						}
					} else if (currLiteral.getWorkflowElementType() == WorkflowElement.MEMORY_TYPE) {
						TypeNode currNode = this.allMemoryTypeNodes.get(currLiteral.getUsedInStateArgument());
						if(((Type) currLiteral.getPredicate()).isSimpleType()) {
							currNode.addUsedType((Type) currLiteral.getPredicate());
						} else {
							currNode.addAbstractDescriptionOfUsedType((Type) currLiteral.getPredicate());
						}
					} else if (currLiteral.getWorkflowElementType() == WorkflowElement.USED_TYPE
							&& ((Type) currLiteral.getPredicate()).isSimpleType()) {
						continue;
					} else if(currLiteral.getWorkflowElementType() == WorkflowElement.MEM_TYPE_REFERENCE &&
							((State) (currLiteral.getPredicate())).getAbsoluteStateNumber() != -1) {
						/* add all positive literals that describe memory type references that are not pointing to null state (NULL state has AbsoluteStateNumber == -1) */
						ModuleNode usedTypeNode = this.inputMappingFunction.get(currLiteral.getUsedInStateArgument());
						TypeNode memoryTypeNode = this.allMemoryTypeNodes.get(currLiteral.getPredicate());
						if(usedTypeNode != null) {
							usedTypeNode.addInputType(memoryTypeNode);
						}
						memoryTypeNode.addUsedByTool(usedTypeNode);
					} 
				}
			}
		}
		
		for (Iterator<TypeNode> iterator = workflowInputTypeStates.iterator(); iterator.hasNext();) {
			TypeNode currNode =  iterator.next();
		    if (currNode.isEmpty()) {
		        iterator.remove();
		    }       
		}
		
		for (Iterator<TypeNode> iterator = workflowOutputTypeStates.iterator(); iterator.hasNext();) {
			TypeNode currNode =  iterator.next();
		    if (currNode.isEmpty()) {
		        iterator.remove();
		    }       
		}
		
	}

	public List<ModuleNode> getModuleNodes() {
		return moduleNodes;
	}

	public Set<TypeNode> getWorkflowInputTypeStates() {
		return workflowInputTypeStates;
	}

	public Set<TypeNode> getWorkflowOutputTypeStates() {
		return workflowOutputTypeStates;
	}
	
	public SAT_solution getnativeSATsolution() {
		return nativeSolution;
	}
	
	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray() {
		return nativeSolution.getNegatedMappedSolutionArray();
	}
	
	public String getReadableSolution() {
		StringBuilder solution = new StringBuilder();
		
		solution = solution.append("WORKFLOW_IN:{");
		int i = 0;
		for(TypeNode workflowInput : workflowInputTypeStates) {
			solution = solution.append(workflowInput.toString());
			if(++i < this.workflowInputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("} |");
		
		for(ModuleNode currTool : moduleNodes) {
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
		for(TypeNode workflowOutput : workflowOutputTypeStates) {
			solution = solution.append(workflowOutput.toString());
			if(++i < workflowOutputTypeStates.size()) {
				solution = solution.append(", ");
			}
		}
		solution = solution.append("}");
		
		return solution.toString();
	}
	
	
	public String getSolutionDotFormat() {
		StringBuilder solution = new StringBuilder();
		
		String input = "\"Workflow INPUT\"";
		String output = "\"Workflow OUTPUT\"";
		boolean inputDefined = false, outputDefined = false;
		
		for(TypeNode workflowInput : workflowInputTypeStates) {
			if(!inputDefined) {
				System.out.println(input + " [shape=box, color = red];");
				inputDefined = true;
			}
			solution = solution.append(input+ "->" + workflowInput.getDotID() + ";");
			solution = solution.append(workflowInput.getDotDefinition());
		}
		
		for(ModuleNode currTool : moduleNodes) {
			solution = solution.append(currTool.getDotDefinition());
			for(TypeNode toolInput : currTool.getInputTypes()) {
				if(!toolInput.isEmpty()) {
					solution = solution.append(toolInput.getDotID() + "->" + currTool.getDotID() + "[label = in, fontsize = 10];");
				}
			}
			for(TypeNode toolOutput : currTool.getOutputTypes()) {
				if(!toolOutput.isEmpty()) {
					solution = solution.append(toolOutput.getDotDefinition());
					solution = solution.append(currTool.getDotID() + "->" + toolOutput.getDotID() + " [label = out, fontsize = 10];");
				}
			}
		}
		for(TypeNode workflowOutput : workflowOutputTypeStates) {
			if(!outputDefined) {
				solution = solution.append(output + " [shape=box, color = red];");
				outputDefined = true;
			}
			solution = solution.append(workflowOutput.getDotDefinition());
			solution = solution.append(workflowOutput.getDotID() + "->" + output + ";");
		}
		
		return solution.toString();
	}
	
	
}
