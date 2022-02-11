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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, BIN_CONNECTIVE=7, UN_MODAL=8, 
		BIN_MODAL=9, BOOL=10, LPAREN=11, RPAREN=12, R_REL=13, SLTL_UNTIL=14, SLTL_GLOBALLY=15, 
		SLTL_FINALLY=16, SLTL_NEXT=17, OR=18, AND=19, IMPL=20, EQUIVALENT=21, 
		EQUAL=22, NOT=23, EXISTS=24, FORALL=25, CHARACTER=26, ENDLINE=27, WHITESPACE=28;
	public static final int
		RULE_condition = 0, RULE_formula = 1, RULE_module = 2, RULE_vars = 3, 
		RULE_variable = 4, RULE_constant = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"condition", "formula", "module", "vars", "variable", "constant"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "'>'", "','", "';'", "'?'", "'''", null, null, null, null, 
			"'('", "')'", "'R'", "'U'", "'G'", "'F'", "'X'", "'|'", "'&'", "'->'", 
			"'<->'", "'='", "'!'", "'Exists'", "'Forall'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "BIN_CONNECTIVE", "UN_MODAL", 
			"BIN_MODAL", "BOOL", "LPAREN", "RPAREN", "R_REL", "SLTL_UNTIL", "SLTL_GLOBALLY", 
			"SLTL_FINALLY", "SLTL_NEXT", "OR", "AND", "IMPL", "EQUIVALENT", "EQUAL", 
			"NOT", "EXISTS", "FORALL", "CHARACTER", "ENDLINE", "WHITESPACE"
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
			setState(12);
			formula(0);
			setState(17);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(13);
					match(ENDLINE);
					setState(14);
					formula(0);
					}
					} 
				}
				setState(19);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(23);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDLINE) {
				{
				{
				setState(20);
				match(ENDLINE);
				}
				}
				setState(25);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(26);
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
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
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
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
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
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
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
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
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
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
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
			setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOL:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(29);
				match(BOOL);
				}
				break;
			case LPAREN:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(30);
				match(LPAREN);
				setState(31);
				formula(0);
				setState(32);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new NegUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(34);
				match(NOT);
				setState(35);
				formula(9);
				}
				break;
			case FORALL:
				{
				_localctx = new ForallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(36);
				match(FORALL);
				setState(37);
				match(LPAREN);
				setState(38);
				variable();
				setState(39);
				match(RPAREN);
				setState(40);
				formula(8);
				}
				break;
			case EXISTS:
				{
				_localctx = new ExistsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(EXISTS);
				setState(43);
				match(LPAREN);
				setState(44);
				variable();
				setState(45);
				match(RPAREN);
				setState(46);
				formula(7);
				}
				break;
			case UN_MODAL:
				{
				_localctx = new UnaryModalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(48);
				match(UN_MODAL);
				setState(49);
				formula(6);
				}
				break;
			case T__0:
				{
				_localctx = new ToolRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(50);
				match(T__0);
				setState(51);
				module();
				setState(52);
				match(T__1);
				setState(53);
				formula(5);
				}
				break;
			case R_REL:
				{
				_localctx = new R_relationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				match(R_REL);
				setState(56);
				match(LPAREN);
				setState(57);
				variable();
				setState(58);
				match(T__2);
				setState(59);
				variable();
				setState(60);
				match(RPAREN);
				}
				break;
			case T__5:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(62);
				constant();
				setState(63);
				match(LPAREN);
				setState(64);
				variable();
				setState(65);
				match(RPAREN);
				}
				break;
			case T__4:
				{
				_localctx = new VarEqContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(67);
				variable();
				setState(68);
				match(EQUAL);
				setState(69);
				variable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(79);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryBoolContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(73);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(74);
						match(BIN_CONNECTIVE);
						setState(75);
						formula(11);
						}
						break;
					case 2:
						{
						_localctx = new BinaryModalContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(76);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(77);
						match(BIN_MODAL);
						setState(78);
						formula(5);
						}
						break;
					}
					} 
				}
				setState(83);
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
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
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
			setState(84);
			constant();
			setState(85);
			match(LPAREN);
			setState(86);
			vars();
			setState(87);
			match(T__3);
			setState(88);
			vars();
			setState(89);
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
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
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
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(91);
				variable();
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(92);
					match(T__2);
					setState(93);
					variable();
					}
					}
					setState(98);
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

	public static class VariableContext extends ParserRuleContext {
		public List<TerminalNode> CHARACTER() { return getTokens(SLTLxParser.CHARACTER); }
		public TerminalNode CHARACTER(int i) {
			return getToken(SLTLxParser.CHARACTER, i);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__4);
			setState(103); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(102);
					match(CHARACTER);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(105); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class ConstantContext extends ParserRuleContext {
		public List<TerminalNode> CHARACTER() { return getTokens(SLTLxParser.CHARACTER); }
		public TerminalNode CHARACTER(int i) {
			return getToken(SLTLxParser.CHARACTER, i);
		}
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(T__5);
			setState(109); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(108);
				match(CHARACTER);
				}
				}
				setState(111); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CHARACTER );
			setState(113);
			match(T__5);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36v\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\7\2\22\n\2\f\2\16\2\25"+
		"\13\2\3\2\7\2\30\n\2\f\2\16\2\33\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\5\3J\n\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3R\n\3\f\3\16\3U\13\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\7\5a\n\5\f\5\16\5d\13\5\5\5f\n\5"+
		"\3\6\3\6\6\6j\n\6\r\6\16\6k\3\7\3\7\6\7p\n\7\r\7\16\7q\3\7\3\7\3\7\2\3"+
		"\4\b\2\4\6\b\n\f\2\2\2\u0080\2\16\3\2\2\2\4I\3\2\2\2\6V\3\2\2\2\be\3\2"+
		"\2\2\ng\3\2\2\2\fm\3\2\2\2\16\23\5\4\3\2\17\20\7\35\2\2\20\22\5\4\3\2"+
		"\21\17\3\2\2\2\22\25\3\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\31\3\2\2\2"+
		"\25\23\3\2\2\2\26\30\7\35\2\2\27\26\3\2\2\2\30\33\3\2\2\2\31\27\3\2\2"+
		"\2\31\32\3\2\2\2\32\34\3\2\2\2\33\31\3\2\2\2\34\35\7\2\2\3\35\3\3\2\2"+
		"\2\36\37\b\3\1\2\37J\7\f\2\2 !\7\r\2\2!\"\5\4\3\2\"#\7\16\2\2#J\3\2\2"+
		"\2$%\7\31\2\2%J\5\4\3\13&\'\7\33\2\2\'(\7\r\2\2()\5\n\6\2)*\7\16\2\2*"+
		"+\5\4\3\n+J\3\2\2\2,-\7\32\2\2-.\7\r\2\2./\5\n\6\2/\60\7\16\2\2\60\61"+
		"\5\4\3\t\61J\3\2\2\2\62\63\7\n\2\2\63J\5\4\3\b\64\65\7\3\2\2\65\66\5\6"+
		"\4\2\66\67\7\4\2\2\678\5\4\3\78J\3\2\2\29:\7\17\2\2:;\7\r\2\2;<\5\n\6"+
		"\2<=\7\5\2\2=>\5\n\6\2>?\7\16\2\2?J\3\2\2\2@A\5\f\7\2AB\7\r\2\2BC\5\n"+
		"\6\2CD\7\16\2\2DJ\3\2\2\2EF\5\n\6\2FG\7\30\2\2GH\5\n\6\2HJ\3\2\2\2I\36"+
		"\3\2\2\2I \3\2\2\2I$\3\2\2\2I&\3\2\2\2I,\3\2\2\2I\62\3\2\2\2I\64\3\2\2"+
		"\2I9\3\2\2\2I@\3\2\2\2IE\3\2\2\2JS\3\2\2\2KL\f\f\2\2LM\7\t\2\2MR\5\4\3"+
		"\rNO\f\6\2\2OP\7\13\2\2PR\5\4\3\7QK\3\2\2\2QN\3\2\2\2RU\3\2\2\2SQ\3\2"+
		"\2\2ST\3\2\2\2T\5\3\2\2\2US\3\2\2\2VW\5\f\7\2WX\7\r\2\2XY\5\b\5\2YZ\7"+
		"\6\2\2Z[\5\b\5\2[\\\7\16\2\2\\\7\3\2\2\2]b\5\n\6\2^_\7\5\2\2_a\5\n\6\2"+
		"`^\3\2\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2cf\3\2\2\2db\3\2\2\2e]\3\2\2\2"+
		"ef\3\2\2\2f\t\3\2\2\2gi\7\7\2\2hj\7\34\2\2ih\3\2\2\2jk\3\2\2\2ki\3\2\2"+
		"\2kl\3\2\2\2l\13\3\2\2\2mo\7\b\2\2np\7\34\2\2on\3\2\2\2pq\3\2\2\2qo\3"+
		"\2\2\2qr\3\2\2\2rs\3\2\2\2st\7\b\2\2t\r\3\2\2\2\13\23\31IQSbekq";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}