package nl.uu.cs.ape.automaton;

import java.util.List;

/**
 * The {@code Automaton} interface is used to represent general interface that
 * should be implemented by any interface.
 *
 * @author Vedran Kasalica
 */
public interface Automaton {

    /**
     * Gets all states as a List.
     *
     * @return All the States.
     */
    List<State> getAllStates();

}
