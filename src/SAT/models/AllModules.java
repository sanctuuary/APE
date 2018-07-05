package SAT.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AllModules {

	private Set<Module> modules;

	public AllModules() {
		this.modules = new HashSet<>();
	}

	public AllModules(Collection<? extends Module> readCSV) {
		this.modules = new HashSet<>();
		this.modules.addAll(readCSV);
	}

	public Set<Module> getModules() {
		return modules;
	}

	/**
	 * Adds the specified element to this set if it is not already present
	 * (optional operation). More formally, adds the specified element e to this
	 * set if the set contains no element e2 such that (e==null ? e2==null :
	 * e.equals(e2)). If this set already contains the element, the call leaves
	 * the set unchanged and returns false. In combination with the restriction
	 * on constructors, this ensures that sets never contain duplicate elements.
	 * 
	 * @param module
	 * @return
	 */
	public boolean addModule(Module module) {
		return modules.add(module);
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that
	 * (o==null ? e==null : o.equals(e)).
	 * 
	 * @param module
	 * @return
	 */
	public boolean existsModule(Type module) {
		return modules.contains(module);
	}
	
	public int size(){
		return modules.size();
	}

}
