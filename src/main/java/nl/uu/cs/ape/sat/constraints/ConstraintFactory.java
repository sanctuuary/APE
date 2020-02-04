package nl.uu.cs.ape.sat.constraints;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.ConstraintTemplateData;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

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
	
	public Collection<ConstraintTemplate> getConstraintTamplates(){
		return this.constraintTamplates.values();
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
	 * @return String description of all the formats (ID, description and number of
	 *         parameters for each).
	 */
	public boolean initializeConstraints(AllModules allModules, AllTypes allTypes) {

		/*
		 * ID: ite_m If we use module ${parameter_1}, then use module ${parameter_2}
		 * subsequently.
		 */
		
		TaxonomyPredicate rootModule = allModules.getRootPredicate();
		List<TaxonomyPredicate> rootTypes = allTypes.getDataTaxonomyDimensions();
		ConstraintParameter moduleParameter = new ConstraintParameter(Arrays.asList(rootModule));
		ConstraintParameter typeParameter = new ConstraintParameter(rootTypes);
		
		List<ConstraintParameter> moduleParam1 = Arrays.asList(moduleParameter);
		List<ConstraintParameter> moduleParam2 = Arrays.asList(moduleParameter, moduleParameter);
		
		List<ConstraintParameter> typeParam1 = Arrays.asList(typeParameter);
		List<ConstraintParameter> typeParam2 = Arrays.asList(typeParameter, typeParameter);
		
		ConstraintTemplate currTemplate = new Constraint_if_then_module("ite_m", moduleParam2,
				"If we use module ${parameter_1}, then use ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: itn_m If we use module ${parameter_1}, then do not use module
		 * ${parameter_2} subsequently.
		 */
		currTemplate = new Constraint_if_then_not_module("itn_m", moduleParam2,
				"If we use module ${parameter_1}, then do not use ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: depend_m If we use module ${parameter_1}, then we must have used module
		 * ${parameter_2} prior to it.
		 */
		currTemplate = new Constraint_depend_module("depend_m", moduleParam2,
				"If we use module ${parameter_1}, then we must have used ${parameter_2} prior to it.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: next_m If we use module ${parameter_1}, then use ${parameter_2} as
		 * a next module in the sequence.
		 */
		currTemplate = new Constraint_next_module("next_m", moduleParam2,
				"If we use module ${parameter_1}, then use ${parameter_2} as a next module in the sequence.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: prev_m If we use module ${parameter_1}, then we must have used ${parameter_2} as
		 * a previous module in the sequence.
		 */
		currTemplate = new Constraint_prev_module("prev_m", moduleParam2,
				"If we use module ${parameter_1}, then we must have used ${parameter_2} as a previous module in the sequence.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_m Use module ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_use_module("use_m", moduleParam1, "Use module ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_m Do not use module ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_not_use_module("nuse_m", moduleParam1, "Do not use module ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: last_m Use ${parameter_1} as last module in the solution.
		 */
		currTemplate = new Constraint_last_module("last_m", moduleParam1, "Use ${parameter_1} as last module in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_t Use type ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_use_type("use_t", typeParam1, "Use type ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_t Generate type ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_gen_type("gen_t", typeParam1, "Generate type ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: nuse_t Do not use type ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_not_use_type("nuse_t", typeParam1, "Do not use type ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: ngen_t Do not generate type ${parameter_1} in the solution.
		 */
		currTemplate = new Constraint_not_gen_type("ngen_t", typeParam1, "Do not generate type ${parameter_1} in the solution.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: use_ite_t If we have used data type ${parameter_1}, then use type ${parameter_2}
		 * subsequently.
		 */
		currTemplate = new Constraint_if_use_then_type("use_ite_t", typeParam2,
				"If we have used data type ${parameter_1}, then use type ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_ite_t If we have data type ${parameter_1}, then generate type ${parameter_2}
		 * subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_type("gen_ite_t", typeParam2,
				"If we have generated data type ${parameter_1}, then generate type ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: use_itn_t If we have used data type ${parameter_1}, then do not use type
		 * ${parameter_2} subsequently.
		 */
		currTemplate = new Constraint_if_use_then_not_type("use_itn_t", typeParam2,
				"If we have used data type ${parameter_1}, then do not use type ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);
		
		/*
		 * ID: gen_itn_t If we have generated data type ${parameter_1}, then do not generate type
		 * ${parameter_2} subsequently.
		 */
		currTemplate = new Constraint_if_gen_then_not_type("gen_itn_t", typeParam2,
				"If we have generated data type ${parameter_1}, then do not generate type ${parameter_2} subsequently.");
		addConstraintTamplate(currTemplate);

		/*
		 * ID: X Use ${parameter_1} as N-th module in the solution (where
		 * ${parameter_1} = N).
		 */
//		currTemplate = new Constraint_nth_module(2,
//				"Use ${parameter_1} as ${parameter_2}-th (N-th) module in the solution (where ${parameter_2} = N)");
//		addConstraintTamplate(currTemplate);

		return true;

	}

	/**
	 * @param constr
	 * @return
	 */
	public String getDescription(ConstraintTemplateData constr) {
		ConstraintTemplate currTmpl = this.constraintTamplates.get(constr.getConstraintID());
		List<ConstraintParameter> params = constr.getParameters();
		String description = currTmpl.getDescription();
		for(int i = 0; i < params.size(); i++) {
			description = description.replace("${parameter_" + i + "}", params.get(i).toString());
		}
		return description;
	}

}
