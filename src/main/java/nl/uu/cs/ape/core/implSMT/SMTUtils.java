package nl.uu.cs.ape.core.implSMT;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.Mappings;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.smtStruc.Assertion;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Row;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;

/**
 * The {@code ModuleUtils} class is used for storing {@code Static} methods related to the SMT encoding of the problem.
 *
 * @author Vedran Kasalica
 */
public final class SMTUtils {

	/**
	 * Append textual representation of the slt2lib rows to the existing file. It adds the text at the end of the content of the file.
	 * @param file - file to which the text is appended
	 * @param smtRows - all smt clauses/rows
	 * @throws IOException
	 */
	public static void appendToFile(File file, List<SMTLib2Row> smtRows, SMTSynthesisEngine synthesisInstance) throws IOException {
		Writer fileWriter = new FileWriterWithEncoding(file, "ASCII", true);
		BufferedWriter writer = new BufferedWriter(fileWriter, 8192 * 4);
		for(SMTLib2Row row : smtRows) {
			writer.write(row.getSMT2Encoding(synthesisInstance));
		}
		writer.close();
	}
	
	/**
	 * Remove all unsupported characters by SMTLib2.
	 * @param text
	 * @return
	 */
	public static String removeUnsupportedCharacters(String text) {
		 // provide identifier supported by SMTLib2
        String smtSupported = text.replaceAll("[^A-Za-z0-9_$]", "");
        if(Character.isDigit(smtSupported.charAt(0))) {
        	smtSupported = "_" + smtSupported;
        }
        
        return smtSupported;
	}

	/**
	 * Calculate min size of an unsigned decimal number that can represent the given number.
	 * @param number - number that should be possible to represent.
	 * @return size of an unsigned binary number (or BitVector) that can represent the int number.
	 */
	public static int countBits(int number) {
        return (int)(Math.log(number) /
                     Math.log(2) + 1);
    }
	
	

}