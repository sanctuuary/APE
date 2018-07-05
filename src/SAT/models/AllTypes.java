package SAT.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllTypes {
	
	private Set<Type> types;


	public AllTypes(){
		
		this.types = new HashSet<>();
		
	}
	
	public Set<Type> getTypes() {
		return types;
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional operation). More formally, adds the specified element e to this set if the set contains no element e2 such that (e==null ? e2==null : e.equals(e2)). If this set already contains the element, the call leaves the set unchanged and returns false. In combination with the restriction on constructors, this ensures that sets never contain duplicate elements. 
	 * @param type
	 * @return
	 */
	public boolean addType(Type type){
		return types.add(type);
	}
	
	/**
	 * Returns true if this set contains the specified element. More formally, returns true if and only if this set contains an element e such that (o==null ? e==null : o.equals(e)).
	 * @param type
	 * @return
	 */
	public boolean existsType(Type type){
		return types.contains(type);
	}
	
	public int size(){
		return types.size();
	}

}
