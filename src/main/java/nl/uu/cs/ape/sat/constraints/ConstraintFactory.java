package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.ConstraintTemplateData;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.formulas.SLTL_formula;
import nl.uu.cs.ape.sat.models.formulas.SLTL_formula_F;
import nl.uu.cs.ape.sat.models.formulas.SLTL_formula_G;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

import java.util.*;

/**
 * The {@code ConstraintFactory} class represents the Factory Method Pattern for
 * generating and mapping {@link ConstraintTemplate} classes the set of
 * constraint formats that can be used to describe the desired synthesis output.
 *
 * @author Vedran Kasalica
 */
public class ConstraintFactory {

	private Map<String, ConstraintTemplate> constraintTemplates;

	/**
	 * Instantiates a new Constraint factory.
	 */
	public ConstraintFactory() {
		this.constraintTemplates = new HashMap<String, ConstraintTemplate>();
	}

	/**
	 * Retrieves a list of the constraint templates.
	 *
	 * @return The constraint templates.
	 */
	public Collection<ConstraintTemplate> getConstraintTemplates() {
		return this.constraintTemplates.values();
	}

	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID, or
	 * null if the constraint with the given ID does not exist.
	 *
	 * @param constraintID ID of the {@code ConstraintTemplate}.
	 * @return The {@link ConstraintTemplate} that corresponds to the given ID, or
	 *         null if the ID is not mapped to any constraint.
	 */
	public ConstraintTemplate getConstraintTemplate(String constraintID) {
		return constraintTemplates.get(constraintID);
	}

	/**
	 * Add constraint template to the set of constraints.
	 *
	 * @param constraintTemplate Constraint template that is added to the set.
	 * @return true if the constraint template was successfully added to the set or
	 *         false in case that the constraint ID already exists in the set.
	 */
	public boolean addConstraintTemplate(ConstraintTemplate constraintTemplate) {
		if (constraintTemplates.put(constraintTemplate.getConstraintID(), constraintTemplate) != null) {
			System.err.println("Duplicate constraint ID: " + constraintTemplate.getConstraintID()
					+ ". Please change the ID in order to be able to use the constraint template.");
			return false;
		}
		return true;
	}

	/**
	 * Print the template for encoding each constraint, containing the template ID,
	 * description and required number of parameters.
	 *
	 * @return String representing the description.
	 */
	public String printConstraintsCodes() {
		StringBuilder templates = new StringBuilder("{\n" + "  \"constraints\": [\n");
		for (ConstraintTemplate currConstr : constraintTemplates.values()) {
			templates.append(currConstr.printConstraintCode());
		}
		templates.append("    ]\n}");
		return templates.toString();
	}

