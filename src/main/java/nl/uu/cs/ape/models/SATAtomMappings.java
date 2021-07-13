package nl.uu.cs.ape.models;

import java.util.HashMap;
import java.util.Map;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.Atom;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code SATAtomMappings} class is used to store the data used for representing the atoms with integer numbers.
 * Atoms are not a separate data structure, but a string combination of a {@link TaxonomyPredicate}
 * and a {@link State} as am argument.<br>
 * Required for the SAT representation of the CNF formula.
 *
 * @author Vedran Kasalica
 */
public class SATAtomMappings implements Mappings {

	/** Mapping of the atoms to integers. */
    private Map<Atom, Integer> mappings;
    /** Inverse mapping from integers to atoms. */
    private Map<Integer, Atom> reverseMapping;
    /** Map of all the IDs that were mapped to atoms. */
    private Map<String, Atom> mapped;

    /**
     * Number of mapped predicates.
     */
    private int size;

    /**
     * Number  of auxiliary introduced variables.
     */
    private int auxiliary;

    /**
     * Number of all auxiliary variables.
     */
    private int auxMax = 100000;

    /**
     * Instantiates a new Atom mappings.
     */
    public SATAtomMappings() {
        mappings = new HashMap<Atom, Integer>();
        reverseMapping = new HashMap<Integer, Atom>();
        mapped = new HashMap<String, Atom>();
        /* First auxMax variables are reserved for auxiliary variables */
        size = auxMax + 1;
        auxiliary = 1;
    }

    /**
     * Function is returning the mapping number of the <b>{@code predicate(argument)}</b>. If the Atom did not occur before,
     * it is added to the mapping set and the mapping value is returned, otherwise the existing mapping value is returned.
     *
     * @param predicate   Predicate of the mapped atom.
     * @param usedInState SMTFunctionArgument of the mapped atom (usually name of the type/module state).
     * @param elementType Element that defines what type of a predicate is described (such as {@link SMTDataType#MODULE}.
     * @return Mapping number of the atom (number is always &gt; 0).
     */
    public Integer add(PredicateLabel predicate, State usedInState, WorkflowElement elementType) throws MappingsException {
        Atom atom = new Atom(predicate, usedInState, elementType);

        Integer id;
        if ((id = mappings.get(atom)) == null) {
            if (mapped.get(atom.toString()) != null) {
            	Atom tmp = mapped.get(atom.toString());
                throw MappingsException.mappedAtomsSignaturesOverlap("Encoding error. Two or more mappings map share same string: '" + atom.toString() + "' as ID.");
            }
            size++;
            mappings.put(atom, size);
            reverseMapping.put(size, atom);
            mapped.put(atom.toString(), atom);
            return size;
        }
        return id;
    }

    /**
     * Return the mapping value (Integer) for the <b>atom</b>.
     * If the <b>atom</b> was not mapped it returns null.
     *
     * @param atom String representation of the atom.
     * @return Mapping of the atom.
     */
    public Integer findMapping(Atom atom) {
        return mappings.get(atom);
    }


    /**
     * Return the mapping value (Integer) for the<b>atom</b>.
     * If the <b>atom</b> was not mapped it returns null.
     *
     * @param mapping Integer mapping of the atom.
     * @return The original atom.
     */
    public Atom findOriginal(Integer mapping) {
        return reverseMapping.get(mapping);
    }

    /**
     * Gets size.
     *
     * @return The size of the mapping set.
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the next auxiliary number and increase the counterErrors by 1.
     *
     * @return Mapping number that can be used for auxiliary variables.
     */
    public int getNextAuxNum() {
        return auxiliary++;
    }

    /**
     * Reset aux variables.
     */
    public void resetAuxVariables() {
        auxiliary = 1;
    }


    /**
     * Get the number of mapped auxiliary variables that are not part of the solution.
     *
     * @return Number of mapped auxiliary variables.
     */
    public int getCurrNumOfMappedAuxVar() {
        return auxiliary;
    }

    /**
     * Get the max number of mapped auxiliary variables that are not part of the solution.
     *
     * @return Max number of possible mapped auxiliary variables.
     */
    public int getMaxNumOfMappedAuxVar() {
        return auxMax;
    }
	
}
