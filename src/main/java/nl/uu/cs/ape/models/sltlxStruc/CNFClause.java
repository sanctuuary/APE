package nl.uu.cs.ape.models.sltlxStruc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * The class represents a clause/fact used in the SAT encoding (CNF).
 * 
 * @author Vedran Kasalica
 *
 */
public class CNFClause {

	private final List<Integer> atoms = new ArrayList<>();

	/**
	 * Create clause based on the list of elements (integers, bigger that 0)
	 * 
	 * @param atoms
	 */
	public CNFClause(List<Integer> atoms) {
		super();
		atoms.forEach(atom -> this.atoms.add(atom));
	}

	/**
	 * Create a clause that has only one element.
	 * 
	 * @param atom - integer that corresponds to the mapping of the atom
	 */
	public CNFClause(Integer atom) {
		super();
		this.atoms.add(atom);
	}

	/**
	 * Return conjunction of the collectors of clauses. Take a set/list of
	 * collections of {@link CNFClause}s and combine them under the AND logic
	 * operator.
	 * 
	 * @param facts - collections of 'collections of clauses' that are conjunct
	 * @return Set of {@link CNFClause}s that represent conjunction of the given
	 *         collections of clauses.
	 */
	public static Set<String> conjunctClausesCollection(Set<Set<String>> facts) {
		Set<String> allClauses = new HashSet<>();
		facts.forEach(allClauses::addAll);

		return allClauses;
	}

	/**
	 * Return disjunction of the collectors of clauses. Take a set/list of
	 * collections of {@link CNFClause}s and combine them under the OR logic
	 * operator.
	 * 
	 * @param facts - collections of 'collections of clauses' that are disjoint.
	 * @return Set of {@link CNFClause}s that represent disjunction of the given
	 *         collections of clauses.
	 */
	public static Set<String> disjoinClausesCollection(Set<Set<String>> facts) {
		List<String> clausesList = new ArrayList<>();
		Iterator<Set<String>> currDisjFact = facts.iterator();

		if (currDisjFact.hasNext()) {
			clausesList.addAll(currDisjFact.next());
			while (currDisjFact.hasNext()) {
				Collection<String> newClauses = currDisjFact.next();
				/* .. and combine it with all the other elements. */
				ListIterator<String> allClausesIt = clausesList.listIterator();
				while (allClausesIt.hasNext()) {
					/* Remove the existing element .. */
					String existingClause = allClausesIt.next();
					allClausesIt.remove();

					/* ... and add all the combinations of that elements and the new elements. */
					for (String newClause : newClauses) {
						allClausesIt.add(CNFClause.disjoin2Clauses(existingClause, newClause));
					}
				}
			}
		}
		Set<String> allClauses = new HashSet<>();
		allClauses.addAll(clausesList);
		return allClauses;
	}

	/**
	 * Return a new clause that combines the two clauses. The method combines the 2
	 * strings, each comprising disjoint Atoms.
	 * 
	 * @param clause1 - 1st clause that should be combined
	 * @param clause2 - 2nd clause that should be combined
	 * @return String that represents the disjunction of the two clauses.
	 */
	public static String disjoin2Clauses(String clause1, String clause2) {
		String clause1Cleaned = clause1.substring(0, clause1.indexOf("0\n"));
		return clause1Cleaned + clause2;
	}

	public Set<CNFClause> createCNFEncoding() {
		Set<CNFClause> clause = new HashSet<>();
		clause.add(this);
		return clause;
	}

	public Set<CNFClause> createNegatedCNFEncoding() {
		Set<CNFClause> clauses = new HashSet<>();
		for (int element : this.atoms) {
			clauses.add(new CNFClause(-element));
		}

		return clauses;
	}

	public Set<String> toCNF() {
		StringBuilder cnf = new StringBuilder();
		atoms.forEach(elem -> cnf.append(elem + " "));
		cnf.append("0\n");

		Set<String> clauses = new HashSet<>();
		clauses.add(cnf.toString());
		return clauses;
	}

	public Set<String> toNegatedCNF() {
		Set<String> clauses = new HashSet<>();
		atoms.forEach(elem -> clauses.add((-elem) + " 0\n"));

		return clauses;
	}

}
