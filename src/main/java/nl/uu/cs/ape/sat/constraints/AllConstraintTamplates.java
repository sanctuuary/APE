package nl.uu.cs.ape.sat.constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * The  {@code AllConstraintTamplates} class represents the set of constraint formats that can be used to describe the desired synthesis output.
 * @author Vedran Kasalica
 *
 */
public class AllConstraintTamplates {

	private Map<String, ConstraintTemplate> constraintTamplates;
	
	public AllConstraintTamplates() {
		this.constraintTamplates = new HashMap<String, ConstraintTemplate>();
	}
	
	public Map<String, ConstraintTemplate> getConstraintTamplates(){
		return this.constraintTamplates;
	}
	
	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID.
	 * @param constraintID - ID of the {@code ConstraintTemplate}.
	 * @return {@code ConstraintTemplate} or {@code null} if this map contains no mapping for the ID.
	 */
	public ConstraintTemplate getConstraintTamplate(String constraintID) {
		return constraintTamplates.get(constraintID);
	}
	
	/**
	 * Add constraint template to the set of constraints.
	 * @param constraintTemplate - constraint template that is added to the set.
	 * @return {@code true} if the constraint template was successfully added to the set or {@code false} in case that the constraint ID already exists in the set.
	 */
	public boolean addConstraintTamplate(ConstraintTemplate constraintTemplate) {
		if(constraintTamplates.put(constraintTemplate.getConstraintID(), constraintTemplate) != null) {
			System.err.println("Duplicate constraint ID: " + constraintTemplate.getConstraintID() + ". Please change the ID in order to be able to use the constraint template.");
			return false;
		}
		return true;
	}
	
	/**
	 * Print the template for encoding each constraint, containing the template ID, description and required number of parameters.
	 * @return String representing the description.
	 */
	public String printConstraintsCodes() {
		String templates = "Constraint ID;\tNo. of parameters;\tDescription\n";
		for(ConstraintTemplate currConstr : constraintTamplates.values()) {
			templates += currConstr.printConstraintCode();
		}
		return templates;
	}
	
}
