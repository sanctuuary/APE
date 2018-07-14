package SAT.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.spi.TransactionalWriter;

public class Type implements Predicate {

	private String typeName;
	private String typeID;
	// set of subtypes, null in case of a simple type
	private Set<String> subTypes;
	// represents whether the type is a simple/leaf type. If false the type is an
	// abstract (non-leaf) type.
	private boolean simpleType;

	public Type(String typeName, String typeID, boolean simpleType) {
		super();
		this.typeName = typeName;
		this.typeID = typeID;
		this.simpleType = simpleType;
		if (!simpleType) {
			this.subTypes = new HashSet<String>();
		}
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

	@Override
	public String getPredicate() {
		return typeID;
	}

	@Override
	public String getType() {
		return "type";
	}

	/**
	 * Adds a subtype to a non-simple type.
	 * 
	 * @param type
	 *            - type that will be added as a subclass
	 * @return True if subtype was added, false otherwise.
	 */
	public boolean addSubType(Type type) {
		if (!simpleType) {
			subTypes.add(type.getTypeID());
			return true;
		} else {
			System.err.println("Cannot add subtypes to a simpleType!");
			return false;
		}
	}

	/**
	 * Adds a subtype to a non-simple type, if it was not added present already.
	 * 
	 * @param typeID
	 *            - ID of the type that will be added as a subclass
	 * @return True if subtype was added, false otherwise.
	 */
	public boolean addSubType(String typeID) {
		if (!simpleType) {
			return subTypes.add(typeID);
		} else {
			System.err.println("Cannot add subtypes to a simpleType!");
			return false;
		}
	}

	/**
	 * Returns the list of the types that are directly subsumed by the type.
	 * 
	 * @return List of the subtypes or null in case of a simple/leaf type
	 */
	public Set<String> getSubTypes() {
		return subTypes;
	}

	@Override
	public boolean equals(Object obj) {
		Type other = (Type) obj;
		return this.typeID.matches(other.typeID);
	}

	@Override
	public int hashCode() {
		return typeID.hashCode();
	}

	/**
	 * Returns true if the type is a simple/leaf type, otherwise returns false - the
	 * type is an abstract (non-leaf) type.
	 * 
	 * @return true (simple/leaf type) or false (abstract/non-leaf type)
	 */
	public boolean isSimpleType() {
		return simpleType;
	}

	public void setSimpleType(boolean isSimpleType) {
		this.simpleType = isSimpleType;
	}

	/**
	 * The class is used to check weather the type with typeID was already
	 * introduced earlier on in allTypes. In case it was, it returns the item,
	 * otherwise the new element is generated and returned.
	 * 
	 * @param typeName
	 *            - type name
	 * @param typeID
	 *            - unique type identifier
	 * @param simpleType
	 *            - determines whether the type is a simple/leaf type
	 * @param allTypes
	 *            - set of all the types created so far
	 * @return the Type representing the item.
	 */
	public static Type generateType(String typeName, String typeID, boolean simpleType, AllTypes allTypes) {

		Type tmpType;
		if ((tmpType = allTypes.get(typeID)) == null) {
			tmpType = new Type(typeName, typeID, simpleType);
			allTypes.addType(tmpType);
		}
		return tmpType;

	}

	/**
	 * Print the tree shaped representation of the type taxonomy
	 * 
	 * @param str
	 */
	public void printTree(String str, AllTypes allTypes) {
		System.out.println(str + print());
		if (subTypes != null)
			for (String typeID : subTypes) {
				allTypes.get(typeID).printTree(str + "   ", allTypes);
			}
	}

	private String print() {
		if (isSimpleType()) {
			return typeID + "[S]";
		} else {
			return typeID;
		}
	}

}
