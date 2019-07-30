package nl.uu.cs.ape.sat.automaton;

/**
 * Defines the values describing the states in the workflow.
 * <br>
 * <br>
 * values:
 * <br>
 * {@code TOOL, MEMORY_TYPE, USED_TYPE}
 */
public enum WorkflowElement{
	
	/**
	 * Depicts usage of a tool/module.
	 */
	MODULE,
	/**
	 * Depicts the creation of a new type instance to the memory.
	 */
	MEMORY_TYPE,
	/**
	 * Depicts the usage of an already created type instance. Usually as an input for a tool.
	 */
	USED_TYPE
	
}