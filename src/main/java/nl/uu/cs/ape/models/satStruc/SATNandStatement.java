package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model nand relation of terms in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATNandStatement implements SATFact {

private List<SATFact> nconjunctedFacts;
	
	
	public SATNandStatement(SATFact arg1, SATFact arg2) {
		super();
		this.nconjunctedFacts = new ArrayList<SATFact>();
		this.nconjunctedFacts.add(arg1);
		this.nconjunctedFacts.add(arg2);
	}

	public SATNandStatement(List<? extends SATFact> nconjunctedFacts) {
		super();
		this.nconjunctedFacts = new ArrayList<SATFact>();
		nconjunctedFacts.forEach(fact -> this.nconjunctedFacts.add(fact));
	}

	@Override
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(nconjunctedFacts.size() == 1) {
			return nconjunctedFacts.get(0).getPropositionalEncoding(synthesisEngine);
		}

		Iterator<SATFact> currFact = nconjunctedFacts.iterator();
		if (currFact.hasNext()) {
			constraints.append("(~").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  while (currFact.hasNext()) {
			  constraints.append(" | ~").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  }
		  constraints.append(")");
		}
		
		return constraints.toString();
	}

	@Override
	public List<SATClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SATFact getNegated() {
		return new SATAndStatement(nconjunctedFacts);
	}

}
