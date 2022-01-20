package nl.uu.cs.ape.core.implSAT;

import java.util.*;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.SolutionInterpreter;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.satStruc.Literal;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code SATSolution} class describes the solution produced by the SAT
 * solver. It stores the original solution and the mapped one. In case of the
 * parameter <b>unsat</b> being true, there are no solutions.
 * <p>
 * It also implements general solution interface {@link SolutionInterpreter}.
 *
 * @author Vedran Kasalica
 */
public class SATSolution extends SolutionInterpreter {

    /**
     * List of all the literals provided by the solution.
     */
    private final List<Literal> literals;

    /**
     * List of all the positive literals provided by the solution.
     */
    private final List<Literal> positiveLiterals;

    /**
     * List of only relevant (positive) literals that represent implemented modules/tools.
     */
    private final List<Literal> relevantModules;

    /**
     * List of only relevant (positive) literals that represent simple types.
     */
    private final List<Literal> relevantTypes;

    /**
     * List of all the relevant types and modules combined.
     */
    private final List<Literal> relevantElements;

    /**
     * List of all the references for the types in the memory, when used as tool inputs.
     */
    private final List<Literal> references2MemTypes;
    private final Set<PredicateLabel> usedTypeStates;

    /**
     * true if the there is no solution to the problem. Problem is UNASATISFIABLE.
     */
    private final boolean unsat;

    /**
     * Creating a list of Literals to represent the solution.
     *
     * @param satSolution       list of mapped literals given as a list of integers (library SAT output)
     * @param synthesisInstance Mapping of the atoms.
     */
    public SATSolution(int[] satSolution, SATSynthesisEngine synthesisInstance) {
        unsat = false;
        literals = new ArrayList<Literal>();
        positiveLiterals = new ArrayList<Literal>();
        relevantModules = new ArrayList<Literal>();
        relevantTypes = new ArrayList<Literal>();
        relevantElements = new ArrayList<Literal>();
        references2MemTypes = new ArrayList<Literal>();
        usedTypeStates = new HashSet<PredicateLabel>();
        for (int mappedLiteral : satSolution) {
            if (mappedLiteral >= synthesisInstance.getMappings().getInitialNumOfMappedAtoms()) {
                Literal currLiteral = new Literal(Integer.toString(mappedLiteral), synthesisInstance.getMappings());
                literals.add(currLiteral);
                if (!currLiteral.isNegated()) {
                    positiveLiterals.add(currLiteral);
                    if (currLiteral.getPredicate() instanceof AuxiliaryPredicate) {
                        continue;
                    } else if (currLiteral.getPredicate() instanceof Module) {
                        /* add all positive literals that describe tool implementations */
                        relevantElements.add(currLiteral);
                        relevantModules.add(currLiteral);
                    } else if (currLiteral.getWorkflowElementType() != AtomType.MODULE
                            && currLiteral.getWorkflowElementType() != AtomType.MEM_TYPE_REFERENCE
                            && currLiteral.getWorkflowElementType() != AtomType.TYPE_DEPENDENCY
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
     * Creating an empty solution, for UNSAT problem. The list <b>literals</b> is null.
     */
    public SATSolution() {
        unsat = true;
        literals = null;
        positiveLiterals = null;
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
            for (Literal literal : positiveLiterals) {
                solution.append(literal.toString()).append(" ");
            }
        }
        return solution.toString();
    }

    /**
     * Returns the complete solution in human readable format, including the negative predicates.
     *
     * @return String representing the solution positive and negative literals).
     */
    public String getCompleteSolution() {
        StringBuilder solution = new StringBuilder();
        if (unsat) {
            solution = new StringBuilder("UNSAT");
        } else {
            for (Literal literal : literals) {
                solution.append(literal.toString()).append(" ");
            }
        }
        return solution.toString();
    }

    /**
     * Returns only the most important part of the solution in human readable format,
     * filtering out the information that are not required to generate the workflow.
     * The solution literals are sorted according the state they are used in.
     *
     * @return String representing the tools used in the solution.
     */
    public String getRelevantToolsInSolution() {
        StringBuilder solution = new StringBuilder();
        if (unsat) {
            solution = new StringBuilder("UNSAT");
        } else {
            for (Literal literal : relevantModules) {
                solution.append(literal.getPredicate().getPredicateLabel()).append(" -> ");
            }
        }
        return APEUtils.removeNLastChar(solution.toString(), 4);
    }

    /**
     * Returns only the most important part of the solution in human readable format,
     * filtering out the information that are not required to generate the workflow.
     * The solution literals are sorted according the state they are used in.
     *
     * @return String representing the tools and data used in the solutions.
     */
    public String getRelevantSolution() {
        StringBuilder solution = new StringBuilder();
        if (unsat) {
            solution = new StringBuilder("UNSAT");
        } else {
            for (Literal relevantElement : relevantElements) {
                solution.append(relevantElement.toString() + " ");
            }
        }
        return solution.toString();
    }

    /**
     * Returns the list of modules, corresponding to their position in the workflow.
     *
     * @param allModules List of all the modules in the domain.
     * @return List of {@link Module}s in the order they appear in the solution
     * workflow.
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
     * Returns the solution in mapped format. The original solution created by the SAT solver.
     *
     * @return String representing the mapped solution created by SAT solver.
     */
    public String getOriginalSATSolution() {
        StringBuilder solution = new StringBuilder();
        if (!unsat) {
            for (Literal literal : literals) {
                solution.append(literal.toMappedString()).append(" ");
            }
        }
        return solution.toString();
    }

	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 *
     * @param toolSeqRepeat variable defining if the provided solutions should be distinguished based on the tool sequences alone
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
				if (literal.getWorkflowElementType() != AtomType.MEMORY_TYPE) {
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
     * Returns the satisfiability of the problem. Returns true if the problem is
     * satisfiable, false otherwise.
     *
     * @return true if the problem is satisfiable, false otherwise.
     */
    public boolean isSat() {
        return !unsat;
    }

}
