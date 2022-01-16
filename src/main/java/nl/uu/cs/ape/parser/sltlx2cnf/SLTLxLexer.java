// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.sltlx2cnf;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SLTLxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, BIN_CONNECTIVE=5, UN_MODAL=6, BIN_MODAL=7, 
		VARS=8, BOOL=9, LPAREN=10, RPAREN=11, VARIABLE=12, CONSTANT=13, R_REL=14, 
		SLTL_UNTIL=15, SLTL_GLOBALLY=16, SLTL_FINALLY=17, SLTL_NEXT=18, OR=19, 
		AND=20, IMPL=21, EQUIVALENT=22, EQUAL=23, NOT=24, EXISTS=25, FORALL=26, 
		CHARACTER=27, ENDLINE=28, WHITESPACE=29;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"VARS", "BOOL", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", 
			"SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", 
			"IMPL", "EQUIVALENT", "EQUAL", "NOT", "EXISTS", "FORALL", "CHARACTER", 
			"ENDLINE", "WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "','", "';'", null, null, null, null, null, "'('", 
			"')'", null, null, "'R'", "'U'", "'G'", "'F'", "'X'", "'|'", "'&'", "'->'", 
			"'<->'", "'='", "'!'", "'Exists'", "'Forall'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"VARS", "BOOL", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", 
			"SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", 
			"IMPL", "EQUIVALENT", "EQUAL", "NOT", "EXISTS", "FORALL", "CHARACTER", 
			"ENDLINE", "WHITESPACE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SLTLxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SLTLx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\37\u00ad\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\5\6J\n\6\3\7\3\7\3\7\5\7O\n\7\3\b\3"+
		"\b\3\t\3\t\3\t\7\tV\n\t\f\t\16\tY\13\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\5\nd\n\n\3\13\3\13\3\f\3\f\3\r\3\r\6\rl\n\r\r\r\16\rm\3\16\3\16\6"+
		"\16r\n\16\r\16\16\16s\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\34\5\34\u00a0\n\34\3\35\6\35\u00a3\n\35\r\35\16\35\u00a4"+
		"\3\36\6\36\u00a8\n\36\r\36\16\36\u00a9\3\36\3\36\2\2\37\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37\3\2\5\5\2\62"+
		";C\\c|\4\2\f\f\17\17\4\2\13\13\"\"\2\u00b7\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2"+
		"\7A\3\2\2\2\tC\3\2\2\2\13I\3\2\2\2\rN\3\2\2\2\17P\3\2\2\2\21R\3\2\2\2"+
		"\23c\3\2\2\2\25e\3\2\2\2\27g\3\2\2\2\31i\3\2\2\2\33o\3\2\2\2\35w\3\2\2"+
		"\2\37y\3\2\2\2!{\3\2\2\2#}\3\2\2\2%\177\3\2\2\2\'\u0081\3\2\2\2)\u0083"+
		"\3\2\2\2+\u0085\3\2\2\2-\u0088\3\2\2\2/\u008c\3\2\2\2\61\u008e\3\2\2\2"+
		"\63\u0090\3\2\2\2\65\u0097\3\2\2\2\67\u009f\3\2\2\29\u00a2\3\2\2\2;\u00a7"+
		"\3\2\2\2=>\7>\2\2>\4\3\2\2\2?@\7@\2\2@\6\3\2\2\2AB\7.\2\2B\b\3\2\2\2C"+
		"D\7=\2\2D\n\3\2\2\2EJ\5)\25\2FJ\5\'\24\2GJ\5+\26\2HJ\5-\27\2IE\3\2\2\2"+
		"IF\3\2\2\2IG\3\2\2\2IH\3\2\2\2J\f\3\2\2\2KO\5!\21\2LO\5#\22\2MO\5%\23"+
		"\2NK\3\2\2\2NL\3\2\2\2NM\3\2\2\2O\16\3\2\2\2PQ\5\37\20\2Q\20\3\2\2\2R"+
		"W\5\31\r\2ST\7.\2\2TV\5\31\r\2US\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2"+
		"X\22\3\2\2\2YW\3\2\2\2Z[\7v\2\2[\\\7t\2\2\\]\7w\2\2]d\7g\2\2^_\7h\2\2"+
		"_`\7c\2\2`a\7n\2\2ab\7u\2\2bd\7g\2\2cZ\3\2\2\2c^\3\2\2\2d\24\3\2\2\2e"+
		"f\7*\2\2f\26\3\2\2\2gh\7+\2\2h\30\3\2\2\2ik\7A\2\2jl\5\67\34\2kj\3\2\2"+
		"\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\32\3\2\2\2oq\7)\2\2pr\5\67\34\2qp\3"+
		"\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\7)\2\2v\34\3\2\2\2wx"+
		"\7T\2\2x\36\3\2\2\2yz\7W\2\2z \3\2\2\2{|\7I\2\2|\"\3\2\2\2}~\7H\2\2~$"+
		"\3\2\2\2\177\u0080\7Z\2\2\u0080&\3\2\2\2\u0081\u0082\7~\2\2\u0082(\3\2"+
		"\2\2\u0083\u0084\7(\2\2\u0084*\3\2\2\2\u0085\u0086\7/\2\2\u0086\u0087"+
		"\7@\2\2\u0087,\3\2\2\2\u0088\u0089\7>\2\2\u0089\u008a\7/\2\2\u008a\u008b"+
		"\7@\2\2\u008b.\3\2\2\2\u008c\u008d\7?\2\2\u008d\60\3\2\2\2\u008e\u008f"+
		"\7#\2\2\u008f\62\3\2\2\2\u0090\u0091\7G\2\2\u0091\u0092\7z\2\2\u0092\u0093"+
		"\7k\2\2\u0093\u0094\7u\2\2\u0094\u0095\7v\2\2\u0095\u0096\7u\2\2\u0096"+
		"\64\3\2\2\2\u0097\u0098\7H\2\2\u0098\u0099\7q\2\2\u0099\u009a\7t\2\2\u009a"+
		"\u009b\7c\2\2\u009b\u009c\7n\2\2\u009c\u009d\7n\2\2\u009d\66\3\2\2\2\u009e"+
		"\u00a0\t\2\2\2\u009f\u009e\3\2\2\2\u00a08\3\2\2\2\u00a1\u00a3\t\3\2\2"+
		"\u00a2\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5"+
		"\3\2\2\2\u00a5:\3\2\2\2\u00a6\u00a8\t\4\2\2\u00a7\u00a6\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\3\2"+
		"\2\2\u00ab\u00ac\b\36\2\2\u00ac<\3\2\2\2\f\2INWcms\u009f\u00a4\u00a9\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}