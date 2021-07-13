package nl.uu.cs.ape.core.implSMT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.AuxTypePredicate;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.smtStruc.SMTComment;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BinarySMTPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.EqualStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ForallStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NandStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.OrStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBoundedVar;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionArgument;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;
import nl.uu.cs.ape.models.smtStruc.Assertion;
import nl.uu.cs.ape.models.smtStruc.SMT2LibRow;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code SMTTypeUtils} class is used to encode SAT constraints  based on the type annotations.
 *
 * @author Vedran Kasalica
 */
public class SMTTypeUtils {

    /**
     * Private constructor is used to to prevent instantiation.
     */
    private SMTTypeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Generating the mutual exclusion for each pair of tools from @modules
     * (excluding abstract modules from the taxonomy) in each state
     * of @moduleAutomaton.
     *
     * @param allTypes   - all data types in the domain
     * @return String representation of constraints.
     */
    public static List<SMT2LibRow> typeMutualExclusion(AllTypes allTypes) {

        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding rules of mutual exclusion of data types."));
        
        PredicateLabel firstPair, secondPair;
        SMTBoundedVar state = new SMTBoundedVar("state");
        for (Pair<PredicateLabel> pair : allTypes.getTypePairsForEachSubTaxonomy()) {
            firstPair = pair.getFirst();
            secondPair = pair.getSecond();
            
//            System.out.println("-------------->" + firstPair.getPredicateLabel() + "_______and______" +secondPair.getPredicateLabel());
            
            // mutual exclusion of types in all the states (those that represent general memory)
            allClauses.add(new Assertion(
					new ForallStatement(
							state,
							SMTDataType.MEMORY_TYPE_STATE,
							new NandStatement(
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(firstPair)), 
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(secondPair))
							)
					)
			));
            
