package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.logic.constructs.Predicate;
/**
 * The {@code Pair} class represents pairs of objects, in our case used to store pairs of {@link AbstractModule AbstractModules}, {@link Module Modules} or {@link Type DataInstance}.
 * <br>
 * <br>e.g.
 * <br>{@code <type_1, type_2>}
 * 
 * @author Vedran Kasalica
 *
 */
public class Pair {

	private Predicate first, second;
	
	public Pair(Predicate first,Predicate second) {
		this.first = first;
		this.second = second;
	}
	
	public Predicate getFirst() {
		return first;
	}
	
	public Predicate getSecond() {
		return second;
	}
}
