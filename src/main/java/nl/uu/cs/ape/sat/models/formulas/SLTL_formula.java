package nl.uu.cs.ape.sat.models.formulas;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;

/**
 * The class is used to represent general SLTL constraints and to generate the
 * corresponding CNF representation. The form currently supports only trivial
 * formulas under the modal quantifier (atomic type and module formulas).
 * 
 * @author VedranPC
 *
 */
public abstract class SLTL_formula {

	private TaxonomyPredicate predicate;
	/*
	 * Sign of the predicate, <b>false</b> if the predicate is negated, <b>true</b>
	 * otherwise
	 */
	private boolean sign;

	public SLTL_formula(TaxonomyPredicate predicate) {
		this.predicate = predicate;
		sign = true;
	}

	/**
	 * Modal operators are performed over <b>formulas</b>. In case of value of
	 * <b>sign</b> being true, <b>formula</b> is positive, otherwise the formula is
	 * negative (<b>predicate</b> is negated).
	 * 
	 * @param predicate
	 * @param sign
	 */
	public SLTL_formula(boolean sign, TaxonomyPredicate predicate) {
		this.predicate = predicate;
		this.sign = sign;
	}

	/**
	 * Setting whether the predicate is negated or not. If sign is <b>false</b> the
	 * predicate will be negated.
	 * 
	 * @param sign
	 */
	public void setSign(boolean sign) {
		this.sign = sign;
	}

	/**
	 * Returns <b>false</b> if the predicate is negated, <b>true</b> otherwise.
	 * 
	 * @return
	 */
	public boolean getSign() {
		return sign;
	}

	/**
	 * Function returns the formula that was specified under the SLTL operator.<br/>
	 * Currently only predicates.
	 * 
	 * @return PredicateLabel ({@link AbstractModule}, {@link Module} or {@link Type}).
	 */
	public TaxonomyPredicate getSubFormula() {
		return predicate;
	}

