package nl.uu.cs.ape.parser;

import java.util.Set;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxLexer;
import nl.uu.cs.ape.parser.sltlx2cnf.SLTLxParser;

public class Hello {
	public static void main(String[] args) throws Exception {

		
		SLTLxLexer lexer = new SLTLxLexer(CharStreams.fromString("Exists _a, _b true \\/ false "));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SLTLxParser parser = new SLTLxParser(tokens);
		ParseTree tree = parser.formula();
		SLTLxSATVisitor visitor = new SLTLxSATVisitor(0, new SATAtomMappings());
//		ParseTreeWalker walker = new ParseTreeWalker();
//		SLTLxWalker listener = new SLTLxWalker();
//		
//		walker.walk(listener, tree);
		Set<SATFact> res = visitor.visit(tree);

		
		System.out.println("Done.");
	}
}