package nl.uu.cs.ape.models.enums;

/**
 * The {@code SynthesisFlag} class is used to model the reason why the synthesis
 * search was interrupted.
 *
 * @author Vedran Kasalica
 */
public enum SynthesisFlag {

    /**
     * Synthesis found all required solutions (it was not interrupted, it finished
     * successfully).
     */
    NONE,

    /**
     * Synthesis couldn't find any solutions for the given specification and length.
     */
    UNSAT,

    /**
     * Synthesis search was interrupted because it reached the maximum workflow
     * length without finding the specified number of solutions.
     */
    MAX_LENGTH,

    /**
     * Synthesis was interrupted because it reached the maximum run duration.
     */
    TIMEOUT,

    /**
     * Synthesis was interrupted for an unknown reason.
     */
    UNKNOWN;

    /**
     * Get the message that reflects the reason the synthesis execution was
     * interrupted. <br>
     * {@link SynthesisFlag#NONE} signals that the synthesis search was not
     * interrupted (it finished), so there is no corresponding message.
     * 
     * @return A string corresponding to the message the flag depicts.
     */
    public String getMessage() {
        if (this == SynthesisFlag.NONE) {
            return "";
        } else if (this == SynthesisFlag.UNSAT) {
            return "No solutions can be found for the given specification.";
        } else if (this == SynthesisFlag.MAX_LENGTH) {
            return "Synthesis was interrupted because it reached the maximum workflow length without finding the specified number of solutions.";
        } else if (this == SynthesisFlag.TIMEOUT) {
            return "Synthesis was interrupted because it reached the maximum run duration.";
        } else {
            return "Synthesis was interrupted for an unknown reason.";
        }
    }

}
