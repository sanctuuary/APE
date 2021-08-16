// Generated from SLTLx.g4 by ANTLR 4.9.2
package nl.uu.cs.ape.parser.smtlib2;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SLTLxParser}.
 */
public interface SLTLxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(SLTLxParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(SLTLxParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code toolRef}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitToolRef(SLTLxParser.ToolRefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitUnaryModal(SLTLxParser.UnaryModalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(SLTLxParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code backets}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterBackets(SLTLxParser.BacketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code backets}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitBackets(SLTLxParser.BacketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitBinaryBool(SLTLxParser.BinaryBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitFunction(SLTLxParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryModal}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitBinaryModal(SLTLxParser.BinaryModalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterUnaryBool(SLTLxParser.UnaryBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryBool}
	 * labeled alternative in {@link SLTLxParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitUnaryBool(SLTLxParser.UnaryBoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#exists}.
	 * @param ctx the parse tree
	 */
	void enterExists(SLTLxParser.ExistsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#exists}.
	 * @param ctx the parse tree
	 */
	void exitExists(SLTLxParser.ExistsContext ctx);
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
	 * Enter a parse tree produced by {@link SLTLxParser#atomics}.
	 * @param ctx the parse tree
	 */
	void enterAtomics(SLTLxParser.AtomicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#atomics}.
	 * @param ctx the parse tree
	 */
	void exitAtomics(SLTLxParser.AtomicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SLTLxParser#atomic}.
	 * @param ctx the parse tree
	 */
	void enterAtomic(SLTLxParser.AtomicContext ctx);
	/**
	 * Exit a parse tree produced by {@link SLTLxParser#atomic}.
	 * @param ctx the parse tree
	 */
	void exitAtomic(SLTLxParser.AtomicContext ctx);
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
}