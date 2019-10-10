package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		
		try {
			
			String content = FileUtils.readFileToString(new File("/home/vedran/Desktop/test2"), "utf-8");
		

		// Convert JSON string to JSONObject
		JSONObject jsonObject = new JSONObject(content);
		Object tmp = jsonObject.get("input");
		System.out.println(tmp instanceof JSONArray);
		System.out.println(tmp instanceof JSONObject);
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