	/**
	 * Returns the type of the SLTL formula [<b>F</b>, <b>G</b> or <b>X</b>].
	 * 
	 * @return {@link String} [<b>F</b>, <b>G</b> or <b>X</b>], depending on the
	 *         type of SLTL formula.
	 */
	public abstract String getType();

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton - automaton of all the module states
	 * @param typeAutomaton   - automaton of all the type states
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public abstract String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks, WorkflowElement workflowElement, AtomMappings mappings);

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If <b>if_predicate</b> is used, tool <b>then_predicate</b> has to be used
	 * subsequently.
	 * 
	 * @param if_predicate    - predicate that enforce the usage of
	 *                        <b>then_predicate</b>
	 * @param then_predicate  - predicate that is enforced by <b>if_predicate</b>
	 * @param allModules      - list of all the modules
	 * @param moduleAutomaton - module automaton
	 * @param mappings        - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String ite_module(TaxonomyPredicate if_predicate, TaxonomyPredicate then_predicate, ModuleAutomaton moduleAutomaton,
		AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints = constraints.append("-"
					+ mappings.add(if_predicate, moduleAutomaton.getModuleStates().get(i), WorkflowElement.MODULE)
					+ " ");
			for (int j = i + 1; j < automatonSize; j++) {
				constraints = constraints.append(mappings.add(then_predicate, moduleAutomaton.get(j), WorkflowElement.MODULE)).append(" ");
			}
			constraints = constraints.append("0\n");
		}
		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If type <b>if_predicate</b> is used/generated (depending on the @typeBlocks), then type <b>then_predicate</b> has to be
	 * used/generated subsequently.
	 * 
	 * @param if_predicate    - predicate that enforce the usage of
	 *                        <b>then_predicate</b>
	 * @param then_predicate  - predicate that is enforced by <b>if_predicate</b>
	 * @param allModules      - list of all the modules
	 * @param moduleAutomaton - module automaton
	 * @param typeBlocks   - type blocks (corresponding to the memory or used type states)
	 * @param mappings        - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String ite_type(TaxonomyPredicate if_predicate, TaxonomyPredicate then_predicate, WorkflowElement typeElement, ModuleAutomaton moduleAutomaton,
			List<Block> typeBlocks, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int numberOfBlocks = typeBlocks.size();
		int numberOfStates = typeBlocks.get(0).getBlockSize();
		for (int i_block = 0; i_block < numberOfBlocks; i_block++) {
			for (int i_state = 0; i_state < numberOfStates; i_state++) {
				/*
				 * If if_predicate is used in any state of a certain block
				 */
				constraints = constraints.append("-").append(mappings.add(if_predicate,
						typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");
				/*
				 * then then_predicate must be used in a state of the subsequent blocks.
				 */
				for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
					for (int j_state = i_state + 1; j_state < numberOfBlocks; j_state++) {
						constraints = constraints.append(mappings.add(then_predicate,
								typeBlocks.get(j_block).getState(j_state), typeElement)).append(" ");
					}

				}
				constraints = constraints.append("0\n");
			}
		}
		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * If <b>if_predicate</b> is used, tool <b>then_predicate</b> cannot be used
	 * subsequently.
	 * 
	 * @param if_predicate       - predicate that forbids the usage of
	 *                           <b>then_not_predicate</b>
	 * @param then_not_predicate - module that is forbidden by <b>if_predicate</b>
	 * @param allModules         - list of all the modules
	 * @param moduleAutomaton    - module automaton
	 * @param mappings           - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String itn_module(TaxonomyPredicate if_predicate, TaxonomyPredicate then_not_predicate,
			ModuleAutomaton moduleAutomaton, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize - 1; i++) {
			State currModuleState = moduleAutomaton.getModuleStates().get(i);
			for (int j = i + 1; j < automatonSize; j++) {
				constraints = constraints.append("-").append(mappings.add(if_predicate, currModuleState, WorkflowElement.MODULE)).append(" ");
				constraints = constraints.append("-"
						+ mappings.add(then_not_predicate, moduleAutomaton.get(j), WorkflowElement.MODULE)
						+ " 0\n");
			}
		}

		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If type <b>if_predicate</b> is used/generated (depending on the @typeBlocks), then do not use/generate type
	 * <b>then_not_predicate</b> subsequently.
	 * 
	 * @param if_predicate       - predicate that forbids the usage of
	 *                           <b>then_not_predicate</b>
	 * @param then_not_predicate - predicate that is forbidden by
	 *                           <b>if_predicate</b>
	 * @param typeElement 
	 * @param allModules         - list of all the modules
	 * @param moduleAutomaton    - module automaton
	 * @param typeBlocks      - type blocks (corresponding to the memory or used type states)
	 * @param mappings           - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String itn_type(TaxonomyPredicate if_predicate, TaxonomyPredicate then_not_predicate, WorkflowElement typeElement, ModuleAutomaton moduleAutomaton,
			List<Block> typeBlocks, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int numberOfBlocks = typeBlocks.size();
		int numberOfStates = typeBlocks.get(0).getBlockSize();
		for (int i_block = 0; i_block < numberOfBlocks - 1; i_block++) {
			for (int i_state = 0; i_state < numberOfStates; i_state++) {
				for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
					for (int j_state = 0; j_state < numberOfStates; j_state++) {
						/*
						 * If if_predicate is used in any state of a certain block
						 */
						constraints = constraints.append("-").append(mappings.add(if_predicate,
								typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");
						/*
						 * then then_predicate cannot be used in a state of the subsequent blocks.
						 */
						constraints = constraints.append("-").append(mappings.add(then_not_predicate,
								typeBlocks.get(j_block).getState(j_state), typeElement)).append(" 0\n");
					}

				}
			}
		}
		return constraints.toString();
	}
	
	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If we use module <b>second_module_in_sequence</b>, then we must have used
	 * <b>first_module_in_sequence</b> prior to it.
	 * 
	 * @param first_module_in_sequence  - predicate that is enforced by
	 *                                  <b>second_predicate</b>
	 * @param second_module_in_sequence - predicate that enforces the usage of
	 *                                  <b>first_predicate</b>
	 * @param allModules                - list of all the modules
	 * @param moduleAutomaton           - module automaton
	 * @param mappings                  - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String depend_module(TaxonomyPredicate second_module_in_sequence, TaxonomyPredicate first_module_in_sequence,
			ModuleAutomaton moduleAutomaton,AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints = constraints.append("-").append(mappings.add(second_module_in_sequence,
					moduleAutomaton.getModuleStates().get(i), WorkflowElement.MODULE)).append(" ");
			for (int j = 0; j < i; j++) {
				constraints = constraints.append(mappings.add(first_module_in_sequence,
						moduleAutomaton.get(j), WorkflowElement.MODULE)).append(" ");
			}
			constraints = constraints.append("0\n");
		}
		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint: :<br/>
	 * <br/>
	 * If we use predicate <b>first_module_in_sequence</b>, then use
	 * <b>second_module_in_sequence</b> as a next predicate in the sequence.
	 * 
	 * @param first_module_in_sequence  - predicate that enforce the usage of
	 *                                  <b>second_predicate</b>
	 * @param second_module_in_sequence - predicate that is enforced by
	 *                                  <b>first_predicate</b>
	 * @param allModules                - list of all the modules
	 * @param moduleAutomaton           - module automaton
	 * @param mappings                  - set of the mappings for the literals
	 * @return {@link String} CNF representation of the constraint
	 */
	public static String next_module(TaxonomyPredicate first_module_in_sequence, TaxonomyPredicate second_module_in_sequence,
			ModuleAutomaton moduleAutomaton, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints = constraints.append("-").append(mappings.add(first_module_in_sequence,
					moduleAutomaton.getModuleStates().get(i), WorkflowElement.MODULE)).append(" ");
			/*
			 * Clause that forbids using first_predicate as the last in the sequence
			 */
			if (i < automatonSize - 1) {
				constraints = constraints.append(mappings.add(second_module_in_sequence,
						moduleAutomaton.get(i + 1), WorkflowElement.MODULE)).append(" ");
			}
			constraints = constraints.append("0\n");
		}
		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint: :<br/>
	 * <br/>
	 * If we use predicate <b>first_predicate</b>, then use <b>second_predicate</b>
	 * as a next predicate in the sequence.
	 * 
	 * @param first_predicate  - predicate that enforce the usage of
	 *                         <b>second_predicate</b>
	 * @param second_predicate - predicate that is enforced by
	 *                         <b>first_predicate</b>
	 * @param allModules       - list of all the modules
	 * @param moduleAutomaton  - module automaton
	 * @param mappings         - set of the mappings for the literals
	 * @return {@link String} CNF representation of the constraint
	 */
	public static String prev_module(TaxonomyPredicate second_module_in_sequence, TaxonomyPredicate first_module_in_sequence,
			ModuleAutomaton moduleAutomaton, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints = constraints.append("-").append(mappings.add(second_module_in_sequence,
					moduleAutomaton.getModuleStates().get(i), WorkflowElement.MODULE)).append(" ");
			/*
			 * Clause that forbids using second_module_in_sequence as the first tool in the
			 * sequence
			 */
			if (i > 0) {
				constraints = constraints.append(mappings.add(first_module_in_sequence,
						moduleAutomaton.get(i - 1), WorkflowElement.MODULE)).append(" ");
			}
			constraints = constraints.append("0\n");
		}
		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>last_module</b> as last module in the solution.
	 * 
	 * @param last_module     - the module
	 * @param moduleAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useAsLastModule(TaxonomyPredicate last_module, ModuleAutomaton moduleAutomaton,
			AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();

		List<State> moduleAutomatonStates = moduleAutomaton.getModuleStates();
		State lastModuleState = moduleAutomatonStates.get(moduleAutomatonStates.size() - 1);
		constraints = constraints.append(mappings.add(last_module, lastModuleState, WorkflowElement.MODULE)).append(" 0\n");

		return constraints.toString();
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>module</b> as the <b>n</b>-th module in the solution.
	 * 
	 * @param module          - the module
	 * @param n               - absolute position in the solution
	 * @param moduleAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useAsNthModule(TaxonomyPredicate module, int n, ModuleAutomaton moduleAutomaton,
			AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();

		List<State> moduleAutomatonStates = moduleAutomaton.getModuleStates();
		State nthModuleState = moduleAutomatonStates.get(n - 1);
		constraints = constraints.append(mappings.add(module, nthModuleState, WorkflowElement.MODULE)).append(" 0\n");

		return constraints.toString();
	}

	/**
	 * TODO: Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>module</b> in the solution exactly <b>n</b> times.
	 * 
	 * @param module          - module to be used
	 * @param n               - number of repetitions
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useModuleNtimes(TaxonomyPredicate module, int n, ModuleAutomaton moduleAutomaton,
			AtomMappings mappings) {
//		StringBuilder constraints = new StringBuilder();
//
//		List<State> moduleAutomatonStates = moduleAutomaton.getModuleStates();
//		ModuleState nthModuleState = moduleAutomatonStates.get(index - 1);
//		constraints = constraints.append(mappings.add(module, nthModuleState)).append(" 0\n";

		return null;
	}

	public String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks, WorkflowElement typeElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
