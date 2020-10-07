package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONObject;

/**
 * The {@code AuxModulePredicate} class represents an abstract class used
 * strictly to represent artificially generated modules, used to abstract
 * over existing taxonomy terms. Objects of this class represent disjunctions
 * of conjunctions of existing modules.
 *
 * @author Vedran Kasalica
 */
public class AuxModulePredicate extends AbstractModule implements AuxiliaryPredicate {


	/**
	 * Modules that are generalized with the auxiliary predicate.
	 */
	private SortedSet<TaxonomyPredicate> containingPredicates;

    /**
     * Field defines the connective between the subclasses of the predicate.
     */
    private final LogicOperation logicOp;

    /**
     * Create an auxiliary predicate.<br><br>
     * It is recommended to use the method {@link AuxModulePredicate#generateAuxiliaryPredicate} to generate an auxiliary object.
     *
     * @param predicate the predicate
     * @param logicOp   the logic operator
     */
    private AuxModulePredicate(String moduleName, String moduleID, String rootNode, NodeType nodeType, Collection<TaxonomyPredicate> containingPredicates, LogicOperation logicOp) {
        super(moduleName, moduleID, rootNode, nodeType);
        this.logicOp = logicOp;
        this.containingPredicates = new TreeSet<TaxonomyPredicate>();
        this.containingPredicates.addAll(containingPredicates);
    }
    
    /**
     * Method used to generate a new predicate that should provide an interface for handling multiple predicates.
     * New predicated is used to simplify interaction with a set of related tools/types.
     * <p>
     * The original predicates are available as consumed predicates(see {@link AuxModulePredicate#getGeneralizedPredicates()}) of the new {@link TaxonomyPredicate}.
     *
     * @param relatedPredicates Set of sorted type that are logically related to the new abstract type (label of the equivalent sets is always the same due to its ordering).
     * @param logicOp           Logical operation that describes the relation between the types.
     * @return An abstract predicate that provides abstraction over a disjunction/conjunction of the labels.
     */
    public static AbstractModule generateAuxiliaryPredicate(SortedSet<TaxonomyPredicate> relatedPredicates, LogicOperation logicOp, APEDomainSetup domainSetup) {
        if (relatedPredicates.isEmpty()) {
            return null;
        }
        if (relatedPredicates.size() == 1) {
        	return (AbstractModule) relatedPredicates.first();
        }
        String abstractLabel = APEUtils.getLabelFromList(relatedPredicates, logicOp);

        
        AuxModulePredicate newAbsType = (AuxModulePredicate) domainSetup.getAllModules().addPredicate(new AuxModulePredicate(abstractLabel, abstractLabel, relatedPredicates.first().getRootNodeID(), NodeType.ABSTRACT, relatedPredicates, logicOp));

        if (newAbsType != null) {
        	domainSetup.addHelperPredicate(newAbsType);
        	newAbsType.setAsRelevantTaxonomyTerm(domainSetup.getAllModules());
        }
        return newAbsType;
    }

    /**
     * Gets logic operator used to group the abstracted predicates.
     *
     * @return the field {@link #logicOp}.
     */
    public LogicOperation getLogicOp() {
        return logicOp;
    }

    /**
     * Return the list of {@link TaxonomyPredicate}s that are generalized over using the helper predicate.
     *
     * @return all {@link TaxonomyPredicate}s generalized over.
     */
    public SortedSet<TaxonomyPredicate> getGeneralizedPredicates() {
        return this.containingPredicates;
    }

}
