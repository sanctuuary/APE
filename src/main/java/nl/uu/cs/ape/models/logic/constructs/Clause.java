package nl.uu.cs.ape.models.logic.constructs;

import java.util.ArrayList;

import nl.uu.cs.ape.models.satStruc.Literal;

/**
 * An ordered collection of Literals. In practice it TODO
 *
 * @author Vedran Kasalica
 */
public class Clause extends ArrayList<Literal> {

    private static final long serialVersionUID = -7456831885895888413L;

    /**
     * Instantiates a new Clause.
     */
    public Clause() {
        super();
    }
}