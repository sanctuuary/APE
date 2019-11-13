package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Predicate;

/**
 * The {@code AllModules} class represent the set of all modules/tools that can
 * be part of our program. Each of them is either {@link Module} or
 * {@link AbstractModule}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllModules extends HashMap<String, AbstractModule>{

	/** Set of all the module IDs of the annotated modules in the domain. */
	private Set<String> annotatedModules;
	/** Root of the data taxonomy. */
	private String moduleTaxonomyRoot;

	public AllModules() {
		super();
		this.annotatedModules = new HashSet<String>();
		this.moduleTaxonomyRoot = APEConfig.getConfig().getTool_taxonomy_root();
	}

	/**
	 * Create a map-set containing all modules from @modules, omitting the
	 * duplicates.
	 * 
	 * @param modules
	 */
	public AllModules(Collection<? extends AbstractModule> modules) {
		super();
		for (AbstractModule module : modules) {
			this.addModule(module);
		}
	}

	/**
	 * Return the set of currently defined modules (both {@link AbstractModule} and {@link Module}).
	 * @return
	 */
	public Collection<AbstractModule> getModules() {
		return super.values();
	}

	/**
	 * The class is used to check weather the module with @moduleID was already
	 * introduced earlier on in @allModules. In case it was it returns the item,
	 * otherwise the new element is generated and returned. <br>
	 * <br>
	 * In case of generating a new Module, the object is added to the set of all the
	 * Modules and added as a subModule to the parent Module.
	 * 
	 * @param module - The AbstractModule/Module that needs to be added.
	 * @return The element if it's a new one or the existing element if this set
	 *         contains the specified element.
	 */
	public AbstractModule addModule(AbstractModule module) {
		AbstractModule tmpModule = super.get(module.getPredicateID());
		if (module instanceof Module && (tmpModule != null)) {
			if (tmpModule instanceof Module) {
				return tmpModule;
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
				return tmpModule;
			} else {
				this.put(module.getPredicateID(), module);
				return module;
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
	public void swapAbstractModule2Module(Module newModule, AbstractModule oldModule) {
		this.remove(oldModule.getPredicateID());
		this.put(newModule.getPredicateID(), newModule);
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
		return super.get(moduleID);
	}

	/**
	 * Returns the root module of the taxonomy.
	 * 
	 * @return The root module.
	 */
	public AbstractModule getRootModule() {
		return this.get(moduleTaxonomyRoot);
	}
	
	/**
	 * Returns the ID of the root module of the taxonomy.
	 * 
	 * @return The root module.
	 */
	public String getRootID() {
		return moduleTaxonomyRoot;
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param module
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(AbstractModule module) {
		return this.containsKey(module.getPredicateID());
	}

	public int size() {
		return this.size();
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	public List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<Pair>();

		List<AbstractModule> iterator = new ArrayList<AbstractModule>();
		for (AbstractModule module : this.values()) {
			if (module.isTool())
				iterator.add(module);
		}

//		System.out.println(APEConfig.getConfig().getTool_taxonomy_root() + ": " + iterator.size());

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}


	/**
	 * Return {@code true} if the module is annotated.
	 * 
	 * @param moduleID - ID of the module that is evaluated.
	 * @return {@code true} if the module is annotated, {@code false} otherwise.
	 */
	public boolean getIsAnnotatedModule(String moduleID) {
		return annotatedModules.contains(moduleID);
	}

	/**
	 * Adds the module to the set of annotated modules.
	 * 
	 * @param moduleID - ID of the module that is annotated.
	 */
	public void addAnnotatedModule(String moduleID) {
		annotatedModules.add(moduleID);
	}
	
}
