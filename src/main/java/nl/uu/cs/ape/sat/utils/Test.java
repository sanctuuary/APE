package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;

public class Test {

	public static void main(String[] args) throws IOException {
		
//		runJsonConversionTest();
		
		runSynthesisTest();
		
//		AllModules allModules = new AllModules(apeFramework.getConfig());
//		AllTypes allTypes = new AllTypes(apeFramework.getConfig());
//
//		OWLReader owlReader = new OWLReader(allModules, allTypes, apeFramework.getConfig().getOntology_path());
//		Boolean ontologyRead = owlReader.readOntology();
		
		
//		APEUtils.readModuleJson(toolsPath, allModules, allTypes);
//		allModules.trimTaxonomy();
//		allTypes.trimTaxonomy();
		
//		print(allModules, allTypes);
	}

	/**
	 * 
	 */
	private static void runSynthesisTest() {
		String path = "/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/UseCase6/";
		String fileName = "ape.configuration";
		if (!APEUtils.isValidReadFile(path + fileName)) {
			System.err.println("Bad path.");
			return;
		}

		File file = null;
		try {
			file = File.createTempFile("temp", null);
			file.deleteOnExit();
			String content = APEUtils.readFile(path + fileName, Charset.defaultCharset());
			content = content.replace("./", path);
			APEUtils.write2file(content, file, false);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		APE apeFramework = null;
		try {
			apeFramework = new APE(file.getAbsolutePath());
		} catch (JSONException e) {
			System.err.println("Error in parsing the configuration file.");
			return;
		} catch (IOException e) {
			System.err.println("Error in reading the configuration file.");
			return;
		}
		
		
		String d = apeFramework.getTypeElements("Data").toString();
		String f = apeFramework.getTypeElements("Format").toString();
		
		System.out.println(d);
		
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private static void runJsonConversionTest() throws IOException {
		String text = APEUtils.readFile("/home/vedran/eclipse-workspace/Test/test.json", Charset.defaultCharset());
		JSONObject toolAnnnotations = APEUtils.convertBioTools2Ape(new JSONArray(text));
		
		System.out.println(toolAnnnotations.toString());
		
	}

	private static void print(AllModules allModules, AllTypes allTypes) {
		System.out.println("-------------------------------------------------------------");
		System.out.println("\tTool Taxonomy:");
		System.out.println("-------------------------------------------------------------");
		allModules.getRootPredicate().printTree(" ", allModules);
		System.out.println("\n-------------------------------------------------------------");
		System.out.println("\tData Taxonomy:");
		System.out.println("-------------------------------------------------------------");
		allTypes.getRootPredicate().printTree(" ", allTypes);
	}
	
}
