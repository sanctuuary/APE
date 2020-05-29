package nl.uu.cs.ape.sat.core.implSAT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.core.SolutionInterpreter;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AuxTaxonomyPredicate;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Literal;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;

/**
 * The {@code SAT_solution} class describes the solution produced by the SAT
 * solver. It stores the original solution and the mapped one. In case of the
 * parameter <b>unsat</b> being {@code true}, there are no solutions. <br>
 * <br>
 * It also implements general solution interface {@link SolutionInterpreter}.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_solution extends SolutionInterpreter {

	/** List of all the literals provided by the solution. */
	private final List<Literal> literals;
	/** List of all the positive literals provided by the solution. */
	private final List<Literal> postitiveLiterals;
	/**
	 * List of only relevant (positive) literals that represent implemented
	 * modules/tools.
	 */
	private final List<Literal> relevantModules;
	/** List of only relevant (positive) literals that represent simple types. */
	private final List<Literal> relevantTypes;
	/** List of all the relevant types and modules combined. */
	private final List<Literal> relevantElements;
	/**
	 * List of all the references for the types in the memory, when used as tool
	 * inputs.
	 */
	private final List<Literal> references2MemTypes;
	private final Set<PredicateLabel> usedTypeStates;
	/**
	 * True if the there is no solution to the problem. Problem is UNASATISFIABLE.
	 */
	private final boolean unsat;

	/**
	 * Creating a list of Literals to represent the solution.
	 * 
	 * @param satSolution - list of mapped literals given as a list of integers
	 *                    (library SAT output)
	 * @param atomMapping - mapping of the atoms
	 * @param allModules  - list of all the modules
	 * @param allTypes    - list of all the types
	 */
	public SAT_solution(int[] satSolution, SAT_SynthesisEngine synthesisInstance) {
		unsat = false;
		literals = new ArrayList<Literal>();
		postitiveLiterals = new ArrayList<Literal>();
		relevantModules = new ArrayList<Literal>();
		relevantTypes = new ArrayList<Literal>();
		relevantElements = new ArrayList<Literal>();
		references2MemTypes = new ArrayList<Literal>();
		usedTypeStates = new HashSet<PredicateLabel>();
		for (int mappedLiteral : satSolution) {
			if (mappedLiteral > synthesisInstance.getMappings().getMaxNumOfMappedAuxVar()) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), synthesisInstance.getMappings());
				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					postitiveLiterals.add(currLiteral);
					if(currLiteral.getPredicate() instanceof AuxTaxonomyPredicate) {
						continue;
					} else if (currLiteral.getPredicate() instanceof Module) {
						/* add all positive literals that describe tool implementations */
						relevantElements.add(currLiteral);
						relevantModules.add(currLiteral);
					} else if (currLiteral.getWorkflowElementType() != WorkflowElement.MODULE
							&& currLiteral.getWorkflowElementType() != WorkflowElement.MEM_TYPE_REFERENCE
							&& (currLiteral.getPredicate() instanceof Type)
							&& ((Type) currLiteral.getPredicate()).isSimplePredicate()) {
						/* add all positive literals that describe simple types */
						relevantElements.add(currLiteral);
						relevantTypes.add(currLiteral);
						usedTypeStates.add(currLiteral.getUsedInStateArgument());
					} else if (currLiteral.getPredicate() instanceof State
							&& ((State) (currLiteral.getPredicate())).getAbsoluteStateNumber() != -1) {
						/*
						 * add all positive literals that describe memory type references that are not
						 * pointing to null state (NULL state has AbsoluteStateNumber == -1)
						 */
						references2MemTypes.add(currLiteral);
						relevantElements.add(currLiteral);
					}
				}
			}
		}
		Collections.sort(relevantModules);
		Collections.sort(relevantTypes);
		Collections.sort(references2MemTypes);
		Collections.sort(relevantElements);
	}

	/**
	 * Creating an empty solution, for UNSAT problem. The list <b>literals</b> is
	 * NULL.
	 */
	public SAT_solution() {
		unsat = true;
		literals = null;
		postitiveLiterals = null;
		relevantModules = null;
		relevantTypes = null;
		relevantElements = null;
		references2MemTypes = null;
		usedTypeStates = null;
	}

	/**
	 * Returns the solution in human readable format.
	 * 
	 * @return String representing the solution (only positive literals).
	 */
	public String getSolution() {
		StringBuilder solution = new StringBuilder();
		if (unsat) {
			solution = new StringBuilder("UNSAT");
		} else {
			for (Literal literal : postitiveLiterals) {
				solution = solution.append(literal.toString()).append(" ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns the complete solution in human readable format, including the
	 * negative predicates.
	 * 
	 * @return String representing the solution positive and negative literals).
	 */
	public String getCompleteSolution() {
		StringBuilder solution = new StringBuilder();
		if (unsat) {
			solution = new StringBuilder("UNSAT");
		} else {
			for (Literal literal : literals) {
				solution = solution.append(literal.toString()).append(" ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns only the most important part of the solution in human readable
	 * format, filtering out the information that are not required to generate the
	 * workflow. The solution literals are sorted according the state they are used
	 * in.
	 * 
	 * @return String representing the tools used in the solution
	 */
	public String getRelevantToolsInSolution() {
		StringBuilder solution = new StringBuilder();
		if (unsat) {
			solution = new StringBuilder("UNSAT");
		} else {
			for (Literal literal : relevantModules) {
				solution = solution.append(literal.toString()).append(" ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns only the most important part of the solution in human readable
	 * format, filtering out the information that are not required to generate the
	 * workflow. The solution literals are sorted according the state they are used
	 * in.
	 * 
	 * @return String representing the tools and data used in the solutions
	 */
	public String getRelevantSolution() {
		StringBuilder solution = new StringBuilder();
		if (unsat) {
			solution = new StringBuilder("UNSAT");
		} else {
			for (Literal relevantElement : relevantElements) {
				solution = solution.append(relevantElement.toString() + " ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns the list of modules, corresponding to their position in the workflow.
	 * 
	 * @param allModules - list of all the modules in the domain
	 * @return List of {@link Module}s in the order they appear in the solution
	 *         workflow.
	 */
	public List<Module> getRelevantSolutionModules(AllModules allModules) {
		List<Module> solutionModules = new ArrayList<Module>();
		if (unsat) {
			return null;
		} else {
			for (Literal literal : relevantModules) {
				solutionModules.add((Module) allModules.get(literal.getPredicate().getPredicateID()));
			}
		}
		return solutionModules;
	}

	/**
	 * Returns the solution in mapped format. The original solution created by the
	 * SAT solver.
	 * 
	 * @return String representing the mapped solution created by SAT solver.
	 */
	public String getOriginalSATSolution() {
		StringBuilder solution = new StringBuilder();
		if (!unsat) {
			for (Literal literal : literals) {
				solution = solution.append(literal.toMappedString()).append(" ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray(boolean toolSeqRepeat) {
		List<Integer> negSol = new ArrayList<Integer>();
		if (!unsat) {
			if(!toolSeqRepeat) {
				for (Literal literal : relevantModules) {
					negSol.add(literal.toNegatedMappedInt());
				}
			} else {
			for (Literal literal : relevantElements) {
				if (literal.getWorkflowElementType() != WorkflowElement.MEMORY_TYPE) {
					negSol.add(literal.toNegatedMappedInt());
				}
			}
			}
		}
		int[] negSolList = new int[negSol.size()];
		for (int i = 0; i < negSol.size(); i++) {
			negSolList[i] = negSol.get(i);
		}

		return negSolList;
	}

	/**
	 * Returns the satisfiability of the problem. Returns TRUE if the problem is
	 * satisfiable, FALSE otherwise.
	 * 
	 * @return TRUE if the problem is satisfiable, FALSE otherwise.
	 */
	public boolean isSat() {
		return !unsat;
	}

}
