package nl.uu.cs.ape.models.enums;

import org.checkerframework.checker.units.qual.m;

/**
 * Defines the values describing the predicates over variables, that will
 * reflect in states in the workflow.
 * 
 * @author Vedran Kasalica
 */
public enum AtomVarType {

	/**
	 * Depicts the dependency between two variables that represent data instances
	 * (states). It depicts that a data instance is dependent (was derived from) on
	 * another data instance.
	 */
	R_RELATION_V("rRelVar"),

	/**
	 * Depicts the equivalence of two variables (which represent type
	 * instances/states).
	 */
	VAR_EQUIVALENCE("variableEq"),

	/**
	 * Depicts the usage of an already created type instance, as an input for a
	 * tool. It references a variable that represents a created data type.
	 */
	MEM_TYPE_REF_V("memVarRef"),

	/**
	 * Depicts that the data instance represented as a variable is of a specific
	 * data type.
	 */
	TYPE_V("typeVar"),

	/**
	 * Depicts the instantiation of a variable to a specific type state. It
	 * references state that the variable represents.
	 */
	VAR_VALUE("varValue");

	private final String text;

	private AtomVarType(String s) {
		this.text = s;
	}

	public String toString() {
		return this.text;
	}

	/**
	 * Check if the Atom represent a variable data type.
	 * 
	 * @return {@code true} if it is a data type, {@code false} otherwise.
	 */
	public boolean isVarDataType() {
		return this.equals(TYPE_V) ? true : false;
	}

	/**
	 * Check if the Atom represent a variable memory reference.
	 * 
	 * @return {@code true} if it is a memory reference, {@code false} otherwise.
	 */
	public boolean isVarMemReference() {
		return this.equals(MEM_TYPE_REF_V) ? true : false;
	}

	/**
	 * Check if the Atom represent a binary relation.
	 * 
	 * @return {@code true} if it is a unary property, {@code false} otherwise.
	 */
	public boolean isBinaryRel() {
		if (this.equals(VAR_VALUE) | this.equals(R_RELATION_V) | this.equals(VAR_EQUIVALENCE)
				|| this.equals(MEM_TYPE_REF_V)) {
			return true;
		} else {
			return false;
		}
	}

}
