package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import nl.uu.cs.ape.sat.utils.APEUtils;

public class Main {


	public static void main(String[] args) throws IOException {
		
		String path = "/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/UseCase5/";
		String fileName = "ape.configuration";
		if(!APEUtils.isValidReadFile(path + fileName)) {
			System.err.println("Bad path.");
			return;
		}
		
		 File file = File.createTempFile("temp", null);
	     file.deleteOnExit();
	     
	     String content = APEUtils.readFile(path+fileName, Charset.defaultCharset());
	     content = content.replace("./", path);
	     APEUtils.write2file(content, file, false);
	     
	     APE apeFramework = new APE(file.getAbsolutePath());
	     apeFramework.runSynthesis();
	}
}
