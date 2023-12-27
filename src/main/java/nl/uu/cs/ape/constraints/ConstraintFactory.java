package nl.uu.cs.ape.constraints;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.domain.Domain;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.templateFormulas.SLTLxTemplateFormula;
import nl.uu.cs.ape.models.templateFormulas.SLTLxTemplateFinally;
import nl.uu.cs.ape.models.templateFormulas.SLTLxTemplateGlobally;

/**
 * The {@code ConstraintFactory} class represents the Factory Method Pattern for
 * generating and mapping {@link ConstraintTemplate} classes the set of
 * constraint formats that can be used to describe the desired synthesis output.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class ConstraintFactory {

	private Map<String, ConstraintTemplate> constraintTemplates;

	/**
	 * Instantiates a new Constraint factory.
	 */
	public ConstraintFactory() {
		this.constraintTemplates = new HashMap<>();
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
			log.warn("Duplicate constraint ID: " + constraintTemplate.getConstraintID()
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
		ConstraintTemplate currTemplate = new ConstraintIfThenModule("ite_m", moduleParam2,
				"If 1st operation is used, then 2nd operation must be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: itn_m
		 */
		currTemplate = new ConstraintIfThenNotModule("itn_m", moduleParam2,
				"If 1st operation is used, then 2nd operation cannot be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: depend_m
		 */
		currTemplate = new ConstraintDependModule("depend_m", moduleParam2,
				"If 1st operation is used, then we must have used 2nd operation prior to it.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: next_m
		 */
		currTemplate = new Constraint_nextModule("next_m", moduleParam2,
				"If 1st operation is used, then 2nd operation must be used as the next operation in the sequence.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: prev_m
		 */
		currTemplate = new Constraint_prevModule("prev_m", moduleParam2,
				"If 1st operation is used, then we must have used 2nd operation as a previous operation in the sequence.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_m
		 */
		currTemplate = new ConstraintUseModule("use_m", moduleParam1, "Use operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: nuse_m
		 */
		currTemplate = new ConstraintNotUseModule("nuse_m", moduleParam1,
				"Do not use operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: last_m
		 */
		currTemplate = new Constraint_lastModule("last_m", moduleParam1,
				"Use operation as the last operation in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_t
		 */
		currTemplate = new ConstraintUseType("use_t", typeParam1, "Use type in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: nuse_t
		 */
		currTemplate = new ConstraintNotUseType("nuse_t", typeParam1,
				"Do not use type in the solution.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_ite_t
		 */
		currTemplate = new ConstraintIfUseThenType("use_ite_t", typeParam2,
				"If 1st data is used, then 2nd data must be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: use_itn_t
		 */
		currTemplate = new ConstraintIfUseThenNotType("use_itn_t", typeParam2,
				"If 1st data is used, then 2nd data cannot be used subsequently.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: operationInput
		 */
		currTemplate = new ConstraintUseModuleWithInput("operationInput", moduleNlabel,
				"Use the operation with an input of the given type.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: operationOutput
		 */
		currTemplate = new ConstraintUseModuleWithOutput("operationOutput", moduleNlabel,
				"Use the operation to generate an output of the given type.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: connected_op
		 */
		currTemplate = new Constraint_connectedModules("connected_op", moduleParam2,
				"1st operation should generate an output used by the 2nd operation.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: not_connected_op
		 */
		currTemplate = new ConstraintNot_connectedModules("not_connected_op", moduleParam2,
				"1st operation should never generate an output used by the 2nd operation.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: not_repeat_op
		 */
		currTemplate = new ConstraintNot_repeatModules("not_repeat_op", moduleParam1,
				"No operation that belongs to the subtree should be repeated over.");
		addConstraintTemplate(currTemplate);

		/*
		 * ID: gen_t
		 * 
		 * currTemplate = new ConstraintGenType("gen_t", typeParam1,
		 * "Generate type ${parameter_1} in the solution.");
		 * addConstraintTemplate(currTemplate);
		 */

		/*
		 * ID: ngen_t Do not generate type ${parameter_1} in the solution.
		 * 
		 * currTemplate = new ConstraintNotGenType("ngen_t", typeParam1,
		 * "Do not generate type ${parameter_1} in the solution.");
		 * addConstraintTemplate(currTemplate);
		 */

		/*
		 * ID: gen_ite_t If we have data type ${parameter_1}, then generate type
		 * ${parameter_2} subsequently.
		 * 
		 * currTemplate = new ConstraintIf_genThenType("gen_ite_t", typeParam2,
		 * "If we have generated data type ${parameter_1}, then generate type ${parameter_2} subsequently."
		 * );
		 * addConstraintTemplate(currTemplate);
		 */

		/*
		 * ID: gen_itn_t If we have generated data type ${parameter_1}, then do not
		 * generate type ${parameter_2} subsequently.
		 * currTemplate = new ConstraintIf_genThenNotType("gen_itn_t", typeParam2,
		 * "If we have generated data type ${parameter_1}, then do not generate type ${parameter_2} subsequently."
		 * );
		 * addConstraintTemplate(currTemplate);
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
	public class ConstraintDependModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint depend module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected ConstraintDependModule(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.dependModule(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Generate type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintGenType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint gen type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintGenType(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateFinally formula = new SLTLxTemplateFinally(parameters.get(0));
			return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), AtomType.MEMORY_TYPE,
					mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have generated data module <b>parameters[0]</b>, then do not generate
	 * type <b>parameters[1]</b> subsequently using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintIf_genThenNotType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if gen then not type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintIf_genThenNotType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.itnType(parameters.get(0), parameters.get(1), AtomType.MEMORY_TYPE,
					typeAutomaton.getMemoryTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have generated data module <b>parameters[0]</b>, then generate
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintIf_genThenType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if gen then type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintIf_genThenType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.iteType(parameters.get(0), parameters.get(1), AtomType.MEMORY_TYPE,
					typeAutomaton.getMemoryTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintIfThenModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected ConstraintIfThenModule(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.iteModule(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b>
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintIfThenNotModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then not module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintIfThenNotModule(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.itnModule(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have used data module <b>parameters[0]</b>, then do not use type
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintIfUseThenNotType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if use then not type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintIfUseThenNotType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.itnType(parameters.get(0), parameters.get(1), AtomType.USED_TYPE,
					typeAutomaton.getUsedTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we have used data module <b>parameters[0]</b>, then use
	 * <b>parameters[1]</b> subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintIfUseThenType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if use then type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintIfUseThenType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.iteType(parameters.get(0), parameters.get(1), AtomType.USED_TYPE,
					typeAutomaton.getUsedTypesBlocks(), mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use <b>parameters[0]</b> as last module in the solution. using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_lastModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint last module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_lastModule(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.useAsLastModule(parameters.get(0), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a
	 * next module in the sequence using the function {@link #getConstraint}.
	 */
	public class Constraint_nextModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint next module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_nextModule(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.nextModule(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not generate type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintNotGenType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not gen type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintNotGenType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateGlobally formula = new SLTLxTemplateGlobally(false, parameters.get(0));
			return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), AtomType.MEMORY_TYPE,
					mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not use module <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintNotUseModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not use module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintNotUseModule(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateGlobally formula = new SLTLxTemplateGlobally(false, parameters.get(0));
			return formula.getCNF(moduleAutomaton, null, AtomType.MODULE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Do not use type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintNotUseType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint not use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintNotUseType(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateGlobally formula = new SLTLxTemplateGlobally(false, parameters.get(0));
			return formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), AtomType.USED_TYPE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * If we use module <b>parameters[0]</b>, then we must have used
	 * <b>parameters[1]</b> as a previous module in the sequence. using the function
	 * {@link #getConstraint}.
	 */
	public class Constraint_prevModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint prev module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected Constraint_prevModule(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.prevModule(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use module <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintUseModule extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintUseModule(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateFinally formula = new SLTLxTemplateFinally(parameters.get(0));
			return formula.getCNF(moduleAutomaton, null, AtomType.MODULE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use type <b>parameters[0]</b> in the solution using the function
	 * {@link #getConstraint}.
	 */
	public class ConstraintUseType extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintUseType(String id, List<ConstraintTemplateParameter> parametersNo, String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			SLTLxTemplateFinally formula = new SLTLxTemplateFinally(parameters.get(0));
			return formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), AtomType.USED_TYPE, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use operation ${parameter_1} with input labeled ${parameter_2}.
	 * {@link #getConstraint}.
	 */
	public class ConstraintUseModuleWithInput extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintUseModuleWithInput(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			return SLTLxTemplateFormula.useModuleInput(parameters.get(0), parameters.get(1), moduleAutomaton,
					typeAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * Use operation ${parameter_1} to generate output labeled ${parameter_2}
	 * {@link #getConstraint}.
	 */
	public class ConstraintUseModuleWithOutput extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint use type.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintUseModuleWithOutput(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}

			return SLTLxTemplateFormula.useModuleOutput(parameters.get(0), parameters.get(1), moduleAutomaton,
					typeAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * 1st operation should generate an output used by the 2nd operation
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class Constraint_connectedModules extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected Constraint_connectedModules(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.connectedModules(parameters.get(0), parameters.get(1),
					moduleAutomaton, typeAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * 1st operation should not generate an output used by the 2nd operation
	 * subsequently using the function {@link #getConstraint}.
	 */
	public class ConstraintNot_connectedModules extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint if then module.
		 *
		 * @param id             the id
		 * @param parameterTypes the parameter types
		 * @param description    the description
		 */
		protected ConstraintNot_connectedModules(String id, List<ConstraintTemplateParameter> parameterTypes,
				String description) {
			super(id, parameterTypes, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.notConnectedModules(parameters.get(0), parameters.get(1),
					moduleAutomaton, typeAutomaton, mappings);
		}
	}

	/**
	 * Implements constraints of the form:<br>
	 * No operation that belongs to the subtree should be repeated over.
	 * {@link #getConstraint}.
	 */
	public class ConstraintNot_repeatModules extends ConstraintTemplate {
		/**
		 * Instantiates a new Constraint last module.
		 *
		 * @param id           the id
		 * @param parametersNo the parameters no
		 * @param description  the description
		 */
		protected ConstraintNot_repeatModules(String id, List<ConstraintTemplateParameter> parametersNo,
				String description) {
			super(id, parametersNo, description);
		}

		@Override
		public String getConstraint(List<TaxonomyPredicate> parameters, Domain domainSetup,
				ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
			if (parameters.size() != this.getNoOfParameters()) {
				super.throwParametersError(parameters.size());
				return null;
			}
			return SLTLxTemplateFormula.notRepeatModules(parameters.get(0), domainSetup, moduleAutomaton, typeAutomaton,
					mappings);
		}
	}
}
