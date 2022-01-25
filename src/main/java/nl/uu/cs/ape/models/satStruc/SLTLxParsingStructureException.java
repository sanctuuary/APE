package nl.uu.cs.ape.models.satStruc;

import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * The {@codeSLTLxParsingStructureException} exception will be thrown if the SLTLx formula is not well formatted, i.e., it does not follow the grammar rules. 
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxParsingStructureException extends ParseCancellationException {
	
	public SLTLxParsingStructureException(int line, int charPositionInLine, String msg) {
		super("SLTLx formula parsing error:\nline " + line + ":" + charPositionInLine + " " + msg);
	}

}
