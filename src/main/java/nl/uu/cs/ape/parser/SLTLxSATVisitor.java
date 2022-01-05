package nl.uu.cs.ape.parser;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;

import nl.uu.cs.ape.automaton.TypeStateVar;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.SATAndStatement;
import nl.uu.cs.ape.models.satStruc.SATOrStatement;
import nl.uu.cs.ape.models.satStruc.SATUntil;
import nl.uu.cs.ape.models.satStruc.SATImplicationStatement;
import nl.uu.cs.ape.models.satStruc.SATEquivalenceStatement;
import nl.uu.cs.ape.models.satStruc.SATExists;
import nl.uu.cs.ape.models.satStruc.SATAtom;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.models.satStruc.SATGlobally;
import nl.uu.cs.ape.models.satStruc.SATFinally;
import nl.uu.cs.ape.models.satStruc.SATForall;
import nl.uu.cs.ape.models.satStruc.SATNext;
import nl.uu.cs.ape.models.satStruc.SATNotStatement;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxBaseVisitor;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.*;

public class SLTLxSATVisitor extends SLTLxBaseVisitor<SATFact> {

	static int usedState = 0;
	int memIndexFactor;
	SATAtomMappings mapping;
	private final AllTypes allTypes;
	private final AllModules allModules;
	
	
	
	public SLTLxSATVisitor(SATSynthesisEngine synthesisEngine) {
		super();
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
		// TODO Auto-generated method stub
		return visitChildren(ctx);
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
			/* In case modal operator is not recognised return null. */
			return null;
		}
	}



	@Override
	public SATFact visitBoolean(BooleanContext ctx) {
		if(ctx.getChild(0).getText().equals("'true'")) {
			return SATAtom.builderTrue();
		} else {
			return SATAtom.builderFalse();
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
		TypeStateVar variable = new TypeStateVar(ctx.getChild(2).getText());
		SATFact subFormula = visit(ctx.getChild(4));
		return new SATForall(variable, subFormula);
	}



	@Override
	public SATFact visitFunction(FunctionContext ctx) {
		String typePredicateID = ctx.getChild(0).getText().replace("'", "");
		String variableID = ctx.getChild(2).getText();
		TaxonomyPredicate typePred = allTypes.get(typePredicateID);
		
		return new SATAtom(WorkflowElement.TYPE_VAR, typePred, new TypeStateVar(variableID));
	}



	@Override
	public SATFact visitExists(ExistsContext ctx) {
		TypeStateVar variable = new TypeStateVar(ctx.getChild(2).getText());
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
	public SATFact visitVarEq(VarEqContext ctx) {
		// TODO Auto-generated method stub
		return visitChildren(ctx);
	}



	@Override
	public SATFact visitModule(ModuleContext ctx) {
		// TODO Auto-generated method stub
		return visitChildren(ctx);
	}



	@Override
	public SATFact visitVars(VarsContext ctx) {
		// TODO Auto-generated method stub
		return visitChildren(ctx);
	}

}
