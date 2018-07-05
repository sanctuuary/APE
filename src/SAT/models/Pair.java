package SAT.models;

public class Pair {

	private Atom first, second;
	
	public Pair(Atom first,Atom second) {
		this.first = first;
		this.second = second;
	}
	
	public Atom getFirst() {
		return first;
	}
	
	public Atom getSecond() {
		return second;
	}
}
