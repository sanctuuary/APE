package nl.uu.cs.ape.sat.constraints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Node;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;

/**
 * The  {@code ConstraintFactory} class represents the Factory Method Pattern for generating and mapping {@link Constraint} classes the set of constraint formats that can be used to describe the desired synthesis output.
 * @author Vedran Kasalica
 *
 */
public class ConstraintFactory {

	private Map<String, Constraint> constraintTamplates;
	
	public ConstraintFactory() {
		this.constraintTamplates = new HashMap<String, Constraint>();
	}
	
	public Map<String, Constraint> getConstraintTamplates(){
		return this.constraintTamplates;
	}
	
	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID.
	 * @param constraintID - ID of the {@code ConstraintTemplate}.
	 * @return {@code ConstraintTemplate} or {@code null} if this map contains no mapping for the ID.
	 */
	public Constraint getConstraintTamplate(String constraintID) {
		return constraintTamplates.get(constraintID);
	}
	
	/**
	 * Add constraint template to the set of constraints.
	 * @param constraintTemplate - constraint template that is added to the set.
	 * @return {@code true} if the constraint template was successfully added to the set or {@code false} in case that the constraint ID already exists in the set.
	 */
	public boolean addConstraintTamplate(Constraint constraintTemplate) {
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
		for(Constraint currConstr : constraintTamplates.values()) {
			templates += currConstr.printConstraintCode();
		}
		return templates;
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
		 * ID: ite_m If we use module <b>parameters[0]</b>, then use module <b>parameters[1]</b>
		 * subsequently.
		 */
		Constraint currTemplate = new Constraint_if_then_module("ite_m", 2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: itn_m If we use module <b>parameters[0]</b>, then do not use module
		 * <b>parameters[1]</b> subsequently.
		 */
		currTemplate = new Constraint_if_then_not_module("itn_m", 2,
				"If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: depend_m If we use module <b>parameters[0]</b>, then we must have used module
		 * <b>parameters[1]</b> prior to it.
		 */
		currTemplate = new Constraint_depend_module("depend_m", 2,
				"If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: next_m If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as
		 * a next module in the sequence.
		 */
		currTemplate = new Constraint_next_module("next_m", 2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: prev_m If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> as
		 * a previous module in the sequence.
		 */
		currTemplate = new Constraint_prev_module("prev_m", 2,
				"If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> as a previous module in the sequence.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_m Use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_module("use_m", 1, "Use module <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_m Do not use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_module("nuse_m", 1, "Do not use module <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: last_m Use <b>parameters[0]</b> as last module in the solution.
		 */
		currTemplate = new Constraint_last_module("last_m", 1, "Use <b>parameters[0]</b> as last module in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_t Use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_type("use_t", 1, "Use type <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_t Generate type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_gen_type("gen_t", 1, "Generate type <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_t Do not use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_type("nuse_t", 1, "Do not use type <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: ngen_t Do not generate type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_gen_type("ngen_t", 1, "Do not generate type <b>parameters[0]</b> in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_ite_t If we have used data type <b>parameters[0]</b>, then use type <b>parameters[1]</b>
		 * subsequently.
		 */
		currTemplate = new Constraint_if_use_then_type("use_ite_t", 2,
				"If we have used data type <b>parameters[0]</b>, then use type <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_ite_t If we have data type <b>parameters[0]</b>, then generate type <b>parameters[1]</b>
		 * subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_type("gen_ite_t", 2,
				"If we have generated data type <b>parameters[0]</b>, then generate type <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_itn_t If we have used data type <b>parameters[0]</b>, then do not use type
		 * <b>parameters[1]</b> subsequently.
		 */
		currTemplate = new Constraint_if_use_then_not_type("use_itn_t", 2,
				"If we have used data type <b>parameters[0]</b>, then do not use type <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_itn_t If we have generated data type <b>parameters[0]</b>, then do not generate type
		 * <b>parameters[1]</b> subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_not_type("gen_itn_t", 2,
				"If we have generated data type <b>parameters[0]</b>, then do not generate type <b>parameters[1]</b> subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: X Use <b>parameters[0]</b> as N-th module in the solution (where
		 * <b>parameters[0]</b> = N).
		 */
//		currTemplate = new Constraint_nth_module(2,
//				"Use <b>parameters[0]</b> as <b>parameters[1]</b>-th (N-th) module in the solution (where <b>parameters[1]</b> = N)");
//		addConstraintTamplate(currTemplate);

		return true;

	}

}
