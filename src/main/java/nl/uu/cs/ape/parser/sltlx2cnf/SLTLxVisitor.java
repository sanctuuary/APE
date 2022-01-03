// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.sltlx2cnf;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SLTLxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SLTLxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(SLTLxParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negUnary}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegUnary(SLTLxParser.NegUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forall}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForall(SLTLxParser.ForallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exists}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExists(SLTLxParser.ExistsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negBinaryBool}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegBinaryBool(SLTLxParser.NegBinaryBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrackets(SLTLxParser.BracketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varEq}
	 * labeled alternative in {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarEq(SLTLxParser.VarEqContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#bin_connective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBin_connective(SLTLxParser.Bin_connectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#un_modal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUn_modal(SLTLxParser.Un_modalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#bin_modal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBin_modal(SLTLxParser.Bin_modalContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#module}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule(SLTLxParser.ModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#vars}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVars(SLTLxParser.VarsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(SLTLxParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#separator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeparator(SLTLxParser.SeparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(SLTLxParser.VariableContext ctx);
}