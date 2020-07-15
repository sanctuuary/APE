package nl.uu.cs.ape.sat;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.utils.APEConfigException;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class LocalRun {

    public static void main(String[] args) {

        String path = "/home/vedran/git/APE_UseCases/";
        String subPath = "GeoGMT/E0/";
        String fileName = "ape.configuration";
        if (!APEUtils.isValidReadFile(path + subPath + fileName)) {
            System.err.println("Bad path.");
            return;
        }

        File file = null;
        try {
            file = File.createTempFile("temp", null);
            file.deleteOnExit();
            String content = APEUtils.readFile(path + subPath + fileName, Charset.defaultCharset());
            content = content.replace("./", path);
            APEUtils.write2file(content, file, false);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        APE apeFramework = null;
        try {

            // set up the APE framework
            apeFramework = new APE(file.getAbsolutePath());

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            System.err.println("Error in setting up the APE framework:");
            System.err.println(e.getMessage());
            return;
		}

        SATsolutionsList solutions;
        try {

            // run the synthesis and retrieve the solutions
            solutions = apeFramework.runSynthesis(file.getAbsolutePath());

        } catch (APEConfigException | JSONException | IOException e) {
            System.err.println("Error in synthesis execution:");
            System.err.println(e.getMessage());
            return;
        }

        /*
         * Writing solutions to the specified file in human readable format
         */
        if (solutions != null && solutions.isEmpty()) {
            System.out.println("UNSAT");
        } else {
            try {
                assert solutions != null;
                APE.writeSolutionToFile(solutions);
                APE.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
//				APE.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
                APE.writeExecutableWorkflows(solutions);

//				CWLCreator cwl = new CWLCreator(solutions.get(0), apeFramework.getConfig());
//				APEUtils.write2file(cwl.getCWL(), new File(path + subPath + "tmp"), false);
            } catch (IOException e) {
                System.err.println("Error in writing the solutions. to the file system.");
                e.printStackTrace();
            }

        }

    }
}
