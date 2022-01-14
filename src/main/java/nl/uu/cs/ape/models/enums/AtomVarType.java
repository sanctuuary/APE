package nl.uu.cs.ape.models.enums;

import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;

/**
 * Defines the values describing the predicates over variables, that will reflect in states in the workflow.
 * 
 *  @author Vedran Kasalica
 */
public enum AtomVarType implements SMTFunctionName {

    
    /**
     * Depicts the dependency between two variables that represent data instances (states). It depicts that a data instance is dependent (was derived from) on another data instance.
     */
    TYPE_DEPENDENCY_VAR("typeDepVar"),
    
    /**
     * Depicts the equivalence of two variables (which represent type instances/states).
     */
    VAR_EQUIVALENCE("variableEq"),
    
	/**
	 * Depicts that the data instance represented as a variable is of a specific data type.
     */
    TYPE_VAR("typeVar"),
    
    /**
     * Depicts the instantiation of a variable to a specific type state. It references state that the variable represents.
     */
    VAR_REF("varRef");

	
	 private final String text;

	 private AtomVarType(String s) {
         this.text = s;
     }
	
	    
	 public String toString() {
	     return this.text;
	 }
	 
	 /**
	  * Check if the Atom represent a unary property.
	  * @return {@code true} if it is a unary property, {@code false} otherwise. 
	  */
	 public boolean isUnaryProperty() {
		 if(this.equals(TYPE_DEPENDENCY_VAR)) {
			 return true;
		 } else {
			 return false;
		 }
	 }
	 
	 /**
	  * Check if the Atom represent a binary relation.
	  * @return {@code true} if it is a unary property, {@code false} otherwise. 
	  */
	 public boolean isBinaryRel() {
		 if(this.equals(TYPE_VAR) | this.equals(TYPE_DEPENDENCY_VAR)| this.equals(VAR_EQUIVALENCE)) {
			 return true;
		 } else {
			 return false;
		 }
	 }
	 
}
