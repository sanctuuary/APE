package nl.uu.cs.ape.models;

import java.util.HashMap;
import java.util.Map;

import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * The {@code SMTPredicateMappings} class is used to store the data used for representing the predicates with unique strings.
 * <br>
 * Required for the SMT representation of the CNF formula.
 *
 * @author Vedran Kasalica
 */
public class SMTPredicateMappings implements Mappings {

	/** Mapping of the predicates to Strings. */
    private Map<PredicateLabel, String> mappings;
    /** Inverse mapping from Strings to predicates. */
    private Map<String, PredicateLabel> reverseMapping;
    /** Map of all the predicate IDs that were mapped to predicates. */
    private Map<String, PredicateLabel> mapped;

    /**
     * Number of mapped predicates.
     */
    private int size;

    /**
     * Instantiates a new Atom mappings.
     */
    public SMTPredicateMappings() {
        mappings = new HashMap<PredicateLabel, String>();
        reverseMapping = new HashMap<String, PredicateLabel>();
        mapped = new HashMap<String, PredicateLabel>();
        size = 0;
    }

    /**
     * Function is returning the mapping String of the <b>predicate</b>. The mapping string follows the syntax of an identifier in SMTLib2. If the PredicateLabel did not occur before,
     * it is added to the mapping set and the mapping value is returned, otherwise the existing mapping value is returned.
     *
     * @param predicate   The mapped predicate.
     * @return Mapping number of the atom (number is always &gt; 0).
     */
    public String add(PredicateLabel predicate) throws MappingsException {

        String id;
        if ((id = mappings.get(predicate)) == null) {
            if (mapped.get(predicate.toString()) != null) {
                throw MappingsException.mappedPredicateSignaturesOverlap("Encoding error. Two or more mappings map share same string: '" + predicate.toString() + "' as ID.");
            }
            size++;
            // provide identifier supported by SMTLib2
            
            
            String smtSupportedID = SMTUtils.removeUnsupportedCharacters(predicate.getPredicateLabel());
            
            // add counter if the id was already used
            int count = 1;
            String tempID = smtSupportedID;
            while(reverseMapping.containsKey(tempID)) {
            	tempID = smtSupportedID + "_" + count++; 
            }
            smtSupportedID = tempID;
            
            mappings.put(predicate, smtSupportedID);
            reverseMapping.put(smtSupportedID, predicate);
            mapped.put(predicate.toString(), predicate);
            return smtSupportedID;
        }
        return id;
    }

    /**
     * Return the mapping value (String) for the <b>predicate</b>.
     * If the <b>predicate</b> was not mapped it returns null.
     *
     * @param predicate The predicate.
     * @return Mapping of the predicate.
     */
    public String findMapping(PredicateLabel predicate) {
        return mappings.get(predicate);
    }


    /**
     * Return the predicate associateD with the mapping identifier (String).
     * If the <b>identifier</b> was not mapped it returns null.
     *
     * @param mapping String mapping of the predicate.
     * @return The original predicate.
     */
    public PredicateLabel findOriginal(String mapping) {
        return reverseMapping.get(mapping);
    }

    /**
     * Gets number of the mapped predicates.
     *
     * @return The size of the mapping set.
     */
    public int getSize() {
        return size;
    }
	
}
