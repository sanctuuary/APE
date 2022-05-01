package nl.uu.cs.ape.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import nl.uu.cs.ape.automaton.SLTLxVariable;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtomVar;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxEquivalence;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxExists;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFinally;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxForall;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxGlobally;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxImplication;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegation;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNext;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNextOp;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxOperation;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxParsingAnnotationException;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxParsingBaseErrorListener;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxParsingGrammarException;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxUntil;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxBaseVisitor;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxLexer;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BinaryBoolContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BinaryModalContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BracketsContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ConditionContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ExistsContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ForallContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.FunctionContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ModuleContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.NegUnaryContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.R_relationContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ToolRefContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.TrueContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.UnaryModalContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.VarEqContext;
import nl.uu.cs.ape.utils.APEUtils;

public class SLTLxSATVisitor extends SLTLxBaseVisitor<SLTLxFormula> {

	static int usedState = 0;
	int memIndexFactor;
	private final AllTypes allTypes;
	private final AllModules allModules;
	private String ontologyPrexifURI;
	
	
	
	public SLTLxSATVisitor(SATSynthesisEngine synthesisEngine) {
		super();
		this.ontologyPrexifURI = synthesisEngine.getDomainSetup().getOntologyPrefixIRI();
		this.allTypes = synthesisEngine.getDomainSetup().getAllTypes();
		this.allModules = synthesisEngine.getDomainSetup().getAllModules();
	}
	
