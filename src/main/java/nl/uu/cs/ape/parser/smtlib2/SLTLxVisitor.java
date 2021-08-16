// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.smtlib2;
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
	 * Visit a parse tree produced by {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(SLTLxParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code backets}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackets(SLTLxParser.BacketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryBool(SLTLxParser.UnaryBoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#exists}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExists(SLTLxParser.ExistsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#module}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule(SLTLxParser.ModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#atomics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomics(SLTLxParser.AtomicsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomic(SLTLxParser.AtomicContext ctx);
	/**
	 * Visit a parse tree produced by {@link SLTLxParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(SLTLxParser.BoolContext ctx);
}