package nl.uu.cs.ape.sat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;

public class Test {

	public static void main(String[] args) throws IOException {

//		runSynthesisSetup();

//		runSynthesisTest();
		customParseJson();
//		APE apeFramework = runSynthesisSetup();
//		testConstraintTemplates(apeFramework);
	}

	/**
	 * @param apeFramework 
	 * 
	 */
	private static void testConstraintTemplates(APE apeFramework) {
		Collection<ConstraintTemplate> templates = apeFramework.getConstraintTemplates();
		for(ConstraintTemplate template : templates) {
			System.out.println("------------------------------------------------------"
								+ "\nTemplate: " + template.getDescription() + "\n");
			for(int i=0; i < template.getNoOfParameters(); i++) {
				System.out.println("Param no" + i + ": " + template.getParameter(i).toString());
			}
		}
	}

	/**
	 * 
	 */
	private static APE runSynthesisSetup() {
		String path = "/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/UseCase1/";
		String fileName = "ape.configuration";
		if (!APEUtils.isValidReadFile(path + fileName)) {
			System.err.println("Bad path.");
			return null;
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
			return null;
		} catch (IOException e) {
			System.err.println("Error in reading the configuration file.");
			return null;
		}

//		String d = apeFramework.getTypeElements("Data").toString();
//		String f = apeFramework.getTypeElements("Format").toString();
//
//		System.out.println(d);
		return apeFramework;
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

	public static void customParseJson() {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = Files.newBufferedReader(
				Paths.get("/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/SimpleDemo/tool_annotations.json"))) {

			// read line by line
			String line;
			while ((line = br.readLine()) != null) {
				
				if (line.contains("name")) {
					sb.append(line.replace("name", "label")).append("\n");
				} else if (line.contains("taxonomyTerms")) {
					sb.append(line.replace("taxonomyTerms", "taxonomyOperations")).append("\n");
				} else if (line.contains("operation")) {
					sb.append(line.replace("operation", "id")).append("\n");
				} else {
					sb.append(line).append("\n");
				}
				
				if (line.contains("operation")) {
					sb.append(line.replace("operation", "taxonomyOperations").replace(": \"", ": [\"")
							.replace("\",", "\"],")).append("\n");
				}
				
				
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		APEUtils.write2file(sb.toString(),
				new File("/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/SimpleDemo/tool_annotations1.json"), false);
	}

	public static void cnfTransformationTesting() {
		AtomMappings tmp = new AtomMappings();

//		String formula = "(t & y & z) => a | b";
//		String encoding = APEUtils.convert2CNF(formula, tmp);
//		
//		System.out.println(encoding);

	}

}
