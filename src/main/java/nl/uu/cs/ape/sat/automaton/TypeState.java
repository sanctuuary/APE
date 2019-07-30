package nl.uu.cs.ape.sat.automaton;

/**
 * Class represents a single Type State in the system.
 * @author vedran
 *
 */
public class TypeState implements State {

	private String stateName;
	private int stateNumber;
	private int absoluteStateNumber;
	
	public TypeState(String stateName, int stateNumber, int absoluteStateNumber) {
		this.stateName = stateName;
		this.stateNumber = stateNumber;
		this.absoluteStateNumber = absoluteStateNumber;
	}

	

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stateName == null) ? 0 : stateName.hashCode());
		result = prime * result + stateNumber;
		return result;
	}



	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeState other = (TypeState) obj;
		if (stateName == null) {
			if (other.stateName != null)
				return false;
		} else if (!stateName.equals(other.stateName))
			return false;
		if (stateNumber != other.stateNumber)
			return false;
		return true;
	}



	public String getStateName() {
		return stateName;
	}


	public int getStateNumber() {
		return stateNumber;
	}


	public int getAbsoluteStateNumber() {
		return absoluteStateNumber;
	}
	
	
	
}
