package nl.uu.cs.ape.sat.ape;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import util.GitHubRepo;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Evaluation.success;
import static util.TestResources.getJSONResource;

/**
 * The {@code UseCaseTest} test is an initial version for functional testing that makes use of the API.
 *
 * @author Maurin Voshol
 */
class UseCaseTest {

    private static GitHubRepo repo;

    @BeforeAll
    public static void before() {
        repo = new GitHubRepo("sanctuuary/APE_UseCases", "UnitTest");
    }

    @AfterAll
    public static void after() {
        repo.cleanUp();
    }

    @Test
    void testUseCaseGeoGMT() throws IOException, OWLOntologyCreationException {

        if (!repo.canConnect()) {
            System.out.println("Could not perform use case tests because there is no active internet connection.");
            return;
        }

        System.out.println("--------------------------------------------");
        System.out.println("       RUN USE CASE TESTS");
        System.out.println("--------------------------------------------");

        UseCase useCase = new UseCase("use_cases/GeoGMT_UseCase_Evaluation.json", repo);

        for (UseCase.Mutation mutation : useCase.mutations) {

            System.out.println("\n\n--------------------------------------------");
            System.out.println("    USE CASE: " + useCase.name);
            System.out.println("    CONFIG MUTATION: " + mutation.config.toString());
            System.out.println("--------------------------------------------");

            JSONObject config = useCase.getMutatedConfiguration(mutation);

            final int max_no_solutions = config.getInt("max_solutions");
            int current_solution_length = mutation.solution_length_start;
            for (int no_solutions : mutation.expected_no_solutions) {
                config.put("solution_min_length", current_solution_length);

                //System.out.println(String.format("min_length=%s, no_solutions=%s, max_solutions=%s", config.getInt("solution_min_length"), no_solutions, config.getInt("max_solutions")));

                SATsolutionsList solutions = new APE(config).runSynthesis(config);
                assertEquals(current_solution_length, solutions.get(no_solutions - 1).getSolutionlength(),
                        String.format("Solution with index '%s' should have a length of '%s', but has an actual length of '%s'.", no_solutions-1, current_solution_length, solutions.get(no_solutions-1).getSolutionlength()));
                success("Workflow solution at index %s has expected length of %s", no_solutions - 1, current_solution_length);

                if (no_solutions < max_no_solutions && solutions.getNumberOfSolutions() > no_solutions) {
                    assertEquals(current_solution_length + 1, solutions.get(no_solutions).getSolutionlength(),
                            String.format("Solution with index '%s' should have a length of '%s', but has an actual length of '%s'.", no_solutions, current_solution_length + 1, solutions.get(no_solutions).getSolutionlength()));
                    success("Workflow solution at index %s has expected length of %s", no_solutions, current_solution_length + 1);
                }

                current_solution_length++;
            }

            // no solutions can be found
            if (mutation.expected_no_solutions.length == 0) {
                final int no_solutions = new APE(config).runSynthesis(config).getNumberOfSolutions();
                assertEquals(0, no_solutions, String.format("APE found '%s' solutions, while for this use case APE should have found 0.", no_solutions));
                success("APE did not find any solutions as expected.");
            }
        }

        success("Use case '%s' ran successfully!", useCase.name);

    }

    @Test
    void testUseCaseSimpleDemo() {

    }

    @Test
    void testUseCaseImageMagick() {

    }

    private static class UseCase {

        public final String name;
        public final JSONObject base_configuration;
        public final Mutation[] mutations;

        public UseCase(String useCasePath, GitHubRepo repo) throws IOException {

            JSONObject useCase = getJSONResource(useCasePath);

            // read name
            this.name = useCase.getString("name");

            // read mutations
            JSONArray jsonArray = useCase.getJSONArray("mutations");
            this.mutations = new Mutation[jsonArray.length()];
            for (int i = 0; i < this.mutations.length; i++) {
                this.mutations[i] = new Mutation(jsonArray.getJSONObject(i));
            }

            // read base config
            this.base_configuration = repo.getJSONObject(useCase.getString("base_configuration"));
            // create path/directory for the solutions
            this.base_configuration.put("solutions_path", repo.getRoot() + "\\sat_solutions.txt");
            this.base_configuration.put("execution_scripts_folder", repo.getRoot() + "\\execution_scripts");
            this.base_configuration.put("solution_graphs_folder", repo.getRoot() + "\\solution_graphs");
            this.base_configuration.put("debug_mode", false);

            for (String tag : new String[]{"ontology_path", "tool_annotations_path", "constraints_path"}) {
                this.base_configuration.put(tag, repo.getFile(this.base_configuration.getString(tag)));
            }
        }

        public JSONObject getMutatedConfiguration(Mutation mutation) {
            JSONObject newConfig = new JSONObject(base_configuration, JSONObject.getNames(base_configuration));
            for (String key : mutation.config.keySet()) {
                newConfig.put(key, mutation.config.get(key));
            }
            return newConfig;
        }

        public static class Mutation {

            public final JSONObject config;
            public final int solution_length_start;
            public final int[] expected_no_solutions;

            public Mutation(JSONObject obj) {

                this.config = obj.getJSONObject("config");
                this.solution_length_start = obj.getInt("solution_length_start");

                JSONArray jsonArray = obj.getJSONArray("expected_no_solutions");
                this.expected_no_solutions = new int[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.expected_no_solutions[i] = jsonArray.getInt(i);
                }
            }
        }
    }
}
