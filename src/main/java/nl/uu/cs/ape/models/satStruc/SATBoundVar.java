package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model not statement in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATBoundVar implements Comparable<SATBoundVar> {

	/**
	 * ID of the variable
	 */
	private String varName;
	/**
	 * Location in the workflow at which the variable must already be generated.
	 */
	private int memoryLocation;
	
	/**
	 * Define a variable, bound to a specific state in the workflow.
	 * @param varName - name/id of the variable
	 * @param memoryLoc - step of the workflow in which it must be already generated
	 */
	public SATBoundVar(String varName, int memoryLoc) {
		super();
		this.varName = varName; 
		this.memoryLocation = memoryLoc;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((varName == null) ? 0 : varName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SATBoundVar other = (SATBoundVar) obj;
		if (varName == null) {
			if (other.varName != null)
				return false;
		} else if (!varName.equals(other.varName))
			return false;
		return true;
	}


	@Override
	public int compareTo(SATBoundVar o) {
		return Integer.compare(this.memoryLocation, o.memoryLocation);
	}


	public Set<CNFClause> getExistentialCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Set<CNFClause> getUniversalCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

}
