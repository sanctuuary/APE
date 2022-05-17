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


/**
 * Lexter for SLTLx language.
 * 
 * @author Vedran Kasalica
 *
 */
@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SLTLxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, BIN_CONNECTIVE=5, UN_MODAL=6, BIN_MODAL=7, 
		TRUE=8, LPAREN=9, RPAREN=10, VARIABLE=11, CONSTANT=12, R_REL=13, SLTL_UNTIL=14, 
		SLTL_GLOBALLY=15, SLTL_FINALLY=16, SLTL_NEXT=17, OR=18, AND=19, IMPL=20, 
		EQUIVALENT=21, EQUAL=22, NOT=23, EXISTS=24, FORALL=25, ENDLINE=26, WHITESPACE=27;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"TRUE", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", "SLTL_UNTIL", 
			"SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", 
			"EQUAL", "NOT", "EXISTS", "FORALL", "ENDLINE", "WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "','", "';'", null, null, null, "'true'", "'('", 
			"')'", null, null, "'R'", "'U'", "'G'", "'F'", "'X'", "'|'", "'&'", "'->'", 
			"'<->'", "'='", "'!'", "'Exists'", "'Forall'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"TRUE", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", "SLTL_UNTIL", 
			"SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", 
			"EQUAL", "NOT", "EXISTS", "FORALL", "ENDLINE", "WHITESPACE"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\35\u0098\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\5\6F\n\6\3\7\3\7\3\7\5\7K\n\7\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\13\3\13\3\f\3\f\6\fZ\n\f\r\f\16\f[\3\r\3\r\6\r`\n\r\r\r\16"+
		"\ra\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33"+
		"\6\33\u008e\n\33\r\33\16\33\u008f\3\34\6\34\u0093\n\34\r\34\16\34\u0094"+
		"\3\34\3\34\2\2\35\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65"+
		"\34\67\35\3\2\5\6\2\62;C\\aac|\4\2\f\f\17\17\4\2\13\13\"\"\2\u00a0\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2"+
		"\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2"+
		"\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\39\3\2\2\2\5;\3\2\2"+
		"\2\7=\3\2\2\2\t?\3\2\2\2\13E\3\2\2\2\rJ\3\2\2\2\17L\3\2\2\2\21N\3\2\2"+
		"\2\23S\3\2\2\2\25U\3\2\2\2\27W\3\2\2\2\31]\3\2\2\2\33e\3\2\2\2\35g\3\2"+
		"\2\2\37i\3\2\2\2!k\3\2\2\2#m\3\2\2\2%o\3\2\2\2\'q\3\2\2\2)s\3\2\2\2+v"+
		"\3\2\2\2-z\3\2\2\2/|\3\2\2\2\61~\3\2\2\2\63\u0085\3\2\2\2\65\u008d\3\2"+
		"\2\2\67\u0092\3\2\2\29:\7>\2\2:\4\3\2\2\2;<\7@\2\2<\6\3\2\2\2=>\7.\2\2"+
		">\b\3\2\2\2?@\7=\2\2@\n\3\2\2\2AF\5\'\24\2BF\5%\23\2CF\5)\25\2DF\5+\26"+
		"\2EA\3\2\2\2EB\3\2\2\2EC\3\2\2\2ED\3\2\2\2F\f\3\2\2\2GK\5\37\20\2HK\5"+
		"!\21\2IK\5#\22\2JG\3\2\2\2JH\3\2\2\2JI\3\2\2\2K\16\3\2\2\2LM\5\35\17\2"+
		"M\20\3\2\2\2NO\7v\2\2OP\7t\2\2PQ\7w\2\2QR\7g\2\2R\22\3\2\2\2ST\7*\2\2"+
		"T\24\3\2\2\2UV\7+\2\2V\26\3\2\2\2WY\7A\2\2XZ\t\2\2\2YX\3\2\2\2Z[\3\2\2"+
		"\2[Y\3\2\2\2[\\\3\2\2\2\\\30\3\2\2\2]_\7)\2\2^`\t\2\2\2_^\3\2\2\2`a\3"+
		"\2\2\2a_\3\2\2\2ab\3\2\2\2bc\3\2\2\2cd\7)\2\2d\32\3\2\2\2ef\7T\2\2f\34"+
		"\3\2\2\2gh\7W\2\2h\36\3\2\2\2ij\7I\2\2j \3\2\2\2kl\7H\2\2l\"\3\2\2\2m"+
		"n\7Z\2\2n$\3\2\2\2op\7~\2\2p&\3\2\2\2qr\7(\2\2r(\3\2\2\2st\7/\2\2tu\7"+
		"@\2\2u*\3\2\2\2vw\7>\2\2wx\7/\2\2xy\7@\2\2y,\3\2\2\2z{\7?\2\2{.\3\2\2"+
		"\2|}\7#\2\2}\60\3\2\2\2~\177\7G\2\2\177\u0080\7z\2\2\u0080\u0081\7k\2"+
		"\2\u0081\u0082\7u\2\2\u0082\u0083\7v\2\2\u0083\u0084\7u\2\2\u0084\62\3"+
		"\2\2\2\u0085\u0086\7H\2\2\u0086\u0087\7q\2\2\u0087\u0088\7t\2\2\u0088"+
		"\u0089\7c\2\2\u0089\u008a\7n\2\2\u008a\u008b\7n\2\2\u008b\64\3\2\2\2\u008c"+
		"\u008e\t\3\2\2\u008d\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u008d\3\2"+
		"\2\2\u008f\u0090\3\2\2\2\u0090\66\3\2\2\2\u0091\u0093\t\4\2\2\u0092\u0091"+
		"\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095"+
		"\u0096\3\2\2\2\u0096\u0097\b\34\2\2\u00978\3\2\2\2\t\2EJ[a\u008f\u0094"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}