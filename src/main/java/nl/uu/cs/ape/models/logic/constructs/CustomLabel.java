package nl.uu.cs.ape.models.logic.constructs;

/**
 * The {@code CustomLabel} used to represent custom labels (dimension) of the data.
 * 
 * @author Vedran Kasalica
 *
 */
public class CustomLabel implements PredicateLabel{

	private String label;
	
	public CustomLabel(String label) {
		this.label = label;
	}
	
	@Override
	public int compareTo(PredicateLabel arg0) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		CustomLabel other = (CustomLabel) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

}
