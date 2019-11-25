package nl.uu.cs.ape.sat.constraints;

import java.util.HashMap;
import java.util.Map;

import nl.uu.cs.ape.sat.models.ConstraintData;

/**
 * The  {@code ConstraintFactory} class represents the Factory Method Pattern for generating and mapping {@link ConstraintTemplate} classes the set of constraint formats that can be used to describe the desired synthesis output.
 * @author Vedran Kasalica
 *
 */
public class ConstraintFactory {

	private Map<String, ConstraintTemplate> constraintTamplates;
	
	public ConstraintFactory() {
		this.constraintTamplates = new HashMap<String, ConstraintTemplate>();
	}
	
	public Map<String, ConstraintTemplate> getConstraintTamplates(){
		return this.constraintTamplates;
	}
	
	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID, or {@code null} if the constraint with the given ID doesn not exist.
	 * @param constraintID - ID of the {@code ConstraintTemplate}.
	 * @return the {@code ConstraintTemplate} that corresponds to the given ID, or {@code null} if the ID is not mapped to any constraint.
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
		StringBuilder templates = new StringBuilder("{\n" + "  \"constraints\": [\n");
		for(ConstraintTemplate currConstr : constraintTamplates.values()) {
			templates = templates.append(currConstr.printConstraintCode());
		}
		templates = templates.append("    ]\n}");
		return templates.toString();
	}
	
	/**
	 * Adding each constraint format in the set of all cons. formats
	 * 
	 * @param allConsTemplates - set that represents all the cons. formats
	 * @return String description of all the formats (ID, description and number of
	 *         parameters for each).
	 */
	public boolean initializeConstraints() {

		/*
		 * ID: ite_m If we use module 'parameters[0]', then use module 'parameters[1]'
		 * subsequently.
		 */
		ConstraintTemplate currTemplate = new Constraint_if_then_module("ite_m", 2,
				"If we use module 'parameters[0]', then use 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: itn_m If we use module 'parameters[0]', then do not use module
		 * 'parameters[1]' subsequently.
		 */
		currTemplate = new Constraint_if_then_not_module("itn_m", 2,
				"If we use module 'parameters[0]', then do not use 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: depend_m If we use module 'parameters[0]', then we must have used module
		 * 'parameters[1]' prior to it.
		 */
		currTemplate = new Constraint_depend_module("depend_m", 2,
				"If we use module 'parameters[0]', then we must have used 'parameters[1]' prior to it.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: next_m If we use module 'parameters[0]', then use 'parameters[1]' as
		 * a next module in the sequence.
		 */
		currTemplate = new Constraint_next_module("next_m", 2,
				"If we use module 'parameters[0]', then use 'parameters[1]' as a next module in the sequence.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: prev_m If we use module 'parameters[0]', then we must have used 'parameters[1]' as
		 * a previous module in the sequence.
		 */
		currTemplate = new Constraint_prev_module("prev_m", 2,
				"If we use module 'parameters[0]', then we must have used 'parameters[1]' as a previous module in the sequence.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_m Use module 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_use_module("use_m", 1, "Use module 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_m Do not use module 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_not_use_module("nuse_m", 1, "Do not use module 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: last_m Use 'parameters[0]' as last module in the solution.
		 */
		currTemplate = new Constraint_last_module("last_m", 1, "Use 'parameters[0]' as last module in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_t Use type 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_use_type("use_t", 1, "Use type 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_t Generate type 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_gen_type("gen_t", 1, "Generate type 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_t Do not use type 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_not_use_type("nuse_t", 1, "Do not use type 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: ngen_t Do not generate type 'parameters[0]' in the solution.
		 */
		currTemplate = new Constraint_not_gen_type("ngen_t", 1, "Do not generate type 'parameters[0]' in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_ite_t If we have used data type 'parameters[0]', then use type 'parameters[1]'
		 * subsequently.
		 */
		currTemplate = new Constraint_if_use_then_type("use_ite_t", 2,
				"If we have used data type 'parameters[0]', then use type 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_ite_t If we have data type 'parameters[0]', then generate type 'parameters[1]'
		 * subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_type("gen_ite_t", 2,
				"If we have generated data type 'parameters[0]', then generate type 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_itn_t If we have used data type 'parameters[0]', then do not use type
		 * 'parameters[1]' subsequently.
		 */
		currTemplate = new Constraint_if_use_then_not_type("use_itn_t", 2,
				"If we have used data type 'parameters[0]', then do not use type 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_itn_t If we have generated data type 'parameters[0]', then do not generate type
		 * 'parameters[1]' subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_not_type("gen_itn_t", 2,
				"If we have generated data type 'parameters[0]', then do not generate type 'parameters[1]' subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: X Use 'parameters[0]' as N-th module in the solution (where
		 * 'parameters[0]' = N).
		 */
//		currTemplate = new Constraint_nth_module(2,
//				"Use 'parameters[0]' as 'parameters[1]'-th (N-th) module in the solution (where 'parameters[1]' = N)");
//		addConstraintTamplate(currTemplate);

		return true;

	}

	/**
	 * @param constr
	 * @return
	 */
	public String getDescription(ConstraintData constr) {
		ConstraintTemplate currTmpl = this.constraintTamplates.get(constr.getConstraintID());
		String[] params = constr.getParameters();
		String description = currTmpl.getDescription();
		for(int i = 0; i < params.length; i++) {
			description = description.replace("parameters[" + i + "]", constr.getParameters()[i]);
		}
		return description;
	}

}
