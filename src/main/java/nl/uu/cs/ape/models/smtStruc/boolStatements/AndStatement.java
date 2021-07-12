package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;

/**
 * Structure used to model (and x y) statement in smt2lib.
 * 
 * @author Vedran Kasalica
 *
 */
public class AndStatement implements Fact {

	private Fact arg1;
	private Fact arg2;
	
	
	
	public AndStatement(Fact ifStatement, Fact thanStatement) {
		super();
		this.arg1 = ifStatement;
		this.arg2 = thanStatement;
	}



	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(and ")
				.append(arg1.toString(mapping)).append(" ")
				.append(arg2.toString(mapping))
			.append(")");
		
		return constraints.toString();
	}
}
