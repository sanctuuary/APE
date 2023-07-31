package nl.uu.cs.ape;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.core.solutionStructure.SolutionsList;
import nl.uu.cs.ape.io.APEFiles;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;

/**
 * The entry point of application when the library is used in a Command Line
 * Interface (CLI).
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class Main {

    /**
     * The entry point of application when the library is used in a Command Line
     * Interface (CLI).
     *
     * @param args APE expects at most one (1) argument: The absolute or relative
     *             path to the configuration file.
     */
    public static void main(String[] args) {
        String path;
        int solutionsNo = -1;
        if (args.length == 1) {
            path = args[0];
        } else if (args.length == 2) {
            path = args[0];
            try {
                solutionsNo = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.error("Second parameter is not an integer.");
            }
        } else {
            path = "./config.json";
        }
        if (!APEFiles.isValidReadFile(path)) {
            log.error("Bad path.");
            return;
        }

        APE apeFramework = null;
        try {

            // set up the APE framework
            apeFramework = new APE(path);

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            log.error("Error in setting up the APE framework:");
            log.error(e.getMessage());
            return;
        }

        SolutionsList solutions;
        try {

            JSONObject runConfigJson = APEFiles.readFileToJSONObject(new File(path));
            APERunConfig runConfig = new APERunConfig(runConfigJson, apeFramework.getDomainSetup());

            if (solutionsNo > 0) {
                runConfig.setMaxNoSolutions(solutionsNo);
            }
            // run the synthesis and retrieve the solutions
            solutions = apeFramework.runSynthesis(runConfig);

        } catch (APEConfigException e) {
            log.error("Error in synthesis execution. APE configuration error:");
            log.error(e.getMessage());
            return;
        } catch (JSONException e) {
            log.error(
                    "Error in synthesis execution. Bad JSON formatting (APE configuration or constriants JSON). ");
            log.error(e.getMessage());
            return;
        } catch (IOException e) {
            log.error("Error in synthesis execution.");
            log.error(e.getMessage());
            return;
        }

        /*
         * Writing solutions to the specified file in human readable format
         */
        if (solutions.isEmpty()) {
            log.info("The problem is UNSAT.");
        } else {
            try {
                APE.writeSolutionToFile(solutions);
                APE.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
                // APE.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
                APE.writeExecutableWorkflows(solutions);
                APE.writeCWLWorkflows(solutions);
                // APE.writeExecutableCWLWorkflows(solutions, apeFramework.getConfig());
            } catch (IOException e) {
                log.error("Error in writing the solutions. to the file system.");
                e.printStackTrace();
            }
        }
    }
}
