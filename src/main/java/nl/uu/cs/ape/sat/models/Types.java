package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Types} class represents the list of data types/formats (List<{@link Type}>) that correspond to a single data instance (e.g. to a single input or output).
 * <br>
 * <br>
 * e.g <br>
 * {@code Types printableMap} is described with a pair:  {@code <Map_type, PDF_format>}
 * 
 * @author Vedran Kasalica
 *
 */
public class Types {

	/** List of data types/formats (List<{@link Type}>) that correspond to a single data instance. */
	private List<Type> types;
	
	public Types(List<Type> types) {
		this.types = new ArrayList<Type>();
		for(Type currType : types) {
			this.types.add(currType);
		}
	}
	
	public Types() {
		this.types = new ArrayList<Type>();
	}

	public void addType(Type type) {
		types.add(type);
	}
	
	/**
	 *  Get a list of types/formats that correspond to the specific data instance 
	 * @return List of {@link Type}s
	 */
	public List<Type> getTypes() {
		return types;
	}
	
}
