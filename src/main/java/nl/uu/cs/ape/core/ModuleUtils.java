package nl.uu.cs.ape.core;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code ModuleUtils} class is used to encode SAT constraints based on the
 * module annotations.
 *
 * @author Vedran Kasalica
 */
public abstract class ModuleUtils {

	/**
	 * Return a representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of constraints regarding the required INPUT
	 *         and OUTPUT types of the modules.
	 */
	public String encodeModuleAnnotations(Class<? extends SynthesisEngine> synthesisInstance) {
		StringBuilder constraints = new StringBuilder();
		constraints.append(inputCons(synthesisInstance));

		constraints.append(outputCons(synthesisInstance));
		return constraints.toString();
	}

	/**
	 * Return a formula that preserves the memory structure that is being used
	 * (e.g. 'shared memory'), i.e. ensures that the referenced items are available
	 * according to the mem. structure and that the input type and the referenced
	 * type from the memory represent the same data.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of constraints regarding the required
	 *         memory structure implementation.
	 */
	public String encodeMemoryStructure(Class<? extends SynthesisEngine> synthesisInstance) {
		StringBuilder constraints = new StringBuilder();

		constraints.append(
				allowDataReferencingCons(synthesisInstance));
		constraints.append(enforcingUsageOfGeneratedTypesCons(synthesisInstance));

		constraints.append(enforceDataReferenceRules(synthesisInstance));
		return constraints.toString();
	}
	
	public String encodeDataInstanceDependencyCons(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		
		
		constraints.append(allowDataDependencyCons(typeAutomaton, mappings));
		constraints.append(enforceDataDependencyOverModules(typeAutomaton, mappings));
		constraints.append(enforceDataDependencyOverDataReferencing(typeAutomaton, mappings));
		
		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Returns the representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 *
	 * @return String representation of constraints.
	 */
	public abstract String inputCons(Class<? extends SynthesisEngine> synthesisInstance);

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link AtomType#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 *
	 * @return String representing the constraints required to ensure that the
	 *         {@link AtomType#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	public abstract String enforceDataReferenceRules(Class<? extends SynthesisEngine> synthesisInstance);
	/**
	 * Generate constraints that ensure that the all tool inputs can reference data
	 * that is available in memory at the time.
	 *
	 * <br>
	 * Return the representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Shared Memory Approach.
	 *
	 * @return String representation of constraints.
	 */
	public abstract String allowDataReferencingCons(Class<? extends SynthesisEngine> synthesisInstance);

	/**
	 * Generate constraints that ensure that the data instances can depend on
	 * instances that are available in memory, and that each data instance depends
	 * on itself.
	 * 
	 * @return String representation of constraints.
	 */
	public abstract String allowDataDependencyCons(TypeAutomaton typeAutomaton, SATAtomMappings mappings);

	/**
	 * Generate constraints that ensure that tool inputs that reference data in
	 * memory depend on the same data as the referenced data instance.
	 * 
	 * @return String representation of constraints.
	 */
	public abstract String enforceDataDependencyOverDataReferencing(TypeAutomaton typeAutomaton, SATAtomMappings mappings);

	public abstract String enforceDataDependencyOverModules(TypeAutomaton typeAutomaton, SATAtomMappings mappings);

	/**
	 * Function returns the encoding that ensures that tool outputs are used
	 * according to the configuration, e.g. if the config specifies that all
	 * workflow inputs have to be used, then each of them has to be referenced at
	 * least once.
	 *
	 * @return String representation of constraints.
	 */
	public abstract String enforcingUsageOfGeneratedTypesCons(Class<? extends SynthesisEngine> synthesisInstance);

	/**
	 * Return the representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.<br>
	 * Generate constraints that preserve tool outputs.
	 *
	 * @return String representation of constraints.
	 */
	public abstract String outputCons(Class<? extends SynthesisEngine> synthesisInstance);
	/**
	 * Generating the mutual exclusion constraints for each pair of tools from
	 * modules (excluding abstract modules from the taxonomy) in each state of
	 * moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return The String representation of constraints.
	 */
	public abstract String moduleMutualExclusion(AllModules allModules, ModuleAutomaton moduleAutomaton,
			SATAtomMappings mappings);

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of constraints.
	 */
	public abstract String moduleMandatoryUsage(AllModules allModules, ModuleAutomaton moduleAutomaton,
			SATAtomMappings mappings);

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param currModule      Module that should be used.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications.
	 */
	public abstract String moduleEnforceTaxonomyStructure(AllModules allModules, TaxonomyPredicate currModule,
			ModuleAutomaton moduleAutomaton, SATAtomMappings mappings);
	
	/**
	 * Gets predicate pairs.
	 *
	 * @param predicateList List of predicates.
	 * @return A a list of pairs of tools from modules. Note that the abstract
	 *         modules are not returned, only the unique pairs of modules that are
	 *         representing actual tools.
	 */
	public static List<Pair<PredicateLabel>> getPredicatePairs(List<? extends PredicateLabel> predicateList) {
		List<Pair<PredicateLabel>> pairs = new ArrayList<Pair<PredicateLabel>>();

		for (int i = 0; i < predicateList.size() - 1; i++) {
			for (int j = i + 1; j < predicateList.size(); j++) {

				pairs.add(new Pair<PredicateLabel>(predicateList.get(i), predicateList.get(j)));
			}
		}

		return pairs;
	}
}
