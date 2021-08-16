// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.smtlib2;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SLTLxParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, VARIABLE=9, 
		CONSTANT=10, SLTL_UNTIL=11, SLTL_GLOBALLY=12, SLTL_FINALLY=13, SLTL_NEXT=14, 
		OR=15, AND=16, IMPL=17, EQUAL=18, NOT=19, EXISTS=20, CHARACTER=21, ENDLINE=22, 
		WHITESPACE=23;
	public static final int
		RULE_formula = 0, RULE_proposition = 1, RULE_exists = 2, RULE_module = 3, 
		RULE_atomics = 4, RULE_atomic = 5, RULE_bool = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"formula", "proposition", "exists", "module", "atomics", "atomic", "bool"
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

	@Override
	public String getGrammarFileName() { return "SLTLx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SLTLxParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FormulaContext extends ParserRuleContext {
		public PropositionContext proposition() {
			return getRuleContext(PropositionContext.class,0);
		}
		public ExistsContext exists() {
			return getRuleContext(ExistsContext.class,0);
		}
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXISTS) {
				{
				setState(14);
				exists();
				}
			}

			setState(17);
			proposition(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropositionContext extends ParserRuleContext {
		public PropositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_proposition; }
	 
		public PropositionContext() { }
		public void copyFrom(PropositionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToolRefContext extends PropositionContext {
		public ModuleContext module() {
			return getRuleContext(ModuleContext.class,0);
		}
		public PropositionContext proposition() {
			return getRuleContext(PropositionContext.class,0);
		}
		public ToolRefContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterToolRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitToolRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitToolRef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryModalContext extends PropositionContext {
		public PropositionContext proposition() {
			return getRuleContext(PropositionContext.class,0);
		}
		public TerminalNode SLTL_GLOBALLY() { return getToken(SLTLxParser.SLTL_GLOBALLY, 0); }
		public TerminalNode SLTL_FINALLY() { return getToken(SLTLxParser.SLTL_FINALLY, 0); }
		public TerminalNode SLTL_NEXT() { return getToken(SLTLxParser.SLTL_NEXT, 0); }
		public UnaryModalContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterUnaryModal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitUnaryModal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitUnaryModal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanContext extends PropositionContext {
		public BoolContext bool() {
			return getRuleContext(BoolContext.class,0);
		}
		public BooleanContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BacketsContext extends PropositionContext {
		public PropositionContext proposition() {
			return getRuleContext(PropositionContext.class,0);
		}
		public BacketsContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBackets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBackets(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryBoolContext extends PropositionContext {
		public List<PropositionContext> proposition() {
			return getRuleContexts(PropositionContext.class);
		}
		public PropositionContext proposition(int i) {
			return getRuleContext(PropositionContext.class,i);
		}
		public TerminalNode AND() { return getToken(SLTLxParser.AND, 0); }
		public TerminalNode OR() { return getToken(SLTLxParser.OR, 0); }
		public TerminalNode IMPL() { return getToken(SLTLxParser.IMPL, 0); }
		public BinaryBoolContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBinaryBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBinaryBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBinaryBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionContext extends PropositionContext {
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
		public AtomicContext atomic() {
			return getRuleContext(AtomicContext.class,0);
		}
		public FunctionContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryModalContext extends PropositionContext {
		public List<PropositionContext> proposition() {
			return getRuleContexts(PropositionContext.class);
		}
		public PropositionContext proposition(int i) {
			return getRuleContext(PropositionContext.class,i);
		}
		public TerminalNode SLTL_UNTIL() { return getToken(SLTLxParser.SLTL_UNTIL, 0); }
		public BinaryModalContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBinaryModal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBinaryModal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBinaryModal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryBoolContext extends PropositionContext {
		public TerminalNode NOT() { return getToken(SLTLxParser.NOT, 0); }
		public PropositionContext proposition() {
			return getRuleContext(PropositionContext.class,0);
		}
		public UnaryBoolContext(PropositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterUnaryBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitUnaryBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitUnaryBool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PropositionContext proposition() throws RecognitionException {
		return proposition(0);
	}

	private PropositionContext proposition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PropositionContext _localctx = new PropositionContext(_ctx, _parentState);
		PropositionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_proposition, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
			case T__7:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(20);
				bool();
				}
				break;
			case T__0:
				{
				_localctx = new ToolRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(21);
				match(T__0);
				setState(22);
				module();
				setState(23);
				match(T__1);
				setState(24);
				proposition(7);
				}
				break;
			case T__2:
				{
				_localctx = new BacketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(26);
				match(T__2);
				setState(27);
				proposition(0);
				setState(28);
				match(T__3);
				}
				break;
			case NOT:
				{
				_localctx = new UnaryBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				match(NOT);
				setState(31);
				proposition(4);
				}
				break;
			case SLTL_GLOBALLY:
			case SLTL_FINALLY:
			case SLTL_NEXT:
				{
				_localctx = new UnaryModalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(32);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SLTL_GLOBALLY) | (1L << SLTL_FINALLY) | (1L << SLTL_NEXT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(33);
				proposition(3);
				}
				break;
			case CONSTANT:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(34);
				match(CONSTANT);
				setState(35);
				match(T__2);
				setState(36);
				atomic();
				setState(37);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(49);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(47);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryBoolContext(new PropositionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_proposition);
						setState(41);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(42);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << AND) | (1L << IMPL))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(43);
						proposition(6);
						}
						break;
					case 2:
						{
						_localctx = new BinaryModalContext(new PropositionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_proposition);
						setState(44);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(45);
						match(SLTL_UNTIL);
						setState(46);
						proposition(3);
						}
						break;
					}
					} 
				}
				setState(51);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExistsContext extends ParserRuleContext {
		public TerminalNode EXISTS() { return getToken(SLTLxParser.EXISTS, 0); }
		public List<TerminalNode> VARIABLE() { return getTokens(SLTLxParser.VARIABLE); }
		public TerminalNode VARIABLE(int i) {
			return getToken(SLTLxParser.VARIABLE, i);
		}
		public ExistsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exists; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterExists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitExists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitExists(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExistsContext exists() throws RecognitionException {
		ExistsContext _localctx = new ExistsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_exists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(EXISTS);
			setState(53);
			match(VARIABLE);
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(54);
				match(T__4);
				setState(55);
				match(VARIABLE);
				}
				}
				setState(60);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleContext extends ParserRuleContext {
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
		public List<AtomicsContext> atomics() {
			return getRuleContexts(AtomicsContext.class);
		}
		public AtomicsContext atomics(int i) {
			return getRuleContext(AtomicsContext.class,i);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitModule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(CONSTANT);
			setState(62);
			match(T__2);
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE || _la==CONSTANT) {
				{
				setState(63);
				atomics();
				}
			}

			setState(66);
			match(T__5);
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE || _la==CONSTANT) {
				{
				setState(67);
				atomics();
				}
			}

			setState(70);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomicsContext extends ParserRuleContext {
		public List<AtomicContext> atomic() {
			return getRuleContexts(AtomicContext.class);
		}
		public AtomicContext atomic(int i) {
			return getRuleContext(AtomicContext.class,i);
		}
		public AtomicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterAtomics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitAtomics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitAtomics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomicsContext atomics() throws RecognitionException {
		AtomicsContext _localctx = new AtomicsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_atomics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			atomic();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(73);
				match(T__4);
				setState(74);
				atomic();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomicContext extends ParserRuleContext {
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
		public TerminalNode VARIABLE() { return getToken(SLTLxParser.VARIABLE, 0); }
		public AtomicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterAtomic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitAtomic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitAtomic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomicContext atomic() throws RecognitionException {
		AtomicContext _localctx = new AtomicContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atomic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_la = _input.LA(1);
			if ( !(_la==VARIABLE || _la==CONSTANT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolContext extends ParserRuleContext {
		public BoolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolContext bool() throws RecognitionException {
		BoolContext _localctx = new BoolContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==T__7) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return proposition_sempred((PropositionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean proposition_sempred(PropositionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31W\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\5\2\22\n\2\3\2\3\2\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\5\3*\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3\62\n\3\f\3\16\3\65\13\3\3"+
		"\4\3\4\3\4\3\4\7\4;\n\4\f\4\16\4>\13\4\3\5\3\5\3\5\5\5C\n\5\3\5\3\5\5"+
		"\5G\n\5\3\5\3\5\3\6\3\6\3\6\7\6N\n\6\f\6\16\6Q\13\6\3\7\3\7\3\b\3\b\3"+
		"\b\2\3\4\t\2\4\6\b\n\f\16\2\6\3\2\16\20\3\2\21\23\3\2\13\f\3\2\t\n\2["+
		"\2\21\3\2\2\2\4)\3\2\2\2\6\66\3\2\2\2\b?\3\2\2\2\nJ\3\2\2\2\fR\3\2\2\2"+
		"\16T\3\2\2\2\20\22\5\6\4\2\21\20\3\2\2\2\21\22\3\2\2\2\22\23\3\2\2\2\23"+
		"\24\5\4\3\2\24\3\3\2\2\2\25\26\b\3\1\2\26*\5\16\b\2\27\30\7\3\2\2\30\31"+
		"\5\b\5\2\31\32\7\4\2\2\32\33\5\4\3\t\33*\3\2\2\2\34\35\7\5\2\2\35\36\5"+
		"\4\3\2\36\37\7\6\2\2\37*\3\2\2\2 !\7\25\2\2!*\5\4\3\6\"#\t\2\2\2#*\5\4"+
		"\3\5$%\7\f\2\2%&\7\5\2\2&\'\5\f\7\2\'(\7\6\2\2(*\3\2\2\2)\25\3\2\2\2)"+
		"\27\3\2\2\2)\34\3\2\2\2) \3\2\2\2)\"\3\2\2\2)$\3\2\2\2*\63\3\2\2\2+,\f"+
		"\7\2\2,-\t\3\2\2-\62\5\4\3\b./\f\4\2\2/\60\7\r\2\2\60\62\5\4\3\5\61+\3"+
		"\2\2\2\61.\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\5\3\2"+
		"\2\2\65\63\3\2\2\2\66\67\7\26\2\2\67<\7\13\2\289\7\7\2\29;\7\13\2\2:8"+
		"\3\2\2\2;>\3\2\2\2<:\3\2\2\2<=\3\2\2\2=\7\3\2\2\2><\3\2\2\2?@\7\f\2\2"+
		"@B\7\5\2\2AC\5\n\6\2BA\3\2\2\2BC\3\2\2\2CD\3\2\2\2DF\7\b\2\2EG\5\n\6\2"+
		"FE\3\2\2\2FG\3\2\2\2GH\3\2\2\2HI\7\6\2\2I\t\3\2\2\2JO\5\f\7\2KL\7\7\2"+
		"\2LN\5\f\7\2MK\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\13\3\2\2\2QO\3\2"+
		"\2\2RS\t\4\2\2S\r\3\2\2\2TU\t\5\2\2U\17\3\2\2\2\n\21)\61\63<BFO";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}