package nl.uu.cs.ape.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.constraints.ConstraintFormatException;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxAtom;
import nl.uu.cs.ape.models.satStruc.SLTLxAtomVar;
import nl.uu.cs.ape.models.satStruc.SLTLxEquivalence;
import nl.uu.cs.ape.models.satStruc.SLTLxExists;
import nl.uu.cs.ape.models.satStruc.SLTLxFormula;
import nl.uu.cs.ape.models.satStruc.SLTLxFinally;
import nl.uu.cs.ape.models.satStruc.SLTLxForall;
import nl.uu.cs.ape.models.satStruc.SLTLxGlobally;
import nl.uu.cs.ape.models.satStruc.SLTLxImplication;
import nl.uu.cs.ape.models.satStruc.SLTLxNext;
import nl.uu.cs.ape.models.satStruc.SLTLxNextOp;
import nl.uu.cs.ape.models.satStruc.SLTLxNegation;
import nl.uu.cs.ape.models.satStruc.SLTLxOperation;
import nl.uu.cs.ape.models.satStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxUntil;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxBaseVisitor;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BinaryBoolContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BinaryModalContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BooleanContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.BracketsContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ConditionContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ExistsContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ForallContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.FunctionContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ModuleContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.NegUnaryContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.R_relationContext;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.ToolRefContext;
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
		this.ontologyPrexifURI = synthesisEngine.getDomainSetup().getOntologyPrefixURI();
		this.allTypes = synthesisEngine.getDomainSetup().getAllTypes();
		this.allModules = synthesisEngine.getDomainSetup().getAllModules();
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
		} else if(ctx.getChild(0).getText().equals("N")) {
			return new SLTLxNext(subFormula);
		} else {
			/* In case modal operator is not recognized return null. */
			return null;
		}
	}



	@Override
	public SLTLxFormula visitBoolean(BooleanContext ctx) {
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
		SLTLxFormula subFormula1 = visit(ctx.getChild(1));
		SLTLxFormula subFormula2 = visit(ctx.getChild(2));
		if(ctx.getChild(0).getText().equals("|")) {
			return new SLTLxDisjunction(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("&")) {
			return new SLTLxConjunction(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("->")) {
			return new SLTLxImplication(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("<->")) {
			return new SLTLxEquivalence(subFormula1, subFormula2);
		} else {
			/* In case binary operator is not recognised return null. */
			return null;
		}

	}



	@Override
	public SLTLxFormula visitForall(ForallContext ctx) {
		SATVariable variable = new SATVariable(ctx.getChild(2).getText());
		SLTLxFormula subFormula = visit(ctx.getChild(4));
		return new SLTLxForall(variable, subFormula);
	}



	@Override
	public SLTLxFormula visitFunction(FunctionContext ctx) {
		String typePredicateID = ctx.getChild(0).getText().replace("'", "");
		String variableID = ctx.getChild(2).getText();
		TaxonomyPredicate typePred = allTypes.get(typePredicateID);
		
		return new SLTLxAtomVar(AtomVarType.TYPE_VAR, typePred, new SATVariable(variableID));
	}



	@Override
	public SLTLxFormula visitExists(ExistsContext ctx) {
		SATVariable variable = new SATVariable(ctx.getChild(2).getText());
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
		
		return new SLTLxAtomVar(AtomVarType.TYPE_DEPENDENCY_VAR, new SATVariable(variableID1), new SATVariable(variableID2));
	}

	@Override
	public SLTLxFormula visitVarEq(VarEqContext ctx) {
		String variableID1 = ctx.getChild(0).getText();
		String variableID2 = ctx.getChild(2).getText();
		
		return new SLTLxAtomVar(AtomVarType.VAR_EQUIVALENCE, new SATVariable(variableID1), new SATVariable(variableID2));
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
			throw ConstraintFormatException.wrongSLTLxOperation("Operation '" + operationID + "' does not exist in the taxonomy/tool annotations.");
		}
		List<SATVariable> inputs = new ArrayList<SATVariable>();
	
		ParseTree inputElems = ctx.getChild(2);
		for(int i = 0; i < inputElems.getChildCount(); i=i+2) {
			String variableID = inputElems.getChild(i).getText();
			inputs.add(new SATVariable(variableID));
		}
		
		List<SATVariable> outputs = new ArrayList<SATVariable>();
		
		ParseTree outputElems = ctx.getChild(4);
		for(int i = 0; i < outputElems.getChildCount(); i=i+2) {
			String variableID = outputElems.getChild(i).getText();
			outputs.add(new SATVariable(variableID));
		}
		
		return new SLTLxOperation(currOperation, inputs, outputs);
	}


}
