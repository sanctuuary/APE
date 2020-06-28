package nl.uu.cs.ape.functional;

import org.json.JSONObject;

public class UseCaseMutation {

    public final JSONObject config;
    public final int solution_length_start;
    public final int[] expected_no_solutions;

    public UseCaseMutation(JSONObject config, int solution_length_start, int[] expected_no_solutions){
        this.config = config;
        this.solution_length_start = solution_length_start;
        this.expected_no_solutions = expected_no_solutions;
    }
}
