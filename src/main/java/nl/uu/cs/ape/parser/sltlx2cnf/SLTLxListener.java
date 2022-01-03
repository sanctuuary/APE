// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.sltlx2cnf;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SLTLxParser}.
 */
public interface SLTLxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(SLTLxParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(SLTLxParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negUnary}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterNegUnary(SLTLxParser.NegUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negUnary}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitNegUnary(SLTLxParser.NegUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forall}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterForall(SLTLxParser.ForallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forall}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitForall(SLTLxParser.ForallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exists}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterExists(SLTLxParser.ExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exists}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitExists(SLTLxParser.ExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negBinaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterNegBinaryBool(SLTLxParser.NegBinaryBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negBinaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitNegBinaryBool(SLTLxParser.NegBinaryBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterBrackets(SLTLxParser.BracketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitBrackets(SLTLxParser.BracketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varEq}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterVarEq(SLTLxParser.VarEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varEq}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitVarEq(SLTLxParser.VarEqContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#bin_connective}.
	 * @param ctx the parse tree
	 */
	void enterBin_connective(SLTLxParser.Bin_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#bin_connective}.
	 * @param ctx the parse tree
	 */
	void exitBin_connective(SLTLxParser.Bin_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#un_modal}.
	 * @param ctx the parse tree
	 */
	void enterUn_modal(SLTLxParser.Un_modalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#un_modal}.
	 * @param ctx the parse tree
	 */
	void exitUn_modal(SLTLxParser.Un_modalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#bin_modal}.
	 * @param ctx the parse tree
	 */
	void enterBin_modal(SLTLxParser.Bin_modalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#bin_modal}.
	 * @param ctx the parse tree
	 */
	void exitBin_modal(SLTLxParser.Bin_modalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(SLTLxParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(SLTLxParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#vars}.
	 * @param ctx the parse tree
	 */
	void enterVars(SLTLxParser.VarsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#vars}.
	 * @param ctx the parse tree
	 */
	void exitVars(SLTLxParser.VarsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(SLTLxParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(SLTLxParser.BoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#separator}.
	 * @param ctx the parse tree
	 */
	void enterSeparator(SLTLxParser.SeparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#separator}.
	 * @param ctx the parse tree
	 */
	void exitSeparator(SLTLxParser.SeparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(SLTLxParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(SLTLxParser.VariableContext ctx);
}