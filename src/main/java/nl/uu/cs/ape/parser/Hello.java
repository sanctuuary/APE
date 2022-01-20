package nl.uu.cs.ape.parser;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.satStruc.SLTLxFormula;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxLexer;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser;

public class Hello {
	public static void main(String[] args) {

		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString("F (Exists (?x) <'psxy_l'(?x;)> true)"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		ParseTree tree = parser.formula();
//		SLTLxSATVisitor visitor = new SLTLxSATVisitor(null);
////		ParseTreeWalker walker = new ParseTreeWalker();
////		SLTLxWalker listener = new SLTLxWalker();
////		
////		walker.walk(listener, tree);
//		SLTLxFormula res = visitor.visit(tree);

		System.out.println("Done.");
	}

	public static Set<SLTLxFormula> getFact(SATSynthesisEngine synthesisEngine, String formula) {
		Set<SLTLxFormula> facts = new HashSet<>();

		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString(formula));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
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