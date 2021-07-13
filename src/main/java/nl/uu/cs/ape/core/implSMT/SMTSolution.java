package nl.uu.cs.ape.core.implSMT;

import java.util.*;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.SolutionInterpreter;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.Atom;
import nl.uu.cs.ape.models.logic.constructs.Literal;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.Assertion;
import nl.uu.cs.ape.models.smtStruc.SMT2LibRow;
import nl.uu.cs.ape.models.smtStruc.boolStatements.AndStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BinarySMTPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NotStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code SMTSolution} class describes the solution produced by the SMT
 * solver. In case of the
 * parameter <b>unsat</b> being true, there are no solutions.
 * <p>
 * It also implements general solution interface {@link SolutionInterpreter}.
 *
 * @author Vedran Kasalica
 */
public class SMTSolution extends SolutionInterpreter {

    /**
     * List of all the positive literals provided by the solution.
     */
    private final List<Atom> positiveLiterals;

    /**
     * List of only relevant (positive) literals that represent implemented modules/tools.
     */
    private final List<Atom> relevantModules;

    /**
     * List of only relevant (positive) literals that represent simple types.
     */
    private final List<Atom> relevantTypes;

    /**
     * List of all the relevant types and modules combined.
     */
    private final List<Atom> relevantElements;

    /**
     * List of all the references for the types in the memory, when used as tool inputs.
     */
    private final List<Atom> references2MemTypes;
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
    public SMTSolution(List<Atom> facts, SMTSynthesisEngine smtSynthesisEngine) {
        unsat = false;
        positiveLiterals = new ArrayList<Atom>();
        relevantModules = new ArrayList<Atom>();
        relevantTypes = new ArrayList<Atom>();
        relevantElements = new ArrayList<Atom>();
        references2MemTypes = new ArrayList<Atom>();
        usedTypeStates = new HashSet<PredicateLabel>();
        for (Atom currAtom : facts) {
                    positiveLiterals.add(currAtom);
                    if (currAtom.getPredicate() instanceof AuxiliaryPredicate) {
                        continue;
                    } else if (currAtom.getPredicate() instanceof Module) {
                        /* add all positive literals that describe tool implementations */
                        relevantElements.add(currAtom);
                        relevantModules.add(currAtom);
                    } else if (currAtom.getWorkflowElementType() != WorkflowElement.MODULE
                            && currAtom.getWorkflowElementType() != WorkflowElement.MEM_TYPE_REFERENCE
                            && currAtom.getWorkflowElementType() != WorkflowElement.TYPE_DEPENDENCY
                            && (currAtom.getPredicate() instanceof Type)
                            && ((Type) currAtom.getPredicate()).isSimplePredicate()) {
                        /* add all positive literals that describe simple types */
                        relevantElements.add(currAtom);
                        relevantTypes.add(currAtom);
                        usedTypeStates.add(currAtom.getUsedInStateArgument());
                    } else if (currAtom.getPredicate() instanceof State
                            && ((State) (currAtom.getPredicate())).getAbsoluteStateNumber() != -1) {
                        /*
                         * add all positive literals that describe memory type references that are not
                         * pointing to null state (NULL state has AbsoluteStateNumber == -1)
                         */
                        references2MemTypes.add(currAtom);
                        relevantElements.add(currAtom);
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
    public SMTSolution() {
        unsat = true;
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
            for (Atom literal : positiveLiterals) {
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
            for (Atom literal : relevantModules) {
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
            for (Atom relevantElement : relevantElements) {
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
            for (Atom literal : relevantModules) {
                solutionModules.add((Module) allModules.get(literal.getPredicate().getPredicateID()));
            }
        }
        return solutionModules;
    }


	/**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 *
     * @param toolSeqRepeat variable defining if the provided solutions should be distinguished based on the tool sequences alone
	 * @return int[] representing the negated solution
	 */
//	public int[] getNegatedMappedSolutionArray(boolean toolSeqRepeat) {
//		List<Integer> negSol = new ArrayList<Integer>();
//		if (!unsat) {
//			if(!toolSeqRepeat) {
//				for (Atom literal : relevantModules) {
//					negSol.add(literal.toNegatedMappedInt());
//				}
//			} else {
//			for (Atom literal : relevantElements) {
//				if (literal.getWorkflowElementType() != SMTDataType.MEMORY_TYPE) {
//					negSol.add(literal.toNegatedMappedInt());
//				}
//			}
//			}
//		}
//		int[] negSolList = new int[negSol.size()];
//		for (int i = 0; i < negSol.size(); i++) {
//			negSolList[i] = negSol.get(i);
//		}
//
//        return negSolList;
//    }
    
    /**
	 * Returns the negated solution in mapped format. Negating the original solution
	 * created by the SAT solver. Usually used to add to the solver to find new
	 * solutions.
	 *
     * @param toolSeqRepeat variable defining if the provided solutions should be distinguished based on the tool sequences alone
	 * @return int[] representing the negated solution
	 */
	public List<SMT2LibRow> getSMTnegatedSolution(boolean allowToolSeqRepeat) {
		List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
		List<Fact> facts = new ArrayList<Fact>();
		if (!unsat) {
			if(!allowToolSeqRepeat) {
				for (Atom atom : relevantModules) {
					facts.add(new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MODULE), atom.getUsedInStateArgument(), atom.getPredicate()));
				}
			} else {
				for (Atom atom : relevantElements) {
					if (atom.getWorkflowElementType() != WorkflowElement.MEMORY_TYPE) {
						facts.add(new BinarySMTPredicate(new SMTFunctionName(atom.getWorkflowElementType()), atom.getUsedInStateArgument(), atom.getPredicate()));
					}
				}
			}
			
			allClauses.add(new Assertion(
					new NotStatement(
							new AndStatement(facts)
							)));
		}

        return allClauses;
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
    
    
	@Override
	public String getCompleteSolution() {
		return getSolution();
    }

}
