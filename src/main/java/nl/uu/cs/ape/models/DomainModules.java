package nl.uu.cs.ape.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.logic.constructs.Predicate;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code DomainModules} class represent the set of all modules/tools that
 * are part of the configured domain. They can
 * be steps in generated workflows. Each of them is either {@link Module} or
 * {@link AbstractModule}.
 *
 * @author Vedran Kasalica
 */
public class DomainModules extends DomainPredicates {

    /**
     * Instantiates a new All modules.
     *
     * @param config the config
     */
    public DomainModules(APECoreConfig config) {
        super(Arrays.asList(config.getToolTaxonomyRoot()));
    }

    /**
     * Instantiates a new AllModules object.
     *
     * @param moduleTaxonomyRoot root module
     */
    public DomainModules(String moduleTaxonomyRoot) {
        super(Arrays.asList(moduleTaxonomyRoot));
    }

    /**
     * Gets modules.
     *
     * @return The set of currently defined modules (both {@link AbstractModule} and
     *         {@link Module}).
     */
    public Collection<TaxonomyPredicate> getModules() {
        return getMappedPredicates().values();
    }

    /**
     * The class is used to check weather the module with <b>moduleID</b> was
     * already
     * introduced earlier on in <b>allModules</b>. In case it was it returns the
     * item,
     * otherwise the new element is generated and returned.
     * <p>
     * In case of generating a new Module, the object is added to the set of all the
     * Modules and added as a subModule to the parent Module.
     *
     * @param module The AbstractModule/Module that needs to be added.
     * @return The element if it's a new one or the existing element if this set
     *         contains the specified element.
     * @throws ExceptionInInitializerError Error if the provided TaxonomyPredicate
     *                                     <b>module</b> is not an
     *                                     {@link AbstractModule}.
     */
    public AbstractModule addPredicate(TaxonomyPredicate module) throws ExceptionInInitializerError {
        TaxonomyPredicate tmpModule = getMappedPredicates().get(module.getPredicateID());
        if (module instanceof Module && (tmpModule != null)) {
            if (tmpModule instanceof Module) {
                return (Module) tmpModule;
            } else {
                Module newModule = new Module(((Module) module), tmpModule);

                /* swap the AbstractModule with the Module */
                swapAbstractModule2Module(newModule, tmpModule);

                return newModule;
            }
        } else {
            if (tmpModule != null) {
                return (AbstractModule) tmpModule;
            } else if (module instanceof AbstractModule) {
                getMappedPredicates().put(module.getPredicateID(), module);
                return (AbstractModule) module;
            } else {
                throw new ExceptionInInitializerError(String.format(
                        "Type error. Only 'AbstractModule' StateInterface can be added to the set of all modules. '%s' is not a type",
                        module.getPredicateID()));
            }
        }
    }

    /**
     * Removes the {@link AbstractModule} from the set of all modules and adds the
     * {@link Module}
     * element (or vice versa). Swaps the objects in the set of all Modules.
     *
     * @param newModule Object that will be added.
     * @param oldModule Object that will be removed.
     */
    public void swapAbstractModule2Module(Module newModule, TaxonomyPredicate oldModule) {
        getMappedPredicates().remove(oldModule.getPredicateID());
        getMappedPredicates().put(newModule.getPredicateID(), newModule);
    }

    /**
     * Returns the module to which the specified key is mapped to, or null
     * if the moduleID has no mappings.
     *
     * @param moduleID The key whose associated value is to be returned
     * @return {@link AbstractModule} or {@link Module} to which the specified key
     *         is mapped to, or null if the moduleID has no mappings
     */
    @Override
    public AbstractModule get(String moduleID) {
        return (AbstractModule) super.get(moduleID);
    }

    /**
     * Returns a list of pairs of tools from modules. Note that the abstract modules
     * are not returned, only the unique pairs of modules that are representing
     * actual tools.
     *
     * @return List of pairs of modules.
     */
    public Set<Pair<Predicate>> getSimplePairs() {

        Set<Predicate> iterator = new HashSet<Predicate>();
        for (TaxonomyPredicate module : getMappedPredicates().values()) {
            if (module.isSimplePredicate()) {
                iterator.add(module);
            }
        }

        return APEUtils.getUniquePairs(iterator);
    }

    /**
     * Returns the root predicate of the module taxonomy.
     * 
     * @return AbstractModule representing the root operation.
     */
    public AbstractModule getRootModule() {
        return (AbstractModule) getRootPredicates().get(0);
    }

    /**
     * Returns the unique ID of the root predicate of the module taxonomy.
     * 
     * @return String representing the ID of the root operation.
     */
    public String getRootModuleID() {
        return getAllRootIDs().get(0);
    }

    /**
     * Returns true if this set contains the specified element. More formally,
     * returns true if and only if this set contains an element e such that
     * {@code (o==null ? e==null : o.equals(e))}.
     *
     * @param module Module that is searched for.
     * @return true if this set contains the specified element.
     */
    public boolean existsModule(AbstractModule module) {
        return getMappedPredicates().containsKey(module.getPredicateID());
    }

    /**
     * Returns true if this set contains the specified element. More formally,
     * returns true if and only if this set contains an element e such that
     * {@code (o==null ? e==null : o.equals(e))}.
     *
     * @param moduleID ID of the module that is searched for.
     * @return true if this set contains the specified element.
     */
    public boolean existsModule(String moduleID) {
        return getMappedPredicates().containsKey(moduleID);
    }

    public Class<?> getPredicateClass() {
        return AbstractModule.class;
    }
}
