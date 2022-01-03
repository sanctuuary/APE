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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, LPAREN=8, RPAREN=9, 
		CONSTANT=10, SLTL_R=11, SLTL_UNTIL=12, SLTL_GLOBALLY=13, SLTL_FINALLY=14, 
		SLTL_NEXT=15, OR=16, AND=17, IMPL=18, EQUIVALENT=19, EQUAL=20, NOT=21, 
		EXISTS=22, FORALL=23, CHARACTER=24, ENDLINE=25, WHITESPACE=26;
	public static final int
		RULE_condition = 0, RULE_formula = 1, RULE_bin_connective = 2, RULE_un_modal = 3, 
		RULE_bin_modal = 4, RULE_module = 5, RULE_vars = 6, RULE_bool = 7, RULE_separator = 8, 
		RULE_variable = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"condition", "formula", "bin_connective", "un_modal", "bin_modal", "module", 
			"vars", "bool", "separator", "variable"
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
			setState(20);
			formula(0);
			setState(25);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(21);
					match(ENDLINE);
					setState(22);
					formula(0);
					}
					} 
				}
				setState(27);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ENDLINE) {
				{
				{
				setState(28);
				match(ENDLINE);
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
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
		public Un_modalContext un_modal() {
			return getRuleContext(Un_modalContext.class,0);
		}
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
		public BoolContext bool() {
			return getRuleContext(BoolContext.class,0);
		}
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
	public static class BinaryBoolContext extends FormulaContext {
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public Bin_connectiveContext bin_connective() {
			return getRuleContext(Bin_connectiveContext.class,0);
		}
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
		public TerminalNode SLTL_R() { return getToken(SLTLxParser.SLTL_R, 0); }
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public TerminalNode CONSTANT() { return getToken(SLTLxParser.CONSTANT, 0); }
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
		public Bin_modalContext bin_modal() {
			return getRuleContext(Bin_modalContext.class,0);
		}
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
	public static class NegBinaryBoolContext extends FormulaContext {
		public TerminalNode NOT() { return getToken(SLTLxParser.NOT, 0); }
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public Bin_connectiveContext bin_connective() {
			return getRuleContext(Bin_connectiveContext.class,0);
		}
		public NegBinaryBoolContext(FormulaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterNegBinaryBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitNegBinaryBool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitNegBinaryBool(this);
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
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(37);
				bool();
				}
				break;
			case 2:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(38);
				match(LPAREN);
				setState(39);
				formula(0);
				setState(40);
				match(RPAREN);
				}
				break;
			case 3:
				{
				_localctx = new NegBinaryBoolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(NOT);
				setState(43);
				formula(0);
				setState(44);
				bin_connective();
				setState(45);
				formula(10);
				}
				break;
			case 4:
				{
				_localctx = new NegUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47);
				match(NOT);
				setState(48);
				formula(9);
				}
				break;
			case 5:
				{
				_localctx = new ForallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(49);
				match(FORALL);
				setState(50);
				match(LPAREN);
				setState(51);
				variable();
				setState(52);
				match(RPAREN);
				setState(53);
				formula(8);
				}
				break;
			case 6:
				{
				_localctx = new ExistsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(55);
				match(EXISTS);
				setState(56);
				match(LPAREN);
				setState(57);
				variable();
				setState(58);
				match(RPAREN);
				setState(59);
				formula(7);
				}
				break;
			case 7:
				{
				_localctx = new UnaryModalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(61);
				un_modal();
				setState(62);
				formula(6);
				}
				break;
			case 8:
				{
				_localctx = new ToolRefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(64);
				match(T__0);
				setState(65);
				module();
				setState(66);
				match(T__1);
				setState(67);
				formula(5);
				}
				break;
			case 9:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(69);
				match(SLTL_R);
				setState(70);
				match(LPAREN);
				setState(71);
				variable();
				setState(72);
				match(T__2);
				setState(73);
				variable();
				setState(74);
				match(RPAREN);
				}
				break;
			case 10:
				{
				_localctx = new FunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(76);
				match(CONSTANT);
				setState(77);
				match(LPAREN);
				setState(78);
				variable();
				setState(79);
				match(RPAREN);
				}
				break;
			case 11:
				{
				_localctx = new VarEqContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(81);
				variable();
				setState(82);
				match(EQUAL);
				setState(83);
				variable();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(97);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(95);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryBoolContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(87);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(88);
						bin_connective();
						setState(89);
						formula(12);
						}
						break;
					case 2:
						{
						_localctx = new BinaryModalContext(new FormulaContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_formula);
						setState(91);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(92);
						bin_modal();
						setState(93);
						formula(5);
						}
						break;
					}
					} 
				}
				setState(99);
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

	public static class Bin_connectiveContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(SLTLxParser.AND, 0); }
		public TerminalNode OR() { return getToken(SLTLxParser.OR, 0); }
		public TerminalNode IMPL() { return getToken(SLTLxParser.IMPL, 0); }
		public TerminalNode EQUIVALENT() { return getToken(SLTLxParser.EQUIVALENT, 0); }
		public Bin_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bin_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBin_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBin_connective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBin_connective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bin_connectiveContext bin_connective() throws RecognitionException {
		Bin_connectiveContext _localctx = new Bin_connectiveContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_bin_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << AND) | (1L << IMPL) | (1L << EQUIVALENT))) != 0)) ) {
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

	public static class Un_modalContext extends ParserRuleContext {
		public TerminalNode SLTL_GLOBALLY() { return getToken(SLTLxParser.SLTL_GLOBALLY, 0); }
		public TerminalNode SLTL_FINALLY() { return getToken(SLTLxParser.SLTL_FINALLY, 0); }
		public TerminalNode SLTL_NEXT() { return getToken(SLTLxParser.SLTL_NEXT, 0); }
		public Un_modalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_un_modal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterUn_modal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitUn_modal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitUn_modal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Un_modalContext un_modal() throws RecognitionException {
		Un_modalContext _localctx = new Un_modalContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_un_modal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SLTL_GLOBALLY) | (1L << SLTL_FINALLY) | (1L << SLTL_NEXT))) != 0)) ) {
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

	public static class Bin_modalContext extends ParserRuleContext {
		public TerminalNode SLTL_UNTIL() { return getToken(SLTLxParser.SLTL_UNTIL, 0); }
		public Bin_modalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bin_modal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterBin_modal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitBin_modal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitBin_modal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bin_modalContext bin_modal() throws RecognitionException {
		Bin_modalContext _localctx = new Bin_modalContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_bin_modal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(SLTL_UNTIL);
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
		public TerminalNode LPAREN() { return getToken(SLTLxParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SLTLxParser.RPAREN, 0); }
		public List<VarsContext> vars() {
			return getRuleContexts(VarsContext.class);
		}
		public VarsContext vars(int i) {
			return getRuleContext(VarsContext.class,i);
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
		enterRule(_localctx, 10, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(CONSTANT);
			setState(107);
			match(LPAREN);
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(108);
				vars();
				}
			}

			setState(111);
			match(T__3);
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(112);
				vars();
				}
			}

			setState(115);
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
		enterRule(_localctx, 12, RULE_vars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			variable();
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(118);
				match(T__2);
				setState(119);
				variable();
				}
				}
				setState(124);
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
		enterRule(_localctx, 14, RULE_bool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			_la = _input.LA(1);
			if ( !(_la==T__4 || _la==T__5) ) {
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

	public static class SeparatorContext extends ParserRuleContext {
		public SeparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_separator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).enterSeparator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SLTLxListener ) ((SLTLxListener)listener).exitSeparator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SLTLxVisitor ) return ((SLTLxVisitor<? extends T>)visitor).visitSeparator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SeparatorContext separator() throws RecognitionException {
		SeparatorContext _localctx = new SeparatorContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_separator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(T__2);
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
		enterRule(_localctx, 18, RULE_variable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(T__6);
			setState(131); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(130);
					match(CHARACTER);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(133); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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
			return precpred(_ctx, 11);
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34\u008a\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\3\2\3\2\3\2\7\2\32\n\2\f\2\16\2\35\13\2\3\2\7\2 \n\2\f\2\16\2#\13"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"+
		"X\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3b\n\3\f\3\16\3e\13\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\7\5\7p\n\7\3\7\3\7\5\7t\n\7\3\7\3\7\3\b\3\b"+
		"\3\b\7\b{\n\b\f\b\16\b~\13\b\3\t\3\t\3\n\3\n\3\13\3\13\6\13\u0086\n\13"+
		"\r\13\16\13\u0087\3\13\2\3\4\f\2\4\6\b\n\f\16\20\22\24\2\5\3\2\22\25\3"+
		"\2\17\21\3\2\7\b\2\u0091\2\26\3\2\2\2\4W\3\2\2\2\6f\3\2\2\2\bh\3\2\2\2"+
		"\nj\3\2\2\2\fl\3\2\2\2\16w\3\2\2\2\20\177\3\2\2\2\22\u0081\3\2\2\2\24"+
		"\u0083\3\2\2\2\26\33\5\4\3\2\27\30\7\33\2\2\30\32\5\4\3\2\31\27\3\2\2"+
		"\2\32\35\3\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34!\3\2\2\2\35\33\3\2\2\2"+
		"\36 \7\33\2\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2"+
		"\2#!\3\2\2\2$%\7\2\2\3%\3\3\2\2\2&\'\b\3\1\2\'X\5\20\t\2()\7\n\2\2)*\5"+
		"\4\3\2*+\7\13\2\2+X\3\2\2\2,-\7\27\2\2-.\5\4\3\2./\5\6\4\2/\60\5\4\3\f"+
		"\60X\3\2\2\2\61\62\7\27\2\2\62X\5\4\3\13\63\64\7\31\2\2\64\65\7\n\2\2"+
		"\65\66\5\24\13\2\66\67\7\13\2\2\678\5\4\3\n8X\3\2\2\29:\7\30\2\2:;\7\n"+
		"\2\2;<\5\24\13\2<=\7\13\2\2=>\5\4\3\t>X\3\2\2\2?@\5\b\5\2@A\5\4\3\bAX"+
		"\3\2\2\2BC\7\3\2\2CD\5\f\7\2DE\7\4\2\2EF\5\4\3\7FX\3\2\2\2GH\7\r\2\2H"+
		"I\7\n\2\2IJ\5\24\13\2JK\7\5\2\2KL\5\24\13\2LM\7\13\2\2MX\3\2\2\2NO\7\f"+
		"\2\2OP\7\n\2\2PQ\5\24\13\2QR\7\13\2\2RX\3\2\2\2ST\5\24\13\2TU\7\26\2\2"+
		"UV\5\24\13\2VX\3\2\2\2W&\3\2\2\2W(\3\2\2\2W,\3\2\2\2W\61\3\2\2\2W\63\3"+
		"\2\2\2W9\3\2\2\2W?\3\2\2\2WB\3\2\2\2WG\3\2\2\2WN\3\2\2\2WS\3\2\2\2Xc\3"+
		"\2\2\2YZ\f\r\2\2Z[\5\6\4\2[\\\5\4\3\16\\b\3\2\2\2]^\f\6\2\2^_\5\n\6\2"+
		"_`\5\4\3\7`b\3\2\2\2aY\3\2\2\2a]\3\2\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2"+
		"d\5\3\2\2\2ec\3\2\2\2fg\t\2\2\2g\7\3\2\2\2hi\t\3\2\2i\t\3\2\2\2jk\7\16"+
		"\2\2k\13\3\2\2\2lm\7\f\2\2mo\7\n\2\2np\5\16\b\2on\3\2\2\2op\3\2\2\2pq"+
		"\3\2\2\2qs\7\6\2\2rt\5\16\b\2sr\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\7\13\2\2"+
		"v\r\3\2\2\2w|\5\24\13\2xy\7\5\2\2y{\5\24\13\2zx\3\2\2\2{~\3\2\2\2|z\3"+
		"\2\2\2|}\3\2\2\2}\17\3\2\2\2~|\3\2\2\2\177\u0080\t\4\2\2\u0080\21\3\2"+
		"\2\2\u0081\u0082\7\5\2\2\u0082\23\3\2\2\2\u0083\u0085\7\t\2\2\u0084\u0086"+
		"\7\32\2\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0085\3\2\2\2"+
		"\u0087\u0088\3\2\2\2\u0088\25\3\2\2\2\13\33!Wacos|\u0087";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}