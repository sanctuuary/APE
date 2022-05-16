package nl.uu.cs.ape.models;

import java.util.List;

import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@link ConstraintTemplateData} class is used to store the data describing each constrain.
 *
 * @author Vedran Kasalica
 */
public class ConstraintTemplateData {

    private String constraintID;
    private List<TaxonomyPredicate> parameters;

    /**
     * Instantiates a new Constraint template data.
     *
     * @param constraintID the constraint id
     * @param parameters   the parameters
     */
    public ConstraintTemplateData(String constraintID, List<TaxonomyPredicate> parameters) {
        this.constraintID = constraintID;
        this.parameters = parameters;
    }

    /**
     * Gets constraint id.
     *
     * @return The constraintID.
     */
    public String getConstraintID() {
        return constraintID;
    }

    /**
     * Gets parameters.
     *
     * @return The parameters.
     */
    public List<TaxonomyPredicate> getParameters() {
        return parameters;
    }

}
