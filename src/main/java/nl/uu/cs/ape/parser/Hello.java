package nl.uu.cs.ape.parser;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.*;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.satStruc.SLTLxFormula;
import nl.uu.cs.ape.models.satStruc.SLTLxParsingBaseErrorListener;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxLexer;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser;

public class Hello {
	public static void main(String[] args) throws ParseCancellationException {

		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString("FK K <<>(Exists (?x) <'psxy_l'(?x;)> true)"));
		lexer.removeErrorListeners();
		lexer.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);

		ParseTree tree = parser.formula();
//		SLTLxSATVisitor visitor = new SLTLxSATVisitor(null);
////		ParseTreeWalker walker = new ParseTreeWalker();
////		SLTLxWalker listener = new SLTLxWalker();
////		
////		walker.walk(listener, tree);
//		SLTLxFormula res = visitor.visit(tree);

		System.out.println("Done.");
	}

	public static Set<SLTLxFormula> getFact(SATSynthesisEngine synthesisEngine, String formula) throws ParseCancellationException {
		Set<SLTLxFormula> facts = new HashSet<>();

		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString(formula));
		lexer.removeErrorListeners();
		lexer.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(SLTLxParsingBaseErrorListener.INSTANCE);

		ParseTree tree = parser.formula();
		SLTLxSATVisitor visitor = new SLTLxSATVisitor(synthesisEngine);
//		ParseTreeWalker walker = new ParseTreeWalker();
//		SLTLxWalker listener = new SLTLxWalker();
//		
//		walker.walk(listener, tree);
		SLTLxFormula res = visitor.visit(tree);
		facts.add(res);

		return facts;
	}
}