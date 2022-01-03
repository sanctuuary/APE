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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, LPAREN=8, RPAREN=9, 
		CONSTANT=10, SLTL_R=11, SLTL_UNTIL=12, SLTL_GLOBALLY=13, SLTL_FINALLY=14, 
		SLTL_NEXT=15, OR=16, AND=17, IMPL=18, EQUIVALENT=19, EQUAL=20, NOT=21, 
		EXISTS=22, FORALL=23, CHARACTER=24, ENDLINE=25, WHITESPACE=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "LPAREN", "RPAREN", 
			"CONSTANT", "SLTL_R", "SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", 
			"SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", "EQUAL", "NOT", "EXISTS", 
			"FORALL", "CHARACTER", "ENDLINE", "WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "','", "';'", "'true'", "'false'", "'?'", "'('", 
			"')'", null, "'R'", "'U'", "'G'", "'F'", "'X'", "'|'", "'&'", "'->'", 
			"'<=-'", "'='", "'!'", "'Exists'", "'Forall'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "LPAREN", "RPAREN", "CONSTANT", 
			"SLTL_R", "SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", 
			"OR", "AND", "IMPL", "EQUIVALENT", "EQUAL", "NOT", "EXISTS", "FORALL", 
			"CHARACTER", "ENDLINE", "WHITESPACE"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u008c\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\6\13"+
		"S\n\13\r\13\16\13T\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21"+
		"\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\31\5\31\177\n\31\3\32\6\32\u0082\n\32\r\32\16\32\u0083\3\33\6\33\u0087"+
		"\n\33\r\33\16\33\u0088\3\33\3\33\2\2\34\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\65\34\3\2\5\5\2\62;C\\c|\4\2\f\f\17\17\4\2\13\13"+
		"\"\"\2\u008e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2"+
		"\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2"+
		"\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3\67\3\2\2\2\59\3"+
		"\2\2\2\7;\3\2\2\2\t=\3\2\2\2\13?\3\2\2\2\rD\3\2\2\2\17J\3\2\2\2\21L\3"+
		"\2\2\2\23N\3\2\2\2\25P\3\2\2\2\27V\3\2\2\2\31X\3\2\2\2\33Z\3\2\2\2\35"+
		"\\\3\2\2\2\37^\3\2\2\2!`\3\2\2\2#b\3\2\2\2%d\3\2\2\2\'g\3\2\2\2)k\3\2"+
		"\2\2+m\3\2\2\2-o\3\2\2\2/v\3\2\2\2\61~\3\2\2\2\63\u0081\3\2\2\2\65\u0086"+
		"\3\2\2\2\678\7>\2\28\4\3\2\2\29:\7@\2\2:\6\3\2\2\2;<\7.\2\2<\b\3\2\2\2"+
		"=>\7=\2\2>\n\3\2\2\2?@\7v\2\2@A\7t\2\2AB\7w\2\2BC\7g\2\2C\f\3\2\2\2DE"+
		"\7h\2\2EF\7c\2\2FG\7n\2\2GH\7u\2\2HI\7g\2\2I\16\3\2\2\2JK\7A\2\2K\20\3"+
		"\2\2\2LM\7*\2\2M\22\3\2\2\2NO\7+\2\2O\24\3\2\2\2PR\7a\2\2QS\5\61\31\2"+
		"RQ\3\2\2\2ST\3\2\2\2TR\3\2\2\2TU\3\2\2\2U\26\3\2\2\2VW\7T\2\2W\30\3\2"+
		"\2\2XY\7W\2\2Y\32\3\2\2\2Z[\7I\2\2[\34\3\2\2\2\\]\7H\2\2]\36\3\2\2\2^"+
		"_\7Z\2\2_ \3\2\2\2`a\7~\2\2a\"\3\2\2\2bc\7(\2\2c$\3\2\2\2de\7/\2\2ef\7"+
		"@\2\2f&\3\2\2\2gh\7>\2\2hi\7?\2\2ij\7/\2\2j(\3\2\2\2kl\7?\2\2l*\3\2\2"+
		"\2mn\7#\2\2n,\3\2\2\2op\7G\2\2pq\7z\2\2qr\7k\2\2rs\7u\2\2st\7v\2\2tu\7"+
		"u\2\2u.\3\2\2\2vw\7H\2\2wx\7q\2\2xy\7t\2\2yz\7c\2\2z{\7n\2\2{|\7n\2\2"+
		"|\60\3\2\2\2}\177\t\2\2\2~}\3\2\2\2\177\62\3\2\2\2\u0080\u0082\t\3\2\2"+
		"\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084"+
		"\3\2\2\2\u0084\64\3\2\2\2\u0085\u0087\t\4\2\2\u0086\u0085\3\2\2\2\u0087"+
		"\u0088\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008a\3\2"+
		"\2\2\u008a\u008b\b\33\2\2\u008b\66\3\2\2\2\7\2T~\u0083\u0088\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}