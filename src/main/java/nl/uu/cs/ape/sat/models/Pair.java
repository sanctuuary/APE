package nl.uu.cs.ape.sat.models;

/**
 * The {@code Pair} class represents pairs of objects.<br>
 * E.g.: {@code <Type_1, Type_2>}.
 *
 * @author Vedran Kasalica
 */
public class Pair<T> {

    private T first, second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }
}
