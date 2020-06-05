package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

import java.util.List;

/**
 * The {@link ConstraintTemplateData} class is used to store the data describing each constrain.
 *
 * @author Vedran Kasalica
 */
public class ConstraintTemplateData {

    private String constraintID;
    private List<TaxonomyPredicate> parameters;

    public ConstraintTemplateData(String constraintID, List<TaxonomyPredicate> parameters) {
        this.constraintID = constraintID;
        this.parameters = parameters;
    }

    /**
     * @return The constraintID.
     */
    public String getConstraintID() {
        return constraintID;
    }

    /**
     * @return The parameters.
     */
    public List<TaxonomyPredicate> getParameters() {
        return parameters;
    }

}
