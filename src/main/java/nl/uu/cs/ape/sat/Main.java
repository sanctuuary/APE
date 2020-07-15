package nl.uu.cs.ape.sat;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.utils.APEConfigException;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.IOException;

/**
 * The entry point of application when the library is used in a Command Line Interface (CLI).
 *
 * @author Vedran Kasalica
 */
public class Main {

    /**
     * The entry point of application when the library is used in a Command Line Interface (CLI).
     *
     * @param args APE expects only one (1) argument: The absolute or relative path to te configuration file.
     */
    public static void main(String[] args) {
        String path;
        if (args.length == 1) {
            path = args[0];
        } else {
            path = "./ape.configuration";
        }
        if (!APEUtils.isValidReadFile(path)) {
            System.err.println("Bad path.");
            return;
        }

        APE apeFramework = null;
        try {

            // set up the APE framework
            apeFramework = new APE(path);

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            System.err.println("Error in setting up the APE framework:");
            System.err.println(e.getMessage());
            return;
        }

        SATsolutionsList solutions;
        try {

            // run the synthesis and retrieve the solutions
            solutions = apeFramework.runSynthesis(path);

        } catch (APEConfigException | JSONException | IOException e) {
            System.err.println("Error in synthesis execution:");
            System.err.println(e.getMessage());
            return;
        }

        /*
         * Writing solutions to the specified file in human readable format
         */
        if (solutions.isEmpty()) {
            System.out.println("UNSAT");
        } else {
            try {
                APE.writeSolutionToFile(solutions);
                APE.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
//				APE.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
                APE.writeExecutableWorkflows(solutions);
            } catch (IOException e) {
                System.err.println("Error in writing the solutions. to the file system.");
                e.printStackTrace();
            }
        }
    }
}
