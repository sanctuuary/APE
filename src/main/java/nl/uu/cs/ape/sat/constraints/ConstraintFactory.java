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
 * The  {@code ConstraintFactory} class represents the Factory Method Pattern for generating and mapping {@link ConstraintTemplate} classes the set of constraint formats that can be used to describe the desired synthesis output.
 *
 * @author Vedran Kasalica
 */
public class ConstraintFactory {

    private Map<String, ConstraintTemplate> constraintTemplates;

    public ConstraintFactory() {
        this.constraintTemplates = new HashMap<String, ConstraintTemplate>();
    }

    public Collection<ConstraintTemplate> getConstraintTemplates() {
        return this.constraintTemplates.values();
    }

    /**
     * Return the {@code ConstraintTemplate} that corresponds to the given ID, or {@code null} if the constraint with the given ID does not exist.
     *
     * @param constraintID ID of the {@code ConstraintTemplate}.
     * @return The {@link ConstraintTemplate} that corresponds to the given ID, or {@code null} if the ID is not mapped to any constraint.
     */
    public ConstraintTemplate getConstraintTemplate(String constraintID) {
        return constraintTemplates.get(constraintID);
    }

    /**
     * Add constraint template to the set of constraints.
     *
     * @param constraintTemplate Constraint template that is added to the set.
     * @return {@code true} if the constraint template was successfully added to the set or {@code false} in case that the constraint ID already exists in the set.
     */
    public boolean addConstraintTemplate(ConstraintTemplate constraintTemplate) {
        if (constraintTemplates.put(constraintTemplate.getConstraintID(), constraintTemplate) != null) {
            System.err.println("Duplicate constraint ID: " + constraintTemplate.getConstraintID() + ". Please change the ID in order to be able to use the constraint template.");
            return false;
        }
        return true;
    }

    /**
     * Print the template for encoding each constraint, containing the template ID, description and required number of parameters.
     *
     * @return {@link String} representing the description.
     */
    public String printConstraintsCodes() {
        StringBuilder templates = new StringBuilder("{\n" + "  \"constraints\": [\n");
        for (ConstraintTemplate currConstr : constraintTemplates.values()) {
            templates = templates.append(currConstr.printConstraintCode());
        }
        templates = templates.append("    ]\n}");
        return templates.toString();
    }

