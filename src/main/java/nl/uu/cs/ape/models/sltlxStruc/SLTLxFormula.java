package nl.uu.cs.ape.models.sltlxStruc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * Abstract class that represents any SLTLx formula.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class SLTLxFormula implements SLTLxElem {

	
	protected SLTLxFormula() {
	}
	
	/**
	 * Encode a collection of SLTLx formulas to CNF and append it to the existing CNF file. It adds the encoding at the end of the content of the file.
	 * 
	 * @param file - existing cnf file
	 * @param synthesisEngine synthesis engine used for encoding
	 * @param formulas - collection of formulas that should be encoded
	 * @throws IOException Thrown in case of an I/O error.
	 */
	public static void appendCNFToFile(File file, SATSynthesisEngine synthesisEngine,  Collection<SLTLxFormula> formulas) throws IOException {
		StringBuilder cnf = new StringBuilder();
		createCNFEncoding(formulas, 0, synthesisEngine)
							.forEach(clause -> cnf.append(clause));
		APEUtils.appendToFile(file, cnf.toString());
	}
	
	
	
	/**
	 * Create the CNF encoding of the facts and return the set of corresponding clauses in String format.
	 * 
	 * @param facts				- all facts that should be encoded
	 * @param synthesisEngine	- synthesis engine used for encoding
	 * @return Set of clauses in String format that encode the given collector of formulas.
	 */
	private static Set<String> createCNFEncoding(Collection<SLTLxFormula> facts, int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<String>();
		facts.forEach(fact ->
			clauses.addAll(fact.getCNFEncoding(stateNo, new SLTLxVariableSubstitutionCollection(), synthesisEngine))
		);
		return clauses;
	}
	
	/**
	 * Get string that represents the CNF encoding of the constraint. As the
	 * @param synthesisEngine
	 * @return
	 */
	public Set<String> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return this.getCNFEncoding(0, new SLTLxVariableSubstitutionCollection(), synthesisEngine);
	}
	
}
