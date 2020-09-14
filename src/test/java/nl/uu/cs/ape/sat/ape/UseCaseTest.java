package nl.uu.cs.ape.sat.ape;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import util.GitHubRepo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nl.uu.cs.ape.sat.utils.APEUtils.printWarning;
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

        repo = new GitHubRepo("sanctuuary/APE_UseCases", "master");

        final boolean canConnect = repo.canConnect();
        final String message = "There needs to be an active internet connection to run the use case tests.\nSKIP: " + UseCaseTest.class.getName();

        if (!canConnect)
            printWarning(message);

        // ignore use case tests if there is no active internet connection
        Assumptions.assumeTrue(canConnect, message);
    }

    @AfterAll
    public static void after() {
        repo.cleanUp();
    }

    @Test
    void GeoGMT() throws Exception {
        testUseCase("GeoGMT", "use_cases/GeoGMT_UseCase_Evaluation.json");
    }

    @Test
    void ImageMagick() throws Exception {
        testUseCase("ImageMagick", "use_cases/ImageMagick_UseCase_Evaluation.json");
    }

    @Test
    void MassSpectometry() throws Exception {
        testUseCase("MassSpectometry", "use_cases/MassSpectometry_UseCase_Evaluation.json");
    }

    void testUseCase(String name, String evaluationPath) throws Exception {

        System.out.println("-------------------------------------------------------------");
        System.out.println("       TEST " + name);
        System.out.println("-------------------------------------------------------------");

        UseCase useCase = new UseCase(evaluationPath, repo);

        for (UseCase.Mutation mutation : useCase.mutations) {

            mutation.print(useCase.name);

            final JSONObject config = mutation.execute(useCase.base_configuration);

            /*
             * If these are the generated solution lengths: S=[3,3,3,3,3, 4,4,4,4,4,4,4,4,4,4 ...] (from runSynthesis)
             * with starting value v=3 and number of solutions N=[5, 1000] (from use_cases/GeoGMT_UseCase_Evaluation.json)
             * then the following tests must succeed:
             * For each N[i] in N: (S[N[i] - 1] == v + i) AND (S[N[i]] == v + i + 1)
             */

            final SATsolutionsList solutions = new APE(config).runSynthesis(config);

            // Generate workflow images
            // APE.writeDataFlowGraphs(solutions, Rank.RankDir.TOP_TO_BOTTOM);

            final int max_no_solutions = config.getInt("max_solutions");
            int current_solution_length = mutation.solution_length_start;
            for (int no_solutions : mutation.expected_no_solutions) {

                assertEquals(current_solution_length, solutions.get(no_solutions - 1).getSolutionlength(),
                        String.format("Solution with index '%s' should have a length of '%s', but has an actual length of '%s'.", no_solutions - 1, current_solution_length, solutions.get(no_solutions - 1).getSolutionlength()));
                success("Workflow solution at index %s has expected length of %s", no_solutions - 1, current_solution_length);

                // if there are still solutions left (so not above 1000), test the upper bound
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

    private static class UseCase {

        public final String name;
        public final JSONObject base_configuration;
        public final Mutation[] mutations;

        public UseCase(String useCasePath, GitHubRepo repo) {

            JSONObject useCase = getJSONResource(useCasePath);

            // read name
            this.name = useCase.getString("name");

            // read base config
            this.base_configuration = repo.getJSONObject(useCase.getString("base_configuration"));
            // create path/directory for the solutions
            this.base_configuration.put("solutions_dir_path", repo.getRoot());
            this.base_configuration.put("debug_mode", false);

            this.base_configuration.put("ontology_path", repo.getFile(this.base_configuration.getString("ontology_path")));
            this.base_configuration.put("tool_annotations_path", repo.getFile(this.base_configuration.getString("tool_annotations_path")));

            String initial_constraints_path = this.base_configuration.getString("constraints_path");
            this.base_configuration.put("constraints_path", repo.getFile(initial_constraints_path));

            // read mutations
            JSONArray jsonArray = useCase.getJSONArray("mutations");
            this.mutations = new Mutation[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                this.mutations[i] = new Mutation(jsonArray.getJSONObject(i), initial_constraints_path, repo);
            }
        }

        public static class Mutation {

            private final static String CONFIG = "config";
            private final static String START_LENGTH = "solution_length_start";
            private final static String NO_SOLUTIONS = "expected_no_solutions";
            private final static String ADD_CONST = "add_constraints";
            private final static String REPLACE_CONST = "replace_constraints";
            private final static String CONSTRAINTS = "constraints";
            private final static String CONSTRAINTS_PATH = "constraints_path";
            private final static String DESCRIPTION = "description";

            public final int solution_length_start;
            public final int[] expected_no_solutions;
            public JSONObject config_mutations;
            public JSONObject replace_constraints;
            public JSONArray add_constraints;
            public String constraints_path;
            public String description;

            public Mutation(JSONObject mutation, String const_path, GitHubRepo folder) {

                this.config_mutations = mutation.has(CONFIG) ? mutation.getJSONObject(CONFIG) : null;
                this.solution_length_start = mutation.getInt(START_LENGTH);
                this.description = mutation.has(DESCRIPTION) ? mutation.getString(DESCRIPTION) : null;

                JSONArray jsonArray = mutation.getJSONArray(NO_SOLUTIONS);
                this.expected_no_solutions = new int[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.expected_no_solutions[i] = jsonArray.getInt(i);
                }

                if (mutation.has(REPLACE_CONST)) {
                    this.replace_constraints = new JSONObject().put(CONSTRAINTS, mutation.getJSONArray(REPLACE_CONST));
                    this.constraints_path = folder.createJSONFile(replace_constraints, CONSTRAINTS);

                } else if (mutation.has(ADD_CONST)) {
                    JSONArray currentConstraints = folder.getJSONObject(const_path).getJSONArray(CONSTRAINTS);
                    this.add_constraints = mutation.getJSONArray(ADD_CONST);
                    for (int i = 0; i < this.add_constraints.length(); i++) {
                        currentConstraints.put(this.add_constraints.get(i));
                    }
                    this.constraints_path = folder.createJSONFile(new JSONObject().put(CONSTRAINTS, currentConstraints), CONSTRAINTS);
                }
            }

            public JSONObject execute(JSONObject base_configuration) {

                JSONObject config = new JSONObject(base_configuration, JSONObject.getNames(base_configuration)); // copy

                if (this.config_mutations != null) {
                    for (String key : this.config_mutations.keySet()) {
                        config.put(key, this.config_mutations.get(key));
                    }
                }

                if (this.constraints_path != null) {
                    config.put(CONSTRAINTS_PATH, this.constraints_path);
                }

                return config;
            }

            public void print(String name) {
                System.out.println("-------------------------------------------------------------");
                System.out.println("    MUTATION FOR: " + name);

                if (description != null) {
                    System.out.println("    DESCRIPTION: " + description);
                }

                if (config_mutations != null) {
                    System.out.println("    CONFIG MUTATION: " + config_mutations.toString());
                }

                if (add_constraints != null) {
                    System.out.println("    ADD CONSTRAINTS: " + add_constraints.toString());
                }

                if (replace_constraints != null) {
                    System.out.println("    REPLACE CONSTRAINTS: " + replace_constraints.getJSONArray(CONSTRAINTS).toString());
                }

                System.out.println("    MINIMAL SOLUTION LENGTH: " + solution_length_start);
                System.out.println("    EXPECTED AMOUNT OF SOLUTIONS STARTING AT MINIMAL SOLUTIONS: " + Arrays.toString(expected_no_solutions));
                System.out.println("-------------------------------------------------------------\n");
            }
        }
    }
}
