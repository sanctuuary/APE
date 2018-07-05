package SAT.models;

import java.util.ArrayList;
import java.util.List;

public class Type implements Atom{

	private String typeName;
	private String typeID;
	private List<Type> subTypes;
	
	public Type(String typeName, String typeID) {
		super();
		this.typeName = typeName;
		this.typeID = typeID;
		this.subTypes = new ArrayList<>();
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}
	
	public static Type generateType(String typeName, String typeID){
		
		Type tmpType;
		//check in memory if it exists
//		if((tmpType = collection.exists(typeID)) == null)
			tmpType = new Type(typeName, typeID);
		
		return tmpType;
			
	}

	@Override
	public String getAtom() {
		return typeID;
	}

	@Override
	public String getType() {
		return "type";
	}
	
	public void setSubType(Type type){
		subTypes.add(type);
	}
	
	public List<Type> getSubTypes(){
		return subTypes;
	}
	
	@Override
	 public boolean equals(Object obj) {
	   Type other=(Type) obj;
	   return this.typeID.matches(other.typeID);
	 }

	 @Override
	 public int hashCode() {
	    return typeID.hashCode();
	 }
	
}
