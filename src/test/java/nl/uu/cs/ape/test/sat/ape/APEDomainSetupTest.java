package nl.uu.cs.ape.test.sat.ape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        List<String> toolIDs = List.of("comet");
        JSONObject toolAnnotationJson = BioToolsAPI.getAndConvertToolList(toolIDs);
        JSONObject comet = toolAnnotationJson.getJSONArray("functions").getJSONObject(0);
        APE apeFramework = new APE(
                "https://raw.githubusercontent.com/Workflomics/tools-and-domains/refs/heads/main/domains/proteomics/config.json");


        Module cometModule = apeFramework.getDomainSetup()
                .updateModuleFromJson(comet).get();

        assertEquals("Comet", cometModule.getPredicateLabel());

        ToolCWLCreator toolCWLCreator = new ToolCWLCreator(cometModule);
        String cwlRepresentation = toolCWLCreator.generate();
        assertFalse(cwlRepresentation.isEmpty());


    }
}
