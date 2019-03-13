package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.constructs.Literal;
import nl.uu.cs.ape.sat.models.constructs.Predicate;

/**
 * The {@code SAT_solution} class describes the solution produced by the SAT solver. It stores the
 * original solution and the mapped one. In case of the parameter <b>unsat</b> being
 * {@code true}, there are no solutions.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_solution {

	/*
	 * List of all the literals provided by the solution.
	 */
	private List<Literal> literals;
	/*
	 * List of only relevant (positive) literals that represent implemented modules/tools.
	 */
	private List<Literal> relevantModules;
	/*
	 * List of only relevant (positive) literals that represent simple types.
	 */
	private List<Literal> relevantTypes;
	private boolean unsat;

	/**
	 * Creating a list of Literals to represent the solution.
	 * 
	 * @param satOutput
	 *            - list of mapped literals given as text (SAT output)
	 * @param atomMapping
	 *            - mapping of the atoms
	 * @param allModules
	 *            - list of all the modules
	 * @param allTypes
	 *            - list of all the types
	 */
	public SAT_solution(String satOutput, AtomMapping atomMapping, AllModules allModules, AllTypes allTypes) {
		unsat = false;
		literals = new ArrayList<>();
		relevantModules = new ArrayList<>();
		relevantTypes = new ArrayList<>();
		String[] mappedLiterals = satOutput.split(" ");
		for (String mappedLiteral : mappedLiterals) {
			if (!mappedLiteral.matches("0")) {
				Literal currLiteral = new Literal(mappedLiteral, atomMapping, allModules, allTypes);
				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					if (currLiteral.getPredicate() instanceof Module) {
						relevantModules.add(currLiteral);
					} else if (!currLiteral.isModule() && ((Type) currLiteral.getPredicate()).isSimpleType()) {
						relevantTypes.add(currLiteral);
					}
				}
			}
		}
		Collections.sort(relevantModules);
		Collections.sort(relevantTypes);
	}
	
	/**
	 * Creating a list of Literals to represent the solution.
	 * 
	 * @param satSolution
	 *            - list of mapped literals given as a list of integers (library SAT output)
	 * @param atomMapping
	 *            - mapping of the atoms
	 * @param allModules
	 *            - list of all the modules
	 * @param allTypes
	 *            - list of all the types
	 */
	public SAT_solution(int[] satSolution, AtomMapping atomMapping, AllModules allModules, AllTypes allTypes) {
		unsat = false;
		literals = new ArrayList<>();
		relevantModules = new ArrayList<>();
		relevantTypes = new ArrayList<>();
		for (int mappedLiteral : satSolution) {
			if (mappedLiteral != 0) {
				Literal currLiteral = new Literal(Integer.toString(mappedLiteral), atomMapping, allModules, allTypes);
				literals.add(currLiteral);
				if (!currLiteral.isNegated()) {
					if (currLiteral.getPredicate() instanceof Module) {
						relevantModules.add(currLiteral);
					} else if (!currLiteral.isModule() && ((Type) currLiteral.getPredicate()).isSimpleType()) {
						relevantTypes.add(currLiteral);
					}
				}
			}
		}
		Collections.sort(relevantModules);
		Collections.sort(relevantTypes);
	}

	/**
	 * Creating an empty solution, for UNSAT problem. The list <b>literals</b> is NULL.
	 */
	public SAT_solution() {
		unsat = true;
	}

	/**
	 * Returns the solution in human readable format.
	 * 
	 * @return String representing the solution
	 */
	public String getSolution() {
		String solution = "";
		if (unsat) {
			solution = "UNSAT";
		} else {
			for (Literal literal : literals) {
				solution += literal.toString() + " ";
			}
		}
		return solution;
	}

	/**
	 * Returns only the most important part of the solution in human readable
	 * format, filtering out the information that are not required to generate the
	 * workflow. The solution literals are sorted according the state they are used
	 * in.
	 * 
	 * @return String representing the solution
	 */
	public String getRelevantSolution() {
		String solution = "";
		if (unsat) {
			solution = "UNSAT";
		} else {
			for (Literal literal : relevantModules) {
				solution += literal.toString() + " ";
			}
		}
		return solution;
	}
	
	/**
	 * Returns the list of modules, corresponding to their position in the workflow.
	 * 
	 * @return List of {@link Module}s in the order they appear in the solution workflow.
	 */
	public List<Module> getRelevantSolutionModules(AllModules allModules) {
		List<Module> solutionModules = new ArrayList<>();
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
	 * @return String representing the mapped solution
	 */
	public String getMappedSolution() {
		String solution = "";
		if (unsat) {
			solution = "";
		} else {
			for (Literal literal : literals) {
				solution += literal.toMappedString() + " ";
			}
		}
		return solution;
	}

	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return String representing the negated solution
	 */
	public String getNegatedMappedSolution() {
		String solution = "";
		if (unsat) {
			solution = "";
		} else {
			for (Literal literal : literals) {
				if (!literal.isNegated() && literal.isModule() && (literal.getPredicate() instanceof Module)) {
					solution += literal.toNegatedMappedString() + " ";
				}
			}
		}
		return solution + " 0";
	}
	
	
	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 * 
	 * @return int[] representing the negated solution
	 */
	public int[] getNegatedMappedSolutionArray() {
		List<Integer> negSol = new ArrayList<>();
		if (!unsat) {
			for (Literal literal : literals) {
				if(!literal.isNegated())
//				System.out.println(literal.getPredicate().getPredicate() + "\t is N:" + literal.isNegated() + ", is module:" + literal.isModule() + ", Module: " +(literal.getPredicate() instanceof Module));
				if (!literal.isNegated() && literal.isModule() && (literal.getPredicate() instanceof Module)) {
					negSol.add(literal.toNegatedMappedInt());
				}
			}
		}
		int[] negSolList = new int[negSol.size()];
		for(int i=0;i<negSol.size();i++) {
			negSolList[i] = negSol.get(i);
		}
		
		return negSolList;
	}

	/**
	 * TODO
	 * Returns all the permutations of the negated solution in mapped format.
	 * Negating the original solution created by the SAT solver. Usually used to add
	 * to the solver to find new solutions, by omitting the permutations.
	 * @param typeAutomaton 
	 * @param moduleAutomaton 
	 * 
	 * @return String representing all permutations of the negated solution
	 */
	public String getNegatedMappedSolutionPermutations(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		String solution = "";
		if (unsat) {
			return "";
		} else {
			List<String> predicates = new ArrayList<>();
			List<String> types = new ArrayList<>();

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
