// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.smtlib2;
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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, VARIABLE=9, 
		CONSTANT=10, SLTL_UNTIL=11, SLTL_GLOBALLY=12, SLTL_FINALLY=13, SLTL_NEXT=14, 
		OR=15, AND=16, IMPL=17, EQUAL=18, NOT=19, EXISTS=20, CHARACTER=21, ENDLINE=22, 
		WHITESPACE=23;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "VARIABLE", 
			"CONSTANT", "SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", 
			"OR", "AND", "IMPL", "EQUAL", "NOT", "EXISTS", "CHARACTER", "ENDLINE", 
			"WHITESPACE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "'('", "')'", "','", "'|'", "'true'", "'false'", 
			null, null, "'U'", "'G'", "'F'", "'X'", "'\\/'", "'/\\'", "'->'", "'='", 
			"'!'", "'Exists'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "VARIABLE", "CONSTANT", 
			"SLTL_UNTIL", "SLTL_GLOBALLY", "SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", 
			"IMPL", "EQUAL", "NOT", "EXISTS", "CHARACTER", "ENDLINE", "WHITESPACE"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\31\u0082\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\n\3\n\7\nK\n\n\f\n\16\nN\13\n\3\13\3\13\6\13R\n\13"+
		"\r\13\16\13S\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\5\26u\n\26\3\27\6\27x\n\27\r\27\16\27y\3\30\6"+
		"\30}\n\30\r\30\16\30~\3\30\3\30\2\2\31\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\3\2\5\5\2\62;C\\c|\4\2\f\f\17\17\4\2\13\13\"\"\2\u0085\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\3\61\3\2"+
		"\2\2\5\63\3\2\2\2\7\65\3\2\2\2\t\67\3\2\2\2\139\3\2\2\2\r;\3\2\2\2\17"+
		"=\3\2\2\2\21B\3\2\2\2\23H\3\2\2\2\25O\3\2\2\2\27W\3\2\2\2\31Y\3\2\2\2"+
		"\33[\3\2\2\2\35]\3\2\2\2\37_\3\2\2\2!b\3\2\2\2#e\3\2\2\2%h\3\2\2\2\'j"+
		"\3\2\2\2)l\3\2\2\2+t\3\2\2\2-w\3\2\2\2/|\3\2\2\2\61\62\7>\2\2\62\4\3\2"+
		"\2\2\63\64\7@\2\2\64\6\3\2\2\2\65\66\7*\2\2\66\b\3\2\2\2\678\7+\2\28\n"+
		"\3\2\2\29:\7.\2\2:\f\3\2\2\2;<\7~\2\2<\16\3\2\2\2=>\7v\2\2>?\7t\2\2?@"+
		"\7w\2\2@A\7g\2\2A\20\3\2\2\2BC\7h\2\2CD\7c\2\2DE\7n\2\2EF\7u\2\2FG\7g"+
		"\2\2G\22\3\2\2\2HL\7a\2\2IK\5+\26\2JI\3\2\2\2KN\3\2\2\2LJ\3\2\2\2LM\3"+
		"\2\2\2M\24\3\2\2\2NL\3\2\2\2OQ\7)\2\2PR\5+\26\2QP\3\2\2\2RS\3\2\2\2SQ"+
		"\3\2\2\2ST\3\2\2\2TU\3\2\2\2UV\7)\2\2V\26\3\2\2\2WX\7W\2\2X\30\3\2\2\2"+
		"YZ\7I\2\2Z\32\3\2\2\2[\\\7H\2\2\\\34\3\2\2\2]^\7Z\2\2^\36\3\2\2\2_`\7"+
		"^\2\2`a\7\61\2\2a \3\2\2\2bc\7\61\2\2cd\7^\2\2d\"\3\2\2\2ef\7/\2\2fg\7"+
		"@\2\2g$\3\2\2\2hi\7?\2\2i&\3\2\2\2jk\7#\2\2k(\3\2\2\2lm\7G\2\2mn\7z\2"+
		"\2no\7k\2\2op\7u\2\2pq\7v\2\2qr\7u\2\2r*\3\2\2\2su\t\2\2\2ts\3\2\2\2u"+
		",\3\2\2\2vx\t\3\2\2wv\3\2\2\2xy\3\2\2\2yw\3\2\2\2yz\3\2\2\2z.\3\2\2\2"+
		"{}\t\4\2\2|{\3\2\2\2}~\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0080\3\2\2"+
		"\2\u0080\u0081\b\30\2\2\u0081\60\3\2\2\2\b\2LSty~\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}