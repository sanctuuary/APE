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

public interface SATFact extends SATElem {

	/**
	 * Create the CNF encoding of the statement and return the list of clauses.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return a list of clauses that represent cnf clauses.
	 */
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine);
	
	/**
	 * CreatE the CNF encoding of the negation of the statement and return the list of clauses.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return a list of clauses that represent the negated cnf clauses.
	 */
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine);
	
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
	 * Create the CNF encoding of the facts and return the set of corresponding {@link SATClause}s.
	 * 
	 * @param facts				- all facts that should be encoded
	 * @param synthesisEngine	- synthesis engine used for encoding
	 * @return Set of {@link SATClause}s that encode the given collector of formulas.
	 */
	public static Set<SATClause> createCNFEncoding(Collection<SATFact> facts, SATSynthesisEngine synthesisEngine) {
		Set<SATClause> clauses = new HashSet<>();
		facts.forEach(fact -> clauses.addAll(fact.createCNFEncoding(synthesisEngine)));
		
		return clauses;
	}
}
