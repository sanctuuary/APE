package nl.uu.cs.ape.models.satStruc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.output.FileWriterWithEncoding;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * Abstract class thar represents any SLTLx formula.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class SATFact implements SATElem {

	private final Set<SATBoundVar> variables;
	private final int stateNo;
	
	
	protected SATFact(int stateNo) {
		this.variables = new HashSet<SATBoundVar>();
		this.stateNo = stateNo;
	}
	
	protected int getStateNo() {
		return stateNo;
	}
	/**
	 * Add a new variable to the formula. 
	 * @param newVar - new variable
	 * @return {@code true} if the variable was added, {@code false} otherwise.
	 */
	public boolean addVariable(SATBoundVar newVar) {
		return variables.add(newVar);
	}
	/**
	 * Encode a collection of SLTLx formulas to CNF and append it to the existing CNF file. It adds the encoding at the end of the content of the file.
	 * 
	 * @param file - existing cnf file
	 * @param synthesisEngine synthesis engine used for encoding
	 * @param formulas - collection of formulas that should be encoded
	 * @throws IOException  in case of an I/O error
	 */
	public static void appendCNFToFile(File file, SATSynthesisEngine synthesisEngine,  Collection<SATFact> formulas) throws IOException {
		StringBuilder cnf = new StringBuilder();
		createCNFEncoding(formulas, synthesisEngine)
							.forEach(clause -> cnf.append(clause.toCNF()));
		
		APEUtils.appendToFile(file, cnf.toString());
	}
	
	/**
	 * Create the CNF encoding of the facts and return the set of corresponding {@link CNFClause}s.
	 * 
	 * @param facts				- all facts that should be encoded
	 * @param synthesisEngine	- synthesis engine used for encoding
	 * @return Set of {@link CNFClause}s that encode the given collector of formulas.
	 */
	public static Set<CNFClause> createCNFEncoding(Collection<SATFact> facts, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<>();
		facts.forEach(fact -> clauses.addAll(fact.getCNFEncoding(synthesisEngine)));
		
		return clauses;
	}
}
