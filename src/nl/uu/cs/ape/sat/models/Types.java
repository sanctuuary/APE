package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Types} class represents the list of data types/formats that correspond to a single data instance (e.g. to a single input or output).
 * 
 * @author Vedran Kasalica
 *
 */
public class Types {

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
	
	public List<Type> getTypes() {
		return types;
	}
	
}
