package nl.uu.cs.ape.core.implSAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.AuxTypePredicate;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.SATAtom;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.models.satStruc.SATImplicationStatement;
import nl.uu.cs.ape.models.satStruc.SATNandStatement;
import nl.uu.cs.ape.models.satStruc.SATNotStatement;
import nl.uu.cs.ape.models.satStruc.SATOrStatement;
import nl.uu.cs.ape.utils.APEDomainSetup;

/**
 * The {@code SMTTypeUtils} class is used to encode SAT constraints  based on the type annotations.
 *
 * @author Vedran Kasalica
 */
public class SATTypeUtils {

    /**
     * Private constructor is used to to prevent instantiation.
     */
    private SATTypeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Generating the mutual exclusion for each pair of tools from @modules
     * (excluding abstract modules from the taxonomy) in each state
     * of @moduleAutomaton.
     *
     * @param allTypes      TODO
     * @param typeAutomaton TODO
     * @param mappings      TODO
     * @return String representation of constraints.
     */
   public static Set<SATFact> typeMutualExclusion(AllTypes allTypes, TypeAutomaton typeAutomaton) {

        Set<SATFact> cnfEncoding = new HashSet<SATFact>();
        PredicateLabel firstPair, secondPair;
        for (Pair<PredicateLabel> pair : allTypes.getTypePairsForEachSubTaxonomy()) {
            firstPair = pair.getFirst();
            secondPair = pair.getSecond();
            // mutual exclusion of types in all the states (those that represent general memory)
            for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
                for (State memTypeState : typeBlock.getStates()) {
                	cnfEncoding.add(
    						new SATNandStatement(
    									new SATAtom(
    											AtomType.MEMORY_TYPE, 
    											firstPair, 
    											memTypeState),
    									new SATAtom(
    											AtomType.MEMORY_TYPE, 
    											secondPair, 
    											memTypeState)));
                }
            }
            // mutual exclusion of types in all the states (those that represent used instances)
            for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
                for (State usedTypeState : typeBlock.getStates()) {
                	cnfEncoding.add(
    						new SATNandStatement(
    									new SATAtom(
    											AtomType.USED_TYPE, 
    											firstPair, 
    											usedTypeState),
    									new SATAtom(
    											AtomType.USED_TYPE, 
    											secondPair, 
    											usedTypeState)));
                }
            }
        }
        return cnfEncoding;
    }

    /**
     * Generating the mandatory usage constraints of root type @rootType in each
     * state of @moduleAutomaton. It enforces that each type instance is either
     * defined on all the dimensions or is empty.
     *
     * @param domainSetup   TODO
     * @param typeAutomaton TODO
     * @param mappings      TODO
     * @return String representation of constraints.
     */
   public static Set<SATFact> typeMandatoryUsage(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton) {
        Set<SATFact> cnfEncoding = new HashSet<SATFact>();
        Type empty = domainSetup.getAllTypes().getEmptyType();
        Type dataType = AuxTypePredicate.generateAuxiliaryPredicate(domainSetup.getAllTypes().getDataTaxonomyDimensionsAsSortedSet(), LogicOperation.AND, domainSetup);
        // enforcement of types in in all the states (those that represent general
        // memory and used data instances)
        for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
            for (State memTypeState : typeBlock.getStates()) {
            	cnfEncoding.add(
						new SATOrStatement(
									new SATAtom(
											AtomType.MEMORY_TYPE, 
											dataType, 
											memTypeState),
									new SATAtom(
											AtomType.MEMORY_TYPE, 
											empty, 
											memTypeState)));
            }
        }
        for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
            for (State usedTypeState : typeBlock.getStates()) {
            	cnfEncoding.add(
						new SATOrStatement(
									new SATAtom(
											AtomType.USED_TYPE, 
											dataType, 
											usedTypeState),
									new SATAtom(
											AtomType.USED_TYPE, 
											empty, 
											usedTypeState)));
            }
        }

        return cnfEncoding;
    }

    /**
     * Generating the mandatory usage of a subtypes in case of the parent type being
     * used, with respect to the Type Taxonomy. The rule starts from the @rootType
     * and it's valid in each state of @typeAutomaton. @emptyType denotes the type
     * that is being used if the state has no type.
     *
     * @param allTypes      TODO
     * @param typeAutomaton TODO
     * @param mappings      TODO
     * @return The String representation of constraints enforcing taxonomy classifications.
     */
   public static Set<SATFact> typeEnforceTaxonomyStructure(AllTypes allTypes, TypeAutomaton typeAutomaton) {
        Set<SATFact> cnfEncoding = new HashSet<SATFact>();
        // taxonomy enforcement of types in in all the states (those that represent
        // general memory and used data instances)
        for (TaxonomyPredicate dimension : allTypes.getRootPredicates()) {
            for (Block memTypeBlock : typeAutomaton.getMemoryTypesBlocks()) {
                for (State memTypeState : memTypeBlock.getStates()) {
                	cnfEncoding.addAll(typeEnforceTaxonomyStructureForState(dimension, memTypeState, AtomType.MEMORY_TYPE));
                }
            }
            for (Block usedTypeBlock : typeAutomaton.getUsedTypesBlocks()) {
                for (State usedTypeState : usedTypeBlock.getStates()) {
                	cnfEncoding.addAll(typeEnforceTaxonomyStructureForState(dimension, usedTypeState, AtomType.USED_TYPE));
                }
            }
        }
        return cnfEncoding;
    }

    /**
     * Supporting recursive method for typeEnforceTaxonomyStructure.
     */
    private static Set<SATFact> typeEnforceTaxonomyStructureForState(TaxonomyPredicate currType,
                                                                State typeState, AtomType typeElement) {

        SATAtom superTypeState = new SATAtom(typeElement, currType, typeState);

        Set<SATFact> fullCNFEncoding = new HashSet<SATFact>();
		Set<SATFact> currCNFEncoding = new HashSet<SATFact>();
		
		currCNFEncoding.add(
				new SATNotStatement(superTypeState));

        List<SATAtom> subTypesStates = new ArrayList<SATAtom>();
        if (!(currType.getSubPredicates() == null || currType.getSubPredicates().isEmpty())) {
            /*
             * Ensuring the TOP-DOWN taxonomy tree dependency
             */
            for (TaxonomyPredicate subType : currType.getSubPredicates()) {

            	SATAtom subTypeState = new SATAtom(typeElement, subType, typeState);
            	currCNFEncoding.add(subTypeState);
            	subTypesStates.add(subTypeState);

                fullCNFEncoding.addAll(typeEnforceTaxonomyStructureForState(subType, typeState, typeElement));
            }
            fullCNFEncoding.add(new SATOrStatement(currCNFEncoding));
            /*
             * Ensuring the BOTTOM-UP taxonomy tree dependency
             */
            for (SATAtom subTypeState : subTypesStates) {
            	fullCNFEncoding.add(
						new SATImplicationStatement(
								subTypeState,
								superTypeState));
            }
        }
        return fullCNFEncoding;
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
   public static Set<SATFact> encodeInputData(AllTypes allTypes, List<Type> program_inputs, TypeAutomaton typeAutomaton) {
        Set<SATFact> cnfEncoding = new HashSet<SATFact>();

        List<State> workflowInputStates = typeAutomaton.getMemoryTypesBlock(0).getStates();
        for (int i = 0; i < workflowInputStates.size(); i++) {
        	State currState = workflowInputStates.get(i);
            if (i < program_inputs.size()) {
                Type currType = program_inputs.get(i);
                    if (allTypes.get(currType.getPredicateID()) == null) {
                        System.err.println(
                                "Program input '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
                        return null;
                    }
                    cnfEncoding.add(
                    		new SATAtom(
									AtomType.MEMORY_TYPE, 
									currType, 
									currState));
            } else {
                /* Forcing in the rest of the input states to be empty types. */
            	cnfEncoding.add(
                		new SATAtom(
								AtomType.MEMORY_TYPE, 
								allTypes.getEmptyType(), 
								currState));
            }
        }
        return cnfEncoding;
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
   public static Set<SATFact> encodeOutputData(AllTypes allTypes, List<Type> program_outputs, TypeAutomaton typeAutomaton) {
        Set<SATFact> cnfEncoding = new HashSet<SATFact>();

        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
        for (int i = 0; i < workflowOutputStates.size(); i++) {
            if (i < program_outputs.size()) {
            	TaxonomyPredicate currType = program_outputs.get(i);
                    if (allTypes.get(currType.getPredicateID()) == null) {
                        System.err.println(
                                "Program output '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
                        return null;
                    }
                    cnfEncoding.add(
                    		new SATAtom(
									AtomType.USED_TYPE, 
									currType, 
									workflowOutputStates.get(i)));
                    
            } else {
                /* Forcing in the rest of the input states to be empty types. */
            	cnfEncoding.add(
                		new SATAtom(
								AtomType.USED_TYPE, 
								allTypes.getEmptyType(), 
								workflowOutputStates.get(i)));
            }

        }

        return cnfEncoding;
    }
}
