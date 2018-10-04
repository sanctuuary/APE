package SAT.constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * The  {@code AllConstraintTamplates} class represents the set of constraint formats that can be used to describe the desired synthesis output.
 * @author Vedran Kasalica
 *
 */
public class AllConstraintTamplates {

	private Map<Integer, ConstraintTemplate> constraintTamplates;
	
	public AllConstraintTamplates() {
		this.constraintTamplates = new HashMap<>();
	}
	
	public Map<Integer, ConstraintTemplate> getConstraintTamplates(){
		return this.constraintTamplates;
	}
	
	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID.
	 * @param constraintID - ID of the {@code ConstraintTemplate}.
	 * @return {@code ConstraintTemplate} or {@code null} if this map contains no mapping for the ID.
	 */
	public ConstraintTemplate getConstraintTamplate(Integer constraintID) {
		return constraintTamplates.get(constraintID);
	}
	
	
	public Integer addConstraintTamplate(ConstraintTemplate constraintTemplate) {
		int currID = constraintTamplates.size() + 1;
		constraintTamplates.put(currID, constraintTemplate);
		constraintTemplate.setConstraintID(currID);
		return currID;
	}
	
	/**
	 * Print the template for encoding each constraint, containing the template ID, description and required number of parameters.
	 * @return String representing the description.
	 */
	public String printConstraintsCodes() {
		String templates = "Constraint ID;Description;No. of parameters\n";
		for(int i=1; i<=constraintTamplates.size();i++) {
			templates += constraintTamplates.get(i).printConstraintCode();
		}
		return templates;
	}
	
}
