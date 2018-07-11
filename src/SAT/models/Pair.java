package SAT.models;

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
