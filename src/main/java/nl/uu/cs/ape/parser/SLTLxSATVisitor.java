package nl.uu.cs.ape.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.satStruc.CNFClause;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxBaseVisitor;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser.*;

public class SLTLxSATVisitor extends SLTLxBaseVisitor<Set<SATFact>> {

	static int usedState = 0;
	int memIndexFactor;
	SATAtomMappings mapping;
	
	
	
	public SLTLxSATVisitor(int memIndexFactor, SATAtomMappings mapping) {
		super();
		this.memIndexFactor = memIndexFactor;
		this.mapping = mapping;
	}


	@Override
	public Set<SATFact> visitCondition(ConditionContext ctx) {
		Set<SATFact> result = new HashSet<SATFact>();
		int n = ctx.getChildCount();
		for (int i=0; i<n; i++) {
			if (!shouldVisitNextChild(ctx, result)) {
				break;
			}

			ParseTree c = ctx.getChild(i);
			Set<SATFact> childResult = c.accept(this);
			result.addAll(childResult);
		}

		return result;
	}



	@Override
	public Set<SATFact> visitToolRef(ToolRefContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitUnaryModal(UnaryModalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBoolean(BooleanContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitNegUnary(NegUnaryContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBinaryBool(BinaryBoolContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitForall(ForallContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitFunction(FunctionContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitExists(ExistsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBinaryModal(BinaryModalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitNegBinaryBool(NegBinaryBoolContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBrackets(BracketsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitVarEq(VarEqContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBin_connective(Bin_connectiveContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitUn_modal(Un_modalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBin_modal(Bin_modalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitModule(ModuleContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitVars(VarsContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitBool(BoolContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitSeparator(SeparatorContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Set<SATFact> visitVariable(VariableContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}


}
