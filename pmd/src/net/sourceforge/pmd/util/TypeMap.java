package net.sourceforge.pmd.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A specialized map that stores classes by both their full and short names.
 * 
 * @author Brian Remedios
 */
public class TypeMap {

	private Map typesByName;
	
	/**
	 * Constructor for TypeMap.
	 * @param initialSize int
	 */
	public TypeMap(int initialSize) {
		typesByName = new HashMap(initialSize);
	}

	/**
	 * Constructor for TypeMap that takes in an initial set of types.
	 * 
	 * @param types Class[]
	 */
	public TypeMap(Class[] types) {
		this(types.length);
		add(types);
	}
	
	/**
	 * Adds a type to the receiver and stores it keyed by both its full
	 * and short names.
	 * 
	 * @param type Class
	 */
	public void add(Class type) {
		typesByName.put(type.getName(), type);
		typesByName.put(ClassUtil.withoutPackageName(type.getName()), type);
	}
	
	/**
	 * Returns whether the type is known to the receiver.
	 * 
	 * @param type Class
	 * @return boolean
	 */
	public boolean contains(Class type) {
		return typesByName.containsValue(type);
	}
	
	/**
	 * Returns whether the typeName is known to the receiver.
	 * 
	 * @param typeName String
	 * @return boolean
	 */
	public boolean contains(String typeName) {
		return typesByName.containsKey(typeName);
	}
	
	/**
	 * Returns the type for the typeName specified.
	 * 
	 * @param typeName String
	 * @return Class
	 */
	public Class typeFor(String typeName) {
		return (Class)typesByName.get(typeName);
	}
	
	/**
	 * Adds an array of types to the receiver at once.
	 * 
	 * @param types Class[]
	 */
	public void add(Class[] types) {
		for (int i=0; i<types.length; i++) {
			add(types[i]);
		}
	}
}
