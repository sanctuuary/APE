package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.WorkflowElement;
import nl.uu.cs.ape.sat.models.constructs.Literal;
import nl.uu.cs.ape.sat.models.constructs.Predicate;

/**
 * The {@code SAT_solution} class describes the solution produced by the SAT
 * solver. It stores the original solution and the mapped one. In case of the
 * parameter <b>unsat</b> being {@code true}, there are no solutions. <br>
 * <br>
 * It also implements general solution interface {@link Solution}.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_solution extends Solution {

	/** List of all the literals provided by the solution.*/
	private List<Literal> literals;
	/** List of all the positive literals provided by the solution.*/
	private List<Literal> postitiveLiterals;
	/**List of only relevant (positive) literals that represent implemented
	 * modules/tools.  */
	private List<Literal> relevantModules;
	/** List of only relevant (positive) literals that represent simple types.*/
	private List<Literal> relevantTypes;
	/** List of all the relevant types and modules combined.  */
	private List<Literal> relevantElements;
	/** List of all the references for the types in the memory, when used as tool inputs.  */
	private List<Literal> references2MemTypes;
	private Set<Predicate> usedTypeStates;
	/** True if the there is no solution to the problem. Problem is UNASATISFIABLE. */
	private boolean unsat;
	/** Lengths of the current solution. */
	private int solutionLength;


	/**
	 * Creating a list of Literals to represent the solution.
	 * 
	 * @param satSolution - list of mapped literals given as a list of integers
	 *                    (library SAT output)
	 * @param atomMapping - mapping of the atoms
	 * @param allModules  - list of all the modules
	 * @param allTypes    - list of all the types
	 */
	public SAT_solution(int[] satSolution, AtomMapping atomMapping, AllModules allModules, AllTypes allTypes,
			int solutionLength) {
		unsat = false;
		literals = new ArrayList<Literal>();
		postitiveLiterals = new ArrayList<Literal>();
		relevantModules = new ArrayList<Literal>();
		relevantTypes = new ArrayList<Literal>();
		relevantElements = new ArrayList<Literal>();
		references2MemTypes = new ArrayList<Literal>();
		usedTypeStates = new HashSet<Predicate>();
		for (int mappedLiteral : satSolution) {
			if (mappedLiteral > atomMapping.getMaxNumOfMappedAuxVar()) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), atomMapping);
				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					postitiveLiterals.add(currLiteral);
					if (currLiteral.getPredicate() instanceof Module) {
						/* add all positive literals that describe tool implementations */
						relevantElements.add(currLiteral);
						relevantModules.add(currLiteral);
					} else if (currLiteral.getWorkflowElementType() != WorkflowElement.MODULE && currLiteral.getWorkflowElementType() != WorkflowElement.MEM_TYPE_REFERENCE
							&& ((Type) currLiteral.getPredicate()).isSimpleType()) {
						/* add all positive literals that describe simple types */
						relevantElements.add(currLiteral);
						relevantTypes.add(currLiteral);
						usedTypeStates.add(currLiteral.getUsedInStateArgument());
					} else if(currLiteral.getPredicate() instanceof State && ((State) (currLiteral.getPredicate())).getAbsoluteStateNumber() != -1) {
						/* add all positive literals that describe memory type references that are not pointing to null state */
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
		this.solutionLength = solutionLength;
	}

	/**
	 * Creating an empty solution, for UNSAT problem. The list <b>literals</b> is
	 * NULL.
	 */
	public SAT_solution() {
		unsat = true;
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
			for(Literal relevantElement : relevantElements) {
				solution = solution.append(relevantElement.toString() + " ");
			}
		}
		return solution.toString();
	}

	/**
	 * Returns the list of modules, corresponding to their position in the workflow.
	 * 
	 * @return List of {@link Module}s in the order they appear in the solution
	 *         workflow.
	 */
	public List<Module> getRelevantSolutionModules(AllModules allModules) {
		List<Module> solutionModules = new ArrayList<Module>();
		if (unsat) {
			return null;
		} else {
			for (Literal literal : relevantModules) {
				solutionModules.add((Module) allModules.get(literal.getPredicate().getPredicate()));
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
	 * @return String representing the negated solution
	 
	public String getNegatedMappedSolution() {
		StringBuilder solution = new StringBuilder();
		if (!unsat) {
			for (Literal literal : literals) {
				if (!literal.isNegated() && literal.isModule() && (literal.getPredicate() instanceof Module)) {
					solution = solution.append(literal.toNegatedMappedString()).append(" ");
				}
			}
		}
		return solution + " 0";
	}*/

	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray() {
		List<Integer> negSol = new ArrayList<Integer>();
		if (!unsat) {
			for (Literal literal : relevantElements) {
				if (literal.getWorkflowElementType() != WorkflowElement.MEMORY_TYPE) {
					negSol.add(literal.toNegatedMappedInt());
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
	 * TODO - NOT WORKING!! Returns all the permutations of the negated solution in
	 * mapped format. Negating the original solution created by the SAT solver.
	 * Usually used to add to the solver to find new solutions, by omitting the
	 * permutations.
	 * 
	 * @param typeAutomaton
	 * @param moduleAutomaton
	 * 
	 * @return String representing all permutations of the negated solution
	 */
	public String getNegatedMappedSolutionPermutations(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		StringBuilder solution = new StringBuilder();
		if (unsat) {
			return "";
		} else {
			List<String> predicates = new ArrayList<String>();
			List<String> types = new ArrayList<String>();

			for (Literal literal : relevantModules) {
				predicates.add("-" + literal.getPredicate().getPredicate());
			}
			for (Literal literal : relevantTypes) {
				types.add("-" + literal.getPredicate().getPredicate());
			}
		}
		return solution + " 0";
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
