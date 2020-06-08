package nl.uu.cs.ape.sat.models;

/**
 * The {@code Pair} class represents pairs of objects.<br>
 * E.g.: {@code <Type_1, Type_2>}.
 *
 * @param <T> the type parameter
 * @author Vedran Kasalica
 */
public class Pair<T> {

    private T first, second;

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
}