    /**
     * Adding each constraint format in the set of all cons. formats.
     *
     * @return {@link String} description of all the formats (ID, description and number of parameters for each).
     */
    public boolean initializeConstraints(AllModules allModules, AllTypes allTypes) {

        /*
         * ID: ite_m If we use module ${parameter_1}, then use module ${parameter_2} subsequently.
         */

        TaxonomyPredicate rootModule = allModules.getRootPredicates().get(0);
        List<TaxonomyPredicate> rootTypes = allTypes.getDataTaxonomyDimensions();
        ConstraintParameter moduleParameter = new ConstraintParameter(Arrays.asList(rootModule));
        ConstraintParameter typeParameter = new ConstraintParameter(rootTypes);

        List<ConstraintParameter> moduleParam1 = Arrays.asList(moduleParameter);
        List<ConstraintParameter> moduleParam2 = Arrays.asList(moduleParameter, moduleParameter);

        List<ConstraintParameter> typeParam1 = Arrays.asList(typeParameter);
        List<ConstraintParameter> typeParam2 = Arrays.asList(typeParameter, typeParameter);

        ConstraintTemplate currTemplate = new Constraint_if_then_module("ite_m", moduleParam2,
                "If we use module ${parameter_1}, then use ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: itn_m If we use module ${parameter_1}, then do not use module
         * ${parameter_2} subsequently.
         */
        currTemplate = new Constraint_if_then_not_module("itn_m", moduleParam2,
                "If we use module ${parameter_1}, then do not use ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: depend_m If we use module ${parameter_1}, then we must have used module
         * ${parameter_2} prior to it.
         */
        currTemplate = new Constraint_depend_module("depend_m", moduleParam2,
                "If we use module ${parameter_1}, then we must have used ${parameter_2} prior to it.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: next_m If we use module ${parameter_1}, then use ${parameter_2} as
         * a next module in the sequence.
         */
        currTemplate = new Constraint_next_module("next_m", moduleParam2,
                "If we use module ${parameter_1}, then use ${parameter_2} as a next module in the sequence.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: prev_m If we use module ${parameter_1}, then we must have used ${parameter_2} as
         * a previous module in the sequence.
         */
        currTemplate = new Constraint_prev_module("prev_m", moduleParam2,
                "If we use module ${parameter_1}, then we must have used ${parameter_2} as a previous module in the sequence.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: use_m Use module ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_use_module("use_m", moduleParam1, "Use module ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: nuse_m Do not use module ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_not_use_module("nuse_m", moduleParam1, "Do not use module ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: last_m Use ${parameter_1} as last module in the solution.
         */
        currTemplate = new Constraint_last_module("last_m", moduleParam1, "Use ${parameter_1} as last module in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: use_t Use type ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_use_type("use_t", typeParam1, "Use type ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: gen_t Generate type ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_gen_type("gen_t", typeParam1, "Generate type ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: nuse_t Do not use type ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_not_use_type("nuse_t", typeParam1, "Do not use type ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: ngen_t Do not generate type ${parameter_1} in the solution.
         */
        currTemplate = new Constraint_not_gen_type("ngen_t", typeParam1, "Do not generate type ${parameter_1} in the solution.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: use_ite_t If we have used data type ${parameter_1}, then use type ${parameter_2}
         * subsequently.
         */
        currTemplate = new Constraint_if_use_then_type("use_ite_t", typeParam2,
                "If we have used data type ${parameter_1}, then use type ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: gen_ite_t If we have data type ${parameter_1}, then generate type ${parameter_2}
         * subsequently.
         */
        currTemplate = new Constraint_if_gen_then_type("gen_ite_t", typeParam2,
                "If we have generated data type ${parameter_1}, then generate type ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: use_itn_t If we have used data type ${parameter_1}, then do not use type
         * ${parameter_2} subsequently.
         */
        currTemplate = new Constraint_if_use_then_not_type("use_itn_t", typeParam2,
                "If we have used data type ${parameter_1}, then do not use type ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

        /*
         * ID: gen_itn_t If we have generated data type ${parameter_1}, then do not generate type
         * ${parameter_2} subsequently.
         */
        currTemplate = new Constraint_if_gen_then_not_type("gen_itn_t", typeParam2,
                "If we have generated data type ${parameter_1}, then do not generate type ${parameter_2} subsequently.");
        addConstraintTemplate(currTemplate);

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
     * @param constr {@link ConstraintTemplateData}.
     * @return TODO.
     */
    public String getDescription(ConstraintTemplateData constr) {
        ConstraintTemplate currTmpl = this.constraintTemplates.get(constr.getConstraintID());
        List<TaxonomyPredicate> params = constr.getParameters();
        String description = currTmpl.getDescription();
        for (int i = 0; i < params.size(); i++) {
            description = description.replace("${parameter_" + i + "}", params.get(i).toString());
        }
        return description;
    }

    /**
     * @param constraintID ID of the constraint.
     * @param parameters {@link List} of {@link TaxonomyPredicate}.
     * @return The {@link ConstraintTemplateData} if the format is correct, {null} otherwise.
     */
    public ConstraintTemplateData addConstraintTemplateData(String constraintID, List<TaxonomyPredicate> parameters) {
        if (isGoodConstraintFormat(constraintID, parameters)) {
            return new ConstraintTemplateData(constraintID, parameters);
        }
        return null;
    }

    /**
     * TODO: Needs to be implemented. Input should be compared with the constraint templates, wrt the types of parameters and dimensions of each.
     *
     * @param constraintID TODO
     * @param parameters TODO
     * @return TODO
     */
    public boolean isGoodConstraintFormat(String constraintID, List<TaxonomyPredicate> parameters) {
        return true;
    }

    /**
     * Implements constraints of the form:<br>
     * If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it using the function {@link #getConstraint}.
     */
    public class Constraint_depend_module extends ConstraintTemplate {
        public Constraint_depend_module(String id, List<ConstraintParameter> parameterTypes, String description) {
            super(id, parameterTypes, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.depend_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * Generate type <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_gen_type extends ConstraintTemplate {
        public Constraint_gen_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }

            SLTL_formula_F formula = new SLTL_formula_F(parameters.get(0));
            return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), WorkflowElement.MEMORY_TYPE, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we have generated data module <b>parameters[0]</b>,  then do not generate type <b>parameters[1]</b> subsequently
     * using the function {@link #getConstraint}.
     */
    public class Constraint_if_gen_then_not_type extends ConstraintTemplate {
        public Constraint_if_gen_then_not_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.itn_type(parameters.get(0), parameters.get(1), WorkflowElement.MEMORY_TYPE, moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we have generated data module <b>parameters[0]</b>, then generate <b>parameters[1]</b>
     * subsequently using the function {@link #getConstraint}.
     */
    public class Constraint_if_gen_then_type extends ConstraintTemplate {
        public Constraint_if_gen_then_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.ite_type(parameters.get(0), parameters.get(1), WorkflowElement.MEMORY_TYPE, moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
     * subsequently using the function {@link #getConstraint}.
     */
    public class Constraint_if_then_module extends ConstraintTemplate {
        public Constraint_if_then_module(String id, List<ConstraintParameter> parameterTypes, String description) {
            super(id, parameterTypes, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
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
        public Constraint_if_then_not_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.itn_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we have used data module <b>parameters[0]</b>,  then do not use type <b>parameters[1]</b> subsequently
     * using the function {@link #getConstraint}.
     */
    public class Constraint_if_use_then_not_type extends ConstraintTemplate {
        public Constraint_if_use_then_not_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.itn_type(parameters.get(0), parameters.get(1), WorkflowElement.USED_TYPE, moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we have used data module <b>parameters[0]</b>, then use <b>parameters[1]</b>
     * subsequently using the function {@link #getConstraint}.
     */
    public class Constraint_if_use_then_type extends ConstraintTemplate {
        public Constraint_if_use_then_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.ite_type(parameters.get(0), parameters.get(1), WorkflowElement.USED_TYPE, moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * Use <b>parameters[0]</b> as last module in the solution.
     * using the function {@link #getConstraint}.
     */
    public class Constraint_last_module extends ConstraintTemplate {
        public Constraint_last_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.useAsLastModule(parameters.get(0), moduleAutomaton, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence
     * using the function {@link #getConstraint}.
     */
    public class Constraint_next_module extends ConstraintTemplate {
        public Constraint_next_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.next_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * Do not generate type <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_not_gen_type extends ConstraintTemplate {
        public Constraint_not_gen_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }

            SLTL_formula_G formula = new SLTL_formula_G(false, parameters.get(0));
            return formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), WorkflowElement.MEMORY_TYPE, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * Do not use module <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_not_use_module extends ConstraintTemplate {
        public Constraint_not_use_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
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
     * Do not use type <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_not_use_type extends ConstraintTemplate {
        public Constraint_not_use_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
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
     * If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> as a previous module in the sequence.
     * using the function {@link #getConstraint}.
     */
    public class Constraint_prev_module extends ConstraintTemplate {
        public Constraint_prev_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }
            return SLTL_formula.prev_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
        }
    }

    /**
     * Implements constraints of the form:<br>
     * Use module <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_use_module extends ConstraintTemplate {
        public Constraint_use_module(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
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
     * Use type <b>parameters[0]</b> in the solution
     * using the function {@link #getConstraint}.
     */
    public class Constraint_use_type extends ConstraintTemplate {
        public Constraint_use_type(String id, List<ConstraintParameter> parametersNo, String description) {
            super(id, parametersNo, description);
        }

        @Override
        public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                    TypeAutomaton typeAutomaton, AtomMappings mappings) {
            if (parameters.size() != this.getNoOfParameters()) {
                super.throwParametersError(parameters.size());
                return null;
            }

            SLTL_formula_F formula = new SLTL_formula_F(parameters.get(0));
            return formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), WorkflowElement.USED_TYPE, mappings);
        }
    }
}

