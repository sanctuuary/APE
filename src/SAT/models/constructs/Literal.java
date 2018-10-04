package SAT.models.constructs;

import SAT.automaton.State;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;

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
		
		String[] parts = atom.split("\\(");

		predicate = allModules.get(parts[0]);
		if (predicate == null) {
			isModule = false;
			predicate = allTypes.get(parts[0]);
			if (predicate == null) {
				System.out.println("Literal with predicate " + parts[0] + " was not defined.");
			}
		} else {
			isModule = true;
		}
		attribute = parts[1].substring(0, parts[1].length() - 1);
	}
	
//	public Literal(String mappedLiteral, AtomMapping atomMapping, AllModules allModules, AllTypes allTypes) {
//		String atom;
//		String literal = atomMapping.findOriginal(mappedLiteral);
//		String trimLiteral = literal.trim();
//		
//		if (trimLiteral.startsWith("-")) {
//			negated = true;
//			atom = trimLiteral.substring(1);
//		} else {
//			negated = false;
//			atom = trimLiteral;
//		}
//		String[] parts = atom.split("\\(");
//
//		predicate = allModules.get(parts[0]);
//		if (predicate == null) {
//			isModule = false;
//			predicate = allTypes.get(parts[0]);
//			if (predicate == null) {
//				System.out.println("Literal with predicate " + parts[0] + " was not defined.");
//			}
//		} else {
//			isModule = true;
//		}
//		attribute = parts[1].substring(0, parts[1].length() - 1);
//	}
	

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
	 * Returns TRUE in case the literal is a Module, FALSE in case of the literal being a Type.
	 * @return boolean TRUE if Literal is a Module
	 */
	public boolean isModule() {
		return isModule;
	}
	
	public boolean isNegated() {
		return negated;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	
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
