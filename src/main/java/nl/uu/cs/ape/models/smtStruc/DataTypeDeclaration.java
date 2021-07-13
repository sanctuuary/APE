package nl.uu.cs.ape.models.smtStruc;

import java.util.Collection;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;

/**
 * Structure used to model declaration of new datatypes (declare-datatypes () ((A x y)))  in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class DataTypeDeclaration implements SMT2LibRow {

	private SMTDataType dataTypeName;
	private Collection<? extends PredicateLabel> dataTypeValues;
	
	
	public DataTypeDeclaration(SMTDataType dataTypeName, Collection<? extends PredicateLabel> dataTypeValues) {
		this.dataTypeName = dataTypeName;
		this.dataTypeValues = dataTypeValues;
	}
	
	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(declare-datatypes () ((")
				.append(dataTypeName.toString());
		
				for(PredicateLabel dataTypeVal : dataTypeValues) {
					constraints.append(" ").append(mapping.add(dataTypeVal));
				}
			constraints.append(")))");
		return constraints.append("\n").toString();
	}
}
