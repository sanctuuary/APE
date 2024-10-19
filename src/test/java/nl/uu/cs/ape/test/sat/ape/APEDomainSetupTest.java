package nl.uu.cs.ape.test.sat.ape;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.domain.BioToolsAPI;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.solver.solutionStructure.cwl.ToolCWLCreator;

class APEDomainSetupTest {

    @Test
    void updateModuleFromJsonTest() throws IOException, OWLOntologyCreationException {
        List<String> toolIDs = List.of("Comet", "ape", "shic", "Jalview");
        JSONObject toolAnnotationJson = BioToolsAPI.getAndConvertToolList(toolIDs);
        for (int i = 0; i < toolIDs.size(); i++) {
            String biotoolsID = toolIDs.get(i);
            JSONObject toolJson = toolAnnotationJson.getJSONArray("functions").getJSONObject(i);


            assertFalse(toolJson.isEmpty());
            APE apeFramework = new APE(
                    "https://raw.githubusercontent.com/Workflomics/tools-and-domains/refs/heads/main/domains/bio.tools/config.json");

            Module currModule = apeFramework.getDomainSetup()
                    .updateModuleFromJson(toolJson).get();

            assertTrue(toolIDs.stream()
                    .map(String::toLowerCase)
                    .anyMatch(id -> id.equals(currModule.getPredicateLabel().toLowerCase())));

            ToolCWLCreator toolCWLCreator = new ToolCWLCreator(currModule);
            String cwlRepresentation = toolCWLCreator.generate();
            assertFalse(cwlRepresentation.isEmpty());

        }

    }
}
