package nl.uu.cs.ape.sat.automaton;

import nl.uu.cs.ape.sat.StaticFunctions;

/**
 * Class represents a single Module State in the system.
 * @author Vedran Kasalica
 *
 */
public class ModuleState {
//
//	
//	private String stateName;
//	private int stateNumber;
//	private int absoluteStateNumber;
//	private boolean first;
//	private boolean last;
//	
//	
//	public ModuleState(String stateName, int stateNumber, int absoluteStateNumber) {
//		this.stateName = stateName;
//		this.stateNumber = stateNumber;
//		this.absoluteStateNumber = absoluteStateNumber;
//	}
//	
//	
//
//	public ModuleState(WorkflowElement moduleStateType, int stateNumber, int input_branching) {
//		this.stateName = WorkflowElement.getStringShorcut(moduleStateType) + stateNumber;
//		this.stateNumber = stateNumber;
//		this.absoluteStateNumber = StaticFunctions.calculateAbsStateNumber(null, stateNumber, input_branching, moduleStateType);
//	}
//
//
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + (first ? 1231 : 1237);
//		result = prime * result + (last ? 1231 : 1237);
//		result = prime * result + ((stateName == null) ? 0 : stateName.hashCode());
//		result = prime * result + stateNumber;
//		return result;
//	}
//
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ModuleState other = (ModuleState) obj;
//		if (first != other.first)
//			return false;
//		if (last != other.last)
//			return false;
//		if (stateName == null) {
//			if (other.stateName != null)
//				return false;
//		} else if (!stateName.equals(other.stateName))
//			return false;
//		if (stateNumber != other.stateNumber)
//			return false;
//		return true;
//	}
//
//
//	public String getStateName() {
//		return stateName;
//	}
//
//
//	public int getStateNumber() {
//		return stateNumber;
//	}
//
//
//	public int getAbsoluteStateNumber() {
//		return absoluteStateNumber;
//	}
//	
}