	/**
	 * Parse the formulas, where each is separated by a new line, and return the set of {link SLTLxFormula}a that model it.
	 * 
	 * @param synthesisEngine - SAT synthesis engine
	 * @param formulasInSLTLx - SLTLx formulas in textual format (separated by new lines)
	 * @return Set of {link SLTLxFormula} objects, where each represents a row (formula) from the text.
	 * @throws SLTLxParsingGrammarException - Exception is thrown when a formula does not follow the provided grammar rules.
	 * @throws SLTLxParsingAnnotationException - Exception is thrown if the formula follows the given grammar, but cannot be interpreted under the current domain (e.g., used operation does not exist, variable is free, etc.).
	 */
	public static Set<SLTLxFormula> parseFormula(SATSynthesisEngine synthesisEngine, String formulasInSLTLx) throws SLTLxParsingGrammarException, SLTLxParsingAnnotationException {
		Set<SLTLxFormula> facts = new HashSet<>();

		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString(formulasInSLTLx));
		lexer.removeErrorListeners();
		lexer.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);

		ParseTree tree = parser.formula();
		SLTLxSATVisitor visitor = new SLTLxSATVisitor(synthesisEngine);
		SLTLxFormula res = visitor.visit(tree);
		facts.add(res);

		return facts;
	}


	@Override
	public SLTLxFormula visitCondition(ConditionContext ctx) {
		Set<SLTLxFormula> result = new HashSet<SLTLxFormula>();
		int n = ctx.getChildCount();
		for (int i=0; i<n; i++) {
			ParseTree c = ctx.getChild(i);
			SLTLxFormula childResult = c.accept(this);
			result.add(childResult);
		}
		SLTLxConjunction allForulas = new SLTLxConjunction(result);
		
		return allForulas;
	}



	@Override
	public SLTLxFormula visitToolRef(ToolRefContext ctx) {
		SLTLxFormula tool = visit(ctx.getChild(1));
		SLTLxFormula formula = visit(ctx.getChild(3));
		return new SLTLxNextOp(tool, formula);
	}



	@Override
	public SLTLxFormula visitUnaryModal(UnaryModalContext ctx) {
		SLTLxFormula subFormula = visit(ctx.getChild(1));
		if(ctx.getChild(0).getText().equals("G")) {
			return new SLTLxGlobally(subFormula);
		} else if(ctx.getChild(0).getText().equals("F")) {
			return new SLTLxFinally(subFormula);
		} else if(ctx.getChild(0).getText().equals("X")) {
			return new SLTLxNext(subFormula);
		} else {
			System.err.println("Modal operation '"+ ctx.getChild(1).getText() + "' id not recognised.");
			/* In case modal operator is not recognized return null. */
			return null;
		}
	}



	@Override
	public SLTLxFormula visitTrue(TrueContext ctx) {
		if(ctx.getChild(0).getText().equals("true")) {
			return SLTLxAtom.getTrue();
		} else {
			return SLTLxAtom.getFalse();
		}
	}



	@Override
	public SLTLxFormula visitNegUnary(NegUnaryContext ctx) {
		SLTLxFormula subFormula = visit(ctx.getChild(1));
		return new SLTLxNegation(subFormula);
	}



	@Override
	public SLTLxFormula visitBinaryBool(BinaryBoolContext ctx) {
		SLTLxFormula subFormula1 = visit(ctx.getChild(0));
		SLTLxFormula subFormula2 = visit(ctx.getChild(2));
		if(ctx.getChild(1).getText().equals("|")) {
			return new SLTLxDisjunction(subFormula1, subFormula2);
		} else if(ctx.getChild(1).getText().equals("&")) {
			return new SLTLxConjunction(subFormula1, subFormula2);
		} else if(ctx.getChild(1).getText().equals("->")) {
			return new SLTLxImplication(subFormula1, subFormula2);
		} else if(ctx.getChild(1).getText().equals("<->")) {
			return new SLTLxEquivalence(subFormula1, subFormula2);
		} else {
			System.err.println("Binary operation '"+ ctx.getChild(1).getText() + "' is not recognised.");
			/* In case binary operator is not recognised return null. */
			return null;
		}

	}



	@Override
	public SLTLxFormula visitForall(ForallContext ctx) {
		SLTLxVariable variable = new SLTLxVariable(ctx.getChild(2).getText());
		SLTLxFormula subFormula = visit(ctx.getChild(4));
		return new SLTLxForall(variable, subFormula);
	}



	@Override
	public SLTLxFormula visitFunction(FunctionContext ctx) {
		String typePredicateID = ctx.getChild(0).getText().replace("'", "");
		String variableID = ctx.getChild(2).getText();
		
		TaxonomyPredicate typePred = allTypes.get(typePredicateID);
		
		if(typePred == null) {
			String typePredIRI = APEUtils.createClassURI(typePredicateID, this.ontologyPrexifURI);
			typePred = allTypes.get(typePredIRI);
		}
		if(typePred == null) {
			throw SLTLxParsingAnnotationException.typeDoesNoExists("Data type '" + typePredicateID + "' does not exist in the taxonomy.");
		}
		
		return new SLTLxAtomVar(AtomVarType.TYPE_V, typePred, new SLTLxVariable(variableID));
	}



	@Override
	public SLTLxFormula visitExists(ExistsContext ctx) {
		SLTLxVariable variable = new SLTLxVariable(ctx.getChild(2).getText());
		SLTLxFormula subFormula = visit(ctx.getChild(4));
		return new SLTLxExists(variable, subFormula);
	}



	@Override
	public SLTLxFormula visitBinaryModal(BinaryModalContext ctx) {
		SLTLxFormula subFormula1 = visit(ctx.getChild(1));
		SLTLxFormula subFormula2 = visit(ctx.getChild(2));
		return new SLTLxUntil(subFormula1, subFormula2);
	}



	@Override
	public SLTLxFormula visitBrackets(BracketsContext ctx) {
		return visit(ctx.getChild(1));
	}

	@Override
	public SLTLxFormula visitR_relation(R_relationContext ctx) {
		String variableID1 = ctx.getChild(2).getText();
		String variableID2 = ctx.getChild(4).getText();
		
		return new SLTLxAtomVar(AtomVarType.R_RELATION_V, new SLTLxVariable(variableID1), new SLTLxVariable(variableID2));
	}

	@Override
	public SLTLxFormula visitVarEq(VarEqContext ctx) {
		String variableID1 = ctx.getChild(0).getText();
		String variableID2 = ctx.getChild(2).getText();
		
		return new SLTLxAtomVar(AtomVarType.VAR_EQUIVALENCE, new SLTLxVariable(variableID1), new SLTLxVariable(variableID2));
	}



	@Override
	public SLTLxFormula visitModule(ModuleContext ctx) {
		String operationID = ctx.getChild(0).getText().replace("'", "");
		AbstractModule currOperation = allModules.get(operationID);
		if(currOperation == null) {
			String operationIRI = APEUtils.createClassURI(operationID, this.ontologyPrexifURI);
			currOperation = allModules.get(operationIRI);
		}
		if(currOperation == null) {
			throw SLTLxParsingAnnotationException.moduleDoesNoExists("Operation '" + operationID + "' does not exist in the taxonomy/tool annotations.");
		}
		
		List<SLTLxVariable> inputs = new ArrayList<SLTLxVariable>();
	
		ParseTree inputElems = ctx.getChild(2);
		for(int i = 0; i < inputElems.getChildCount(); i = i+2) {
			String variableID = inputElems.getChild(i).getText();
			inputs.add(new SLTLxVariable(variableID));
		}
		
		List<SLTLxVariable> outputs = new ArrayList<SLTLxVariable>();
		
		ParseTree outputElems = ctx.getChild(4);
		for(int i = 0; i < outputElems.getChildCount(); i = i+2) {
			String variableID = outputElems.getChild(i).getText();
			outputs.add(new SLTLxVariable(variableID));
		}
		
		return new SLTLxOperation(currOperation, inputs, outputs);
	}


}