	/**
	 * Adding each constraint format in the set of all cons. formats.
	 *
	 * @param allModules All modules in the domain
	 * @param allTypes   All types in the domain
	 * @return String description of all the formats (ID, description and number of
	 *         parameters for each).
	 */
	public boolean initializeConstraints(AllModules allModules, AllTypes allTypes) {

		TaxonomyPredicate rootModule = allModules.getRootModule();
		List<TaxonomyPredicate> rootTypes = allTypes.getDataTaxonomyDimensions();
		ConstraintTemplateParameter moduleParameter = new ConstraintTemplateParameter(Arrays.asList(rootModule));
		ConstraintTemplateParameter typeParameter = new ConstraintTemplateParameter(rootTypes);

		List<ConstraintTemplateParameter> moduleParam1 = Arrays.asList(moduleParameter);
		List<ConstraintTemplateParameter> moduleParam2 = Arrays.asList(moduleParameter, moduleParameter);

		List<ConstraintTemplateParameter> typeParam1 = Arrays.asList(typeParameter);
		List<ConstraintTemplateParameter> typeParam2 = Arrays.asList(typeParameter, typeParameter);

		List<ConstraintTemplateParameter> moduleNlabel = Arrays.asList(moduleParameter, typeParameter);
		
		/*
		 * ID: ite_m 
		 */
		ConstraintTemplate currTemplate = new Constraint_if_then_module("ite_m", moduleParam2,
				"If 1st operation is used, then 2nd operation must be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: itn_m 
		 */
		currTemplate = new Constraint_if_then_not_module("itn_m", moduleParam2,
				"If 1st operation is used, then 2nd operation cannot be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: depend_m 
		 */
		currTemplate = new Constraint_depend_module("depend_m", moduleParam2,
				"If 1st operation is used, then we must have used 2nd operation prior to it.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: next_m 
		 */
		currTemplate = new Constraint_next_module("next_m", moduleParam2,
				"If 1st operation is used, then 2nd operation must be used as the next operation in the sequence.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: prev_m
		 */
		currTemplate = new Constraint_prev_module("prev_m", moduleParam2,
				"If 1st operation is used, then we must have used 2nd operation as a previous operation in the sequence.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_m 
		 */
		currTemplate = new Constraint_use_module("use_m", moduleParam1, "Use operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: nuse_m
		 */
		currTemplate = new Constraint_not_use_module("nuse_m", moduleParam1,
				"Do not use operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: last_m
		 */
		currTemplate = new Constraint_last_module("last_m", moduleParam1,
				"Use operation as the last operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_t 
		 */
		currTemplate = new Constraint_use_type("use_t", typeParam1, "Use type in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: nuse_t
		 */
		currTemplate = new Constraint_not_use_type("nuse_t", typeParam1,
				"Do not use type in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_ite_t
		 */
		currTemplate = new Constraint_if_use_then_type("use_ite_t", typeParam2,
				"If 1st data is used, then 2nd data must be used subsequently.");
		addConstraintTemplate(currTemplate);
		
		/*
		 * ID: use_itn_t
		 */
		currTemplate = new Constraint_if_use_then_not_type("use_itn_t", typeParam2,
				"If 1st data is used, then 2nd data cannot be used subsequently.");
		addConstraintTemplate(currTemplate);
		
		
		/*
		 * ID: m_in_label
		 */
		currTemplate = new Constraint_use_m_in_label("m_in_label", moduleNlabel,
				"Use operation ${parameter_1} with data labeled ${parameter_2} as one of the inputs.");
		addConstraintTemplate(currTemplate);
		
		/*
		 * ID: m_in1_depen
		 */
		currTemplate = new Constraint_use_m_with_dependence("m_in1_depen", moduleParam1, 1,
				"Use operation ${parameter_1} with data that depends on the first input.");
		addConstraintTemplate(currTemplate);
		
		/*
		 * ID: m_in2_depen
		 */
		currTemplate = new Constraint_use_m_with_dependence("m_in2_depen", moduleParam1, 2,
				"Use operation ${parameter_1} with data that depends on the second input.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: gen_t 
		 
		currTemplate = new Constraint_gen_type("gen_t", typeParam1, "Generate type ${parameter_1} in the solution.");
		addConstraintTemplate(currTemplate);
		*/
		
		/*
		 * ID: ngen_t Do not generate type ${parameter_1} in the solution.
		
		currTemplate = new Constraint_not_gen_type("ngen_t", typeParam1,
				"Do not generate type ${parameter_1} in the solution.");
		addConstraintTemplate(currTemplate);
	 	 */
		
		/*
		 * ID: gen_ite_t If we have data type ${parameter_1}, then generate type
		 * ${parameter_2} subsequently.
		
		currTemplate = new Constraint_if_gen_then_type("gen_ite_t", typeParam2,
				"If we have generated data type ${parameter_1}, then generate type ${parameter_2} subsequently.");
		addConstraintTemplate(currTemplate);
		 */
		
		
		/*
		 * ID: gen_itn_t If we have generated data type ${parameter_1}, then do not
		 * generate type ${parameter_2} subsequently.
		currTemplate = new Constraint_if_gen_then_not_type("gen_itn_t", typeParam2,
				"If we have generated data type ${parameter_1}, then do not generate type ${parameter_2} subsequently.");
		addConstraintTemplate(currTemplate);
		*/
		
		return true;

	}

	/**
	 * Constructs the description of a {@link ConstraintTemplateData}.
	 *
	 * @param constr The {@link ConstraintTemplateData}.
	 * @return The description of a {@link ConstraintTemplateData}.
	 */
	public String getDescription(ConstraintTemplateData constr) {
		ConstraintTemplate currTmpl = this.constraintTemplates.get(constr.getConstraintID());
		List<TaxonomyPredicate> params = constr.getParameters();
		String description = currTmpl.getDescription();
		for (int i = 0; i < params.size(); i++) {
			description = description.replace("${parameter_" + (i + 1) + "}",
					"'" + params.get(i).getPredicateLabel() + "'");
		}
		return description;
	}

	/**
	 * Constructs {@link ConstraintTemplateData} if the taxonomy predicates are
	 * formatted correctly.
	 *
	 * @param constraintID ID of the constraint.
	 * @param parameters   {@link List} of {@link TaxonomyPredicate}.
	 * @return The {@link ConstraintTemplateData} if the format is correct, {null}
	 *         otherwise.
	 */
	public ConstraintTemplateData generateConstraintTemplateData(String constraintID,
			List<TaxonomyPredicate> parameters) {
		if (isGoodConstraintFormat(constraintID, parameters)) {
			return new ConstraintTemplateData(constraintID, parameters);
		}
		return null;
	}

	/**
	 * TODO: Needs to be implemented. Input should be compared with the constraint
	 * templates, wrt the types of parameters and dimensions of each.
	 *
	 * @param constraintID ID of the constraint.
	 * @param parameters   {@link List} of {@link TaxonomyPredicate}.
	 * @return {@code true} if the format is correct, {@code false} otherwise.
	 */
	public boolean isGoodConstraintFormat(String constraintID, List<TaxonomyPredicate> parameters) {
		return true;
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then we must have used
	 * <b>parameters[1]</b> prior to it using the function {@link #getConstraint}.
	 */
	public class Constraint_depend_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint depend module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected Constraint_depend_module(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.depend_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Generate type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_gen_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint gen type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_gen_type(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_F formula = new SLTL_formula_F(parameters.get(0));
			return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), WorkflowElement.MEMORY_TYPE,
					mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have generated data module <b>parameters[0]</b>, then do not generate
	 * type <b>parameters[1]</b> subsequently using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_if_gen_then_not_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if gen then not type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_if_gen_then_not_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.itn_type(parameters.get(0), parameters.get(1), WorkflowElement.MEMORY_TYPE,
					moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have generated data module <b>parameters[0]</b>, then generate
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_if_gen_then_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if gen then type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_if_gen_then_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.ite_type(parameters.get(0), parameters.get(1), WorkflowElement.MEMORY_TYPE,
					moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_if_then_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected Constraint_if_then_module(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.ite_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b>
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_if_then_not_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then not module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_if_then_not_module(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.itn_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have used data module <b>parameters[0]</b>, then do not use type
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_if_use_then_not_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if use then not type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_if_use_then_not_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.itn_type(parameters.get(0), parameters.get(1), WorkflowElement.USED_TYPE,
					moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have used data module <b>parameters[0]</b>, then use
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_if_use_then_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if use then type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_if_use_then_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.ite_type(parameters.get(0), parameters.get(1), WorkflowElement.USED_TYPE,
					moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use <b>parameters[0]</b> as last module in the solution. using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_last_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint last module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_last_module(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.useAsLastModule(parameters.get(0), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a
	 * next module in the sequence using the function {@link #getConstraint}.
	 */
	public class Constraint_next_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint next module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_next_module(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.next_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not generate type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_not_gen_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not gen type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_not_gen_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_G formula = new SLTL_formula_G(false, parameters.get(0));
			return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), WorkflowElement.MEMORY_TYPE,
					mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not use module <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_not_use_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not use module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_not_use_module(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_G formula = new SLTL_formula_G(false, parameters.get(0));
			return formula.getCNF(moduleAutomaton, null, WorkflowElement.MODULE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not use type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_not_use_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_not_use_type(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_G formula = new SLTL_formula_G(false, parameters.get(0));
			return formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), WorkflowElement.USED_TYPE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then we must have used
	 * <b>parameters[1]</b> as a previous module in the sequence. using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_prev_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint prev module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_prev_module(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTL_formula.prev_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use module <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_use_module extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_use_module(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_F formula = new SLTL_formula_F(parameters.get(0));
			return formula.getCNF(moduleAutomaton, null, WorkflowElement.MODULE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_use_type extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_use_type(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTL_formula_F formula = new SLTL_formula_F(parameters.get(0));
			return formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), WorkflowElement.USED_TYPE, mappings);
		}
	}
	
	
	/**
	 * Implements constraints of the form:<br>
	 * Use operation ${parameter_1} with data labeled ${parameter_2} as one of the inputs.
	 * {@link #getConstraint}.
	 */
	public class Constraint_use_m_in_label extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_use_m_in_label(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			return SLTL_formula.use_m_in_label(parameters.get(0), parameters.get(1), moduleAutomaton, typeAutomaton, mappings);
		}
	}
	
	/**
	 * Implements constraints of the form:<br>
	 *Use operation ${parameter_1} with data that depends on the n-th input.
	 * {@link #getConstraint}.
	 */
	public class Constraint_use_m_with_dependence extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		private int inputNo;
		
		protected Constraint_use_m_with_dependence(String id, List<ConstraintTemplateParameter> parametersNo, int i, String description) {
			super(id, parametersNo, description);
			inputNo = i;
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			return SLTL_formula.use_m_with_dependence(parameters.get(0), inputNo, moduleAutomaton, typeAutomaton, mappings);
		}
	}
}
