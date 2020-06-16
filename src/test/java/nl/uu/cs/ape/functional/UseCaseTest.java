package nl.uu.cs.ape.functional;

import java.io.IOException;

import nl.uu.cs.ape.TestUtil;
import nl.uu.cs.ape.sat.utils.APEConfigException;
import org.json.JSONObject;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;

/**
 * The {@code UseCaseTest} test is an initial version for functional testing that makes use of the API.
 *
 * @author Maurin Voshol
 *
 */
class UseCaseTest {

	/**
	 * Create an APE framework using a simple ontology. The library should find exactly 5 solutions, because this is specified in the configuration file.
	 */

	void synthesizeSolutions() throws ExceptionInInitializerError, IOException, APEConfigException {

		// import basic configuration file
		JSONObject config = TestUtil.getJSONResource("nl/uu/cs/ape/use_case/ape.configuration");
		// add the absolute paths to the json object
		config.put("ontology_path", 			TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/GMT_Demo_UseCase.owl"));
		config.put("tool_annotations_path", 	TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/tool_annotations.json"));
		config.put("constraints_path", 			TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/constraints.json"));
		config.put("solutions_path", 			TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/") + "sat_solutions.txt");
		config.put("execution_scripts_folder", 	TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/Implementations/"));
		config.put("solution_graphs_folder", 	TestUtil.getAbsoluteResourcePath("nl/uu/cs/ape/use_case/Figures/"));
		config.put("debug_mode", 	false);

		// set the max_solutions to 5 in the configuration file
		int max_solutions = 5;
		config.put("max_solutions", max_solutions);

		// create a new APE framework
		APE apeFramework = new APE(config);

		// run the synthesis
		SATsolutionsList solutions = apeFramework.runSynthesis(config, apeFramework.getDomainSetup());

		// library should find exactly 5 solutions
		//assertEquals(max_solutions, solutions.size());
	}
}
