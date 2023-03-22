package nl.uu.cs.ape.constraints;

/**
 * The {@code APEDataDimensionsOverlapException} exception will be thrown if the
 * data dimensions are not annotated properly.
 * 
 * @author Vedran Kasalica
 *
 */
public class ConstraintFormatException extends RuntimeException {

     /**
      * Instantiates a new Ape exception.
      *
      * @param message The message that will be passed to the {@link Exception} super
      *                class.
      */
     private ConstraintFormatException(String message) {
          super(message);
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException wrongNumberOfParameters(String message) {
          return new ConstraintFormatException(String.format(
                    "The constraints JSON error. Number of parameters does not match the required number. %s",
                    message));
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException badFormat(String message) {
          return new ConstraintFormatException(String.format("The constraints JSON error. Bad format. %s", message));
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException wrongConstraintID(String message) {
          return new ConstraintFormatException(String
                    .format("The constraints JSON error. The fiven constraint ID cannot be recognised. %s", message));
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException wrongParameter(String message) {
          return new ConstraintFormatException(String
                    .format("The constraints JSON error. One or more parameters cannot be instantiated. %s", message));
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException wrongSLTLxOperation(String message) {
          return new ConstraintFormatException(String.format(
                    "The SLTLx constraints error. One or more Operations used in the formula cannot be recognised. %s",
                    message));
     }

     /**
      * Exception is thrown when a constraint is given wrong number of arguments.
      * 
      * @param message - Application specific message that may help the user solve
      *                the problem.
      * @return Constraint exception with information that may help the user solve
      *         the problem.
      */
     public static ConstraintFormatException wrongSLTLxType(String message) {
          return new ConstraintFormatException(String.format(
                    "The SLTLx constraints error. One or more data types used in the formula cannot be recognised. %s",
                    message));
     }
}
