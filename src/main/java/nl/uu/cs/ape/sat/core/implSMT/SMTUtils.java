package nl.uu.cs.ape.sat.core.implSMT;


import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.automaton.State;

/**
 * The {@code ModuleUtils} class is used to encode SAT constraints based on the
 * module annotations.
 *
 * @author Vedran Kasalica
 */
public final class SMTUtils {

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private SMTUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Declare unary function
	 * @param predicateID
	 * @param argumentType
	 * @return
	 */
	public static String declareUnaryFun(String predicateID, String argumentType) {
		StringBuilder constraints = new StringBuilder();
		
		constraints = constraints.append("(declare-fun ").append(predicateID).append(" (").append(argumentType).append(") Bool)\n");

		return constraints.toString();
	}

}