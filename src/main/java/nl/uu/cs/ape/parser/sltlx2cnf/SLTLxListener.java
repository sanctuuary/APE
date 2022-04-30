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
	 * Enter a parse tree produced by the {@code r_relation}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterR_relation(SLTLxParser.R_relationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code r_relation}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitR_relation(SLTLxParser.R_relationContext ctx);
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
	 * Enter a parse tree produced by the {@code true}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterTrue(SLTLxParser.TrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code true}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitTrue(SLTLxParser.TrueContext ctx);
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
}