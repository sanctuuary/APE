package nl.uu.cs.ape.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Elem;
import nl.uu.cs.ape.parser.smtlib2.SLTLxLexer;
import nl.uu.cs.ape.parser.smtlib2.SLTLxParser;

public class Hello {
	public static void main(String[] args) throws Exception {

		
		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString("Exists _a, _b true \\/ false "));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		ParseTree tree = parser.formula();
		SLTLxSATVisitor visitor = new SLTLxSATVisitor(0, new SATAtomMappings());
		ParseTreeWalker walker = new ParseTreeWalker();
//		SLTLxWalker listener = new SLTLxWalker();
//		
//		walker.walk(listener, tree);
//		SMTLib2Elem res = visitor.visit(tree);

		
		System.out.println("Done.");
	}
}