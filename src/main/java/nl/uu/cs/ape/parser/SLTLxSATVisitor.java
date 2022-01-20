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
import nl.uu.cs.ape.models.satStruc.SATAndStatement;
import nl.uu.cs.ape.models.satStruc.SATAtom;
import nl.uu.cs.ape.models.satStruc.SATAtomVar;
import nl.uu.cs.ape.models.satStruc.SATEquivalenceStatement;
import nl.uu.cs.ape.models.satStruc.SATExists;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.models.satStruc.SATFinally;
import nl.uu.cs.ape.models.satStruc.SATForall;
import nl.uu.cs.ape.models.satStruc.SATGlobally;
import nl.uu.cs.ape.models.satStruc.SATImplicationStatement;
import nl.uu.cs.ape.models.satStruc.SATNext;
import nl.uu.cs.ape.models.satStruc.SATNextOp;
import nl.uu.cs.ape.models.satStruc.SATNotStatement;
import nl.uu.cs.ape.models.satStruc.SATOperation;
import nl.uu.cs.ape.models.satStruc.SATOrStatement;
import nl.uu.cs.ape.models.satStruc.SATUntil;
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

public class SLTLxSATVisitor extends SLTLxBaseVisitor<SATFact> {

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
	public SATFact visitCondition(ConditionContext ctx) {
		Set<SATFact> result = new HashSet<SATFact>();
		int n = ctx.getChildCount();
		for (int i=0; i<n; i++) {
			ParseTree c = ctx.getChild(i);
			SATFact childResult = c.accept(this);
			result.add(childResult);
		}
		SATAndStatement allForulas = new SATAndStatement(result);
		
		return allForulas;
	}



	@Override
	public SATFact visitToolRef(ToolRefContext ctx) {
		SATFact tool = visit(ctx.getChild(1));
		SATFact formula = visit(ctx.getChild(3));
		return new SATNextOp(tool, formula);
	}



	@Override
	public SATFact visitUnaryModal(UnaryModalContext ctx) {
		SATFact subFormula = visit(ctx.getChild(1));
		if(ctx.getChild(0).getText().equals("G")) {
			return new SATGlobally(subFormula);
		} else if(ctx.getChild(0).getText().equals("F")) {
			return new SATFinally(subFormula);
		} else if(ctx.getChild(0).getText().equals("N")) {
			return new SATNext(subFormula);
		} else {
			/* In case modal operator is not recognized return null. */
			return null;
		}
	}



	@Override
	public SATFact visitBoolean(BooleanContext ctx) {
		if(ctx.getChild(0).getText().equals("true")) {
			return SATAtom.getTrue();
		} else {
			return SATAtom.getFalse();
		}
	}



	@Override
	public SATFact visitNegUnary(NegUnaryContext ctx) {
		SATFact subFormula = visit(ctx.getChild(1));
		return new SATNotStatement(subFormula);
	}



	@Override
	public SATFact visitBinaryBool(BinaryBoolContext ctx) {
		SATFact subFormula1 = visit(ctx.getChild(1));
		SATFact subFormula2 = visit(ctx.getChild(2));
		if(ctx.getChild(0).getText().equals("|")) {
			return new SATOrStatement(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("&")) {
			return new SATAndStatement(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("->")) {
			return new SATImplicationStatement(subFormula1, subFormula2);
		} else if(ctx.getChild(0).getText().equals("<->")) {
			return new SATEquivalenceStatement(subFormula1, subFormula2);
		} else {
			/* In case binary operator is not recognised return null. */
			return null;
		}

	}



	@Override
	public SATFact visitForall(ForallContext ctx) {
		SATVariable variable = new SATVariable(ctx.getChild(2).getText());
		SATFact subFormula = visit(ctx.getChild(4));
		return new SATForall(variable, subFormula);
	}



	@Override
	public SATFact visitFunction(FunctionContext ctx) {
		String typePredicateID = ctx.getChild(0).getText().replace("'", "");
		String variableID = ctx.getChild(2).getText();
		TaxonomyPredicate typePred = allTypes.get(typePredicateID);
		
		return new SATAtomVar(AtomVarType.TYPE_VAR, typePred, new SATVariable(variableID));
	}



	@Override
	public SATFact visitExists(ExistsContext ctx) {
		SATVariable variable = new SATVariable(ctx.getChild(2).getText());
		SATFact subFormula = visit(ctx.getChild(4));
		return new SATExists(variable, subFormula);
	}



	@Override
	public SATFact visitBinaryModal(BinaryModalContext ctx) {
		SATFact subFormula1 = visit(ctx.getChild(1));
		SATFact subFormula2 = visit(ctx.getChild(2));
		return new SATUntil(subFormula1, subFormula2);
	}



	@Override
	public SATFact visitBrackets(BracketsContext ctx) {
		return visit(ctx.getChild(1));
	}

	@Override
	public SATFact visitR_relation(R_relationContext ctx) {
		String variableID1 = ctx.getChild(2).getText();
		String variableID2 = ctx.getChild(4).getText();
		
		return new SATAtomVar(AtomVarType.TYPE_DEPENDENCY_VAR, new SATVariable(variableID1), new SATVariable(variableID2));
	}

	@Override
	public SATFact visitVarEq(VarEqContext ctx) {
		String variableID1 = ctx.getChild(0).getText();
		String variableID2 = ctx.getChild(2).getText();
		
		return new SATAtomVar(AtomVarType.VAR_EQUIVALENCE, new SATVariable(variableID1), new SATVariable(variableID2));
	}



	@Override
	public SATFact visitModule(ModuleContext ctx) {
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
		
		return new SATOperation(currOperation, inputs, outputs);
	}


}
