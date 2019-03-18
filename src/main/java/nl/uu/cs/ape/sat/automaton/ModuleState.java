package nl.uu.cs.ape.sat.automaton;

/**
 * Class represents a single Module State in the system.
 * @author vedran
 *
 */
public class ModuleState implements State {

	
	private String stateName;
	private int stateNumber;
	private boolean first;
	private boolean last;
	
	
	public ModuleState(String stateName, int stateNumber) {
		super();
		this.stateName = stateName;
		this.stateNumber = stateNumber;
	}


	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public int getStateNumber() {
		return stateNumber;
	}


	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}
	
	public void setFirst(){
		first = true;
	}
	
	public void setLast(){
		last = true;
	}
	
	public boolean isFirst(){
		return first;
	}
	
	public boolean isLast(){
		return last;
	}
	
}
