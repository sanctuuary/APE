package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTUtils;

/**
 * Structure used to model (assert x) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class ForallStatement implements SMTFact {

	private List<SMTBoundedVar> boundedVars;
	private List<SMTDataType> dataTypes;
	private SMTFact content;
	private boolean explicit;
	
	public ForallStatement(SMTBoundedVar boundedVar, SMTDataType dataType, SMTFact content) {
		this.boundedVars = new ArrayList<SMTBoundedVar>();
		this.boundedVars.add(boundedVar);
		this.dataTypes = new ArrayList<SMTDataType>();
		this.dataTypes.add(dataType);
		this.content = content;
	}
	
	public ForallStatement(List<SMTBoundedVar> boundedVars, List<SMTDataType> dataTypes, SMTFact content) {
		this.boundedVars = boundedVars;
		this.dataTypes = dataTypes;
		this.content = content;
	}
	
	public ForallStatement(List<SMTBoundedVar> boundedVars, List<SMTDataType> dataTypes, SMTFact content, boolean explicit) {
		this.boundedVars = boundedVars;
		this.dataTypes = dataTypes;
		this.content = content;
		this.explicit = explicit;
	}
	

	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		StringBuilder variables = new StringBuilder();
		List<SMTFact> additionalConstrints = new ArrayList<>();
		
		for(int i = 0; i < dataTypes.size(); i++) {
			String smtDataType = this.dataTypes.get(i).toString();
			
			if((synthesisEngine.getAutomatonSize(dataTypes.get(i)) > -1) && !explicit) {
				additionalConstrints.add(boundedVarIsInBounds(boundedVars.get(i), dataTypes.get(i),synthesisEngine));
				smtDataType = this.dataTypes.get(i).toBitVector(synthesisEngine);
			}
			
			variables
			.append("(")
					.append(boundedVars.get(i).getSMT2Encoding(synthesisEngine))
					.append(" ")
					.append(smtDataType)
				.append(") ");
		}
		SMTFact allRules = content;
		// add the limitations if they were generated
		if(additionalConstrints.size() > 0) {
			allRules = new OrStatement(
							new AndStatement(additionalConstrints), 
							content
						);
		}
		
		constraints
			.append("(forall (")
			.append(variables)
			.append(") ")
			.append(allRules.getSMT2Encoding(synthesisEngine))
			.append(")");
		;
		
		return constraints.toString();
	}
	
	private BitVecComparisonStatement boundedVarIsInBounds(SMTBoundedVar boundedVar, SMTDataType dataType, SMTSynthesisEngine synthesisEngine) {
		return new BitVecComparisonStatement(SMTBitVectorOp.GREATER_OR_EQUAL, boundedVar, new SMTBitVec(dataType, synthesisEngine.getAutomatonSize(dataType)));
	}
}
