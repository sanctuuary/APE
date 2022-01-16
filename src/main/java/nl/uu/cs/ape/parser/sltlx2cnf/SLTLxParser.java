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
		VARS=8, BOOL=9, LPAREN=10, RPAREN=11, VARIABLE=12, CONSTANT=13, R_REL=14, 
		SLTL_UNTIL=15, SLTL_GLOBALLY=16, SLTL_FINALLY=17, SLTL_NEXT=18, OR=19, 
		AND=20, IMPL=21, EQUIVALENT=22, EQUAL=23, NOT=24, EXISTS=25, FORALL=26, 
		CHARACTER=27, ENDLINE=28, WHITESPACE=29;
	public static final int
		RULE_condition = 0, RULE_formula = 1, RULE_module = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"condition", "formula", "module"
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
			setState(6);
			formula(0);
			setState(11);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(7);
					match(ENDLINE);
					setState(8);
					formula(0);
					}
					} 
				}
				setState(13);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDLINE) {
				{
				{
				setState(14);
				match(ENDLINE);
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(20);
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
			setState(60);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOL:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(23);
				match(BOOL);
				}
				break;
			case LPAREN:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(24);
				match(LPAREN);
				setState(25);
				formula(0);
				setState(26);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new NegUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(28);
				match(NOT);
				setState(29);
				formula(9);
				}
				break;
			case FORALL:
				{
				_localctx = new ForallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				match(FORALL);
				setState(31);
				match(LPAREN);
				setState(32);
				match(VARIABLE);
				setState(33);
				match(RPAREN);
				setState(34);
				formula(8);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExistsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(35);
				match(EXISTS);
				setState(36);
				match(LPAREN);
				setState(37);
				match(VARIABLE);
				setState(38);
				match(RPAREN);
				setState(39);
				formula(7);
				}
				break;
			case UN_MODAL:
				{
				_localctx = new UnaryModalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(40);
				match(UN_MODAL);
				setState(41);
				formula(6);
				}
				break;
			case T__0:
				{
				_localctx = new ToolRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(T__0);
				setState(43);
				module();
				setState(44);
				match(T__1);
				setState(45);
				formula(5);
				}
				break;
			case R_REL:
				{
				_localctx = new R_relationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47);
				match(R_REL);
				setState(48);
				match(LPAREN);
				setState(49);
				match(VARIABLE);
				setState(50);
				match(T__2);
				setState(51);
				match(VARIABLE);
				setState(52);
				match(RPAREN);
				}
				break;
			case CONSTANT:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(53);
				match(CONSTANT);
				setState(54);
				match(LPAREN);
				setState(55);
				match(VARIABLE);
				setState(56);
				match(RPAREN);
				}
				break;
			case VARIABLE:
				{
				_localctx = new VarEqContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(57);
				match(VARIABLE);
				setState(58);
				match(EQUAL);
				setState(59);
				match(VARIABLE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(70);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(68);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryBoolContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(62);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(63);
						match(BIN_CONNECTIVE);
						setState(64);
						formula(11);
						}
						break;
					case 2:
						{
						_localctx = new BinaryModalContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(65);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(66);
						match(BIN_MODAL);
						setState(67);
						formula(5);
						}
						break;
					}
					} 
				}
				setState(72);
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
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public List<TerminalNode> VARS() { return getTokens(SLTLxParser.VARS); }
		public TerminalNode VARS(int i) {
			return getToken(SLTLxParser.VARS, i);
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
		enterRule(_localctx, 4, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(CONSTANT);
			setState(74);
			match(LPAREN);
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARS) {
				{
				setState(75);
				match(VARS);
				}
			}

			setState(78);
			match(T__3);
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VARS) {
				{
				setState(79);
				match(VARS);
				}
			}

			setState(82);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\37W\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\7\2\f\n\2\f\2\16\2\17\13\2\3\2\7\2\22\n\2\f\2\16"+
		"\2\25\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3?\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3G\n"+
		"\3\f\3\16\3J\13\3\3\4\3\4\3\4\5\4O\n\4\3\4\3\4\5\4S\n\4\3\4\3\4\3\4\2"+
		"\3\4\5\2\4\6\2\2\2b\2\b\3\2\2\2\4>\3\2\2\2\6K\3\2\2\2\b\r\5\4\3\2\t\n"+
		"\7\36\2\2\n\f\5\4\3\2\13\t\3\2\2\2\f\17\3\2\2\2\r\13\3\2\2\2\r\16\3\2"+
		"\2\2\16\23\3\2\2\2\17\r\3\2\2\2\20\22\7\36\2\2\21\20\3\2\2\2\22\25\3\2"+
		"\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\26\3\2\2\2\25\23\3\2\2\2\26\27\7\2"+
		"\2\3\27\3\3\2\2\2\30\31\b\3\1\2\31?\7\13\2\2\32\33\7\f\2\2\33\34\5\4\3"+
		"\2\34\35\7\r\2\2\35?\3\2\2\2\36\37\7\32\2\2\37?\5\4\3\13 !\7\34\2\2!\""+
		"\7\f\2\2\"#\7\16\2\2#$\7\r\2\2$?\5\4\3\n%&\7\33\2\2&\'\7\f\2\2\'(\7\16"+
		"\2\2()\7\r\2\2)?\5\4\3\t*+\7\b\2\2+?\5\4\3\b,-\7\3\2\2-.\5\6\4\2./\7\4"+
		"\2\2/\60\5\4\3\7\60?\3\2\2\2\61\62\7\20\2\2\62\63\7\f\2\2\63\64\7\16\2"+
		"\2\64\65\7\5\2\2\65\66\7\16\2\2\66?\7\r\2\2\678\7\17\2\289\7\f\2\29:\7"+
		"\16\2\2:?\7\r\2\2;<\7\16\2\2<=\7\31\2\2=?\7\16\2\2>\30\3\2\2\2>\32\3\2"+
		"\2\2>\36\3\2\2\2> \3\2\2\2>%\3\2\2\2>*\3\2\2\2>,\3\2\2\2>\61\3\2\2\2>"+
		"\67\3\2\2\2>;\3\2\2\2?H\3\2\2\2@A\f\f\2\2AB\7\7\2\2BG\5\4\3\rCD\f\6\2"+
		"\2DE\7\t\2\2EG\5\4\3\7F@\3\2\2\2FC\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2"+
		"\2I\5\3\2\2\2JH\3\2\2\2KL\7\17\2\2LN\7\f\2\2MO\7\n\2\2NM\3\2\2\2NO\3\2"+
		"\2\2OP\3\2\2\2PR\7\6\2\2QS\7\n\2\2RQ\3\2\2\2RS\3\2\2\2ST\3\2\2\2TU\7\r"+
		"\2\2U\7\3\2\2\2\t\r\23>FHNR";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}