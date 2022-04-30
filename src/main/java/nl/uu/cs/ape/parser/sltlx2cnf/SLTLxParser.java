// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.sltlx2cnf;
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
		T__0=1, T__1=2, T__2=3, T__3=4, BIN_CONNECTIVE=5, UN_MODAL=6, BIN_MODAL=7, 
		BOOL=8, LPAREN=9, RPAREN=10, VARIABLE=11, CONSTANT=12, R_REL=13, SLTL_UNTIL=14, 
		SLTL_GLOBALLY=15, SLTL_FINALLY=16, SLTL_NEXT=17, OR=18, AND=19, IMPL=20, 
		EQUIVALENT=21, EQUAL=22, NOT=23, EXISTS=24, FORALL=25, ENDLINE=26, WHITESPACE=27;
	public static final int
		RULE_condition = 0, RULE_formula = 1, RULE_module = 2, RULE_vars = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"condition", "formula", "module", "vars"
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

	public static class ConditionContext extends ParserRuleContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode EOF() { return getToken(SLTLxParser.EOF, 0); }
		public List<TerminalNode> ENDLINE() { return getTokens(SLTLxParser.ENDLINE); }
		public TerminalNode ENDLINE(int i) {
			return getToken(SLTLxParser.ENDLINE, i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_condition);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			formula(0);
			setState(13);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(9);
					match(ENDLINE);
					setState(10);
					formula(0);
					}
					} 
				}
				setState(15);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(19);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDLINE) {
				{
				{
				setState(16);
				match(ENDLINE);
				}
				}
				setState(21);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(22);
			match(EOF);
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

	public static class FormulaContext extends ParserRuleContext {
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
	 
		public FormulaContext() { }
		public void copyFrom(FormulaContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToolRefContext extends FormulaContext {
		public ModuleContext module() {
			return getRuleContext(ModuleContext.class,0);
		}
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public ToolRefContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class UnaryModalContext extends FormulaContext {
		public TerminalNode UN_MODAL() { return getToken(SLTLxParser.UN_MODAL, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public UnaryModalContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class BooleanContext extends FormulaContext {
		public TerminalNode BOOL() { return getToken(SLTLxParser.BOOL, 0); }
		public BooleanContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class NegUnaryContext extends FormulaContext {
		public TerminalNode NOT() { return getToken(SLTLxParser.NOT, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public NegUnaryContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterNegUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitNegUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitNegUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class R_relationContext extends FormulaContext {
		public TerminalNode R_REL() { return getToken(SLTLxParser.R_REL, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public List<TerminalNode> VARIABLE() { return getTokens(SLTLxParser.VARIABLE); }
		public TerminalNode VARIABLE(int i) {
			return getToken(SLTLxParser.VARIABLE, i);
		}
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public R_relationContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterR_relation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitR_relation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitR_relation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryBoolContext extends FormulaContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode BIN_CONNECTIVE() { return getToken(SLTLxParser.BIN_CONNECTIVE, 0); }
		public BinaryBoolContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class ForallContext extends FormulaContext {
		public TerminalNode FORALL() { return getToken(SLTLxParser.FORALL, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public TerminalNode VARIABLE() { return getToken(SLTLxParser.VARIABLE, 0); }
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public ForallContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterForall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitForall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitForall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionContext extends FormulaContext {
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public TerminalNode VARIABLE() { return getToken(SLTLxParser.VARIABLE, 0); }
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public FunctionContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class ExistsContext extends FormulaContext {
		public TerminalNode EXISTS() { return getToken(SLTLxParser.EXISTS, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public TerminalNode VARIABLE() { return getToken(SLTLxParser.VARIABLE, 0); }
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public ExistsContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class BinaryModalContext extends FormulaContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public TerminalNode BIN_MODAL() { return getToken(SLTLxParser.BIN_MODAL, 0); }
		public BinaryModalContext(FormulaContext ctx) { copyFrom(ctx); }
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
	public static class BracketsContext extends FormulaContext {
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public FormulaContext formula() {
			return getRuleContext(FormulaContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public BracketsContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBrackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBrackets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBrackets(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarEqContext extends FormulaContext {
		public List<TerminalNode> VARIABLE() { return getTokens(SLTLxParser.VARIABLE); }
		public TerminalNode VARIABLE(int i) {
			return getToken(SLTLxParser.VARIABLE, i);
		}
		public TerminalNode EQUAL() { return getToken(SLTLxParser.EQUAL, 0); }
		public VarEqContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterVarEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitVarEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitVarEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		return formula(0);
	}

	private FormulaContext formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
		FormulaContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOL:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(25);
				match(BOOL);
				}
				break;
			case LPAREN:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(26);
				match(LPAREN);
				setState(27);
				formula(0);
				setState(28);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new NegUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				match(NOT);
				setState(31);
				formula(9);
				}
				break;
			case FORALL:
				{
				_localctx = new ForallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(32);
				match(FORALL);
				setState(33);
				match(LPAREN);
				setState(34);
				match(VARIABLE);
				setState(35);
				match(RPAREN);
				setState(36);
				formula(8);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExistsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(37);
				match(EXISTS);
				setState(38);
				match(LPAREN);
				setState(39);
				match(VARIABLE);
				setState(40);
				match(RPAREN);
				setState(41);
				formula(7);
				}
				break;
			case UN_MODAL:
				{
				_localctx = new UnaryModalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(UN_MODAL);
				setState(43);
				formula(6);
				}
				break;
			case T__0:
				{
				_localctx = new ToolRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(44);
				match(T__0);
				setState(45);
				module();
				setState(46);
				match(T__1);
				setState(47);
				formula(5);
				}
				break;
			case R_REL:
				{
				_localctx = new R_relationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(49);
				match(R_REL);
				setState(50);
				match(LPAREN);
				setState(51);
				match(VARIABLE);
				setState(52);
				match(T__2);
				setState(53);
				match(VARIABLE);
				setState(54);
				match(RPAREN);
				}
				break;
			case CONSTANT:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				match(CONSTANT);
				setState(56);
				match(LPAREN);
				setState(57);
				match(VARIABLE);
				setState(58);
				match(RPAREN);
				}
				break;
			case VARIABLE:
				{
				_localctx = new VarEqContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(59);
				match(VARIABLE);
				setState(60);
				match(EQUAL);
				setState(61);
				match(VARIABLE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(72);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(70);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryBoolContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(64);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(65);
						match(BIN_CONNECTIVE);
						setState(66);
						formula(11);
						}
						break;
					case 2:
						{
						_localctx = new BinaryModalContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(67);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(68);
						match(BIN_MODAL);
						setState(69);
						formula(5);
						}
						break;
					}
					} 
				}
				setState(74);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
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

	public static class ModuleContext extends ParserRuleContext {
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public List<VarsContext> vars() {
			return getRuleContexts(VarsContext.class);
		}
		public VarsContext vars(int i) {
			return getRuleContext(VarsContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
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
		enterRule(_localctx, 4, RULE_module);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(CONSTANT);
			setState(76);
			match(LPAREN);
			setState(77);
			vars();
			setState(78);
			match(T__3);
			setState(79);
			vars();
			setState(80);
			match(RPAREN);
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

	public static class VarsContext extends ParserRuleContext {
		public List<TerminalNode> VARIABLE() { return getTokens(SLTLxParser.VARIABLE); }
		public TerminalNode VARIABLE(int i) {
			return getToken(SLTLxParser.VARIABLE, i);
		}
		public VarsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterVars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitVars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitVars(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarsContext vars() throws RecognitionException {
		VarsContext _localctx = new VarsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARIABLE) {
				{
				setState(82);
				match(VARIABLE);
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(83);
					match(T__2);
					setState(84);
					match(VARIABLE);
					}
					}
					setState(89);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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
			return formula_sempred((FormulaContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 10);
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\35_\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\7\2\16\n\2\f\2\16\2\21\13\2\3\2\7\2\24"+
		"\n\2\f\2\16\2\27\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3A\n\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\7\3I\n\3\f\3\16\3L\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\7\5X\n\5\f\5\16\5[\13\5\5\5]\n\5\3\5\2\3\4\6\2\4\6\b\2\2\2i\2\n\3\2\2"+
		"\2\4@\3\2\2\2\6M\3\2\2\2\b\\\3\2\2\2\n\17\5\4\3\2\13\f\7\34\2\2\f\16\5"+
		"\4\3\2\r\13\3\2\2\2\16\21\3\2\2\2\17\r\3\2\2\2\17\20\3\2\2\2\20\25\3\2"+
		"\2\2\21\17\3\2\2\2\22\24\7\34\2\2\23\22\3\2\2\2\24\27\3\2\2\2\25\23\3"+
		"\2\2\2\25\26\3\2\2\2\26\30\3\2\2\2\27\25\3\2\2\2\30\31\7\2\2\3\31\3\3"+
		"\2\2\2\32\33\b\3\1\2\33A\7\n\2\2\34\35\7\13\2\2\35\36\5\4\3\2\36\37\7"+
		"\f\2\2\37A\3\2\2\2 !\7\31\2\2!A\5\4\3\13\"#\7\33\2\2#$\7\13\2\2$%\7\r"+
		"\2\2%&\7\f\2\2&A\5\4\3\n\'(\7\32\2\2()\7\13\2\2)*\7\r\2\2*+\7\f\2\2+A"+
		"\5\4\3\t,-\7\b\2\2-A\5\4\3\b./\7\3\2\2/\60\5\6\4\2\60\61\7\4\2\2\61\62"+
		"\5\4\3\7\62A\3\2\2\2\63\64\7\17\2\2\64\65\7\13\2\2\65\66\7\r\2\2\66\67"+
		"\7\5\2\2\678\7\r\2\28A\7\f\2\29:\7\16\2\2:;\7\13\2\2;<\7\r\2\2<A\7\f\2"+
		"\2=>\7\r\2\2>?\7\30\2\2?A\7\r\2\2@\32\3\2\2\2@\34\3\2\2\2@ \3\2\2\2@\""+
		"\3\2\2\2@\'\3\2\2\2@,\3\2\2\2@.\3\2\2\2@\63\3\2\2\2@9\3\2\2\2@=\3\2\2"+
		"\2AJ\3\2\2\2BC\f\f\2\2CD\7\7\2\2DI\5\4\3\rEF\f\6\2\2FG\7\t\2\2GI\5\4\3"+
		"\7HB\3\2\2\2HE\3\2\2\2IL\3\2\2\2JH\3\2\2\2JK\3\2\2\2K\5\3\2\2\2LJ\3\2"+
		"\2\2MN\7\16\2\2NO\7\13\2\2OP\5\b\5\2PQ\7\6\2\2QR\5\b\5\2RS\7\f\2\2S\7"+
		"\3\2\2\2TY\7\r\2\2UV\7\5\2\2VX\7\r\2\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2Y"+
		"Z\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2\\T\3\2\2\2\\]\3\2\2\2]\t\3\2\2\2\t\17\25"+
		"@HJY\\";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}