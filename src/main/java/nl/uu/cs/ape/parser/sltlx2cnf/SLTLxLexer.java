// Generated from SLTLx.g4 by ANTLR 4.10.1
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
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

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
		"\u0004\u0000\u001b\u0096\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004D\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005"+
		"I\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0004\nX\b\n\u000b\n\f\nY\u0001\u000b\u0001\u000b\u0004\u000b^\b\u000b"+
		"\u000b\u000b\f\u000b_\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r"+
		"\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0019\u0004\u0019\u008c\b\u0019\u000b\u0019\f\u0019\u008d\u0001\u001a"+
		"\u0004\u001a\u0091\b\u001a\u000b\u001a\f\u001a\u0092\u0001\u001a\u0001"+
		"\u001a\u0000\u0000\u001b\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004"+
		"\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017"+
		"\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'"+
		"\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a5\u001b\u0001\u0000\u0003"+
		"\u0004\u000009AZ__az\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u009e\u0000"+
		"\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000"+
		"\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/"+
		"\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000"+
		"\u0000\u0000\u00005\u0001\u0000\u0000\u0000\u00017\u0001\u0000\u0000\u0000"+
		"\u00039\u0001\u0000\u0000\u0000\u0005;\u0001\u0000\u0000\u0000\u0007="+
		"\u0001\u0000\u0000\u0000\tC\u0001\u0000\u0000\u0000\u000bH\u0001\u0000"+
		"\u0000\u0000\rJ\u0001\u0000\u0000\u0000\u000fL\u0001\u0000\u0000\u0000"+
		"\u0011Q\u0001\u0000\u0000\u0000\u0013S\u0001\u0000\u0000\u0000\u0015U"+
		"\u0001\u0000\u0000\u0000\u0017[\u0001\u0000\u0000\u0000\u0019c\u0001\u0000"+
		"\u0000\u0000\u001be\u0001\u0000\u0000\u0000\u001dg\u0001\u0000\u0000\u0000"+
		"\u001fi\u0001\u0000\u0000\u0000!k\u0001\u0000\u0000\u0000#m\u0001\u0000"+
		"\u0000\u0000%o\u0001\u0000\u0000\u0000\'q\u0001\u0000\u0000\u0000)t\u0001"+
		"\u0000\u0000\u0000+x\u0001\u0000\u0000\u0000-z\u0001\u0000\u0000\u0000"+
		"/|\u0001\u0000\u0000\u00001\u0083\u0001\u0000\u0000\u00003\u008b\u0001"+
		"\u0000\u0000\u00005\u0090\u0001\u0000\u0000\u000078\u0005<\u0000\u0000"+
		"8\u0002\u0001\u0000\u0000\u00009:\u0005>\u0000\u0000:\u0004\u0001\u0000"+
		"\u0000\u0000;<\u0005,\u0000\u0000<\u0006\u0001\u0000\u0000\u0000=>\u0005"+
		";\u0000\u0000>\b\u0001\u0000\u0000\u0000?D\u0003%\u0012\u0000@D\u0003"+
		"#\u0011\u0000AD\u0003\'\u0013\u0000BD\u0003)\u0014\u0000C?\u0001\u0000"+
		"\u0000\u0000C@\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CB\u0001"+
		"\u0000\u0000\u0000D\n\u0001\u0000\u0000\u0000EI\u0003\u001d\u000e\u0000"+
		"FI\u0003\u001f\u000f\u0000GI\u0003!\u0010\u0000HE\u0001\u0000\u0000\u0000"+
		"HF\u0001\u0000\u0000\u0000HG\u0001\u0000\u0000\u0000I\f\u0001\u0000\u0000"+
		"\u0000JK\u0003\u001b\r\u0000K\u000e\u0001\u0000\u0000\u0000LM\u0005t\u0000"+
		"\u0000MN\u0005r\u0000\u0000NO\u0005u\u0000\u0000OP\u0005e\u0000\u0000"+
		"P\u0010\u0001\u0000\u0000\u0000QR\u0005(\u0000\u0000R\u0012\u0001\u0000"+
		"\u0000\u0000ST\u0005)\u0000\u0000T\u0014\u0001\u0000\u0000\u0000UW\u0005"+
		"?\u0000\u0000VX\u0007\u0000\u0000\u0000WV\u0001\u0000\u0000\u0000XY\u0001"+
		"\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000"+
		"Z\u0016\u0001\u0000\u0000\u0000[]\u0005\'\u0000\u0000\\^\u0007\u0000\u0000"+
		"\u0000]\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_]\u0001\u0000"+
		"\u0000\u0000_`\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000ab\u0005"+
		"\'\u0000\u0000b\u0018\u0001\u0000\u0000\u0000cd\u0005R\u0000\u0000d\u001a"+
		"\u0001\u0000\u0000\u0000ef\u0005U\u0000\u0000f\u001c\u0001\u0000\u0000"+
		"\u0000gh\u0005G\u0000\u0000h\u001e\u0001\u0000\u0000\u0000ij\u0005F\u0000"+
		"\u0000j \u0001\u0000\u0000\u0000kl\u0005X\u0000\u0000l\"\u0001\u0000\u0000"+
		"\u0000mn\u0005|\u0000\u0000n$\u0001\u0000\u0000\u0000op\u0005&\u0000\u0000"+
		"p&\u0001\u0000\u0000\u0000qr\u0005-\u0000\u0000rs\u0005>\u0000\u0000s"+
		"(\u0001\u0000\u0000\u0000tu\u0005<\u0000\u0000uv\u0005-\u0000\u0000vw"+
		"\u0005>\u0000\u0000w*\u0001\u0000\u0000\u0000xy\u0005=\u0000\u0000y,\u0001"+
		"\u0000\u0000\u0000z{\u0005!\u0000\u0000{.\u0001\u0000\u0000\u0000|}\u0005"+
		"E\u0000\u0000}~\u0005x\u0000\u0000~\u007f\u0005i\u0000\u0000\u007f\u0080"+
		"\u0005s\u0000\u0000\u0080\u0081\u0005t\u0000\u0000\u0081\u0082\u0005s"+
		"\u0000\u0000\u00820\u0001\u0000\u0000\u0000\u0083\u0084\u0005F\u0000\u0000"+
		"\u0084\u0085\u0005o\u0000\u0000\u0085\u0086\u0005r\u0000\u0000\u0086\u0087"+
		"\u0005a\u0000\u0000\u0087\u0088\u0005l\u0000\u0000\u0088\u0089\u0005l"+
		"\u0000\u0000\u00892\u0001\u0000\u0000\u0000\u008a\u008c\u0007\u0001\u0000"+
		"\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000"+
		"\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000"+
		"\u0000\u008e4\u0001\u0000\u0000\u0000\u008f\u0091\u0007\u0002\u0000\u0000"+
		"\u0090\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000"+
		"\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0006\u001a\u0000\u0000"+
		"\u00956\u0001\u0000\u0000\u0000\u0007\u0000CHY_\u008d\u0092\u0001\u0006"+
		"\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}