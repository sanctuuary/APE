package nl.uu.cs.ape.models;

import java.util.HashMap;
import java.util.Map;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtomVar;

/**
 * The {@code SATAtomMappings} class is used to store the data used for
 * representing the atoms with integer numbers.
 * Atoms are not a separate data structure, but a string combination of a
 * {@link TaxonomyPredicate}
 * and a {@link State} as am argument.<br>
 * Required for the SAT representation of the CNF formula.
 *
 * @author Vedran Kasalica
 */
public class SATAtomMappings {

    /**
     * First variable that can be used for auxiliary variables.
     * Numbers 1 and 2 are reserved for special symbols. 1 is {@code true} and 2 is
     * {@code false}.
     */
    private static final int auxDefaultInit = 3;
    /** Max number of all auxiliary variables. */
    private static final int auxMax = 100000;
    /** Max number of all expected atoms containing variables. */
    private static final int atomVarMaxNo = 100000;
    /** Mapping of the atoms to integers. */
    private Map<SLTLxAtom, Integer> mappings;
    /** Inverse mapping from integers to atoms. */
    private Map<Integer, SLTLxAtom> reverseMapping;
    /** Map of all the IDs that were mapped to atoms. */
    private Map<String, SLTLxAtom> mapped;

    /** Mapping of the atoms over variables to integers. */
    private Map<SLTLxAtomVar, Integer> vMappings;
    /** Inverse mapping from integers to atoms containing variables. */
    private Map<Integer, SLTLxAtomVar> vReverseMapping;
    /** Map of all the IDs that were mapped to atoms containing variables. */
    private Map<String, SLTLxAtomVar> vMapped;

    /**
     * Number of mapped atoms.
     */
    private int atomNo;

    /**
     * Number of mapped atoms containing variables.
     */
    private int atomVarNo;

    /**
     * Last number used to represent auxiliary introduced variables.
     * Numbers 1 and 2 are special symbols. 1 is {@code true} and 2 is
     * {@code false}.
     */
    private int auxiliary;

    /**
     * Instantiates a new SLTLxAtom mappings.
     */
    public SATAtomMappings() {
        mappings = new HashMap<SLTLxAtom, Integer>();
        reverseMapping = new HashMap<Integer, SLTLxAtom>();
        mapped = new HashMap<String, SLTLxAtom>();
        vMappings = new HashMap<SLTLxAtomVar, Integer>();
        vReverseMapping = new HashMap<Integer, SLTLxAtomVar>();
        vMapped = new HashMap<String, SLTLxAtomVar>();

        /* First auxMax variables are reserved for auxiliary variables */
        auxiliary = auxDefaultInit;
        atomVarNo = auxMax + 1;
        atomNo = auxMax + atomVarMaxNo + 1;
    }

    /**
     * Function is returning the mapping number of the
     * <b>{@code predicate(argument)}</b>. If the SLTLxAtom did not occur before,
     * it is added to the mapping set and the mapping value is returned, otherwise
     * the existing mapping value is returned.
     *
     * @param predicate   Predicate of the mapped atom.
     * @param usedInState SMTFunctionArgument of the mapped atom (usually name of
     *                    the type/module state).
     * @param elementType Element that defines what type of a predicate is described
     *                    (such as {@link AtomType#MODULE}.
     * @return Mapping number of the atom (number is always &gt; 0).
     */
    public synchronized Integer add(PredicateLabel predicate, State usedInState, AtomType elementType)
            throws MappingsException {
        SLTLxAtom atom = new SLTLxAtom(elementType, predicate, usedInState);

        Integer id;
        if ((id = mappings.get(atom)) == null) {
            if (mapped.get(atom.toString()) != null) {
                throw MappingsException.mappedAtomsSignaturesOverlap(
                        "Encoding error. Two or more mappings map share same string: '" + atom.toString() + "' as ID.");
            }
            atomNo++;
            mappings.put(atom, atomNo);
            reverseMapping.put(atomNo, atom);
            mapped.put(atom.toString(), atom);
            return atomNo;
        }
        return id;
    }

