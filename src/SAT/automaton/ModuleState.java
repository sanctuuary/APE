package SAT.automaton;

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


	@Override
	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	@Override
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
