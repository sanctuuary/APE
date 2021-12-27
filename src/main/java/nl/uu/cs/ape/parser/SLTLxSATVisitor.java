package nl.uu.cs.ape.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.antlr.v4.runtime.tree.TerminalNode;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.satStruc.CNFClause;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Elem;
import nl.uu.cs.ape.models.smtStruc.boolStatements.AndStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BinarySMTPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.OrStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ImplicationStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NotStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BoolVal;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ExistsStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFact;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBoundedVar;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTPredicateFunArg;
import nl.uu.cs.ape.parser.smtlib2.SLTLxBaseVisitor;
import nl.uu.cs.ape.parser.smtlib2.SLTLxParser;
import nl.uu.cs.ape.parser.smtlib2.SLTLxParser.ExistsContext;

class SATBoundedVars implements SATFact {
	List<SATBoundedVars> boundedVars;
	
	public SATBoundedVars(){
		boundedVars = new ArrayList<>();
	}
	
	public boolean addVar(SATBoundedVars var) {
		return this.boundedVars.add(var);
	}

	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

}
public class SLTLxSATVisitor extends SLTLxBaseVisitor<SMTLib2Elem> {

	static int usedState = 0;
	int memIndexFactor;
	SATAtomMappings mapping;
	
	
	
	public SLTLxSATVisitor(int memIndexFactor, SATAtomMappings mapping) {
		super();
		this.memIndexFactor = memIndexFactor;
		this.mapping = mapping;
	}

	@Override
	public SMTLib2Elem visitFormula(SLTLxParser.FormulaContext ctx) {
		System.out.println("formula je:\n");
//		System.out.println("Rule use ->" + ctx.toStringTree());
		
		if(ctx.exists() == null) {
			return visit(ctx.proposition());
		} else {
			List<SMTBoundedVar> boundedVars = visitExists(ctx.exists()).boundedVars;
			
			List<SMTDataType> types = new ArrayList<>();
			boundedVars.forEach(var -> types.add(SMTDataType.MEMORY_TYPE_STATE));
			
			return new ExistsStatement(boundedVars, types, (SMTFact) visit(ctx.proposition()));
		}
	}

	@Override
	public SMTBoundedVars visitExists(SLTLxParser.ExistsContext ctx) {
		SMTBoundedVars vars = new SMTBoundedVars();
		for(TerminalNode var : ctx.VARIABLE()) {
			vars.addVar(new SMTBoundedVar(var.getText()));
		}
		return vars;
	}
		
	@Override
	public BoolVal visitBoolean(SLTLxParser.BooleanContext ctx) {
		return visitBool(ctx.bool());
	}
	
	@Override
	public BoolVal visitBool(SLTLxParser.BoolContext ctx) {
		return new BoolVal(ctx.getText());
	}
	
	//TODO
	@Override
	public SMTLib2Elem visitToolRef(SLTLxParser.ToolRefContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public SMTLib2Elem visitBackets(SLTLxParser.BacketsContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public SMTLib2Elem visitBinaryBool(SLTLxParser.BinaryBoolContext ctx) {
		if(ctx.AND() != null) {
			return new AndStatement(
							ctx.proposition().stream()
							.map(prop 
									-> (SMTFact) visit(prop))
							.collect(Collectors.toList()));
		}
		if(ctx.OR() != null) {
			return new OrStatement(
							ctx.proposition().stream()
							.map(prop 
									-> (SMTFact) visit(prop))
							.collect(Collectors.toList()));
		}
		if(ctx.IMPL() != null) {
			return new ImplicationStatement(
						(SMTFact) visit(ctx.proposition().get(0)),
						(SMTFact) visit(ctx.proposition().get(1)));
		}
			
		return null;
	}

	@Override
	public SMTLib2Elem visitUnaryBool(SLTLxParser.UnaryBoolContext ctx) {
		return new NotStatement(
				(SMTFact) visit(ctx.proposition()));
	}
	//TODO
	@Override
	public SMTLib2Elem visitUnaryModal(SLTLxParser.UnaryModalContext ctx) {
		
		if(ctx.SLTL_FINALLY() != null);
		
		return visitChildren(ctx);
	}
	//TODO
	@Override
	public SMTLib2Elem visitBinaryModal(SLTLxParser.BinaryModalContext ctx) {
		return visitChildren(ctx);
	}
	
	//TODO
	@Override
	public SMTLib2Elem visitFunction(SLTLxParser.FunctionContext ctx) {
//		new BinarySATPredicate(WorkflowElement.MEMORY_TYPE, new SMTPredicateFunArg(null), null)
		return visitChildren(ctx);
	}
	
	//TODO
	@Override
	public SMTLib2Elem visitModule(SLTLxParser.ModuleContext ctx) {
		return visitChildren(ctx);
	}

	
	@Override
	public SMTLib2Elem visitAtomics(SLTLxParser.AtomicsContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public SMTLib2Elem visitAtomic(SLTLxParser.AtomicContext ctx) {
		return visitChildren(ctx);
	}

}
