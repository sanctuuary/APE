package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;

/**
 * The {@code AllModules} class represent the set of all modules/tools that can
 * be part of our program. Each of them is either {@link Module} or
 * {@link AbstractModule}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllModules extends AllPredicates {


	public AllModules(APEConfig config) {
		super(Arrays.asList(config.getToolTaxonomyRoot()));
	}

	/**
	 * Return the set of currently defined modules (both {@link AbstractModule} and {@link Module}).
	 * @return
	 */
	public Collection<TaxonomyPredicate> getModules() {
		return getPredicates().values();
	}

	/**
	 * The class is used to check weather the module with <b>moduleID</b> was already
	 * introduced earlier on in <b>allModules</b>. In case it was it returns the item,
	 * otherwise the new element is generated and returned. <br>
	 * <br>
	 * In case of generating a new Module, the object is added to the set of all the
	 * Modules and added as a subModule to the parent Module.
	 * 
	 * @param module - The AbstractModule/Module that needs to be added.
	 * @return The element if it's a new one or the existing element if this set
	 *         contains the specified element.
	 * @throws ExceptionInInitializerError - if the given TaxonomyPredicate <b>module</b> is not an {@link AbtractModule}.
	 */
	public AbstractModule addPredicate(TaxonomyPredicate module) throws ExceptionInInitializerError {
		TaxonomyPredicate tmpModule = getPredicates().get(module.getPredicateID());
		if (module instanceof Module && (tmpModule != null)) {
			if (tmpModule instanceof Module) {
				return (Module) tmpModule;
			} else {
				Module newModule = new Module(((Module) module), tmpModule);
				/*
				 * swap the AbstractModule with the Module
				 */
				swapAbstractModule2Module(newModule, tmpModule);
				
				return newModule;
			}
		} else {
			if (tmpModule != null) {
				return (AbstractModule) tmpModule;
			} else if(module instanceof AbstractModule){
				getPredicates().put(module.getPredicateID(), module);
				return (AbstractModule) module;
			} else {
				throw new ExceptionInInitializerError("Type error. Only AbstractModule PredicateLabel can be added to AllModules.");
			}
		}
	}
	

	/**
	 * Removes the {@link AbstractModule} from the set of all modules and adds the
	 * {@link Module} element (or vice versa). Swaps the objects in the set of all
	 * Modules.
	 * 
	 * @param newModule - object that will be added
	 * @param oldModule - object that will be removed
	 */
	public void swapAbstractModule2Module(Module newModule, TaxonomyPredicate oldModule) {
		getPredicates().remove(oldModule.getPredicateID());
		getPredicates().put(newModule.getPredicateID(), newModule);
	}

	/**
	 * Returns the module to which the specified key is mapped to, or {@code null}
	 * if the moduleID has no mappings.
	 * 
	 * @param moduleID - the key whose associated value is to be returned
	 * @return {@link AbstractModule} or {@link Module} to which the specified key
	 *         is mapped to, or {@code null} if the moduleID has no mappings
	 */
	public AbstractModule get(String moduleID) {
		return (AbstractModule) getPredicates().get(moduleID);
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	public List<Pair<PredicateLabel>> getSimplePairs() {
		List<Pair<PredicateLabel>> pairs = new ArrayList<Pair<PredicateLabel>>();

		List<TaxonomyPredicate> iterator = new ArrayList<TaxonomyPredicate>();
		for (TaxonomyPredicate module : getPredicates().values()) {
			if(module.isSimplePredicate()) {
				iterator.add(module);
			}
		}

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {
				pairs.add(new Pair<PredicateLabel>(iterator.get(i), iterator.get(j)));
			}
		}
		return pairs;
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param module that is searched for
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(AbstractModule module) {
		return getPredicates().containsKey(module.getPredicateID());
	}
	
	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param ID of the module that is searched for
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(String moduleID) {
		return getPredicates().containsKey(moduleID);
	}

	public int size() {
		return this.size();
	}
	
	public Class<?> getPredicateClass(){
		return AbstractModule.class;
	}

}
