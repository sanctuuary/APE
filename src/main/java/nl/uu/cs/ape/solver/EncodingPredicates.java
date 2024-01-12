package nl.uu.cs.ape.solver;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.uu.cs.ape.models.AuxiliaryPredicate;

/**
 * The {@code EncodingPredicates} class is used to store the helper predicates,
 * i.e., predicates that are used to encode complex
 * logic expressions.
 */
@NoArgsConstructor
public abstract class EncodingPredicates {

    /**
     * Helper predicates defined within the domain model.
     */
    @Getter
    private final List<AuxiliaryPredicate> helperPredicates = new ArrayList<>();

    /**
     * Add predicate to the list of auxiliary predicates that should be encoded.
     * 
     * @param helperPredicate
     */
    public void addHelperPredicate(AuxiliaryPredicate helperPredicate) {
        helperPredicates.add(helperPredicate);
    }
}
