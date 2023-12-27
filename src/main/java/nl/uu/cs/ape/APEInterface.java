package nl.uu.cs.ape;

import java.io.IOException;
import java.util.Collection;
import java.util.SortedSet;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.domain.Domain;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;

/**
 * The {@code APEInterface} is the interface of the main class of the library
 * and is supposed to be
 * the main interface for working with the library.
 *
 * @author Vedran Kasalica
 */
public interface APEInterface {

	/**
	 * Method that return all the supported constraint templates.
	 *
	 * @return List of {@link ConstraintTemplate} objects.
	 */
	public Collection<ConstraintTemplate> getConstraintTemplates();

	/**
	 * The method returns the configuration file of the APE instance.
	 *
	 * @return The {@link APECoreConfig} that contains all core configuration
	 *         parameters.
	 */
	public APECoreConfig getConfig();

	/**
	 * Gets domain setup.
	 *
	 * @return The object that contains all crucial information about the domain
	 *         (e.g. list of tools, data types, constraint factory, etc.)
	 */
	public Domain getDomainSetup();

	/**
	 * Returns all the taxonomy elements that are subclasses of the given element.
	 * Can be used to retrieve all data types, formats or all taxonomy operations.
	 *
	 * @param taxonomyElementID ID of the taxonomy element that is parent of all the
	 *                          returned elements.
	 * @return Sorted set of elements that belong to the given taxonomy subtree.
	 */
	public SortedSet<TaxonomyPredicate> getTaxonomySubclasses(String taxonomyElementID);

	/**
	 * Returns the {@link TaxonomyPredicate} that corresponds to the given ID.
	 *
	 * @param taxonomyElementID ID of the taxonomy element
	 * @return The corresponding {@link TaxonomyPredicate}
	 */
	public TaxonomyPredicate getTaxonomyElement(String taxonomyElementID);

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param configObject Object that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(JSONObject configObject) throws IOException, APEConfigException;

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param runConfigPath Path to the JSON that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(String runConfigPath) throws IOException, JSONException, APEConfigException;

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param runConfig Configuration object that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(APERunConfig runConfig) throws IOException;

}
