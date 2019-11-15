package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.json.JSONException;

import nl.uu.cs.ape.sat.utils.APEUtils;

public class Main {

	public static void main(String[] args) {

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
		
		try {
			apeFramework.runSynthesis();
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return;
		}

	}
}
