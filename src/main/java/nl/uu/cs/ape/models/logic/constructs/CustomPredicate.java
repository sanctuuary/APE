package nl.uu.cs.ape.models.logic.constructs;

/**
 * The {@code CustomPredicate} used to represent custom predicates describing
 * the data,
 * e.g., new data dimensions.
 * 
 * @author Vedran Kasalica
 *
 */
public class CustomPredicate implements Predicate {

	private String label;

	public CustomPredicate(String label) {
		this.label = label;
	}

	@Override
	public int compareTo(Predicate arg0) {
		return this.getPredicateID().compareTo(arg0.getPredicateID());
	}

	@Override
	public String getPredicateID() {
		return this.label;
	}

	@Override
	public String getPredicateLabel() {
		return this.label;
	}

	@Override
	public String getPredicateLongLabel() {
		return this.label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomPredicate other = (CustomPredicate) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

}
