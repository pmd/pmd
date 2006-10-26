package net.sourceforge.pmd.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Generic collection and array-related utility functions.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class CollectionUtil {

	public static final TypeMap collectionInterfacesByNames = new TypeMap( new Class[] {
		java.util.List.class,
		java.util.Collection.class,
		java.util.Map.class,
		java.util.Set.class,
		});
		
	public static final TypeMap collectionClassesByNames = new TypeMap( new Class[] {
		java.util.ArrayList.class,
		java.util.LinkedList.class,
		java.util.Vector.class,
		java.util.HashMap.class,
		java.util.LinkedHashMap.class,
		java.util.TreeMap.class,
		java.util.TreeSet.class,
		java.util.HashSet.class,
		java.util.LinkedHashSet.class
		});
	
	private CollectionUtil() {};
	
	/**
	 * Returns the collection type if we recognize it by its short name.
	 * 
	 * @param shortName String
	 * @return Class
	 */
	public static Class getCollectionTypeFor(String shortName) {
		Class cls = collectionClassesByNames.typeFor(shortName);
		if (cls != null) return cls;
		
		return collectionInterfacesByNames.typeFor(shortName);
	}
	
	/**
	 * Return whether we can identify the typeName as a java.util collection class
	 * or interface as specified.
	 * 
	 * @param typeName String
	 * @param includeInterfaces boolean
	 * @return boolean
	 */
	public static boolean isCollectionType(String typeName, boolean includeInterfaces) {
		
		if (collectionClassesByNames.contains(typeName)) return true;

		return includeInterfaces && collectionInterfacesByNames.contains(typeName);
	}
	
    /**
     * Return whether we can identify the typeName as a java.util collection class
     * or interface as specified.
     * 
     * @param clazzType Class
     * @param includeInterfaces boolean
     * @return boolean
     */
    public static boolean isCollectionType(Class clazzType, boolean includeInterfaces) {

        if (collectionClassesByNames.contains(clazzType)) {
            return true;
        }

        return includeInterfaces && collectionInterfacesByNames.contains(clazzType);
    }

    /**
     * Returns the items as a populated set.
     * 
     * @param items Object[]
     * @return Set
     */
    public static Set asSet(Object[] items) {
    	
    	Set set = new HashSet(items.length);
    	for (int i=0; i<items.length; i++) {
    		set.add(items[i]);
    	}
    	return set;
    }	
    
	/**
	 * Creates and returns a map populated with the keyValuesSets where
	 * the value held by the tuples are they key and value in that order.
	 * 
	 * @param keyValueSets Object[][]
	 * @return Map
	 */
	public static Map mapFrom(Object[][] keyValueSets) {
		Map map = new HashMap(keyValueSets.length);
		for (int i=0; i<keyValueSets.length; i++) {
			map.put(keyValueSets[i][0], keyValueSets[i][1]);
		}
		return map;
	}
	
	/**
	 * Returns a map based on the source but with the key & values swapped.
	 * 
	 * @param source Map
	 * @return Map
	 */
	public static Map invertedMapFrom(Map source) {
		Map map = new HashMap(source.size());
		Iterator iter = source.entrySet().iterator();
		Entry entry;
		while (iter.hasNext()) {
			entry = (Entry)iter.next();
			map.put(entry.getValue(), entry.getKey());
		}
		return map;
	}
	
	/**
	 * Returns true if the objects are array instances and each of their elements compares
	 * via equals as well.
	 * 
	 * @param value Object
	 * @param otherValue Object
	 * @return boolean
	 */
	public static final boolean arraysAreEqual(Object value, Object otherValue) {
		if (value instanceof Object[]) {
			if (otherValue instanceof Object[]) return valuesAreTransitivelyEqual((Object[])value, (Object[])otherValue);
			return false;
		}
		return false;
	}
	
	/**
	 * Returns whether the arrays are equal by examining each of their elements, even if they are
	 * arrays themselves.
	 * 
	 * @param thisArray Object[]
	 * @param thatArray Object[]
	 * @return boolean
	 */
	public static final boolean valuesAreTransitivelyEqual(Object[] thisArray, Object[] thatArray) {
		if (thisArray == thatArray) return true;
		if ((thisArray == null) || (thatArray == null)) return false;
		if (thisArray.length != thatArray.length) return false;
		for (int i = 0; i < thisArray.length; i++) {
			if (!areEqual(thisArray[i], thatArray[i])) return false;	// recurse if req'd
		}
		return true;
	}

	/**
	 * A comprehensive isEqual method that handles nulls and arrays safely.
	 * 
	 * @param value Object
	 * @param otherValue Object
	 * @return boolean
	 */
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) return true;
		if (value == null) return false;
		if (otherValue == null) return false;

		if (value.getClass().getComponentType() != null) return arraysAreEqual(value, otherValue);
		return value.equals(otherValue);
	}
}
