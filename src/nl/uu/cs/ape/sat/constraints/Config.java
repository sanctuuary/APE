package nl.uu.cs.ape.sat.constraints;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class Config {

	private String ontology_path;
	private String tool_annotations_path;
	private String constraints_path;
	private String solutions_path;
	private int solution_min_length;
	private boolean pipeline;
	private int max_solutions;
	
	
	public Config() {
		
	}

	public Config(String ontology_path, String tool_annotations_path, String constraints_path, String solutions_path,
			int solution_min_length, boolean pipeline, int max_solutions) {
		super();
		this.ontology_path = ontology_path;
		this.tool_annotations_path = tool_annotations_path;
		this.constraints_path = constraints_path;
		this.solutions_path = solutions_path;
		this.solution_min_length = solution_min_length;
		this.pipeline = pipeline;
		this.max_solutions = max_solutions;
	}

	public String getOntology_path() {
		return ontology_path;
	}

	public void setOntology_path(String ontology_path) {
		this.ontology_path = ontology_path;
	}

	public String getTool_annotations_path() {
		return tool_annotations_path;
	}

	public void setTool_annotations_path(String tool_annotations_path) {
		this.tool_annotations_path = tool_annotations_path;
	}

	public String getConstraints_path() {
		return constraints_path;
	}

	public void setConstraints_path(String constraints_path) {
		this.constraints_path = constraints_path;
	}

	public String getSolutions_path() {
		return solutions_path;
	}

	public void setSolutions_path(String solutions_path) {
		this.solutions_path = solutions_path;
	}

	public int getSolution_min_length() {
		return solution_min_length;
	}

	public void setSolution_min_length(int solution_min_length) {
		this.solution_min_length = solution_min_length;
	}

	public boolean isPipeline() {
		return pipeline;
	}

	public void setPipeline(boolean pipeline) {
		this.pipeline = pipeline;
	}

	public int getMax_solutions() {
		return max_solutions;
	}

	public void setMax_solutions(int max_solutions) {
		this.max_solutions = max_solutions;
	}
	
	
}
