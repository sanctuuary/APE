package nl.uu.cs.ape.models.sltlxStruc;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * The {SLTLxParsingBaseErrorListener} is an extension of the BaseErrorListener
 * used to throw Exception when the given SLTLx formula does not follow the
 * grammar.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxParsingBaseErrorListener extends BaseErrorListener {

	public static final SLTLxParsingBaseErrorListener INSTANCE = new SLTLxParsingBaseErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e)
			throws SLTLxParsingGrammarException {
		throw new SLTLxParsingGrammarException(line, charPositionInLine, msg);
	}
}