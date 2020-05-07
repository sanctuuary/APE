package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code DataInstance} class represents a data instance characterized by one or more data dimensions. Object of this class correspond 
 * to a single data instance (e.g. to a single input or output).
 * Usually the type is defined by one (e.g. data type) or two (e.g. data type and format) dimensions. 
 * However, in some domains more dimensions are useful.
 * <br>
 * <br>
 * e.g <br>
 * {@code DataInstance printableMap} is described with a pair:  {@code <Map_type, PDF_format>}
 * 
 * @author Vedran Kasalica
 *
 */
public class DataInstance {

	/** List of data types that describe different data dimensions and correspond to a single data instance. */
	private List<TaxonomyPredicate> types;
	
	/**
	 * Create a new data instance. The instance will be characterized by different data type dimentions.
	 */
	public DataInstance() {
		this.types = new ArrayList<TaxonomyPredicate>();
	}

	/**
	 * Add a new data dimension to characterize the data instance.
	 * @param type - data type that characterizes the data instance.
	 */
	public void addType(TaxonomyPredicate type) {
		if(type == null) {
			System.err.println("Cannot add null as data instance!");
		} else {
			types.add(type);
		}
	}
	
	/**
	 *  Get a list of types/formats that correspond to the specific data instance 
	 * @return List of {@link TaxonomyPredicate}s
	 */
	public List<TaxonomyPredicate> getTypes() {
		return types;
	}
	
}