    /**
     * Function is returning the mapping number of the
     * <b>{@code predicate(argument)}</b>. If the SLTLxAtom did not occur before,
     * it is added to the mapping set and the mapping value is returned, otherwise
     * the existing mapping value is returned.
     *
     * @param atom atom that is added
     * @return Mapping number of the atom (number is always &gt; 0).
     */
    public synchronized Integer add(SLTLxAtom atom) throws MappingsException {

        Integer id;
        if ((id = mappings.get(atom)) == null) {
            if (mapped.get(atom.toString()) != null) {
                throw MappingsException.mappedAtomsSignaturesOverlap(
                        "Encoding error. Two or more mappings map share same string: '" + atom.toString() + "' as ID.");
            }
            atomNo++;
            mappings.put(atom, atomNo);
            reverseMapping.put(atomNo, atom);
            mapped.put(atom.toString(), atom);
            return atomNo;
        }
        return id;
    }

    /**
     * Function is returning the mapping number of the
     * <b>{@code predicate(argument)}</b>.
     * If the SLTLxAtomVar did not occur before,
     * it is added to the mapping set and the mapping value is returned,
     * otherwise the existing mapping value is returned.
     *
     * @param atomVar atom containing variable(s) that is added
     * @return Mapping number of the atom (number is always &gt; 0).
     */
    public synchronized Integer add(SLTLxAtomVar atomVar) throws MappingsException {

        Integer id;
        if ((id = vMappings.get(atomVar)) == null) {
            if (vMapped.get(atomVar.toString()) != null) {
                throw MappingsException
                        .mappedAtomsSignaturesOverlap("Encoding error. Two or more mappings map share same string: '"
                                + atomVar.toString() + "' as ID.");
            }
            atomVarNo++;
            vMappings.put(atomVar, atomVarNo);
            vReverseMapping.put(atomVarNo, atomVar);
            vMapped.put(atomVar.toString(), atomVar);
            return atomVarNo;
        } else {
            return id;
        }

    }

    /**
     * Return the mapping value (Integer) for the <b>atom</b>.
     * If the <b>atom</b> was not mapped it returns null.
     *
     * @param atom String representation of the atom.
     * @return Mapping of the atom.
     */
    public Integer findMapping(SLTLxAtom atom) {
        return this.mappings.get(atom);
    }

    /**
     * Return the <b>atom</b> for the mapping value (Integer).
     * If the <b>integer</b> was not mapped it returns null.
     *
     * @param mappedValue Integer mapping of the atom.
     * @return The original atom.
     */
    public SLTLxAtom findOriginal(Integer mappedValue) {
        return this.reverseMapping.get(mappedValue);
    }

    /**
     * Return the <b>atom with variables</b> for the mapping value (Integer).
     * If the <b>integer</b> was not mapped it returns null.
     *
     * @param mappedValue Integer mapping of the atom.
     * @return The original atom (which contains variables).
     */
    public SLTLxAtomVar findOriginalVar(Integer mappedValue) {
        return this.vReverseMapping.get(mappedValue);
    }

    /**
     * Gets atomNo.
     *
     * @return The atomNo of the mapping set.
     */
    public int getSize() {
        return atomNo;
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
        auxiliary = auxDefaultInit;
    }

    /**
     * Get the number of mapped auxiliary variables that are not part of the
     * solution.
     *
     * @return Number of mapped auxiliary variables.
     */
    public int getCurrNumOfMappedAuxVar() {
        return auxiliary;
    }

    /**
     * Get the max number of mapped auxiliary variables that are not part of the
     * solution.
     *
     * @return Max number of possible mapped auxiliary variables.
     */
    public int getInitialNumOfMappedAtoms() {
        return auxMax + atomVarMaxNo + 1;
    }

}
