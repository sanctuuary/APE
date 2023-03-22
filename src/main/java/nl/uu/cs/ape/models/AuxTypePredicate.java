package nl.uu.cs.ape.models;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.NodeType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code AuxTypePredicate} class represents an abstract class used
 * strictly to represent artificially generated types, used to abstract
 * over existing taxonomy terms. Objects of this class represent disjunctions
 * of conjunctions of existing types.
 *
 * @author Vedran Kasalica
 */
public class AuxTypePredicate extends Type implements AuxiliaryPredicate {

    /**
     * Types that are generalized with the auxiliary predicate.
     */
    private SortedSet<TaxonomyPredicate> containingPredicates;

    /**
     * Field defines the connective between the subclasses of the predicate.
     */
    private final LogicOperation logicOp;

    /**
     * Create an auxiliary predicate.<br>
     * <br>
     * It is recommended to use the method
     * {@link AuxTypePredicate#generateAuxiliaryPredicate} to generate an auxiliary
     * object.
     *
     * @param predicate the predicate
     * @param logicOp   the logic operator
     */
    private AuxTypePredicate(String typeName, String typeID, String rootNode, NodeType nodeType,
            Collection<TaxonomyPredicate> containingPredicates, LogicOperation logicOp) {
        super(typeName, typeID, rootNode, nodeType);
        this.logicOp = logicOp;
        this.containingPredicates = new TreeSet<TaxonomyPredicate>();
        this.containingPredicates.addAll(containingPredicates);
    }

    /**
     * Method used to generate a new predicate that should provide an interface for
     * handling multiple predicates.
     * New predicated is used to simplify interaction with a set of related
     * tools/types.
     * <p>
     * The original predicates are available as consumed predicates (see
     * {@link AuxTypePredicate#getGeneralizedPredicates()}) of the new
     * {@link TaxonomyPredicate}.
     *
     * @param relatedPredicates Set of sorted type that are logically related to the
     *                          new abstract type (label of the equivalent sets is
     *                          always the same due to its ordering).
     * @param logicOp           Logical operation that describes the relation
     *                          between the types.
     * @return An abstract predicate that provides abstraction over a
     *         disjunction/conjunction of the labels.
     */
    public static Type generateAuxiliaryPredicate(SortedSet<TaxonomyPredicate> relatedPredicates,
            LogicOperation logicOp, APEDomainSetup domainSetup) {
        if (relatedPredicates.isEmpty()) {
            return null;
        }
        if (relatedPredicates.size() == 1) {
            return (Type) relatedPredicates.first();
        }
        String abstractLabel = APEUtils.getLabelFromList(relatedPredicates, logicOp);

        AuxTypePredicate newAbsType = (AuxTypePredicate) domainSetup.getAllTypes()
                .addPredicate(new AuxTypePredicate(abstractLabel, abstractLabel,
                        relatedPredicates.first().getRootNodeID(), NodeType.ABSTRACT, relatedPredicates, logicOp));

        if (newAbsType != null) {
            domainSetup.addHelperPredicate(newAbsType);
            newAbsType.setAsRelevantTaxonomyTerm(domainSetup.getAllTypes());
        }
        return newAbsType;
    }

    public LogicOperation getLogicOp() {
        return logicOp;
    }

    public SortedSet<TaxonomyPredicate> getGeneralizedPredicates() {
        return this.containingPredicates;
    }

}
