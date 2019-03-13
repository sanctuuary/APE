package nl.uu.cs.ape.sat.models.constructs;

import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;

/**
 * The {@code Literal} class represents literals (atoms that can be negated) corresponding to the usage of the modules and types in the solution. It is of the form {@code Predicate(Attribute)} where
 * {@code Predicate} represents a single predicate/label used to depict {@code AbstractModule, Module} or {@code Type}, while {@code Attribute} represents the state in the module/type automaton where the module/type is used or not used. <br>
 * {@code Literal}s can be negated.
 * @author Vedran Kasalica
 *
 */
public class Literal implements Comparable<Literal>{

	private String mappedAtom;
	private Predicate predicate;
	private String attribute;
	private boolean negated;
	private boolean isModule;

	/**
	 * Generating an object from a mapped representation of the Literal.
	 * @param literal - mapped literal
	 * @param atomMapping - mapping of the atoms
	 * @param allModules - list of all the modules
	 * @param allTypes - list of all the types
	 */
	public Literal(String mappedLiteral, AtomMapping atomMapping, AllModules allModules, AllTypes allTypes) {
		
		if (mappedLiteral.startsWith("-")) {
			negated = true;
			mappedAtom = mappedLiteral.substring(1);
		} else {
			negated = false;
			mappedAtom = mappedLiteral;
		}
		
		String atom = atomMapping.findOriginal(Integer.parseInt(mappedAtom)).trim();
		
		int splitIndex = atom.lastIndexOf("(");
		String predicateStr = atom.substring(0, splitIndex);
		predicate = allModules.get(predicateStr);
		if (predicate == null) {
			isModule = false;
			predicate = allTypes.get(predicateStr);
			if (predicate == null) {
				System.out.println("Literal over predicate: " + predicateStr + " was not defined. Error while scanning the predicate: " + atom);
			}
		} else {
			isModule = true;
		}
		attribute = atom.substring(splitIndex + 1, atom.length() - 1);
	}
	
	/**
	 * Returns the Original (human readable) value of the literal
	 *  @return The value of the original literal
	 */
	public String toString() {
		if (negated) {
			return "-" + predicate.getPredicate() + "(" + attribute + ")";
		} else {
			return predicate.getPredicate() + "(" + attribute + ")";
		}
	}
	
	
	/**
	 * Returns the Mapped Literal
	 * @return The value of the mapped literal
	 */
	public String toMappedString() {
		if (negated) {
			return "-" + mappedAtom;
		} else {
			return mappedAtom;
		}
	}
	
	/**
	 * Returns the negation of the Mapped Literal
	 * @return Negation of the value of the mapped literal
	 */
	public String toNegatedMappedString() {
		if (negated) {
			return mappedAtom;
		} else {
			return "-" + mappedAtom;
		}
	}
	
	/**
	 * Returns the negation of the Mapped Literal
	 * @return Negation of the value of the mapped literal
	 */
	public int toNegatedMappedInt() {
		if (negated) {
			return Integer.parseInt(mappedAtom);
		} else {
			return Integer.parseInt("-" + mappedAtom);
		}
	}
	
	/**
	 * Returns {@code true} in case the literal is a Module, {@code false} in case of the literal being a Type.
	 * @return boolean {@code true} if Literal is a Module
	 */
	public boolean isModule() {
		return isModule;
	}
	
	/**
	 * Returns {@code true} in case the literal is NEGATED, {@code false} otherwise.
	 * @return boolean {@code true} if Literal is negated.
	 */
	public boolean isNegated() {
		return negated;
	}
	
	/**
	 * Returns the label used to depict the state in the automaton.
	 * @return String representation of the module/type automaton state.
	 */
	public String getAttribute() {
		return attribute;
	}
	
	/**
	 * Returns the predicate/label used to depict {@code AbstractModule, Module} or {@code Type}.
	 * @return Predicate object
	 */
	public Predicate getPredicate() {
		return predicate;
	}
	
	/**
	 *	Compare the two Literals according to the state they are used in. Returns a negative integer, zero, or a positive integer as this Literal's state comes before than, is equal to, or comes after than the @otherLiteral's state.
	 * 
	 *  @param otherLiteral - the Literal to be compared
	 *  @return the value 0 if the argument Literal's state is equal to this Literal's state; a value less than 0 if this Literal's state comes before the @otherLiteral's state; and a value greater than 0 if this Literal's state comes after the @otherLiteral's state.
	 */
	@Override
	public int compareTo(Literal otherLiteral) {
		if(attribute == null) {
			return -1;
		}
		if(otherLiteral == null) {
			return 1;
		}
	    return attribute.compareTo(otherLiteral.getAttribute());
	}
}
