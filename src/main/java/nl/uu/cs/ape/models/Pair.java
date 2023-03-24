package nl.uu.cs.ape.models;

/**
 * The {@code Pair} class represents pairs of objects.<br>
 * E.g.: {@code <a, b>}.
 *
 * @param <T> the type parameter
 * @author Vedran Kasalica
 */
public class Pair<T> {

	private T first;
	private T second;

	/**
	 * Instantiates a new Pair.
	 *
	 * @param first  the first
	 * @param second the second
	 */
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets first.
	 *
	 * @return the first
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Gets second.
	 *
	 * @return the second
	 */
	public T getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
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
		Pair<T> other = (Pair<T>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

}