            // mutual exclusion of types in all the states (those that represent used instances)
            allClauses.add(new Assertion(
					new ForallStatement(
							state,
							SMTDataType.USED_TYPE_STATE,
							new NandStatement(
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(firstPair)), 
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(secondPair))
							)
					)
			));
        }
        return allClauses;
    }

    /**
     * Generating the mandatory usage constraints of root type @rootType in each
     * state of @toolAutomaton. It enforces that each type instance is either
     * defined on all the dimensions or is empty.
     *
     * @param allTypes   - all data types in the domain
     * @return String representation of constraints.
     */
    public static List<SMT2LibRow> typeMandatoryUsage(APEDomainSetup domainSetup) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding rules of mandatory usage of types."));
		
        Type empty = domainSetup.getAllTypes().getEmptyType();
        Type dataType = AuxTypePredicate.generateAuxiliaryPredicate(domainSetup.getAllTypes().getDataTaxonomyDimensionsAsSortedSet(), LogicOperation.AND, domainSetup);
        // enforcement of types in in all the states (those that represent general memory and used data instances)
        SMTBoundedVar state = new SMTBoundedVar("state");
    	allClauses.add(new Assertion(
    			new ForallStatement(
    				state,
    				SMTDataType.MEMORY_TYPE_STATE,
        			new OrStatement(
        					new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(dataType)),
        					new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(empty))
        					)
        			)
    	));
    	
    	allClauses.add(new Assertion(
    			new ForallStatement(
    				state,
    				SMTDataType.USED_TYPE_STATE,
        			new OrStatement(
        					new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(dataType)),
        					new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(empty))
        					)
        			)
    	));

        return allClauses;
    }

    /**
     * Generating the mandatory usage of a parent type in case of one of the child types being
     * used, with respect to the Type Taxonomy. The rule starts from the @rootType and works its way down the taxonomy,
     * and it's valid in each state of @typeAutomaton.
     * 
     * Taxonomy structure is enforced bottom-up, i.e., the subsumption relation is not complete. 
     *
     * @param allTypes    - all data types in the domain
     * @return The list of {@link SMT2LibRow}s representation of constraints enforcing taxonomy classifications.
     */
    public static List<SMT2LibRow> typeEnforceTaxonomyStructure(AllTypes allTypes) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding rules to preserve the structure of the type taxonomies."));

        for (TaxonomyPredicate dimension : allTypes.getDataTaxonomyDimensionsAndLabel()) {
        	allClauses.addAll(typeEnforceTaxonomyStructurePerDimension(dimension));
        }
        return allClauses;
    }
    
    /**
     * Generating the mandatory usage of a parent type in case of one of the child types being
     * used, with respect to the Type Taxonomy.
     * 
     * Taxonomy structure is enforced bottom-up, i.e., the subsumption relation is not complete. 
     * 
     * @param parentType - parent type for which the rules are created
     * @return The list of {@link SMT2LibRow}s  representation of constraints enforcing subtaxonomy classifications.
     */
    private static List<SMT2LibRow> typeEnforceTaxonomyStructurePerDimension(TaxonomyPredicate parentType) {

		List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
		
		SMTBoundedVar state = new SMTBoundedVar("state");
		if (!(parentType.getSubPredicates() == null || parentType.getSubPredicates().isEmpty())) {
			allClauses.add(new Assertion(
				new ForallStatement(
					state,
					SMTDataType.USED_TYPE_STATE,
					new EqualStatement(
						new OrStatement(parentType.getSubPredicates().stream()
								.map(subModule -> new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(subModule)))
								.collect(Collectors.toList())
								),
						new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(parentType))
						)
					)
				));
			
			allClauses.add(new Assertion(
					new ForallStatement(
						state,
						SMTDataType.MEMORY_TYPE_STATE,
						new EqualStatement(
							new OrStatement(parentType.getSubPredicates().stream()
									.map(subModule -> new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(subModule)))
									.collect(Collectors.toList())
									),
							new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(parentType))
							)
						)
					));
			
			for (TaxonomyPredicate subModule : APEUtils.safe(parentType.getSubPredicates())) {
				allClauses.addAll(typeEnforceTaxonomyStructurePerDimension(subModule));
			}
		}
		
		return allClauses;
	}


    /**
     * Encoding the initial workflow input.
     *
     * @param allTypes       Set of all the types in the domain
     * @param program_inputs Input types for the program.
     * @param typeAutomaton  Automaton representing the type states in the model
     * @param mappings       All the atom mappings
     * @return The String representation of the initial input encoding.
     */
    public static List<SMT2LibRow> encodeInputData(AllTypes allTypes, List<Type> program_inputs, TypeAutomaton typeAutomaton) throws APEConfigException {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding the defined workflow inputs."));
        
        List<State> workflowInputStates = typeAutomaton.getMemoryTypesBlock(0).getStates();
        for (int i = 0; i < workflowInputStates.size(); i++) {
        	State currState = workflowInputStates.get(i);
            if (i < program_inputs.size()) {
                Type currType = program_inputs.get(i);
                    if (allTypes.get(currType.getPredicateID()) == null) {
                    	throw APEConfigException.invalidValue("input", currType.toString(), "Invalid workflow input value.");
                    }
                    allClauses.add(new Assertion(new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), currState, currType)));
            } else {
                /* Forcing in the rest of the input states to be empty types. */
                allClauses.add(new Assertion(new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), currState, allTypes.getEmptyType())));
            }
        }
        return allClauses;
    }

    /**
     * Encoding the workflow output. The provided output files have to occur
     * as the final list of "used" data types. In the predefined order.
     *
     * @param allTypes        Set of all the types in the domain
     * @param program_outputs Output types for the program.
     * @param typeAutomaton   Automaton representing the type states in the model
     * @param mappings       All the atom mappings
     * @return String representation of the workflow output encoding.
     */
    public static List<SMT2LibRow> encodeOutputData(AllTypes allTypes, List<Type> program_outputs, TypeAutomaton typeAutomaton) throws APEConfigException{
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding the desired workflow outputs."));
        
        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
        for (int i = 0; i < workflowOutputStates.size(); i++) {
            if (i < program_outputs.size()) {
            	TaxonomyPredicate currType = program_outputs.get(i);
                    if (allTypes.get(currType.getPredicateID()) == null) {
                    	throw APEConfigException.invalidValue("output", currType.toString(), "Invalid workflow output value.");
                    }
                    allClauses.add(new Assertion(new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), workflowOutputStates.get(i), currType)));
//					currType.setAsRelevantTaxonomyTerm(allTypes);
            } else {
                /* Forcing in the rest of the input states to be empty types. */
            	allClauses.add(new Assertion(new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), workflowOutputStates.get(i), allTypes.getEmptyType())));
            }

        }

        return allClauses;
    }
}
