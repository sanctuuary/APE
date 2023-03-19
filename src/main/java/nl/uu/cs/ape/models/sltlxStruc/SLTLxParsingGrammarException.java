package nl.uu.cs.ape.models.sltlxStruc;

import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * The {@link SLTLxParsingGrammarException} exception will be thrown if the
 * SLTLx formula is not well formatted, i.e., it does not follow the grammar
 * rules.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxParsingGrammarException extends ParseCancellationException {

	public SLTLxParsingGrammarException(int line, int charPositionInLine, String msg) {
		super("SLTLx formula parsing error:\nline " + line + ":" + charPositionInLine + " " + msg);
	}

}
