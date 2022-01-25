package nl.uu.cs.ape.core.implSMT;

import java.util.*;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.SolutionInterpreter;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.satStruc.SLTLxAtom;
import nl.uu.cs.ape.models.satStruc.SLTLxLiteral;
import nl.uu.cs.ape.models.smtStruc.SMTAssertion;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Row;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTAndStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBinaryPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFact;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTNotStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBitVec;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTPredicateFunArg;
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
    private final List<SLTLxAtom> positiveLiterals;

    /**
     * List of only relevant (positive) literals that represent implemented modules/tools.
     */
    private final List<SLTLxAtom> relevantModules;

    /**
     * List of only relevant (positive) literals that represent simple types.
     */
    private final List<SLTLxAtom> relevantTypes;

    /**
     * List of all the relevant types and modules combined.
     */
    private final List<SLTLxAtom> relevantElements;

    /**
     * List of all the references for the types in the memory, when used as tool inputs.
     */
    private final List<SLTLxAtom> references2MemTypes;
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
    public SMTSolution(List<SLTLxAtom> facts, SMTSynthesisEngine smtSynthesisEngine) {
        unsat = false;
        positiveLiterals = new ArrayList<SLTLxAtom>();
        relevantModules = new ArrayList<SLTLxAtom>();
        relevantTypes = new ArrayList<SLTLxAtom>();
        relevantElements = new ArrayList<SLTLxAtom>();
        references2MemTypes = new ArrayList<SLTLxAtom>();
        usedTypeStates = new HashSet<PredicateLabel>();
        for (SLTLxAtom currAtom : facts) {
                    positiveLiterals.add(currAtom);
                    if (currAtom.getPredicate() instanceof AuxiliaryPredicate) {
                        continue;
                    } else if (currAtom.getPredicate() instanceof Module) {
                        /* add all positive literals that describe tool implementations */
                        relevantElements.add(currAtom);
                        relevantModules.add(currAtom);
                    } else if (currAtom.getWorkflowElementType() != AtomType.MODULE
                            && currAtom.getWorkflowElementType() != AtomType.MEM_TYPE_REFERENCE
                            && currAtom.getWorkflowElementType() != AtomType.R_RELATON
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
            for (SLTLxAtom literal : positiveLiterals) {
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
            for (SLTLxAtom literal : relevantModules) {
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
            for (SLTLxAtom relevantElement : relevantElements) {
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
            for (SLTLxAtom literal : relevantModules) {
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
	public List<SMTLib2Row> getSMTnegatedSolution(boolean allowToolSeqRepeat) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		List<SMTFact> facts = new ArrayList<SMTFact>();
		if (!unsat) {
			if(!allowToolSeqRepeat) {
				for (SLTLxAtom atom : relevantModules) {
					facts.add(new SMTBinaryPredicate(
									AtomType.MODULE, 
									new SMTBitVec(SMTDataType.MODULE_STATE, atom.getUsedInStateArgument()), 
									new SMTPredicateFunArg(atom.getPredicate())
								));
				}
			} else {
				for (SLTLxAtom atom : relevantElements) {
					if (atom.getWorkflowElementType() == AtomType.MODULE) {
							facts.add(new SMTBinaryPredicate(
										AtomType.MODULE, 
										new SMTBitVec(SMTDataType.MODULE_STATE, atom.getUsedInStateArgument()), 
										new SMTPredicateFunArg(atom.getPredicate())
									));
					} else if (atom.getWorkflowElementType() == AtomType.USED_TYPE) {
							facts.add(new SMTBinaryPredicate(
									AtomType.USED_TYPE, 
									new SMTBitVec(SMTDataType.USED_TYPE_STATE, atom.getUsedInStateArgument()), 
									new SMTPredicateFunArg(atom.getPredicate())
								));
					} else {
						// SKIP Memory Type
					}
				}
			}
			
			allClauses.add(new SMTAssertion(
					new SMTNotStatement(
							new SMTAndStatement(facts)
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
