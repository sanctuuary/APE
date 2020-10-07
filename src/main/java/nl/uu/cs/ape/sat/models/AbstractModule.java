package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The {@code AbstractModule} class represents modules/tools provided by the
 * Module Taxonomy as well as their abstraction classes. Instances of
 * {@link AbstractModule} can be actual tools or their abstraction classes,
 * while all instances of the actual tools are extended to {@link Module}.
 *
 * @author Vedran Kasalica
 */
public class AbstractModule extends TaxonomyPredicate {

    private final String moduleName;
    private final String moduleID;

    /**
     * Creates an abstract module from {@code moduleName} and {@code moduleID}.
     *
     * @param moduleName Name of the module.
     * @param moduleID   Unique module identifier.
     * @param rootNode   ID of the Taxonomy Root node corresponding to the Module.
     * @param nodeType   {@link NodeType} object describing the type w.r.t. the Module Taxonomy.
     */
    public AbstractModule(String moduleName, String moduleID, String rootNode, NodeType nodeType) {
        super(rootNode, nodeType);
        this.moduleName = moduleName;
        this.moduleID = moduleID;
    }

    /**
     * Generate an AbstractModule from an existing one. In order to provide means
     * for combining Module and AbstractModule objects.
     *
     * @param abstractModule Abstract module that is being copied
     * @param nodeType       {@link NodeType} object describing the type w.r.t. the Module Taxonomy.
     */
    public AbstractModule(TaxonomyPredicate abstractModule, NodeType nodeType) {
        super(abstractModule, (nodeType != null) ? nodeType : abstractModule.getNodeType());
        this.moduleName = abstractModule.getPredicateLabel();
        this.moduleID = abstractModule.getPredicateID();
    }

    @Override
    public String getPredicateID() {
        return moduleID;
    }

    @Override
    public String getPredicateLabel() {
        return moduleName;
    }

    /**
     * Returns null. Abstract classes do not have input types.
     *
     * @return null module input
     */
    public List<Type> getModuleInput() {
        return null;
    }

    /**
     * Returns null. Abstract classes do not have output types.
     *
     * @return null module output
     */
    public List<Type> getModuleOutput() {
        return null;
    }

    @Override
    public String getType() {
        return "abstract module";
    }

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
