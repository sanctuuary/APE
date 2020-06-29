package nl.uu.cs.ape.functional;

import java.io.*;

import nl.uu.cs.ape.functional.github.GitHubRepo;
import nl.uu.cs.ape.sat.utils.APEConfigException;
import nl.uu.cs.ape.sat.utils.APEDimensionsException;

import org.json.JSONException;
import org.json.JSONObject;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import static nl.uu.cs.ape.TestUtil.getAbsoluteResourcePath;
import static nl.uu.cs.ape.TestUtil.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The {@code UseCaseTest} test is an initial version for functional testing that makes use of the API.
 *
 * @author Maurin Voshol
 *
 */
class UseCaseTest {

	private static GitHubRepo repo;

	@BeforeAll
	public static void before(){
		repo = new GitHubRepo("sanctuuary/APE_UseCases");
	}

	@AfterAll
	public static void after(){
		repo.cleanUp();
	}

	@Test
	void useCases() throws IOException, OWLOntologyCreationException {

		if(!repo.canConnect()){
			System.out.println("Could not perform use case tests because there is no active internet connection.");
			return;
		}

		System.out.println("--------------------------------------------");
		System.out.println("       RUN USE CASE TESTS");
		System.out.println("--------------------------------------------");

		for(UseCaseInfo useCaseInfo : readUseCases(getAbsoluteResourcePath("use_case_tests.json"))){

			repo.setCommit(useCaseInfo.commit);

			JSONObject config = readBase(useCaseInfo.config_path);

			for(UseCaseMutation mutationInfo : useCaseInfo.mutations){

				System.out.println("\n\n--------------------------------------------");
				System.out.println("    USE CASE: " + useCaseInfo.name);
				System.out.println("    CONFIG MUTATION: " + mutationInfo.config.toString());
				System.out.println("--------------------------------------------");

				mutate(config, mutationInfo); // mutate the configuration file

				final int max_no_solutions = config.getInt("max_solutions");
				int current_solution_length = mutationInfo.solution_length_start;
				for(int no_solutions : mutationInfo.expected_no_solutions){

					setMinSolutionLength(config, current_solution_length);
					SATsolutionsList solutions = new APE(config).runSynthesis(config);
					assertEquals(current_solution_length, solutions.get(no_solutions-1).getSolutionlength());
					success("Workflow solutions[%s] has expected length of %s", no_solutions-1, current_solution_length);

					if(no_solutions < max_no_solutions){
						assertEquals(current_solution_length + 1, solutions.get(no_solutions).getSolutionlength());
						success("Workflow solutions[%s] has expected length of %s", no_solutions, current_solution_length + 1);
					}

					current_solution_length++;
				}
			}

			success("Use case '%s' ran successfully!", useCaseInfo.name);
		}
	}

	private void setMinSolutionLength(JSONObject config, int length) {
		config.put("solution_min_length", length);
		config.put("solution_max_length", length + 1);
	}

	private void mutate(JSONObject config, UseCaseMutation mutation) {
		for(String tag : mutation.config.keySet()){
			config.put(tag, mutation.config.get(tag));
		}
	}

	private static UseCaseInfo[] readUseCases(String JSONPath) {
		// TODO: read use cases from a JSON file?
		return new UseCaseInfo[]{
				new UseCaseInfo("SimpleDemo", "master", "SimpleDemo/ape.configuration",
						new UseCaseMutation[]{
								new UseCaseMutation(new JSONObject(),
										6, new int[]{ 100 }),
								new UseCaseMutation(new JSONObject().put("use_workflow_input", "one"),
										5, new int[]{ 24, 100 })
						})
		};
	}

	private JSONObject readBase(String filePath){

		JSONObject config = repo.getJSONObject(filePath);

		// create path/directory for the solutions
		config.put("solutions_path", repo.getRoot() + "\\sat_solutions.txt");
		config.put("execution_scripts_folder", repo.getRoot() + "\\execution_scripts");
		config.put("solution_graphs_folder", repo.getRoot() + "\\solution_graphs");
		config.put("debug_mode", false);

		for(String tag : new String[]{ "ontology_path", "tool_annotations_path", "constraints_path"  }){
			final String downloadedPath = repo.getFile(config.getString(tag));
			config.put(tag, downloadedPath);
		}
		return config;
	}
}
