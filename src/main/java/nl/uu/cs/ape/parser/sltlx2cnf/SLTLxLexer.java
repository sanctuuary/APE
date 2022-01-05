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
		BOOL=8, LPAREN=9, RPAREN=10, VARIABLE=11, CONSTANT=12, R_REL=13, SLTL_UNTIL=14, 
		SLTL_GLOBALLY=15, SLTL_FINALLY=16, SLTL_NEXT=17, OR=18, AND=19, IMPL=20, 
		EQUIVALENT=21, EQUAL=22, NOT=23, EXISTS=24, FORALL=25, CHARACTER=26, ENDLINE=27, 
		WHITESPACE=28;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"BOOL", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", "SLTL_UNTIL", 
			"SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", 
			"EQUAL", "NOT", "EXISTS", "FORALL", "CHARACTER", "ENDLINE", "WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "','", "';'", null, null, null, null, "'('", "')'", 
			null, null, "'R'", "'U'", "'G'", "'F'", "'X'", "'|'", "'&'", "'->'", 
			"'<->'", "'='", "'!'", "'Exists'", "'Forall'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "BIN_CONNECTIVE", "UN_MODAL", "BIN_MODAL", 
			"BOOL", "LPAREN", "RPAREN", "VARIABLE", "CONSTANT", "R_REL", "SLTL_UNTIL", 
			"SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", 
			"EQUAL", "NOT", "EXISTS", "FORALL", "CHARACTER", "ENDLINE", "WHITESPACE"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u00a3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\5\6H\n\6\3\7\3\7\3\7\5\7M\n\7\3\b\3\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tZ\n\t\3\n\3\n\3\13\3\13\3\f\3\f\6\fb\n"+
		"\f\r\f\16\fc\3\r\3\r\6\rh\n\r\r\r\16\ri\3\r\3\r\3\16\3\16\3\17\3\17\3"+
		"\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26\3"+
		"\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\5\33\u0096\n\33\3\34\6\34\u0099"+
		"\n\34\r\34\16\34\u009a\3\35\6\35\u009e\n\35\r\35\16\35\u009f\3\35\3\35"+
		"\2\2\36\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17"+
		"\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\35"+
		"9\36\3\2\5\5\2\62;C\\c|\4\2\f\f\17\17\4\2\13\13\"\"\2\u00ac\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2"+
		"\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5=\3"+
		"\2\2\2\7?\3\2\2\2\tA\3\2\2\2\13G\3\2\2\2\rL\3\2\2\2\17N\3\2\2\2\21Y\3"+
		"\2\2\2\23[\3\2\2\2\25]\3\2\2\2\27_\3\2\2\2\31e\3\2\2\2\33m\3\2\2\2\35"+
		"o\3\2\2\2\37q\3\2\2\2!s\3\2\2\2#u\3\2\2\2%w\3\2\2\2\'y\3\2\2\2){\3\2\2"+
		"\2+~\3\2\2\2-\u0082\3\2\2\2/\u0084\3\2\2\2\61\u0086\3\2\2\2\63\u008d\3"+
		"\2\2\2\65\u0095\3\2\2\2\67\u0098\3\2\2\29\u009d\3\2\2\2;<\7>\2\2<\4\3"+
		"\2\2\2=>\7@\2\2>\6\3\2\2\2?@\7.\2\2@\b\3\2\2\2AB\7=\2\2B\n\3\2\2\2CH\5"+
		"\'\24\2DH\5%\23\2EH\5)\25\2FH\5+\26\2GC\3\2\2\2GD\3\2\2\2GE\3\2\2\2GF"+
		"\3\2\2\2H\f\3\2\2\2IM\5\37\20\2JM\5!\21\2KM\5#\22\2LI\3\2\2\2LJ\3\2\2"+
		"\2LK\3\2\2\2M\16\3\2\2\2NO\5\35\17\2O\20\3\2\2\2PQ\7v\2\2QR\7t\2\2RS\7"+
		"w\2\2SZ\7g\2\2TU\7h\2\2UV\7c\2\2VW\7n\2\2WX\7u\2\2XZ\7g\2\2YP\3\2\2\2"+
		"YT\3\2\2\2Z\22\3\2\2\2[\\\7*\2\2\\\24\3\2\2\2]^\7+\2\2^\26\3\2\2\2_a\7"+
		"A\2\2`b\5\65\33\2a`\3\2\2\2bc\3\2\2\2ca\3\2\2\2cd\3\2\2\2d\30\3\2\2\2"+
		"eg\7)\2\2fh\5\65\33\2gf\3\2\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2\2\2jk\3\2\2"+
		"\2kl\7)\2\2l\32\3\2\2\2mn\7T\2\2n\34\3\2\2\2op\7W\2\2p\36\3\2\2\2qr\7"+
		"I\2\2r \3\2\2\2st\7H\2\2t\"\3\2\2\2uv\7Z\2\2v$\3\2\2\2wx\7~\2\2x&\3\2"+
		"\2\2yz\7(\2\2z(\3\2\2\2{|\7/\2\2|}\7@\2\2}*\3\2\2\2~\177\7>\2\2\177\u0080"+
		"\7/\2\2\u0080\u0081\7@\2\2\u0081,\3\2\2\2\u0082\u0083\7?\2\2\u0083.\3"+
		"\2\2\2\u0084\u0085\7#\2\2\u0085\60\3\2\2\2\u0086\u0087\7G\2\2\u0087\u0088"+
		"\7z\2\2\u0088\u0089\7k\2\2\u0089\u008a\7u\2\2\u008a\u008b\7v\2\2\u008b"+
		"\u008c\7u\2\2\u008c\62\3\2\2\2\u008d\u008e\7H\2\2\u008e\u008f\7q\2\2\u008f"+
		"\u0090\7t\2\2\u0090\u0091\7c\2\2\u0091\u0092\7n\2\2\u0092\u0093\7n\2\2"+
		"\u0093\64\3\2\2\2\u0094\u0096\t\2\2\2\u0095\u0094\3\2\2\2\u0096\66\3\2"+
		"\2\2\u0097\u0099\t\3\2\2\u0098\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
		"\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b8\3\2\2\2\u009c\u009e\t\4\2\2"+
		"\u009d\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0"+
		"\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\b\35\2\2\u00a2:\3\2\2\2\13\2"+
		"GLYci\u0095\u009a\u009f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}