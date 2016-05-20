/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic collection and array-related utility functions for java.util types. See ClassUtil 
 * for comparable facilities for short name lookup.
 *
 * @author Brian Remedios
 * @version $Revision$
 */
public final class CollectionUtil {

    @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
    public static final TypeMap COLLECTION_INTERFACES_BY_NAMES = new TypeMap(new Class[] { java.util.List.class,
	    java.util.Collection.class, java.util.Map.class, java.util.Set.class, });

    @SuppressWarnings({"PMD.LooseCoupling", "PMD.UnnecessaryFullyQualifiedName"})
    public static final TypeMap COLLECTION_CLASSES_BY_NAMES = new TypeMap(new Class[] { java.util.ArrayList.class,
	    java.util.LinkedList.class, java.util.Vector.class, java.util.HashMap.class, java.util.LinkedHashMap.class,
	    java.util.TreeMap.class, java.util.TreeSet.class, java.util.HashSet.class, java.util.LinkedHashSet.class,
	    java.util.Hashtable.class});

    private CollectionUtil() {
    }

	/**
	 * Add elements from the source to the target as long as they don't already exist there.
	 * Return the number of items actually added.
	 * 
	 * @param source
	 * @param target
	 * @return int
	 */
	public static int addWithoutDuplicates(Collection<String> source, Collection<String> target) {
		
		int added = 0;
		
		for (String item : source) {
			if (target.contains(item)) {
			    continue;
			}
			target.add(item);
			added++;
		}
		
		return added;
	}
    
    /**
     * Returns the collection type if we recognize it by its short name.
     *
     * @param shortName String
     * @return Class
     */
    public static Class<?> getCollectionTypeFor(String shortName) {
	   Class<?> cls = COLLECTION_CLASSES_BY_NAMES.typeFor(shortName);
	   if (cls != null) {
	     return cls;
	   }

	   return COLLECTION_INTERFACES_BY_NAMES.typeFor(shortName);
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

	   if (COLLECTION_CLASSES_BY_NAMES.contains(typeName)) {
	     return true;
	   }

	   return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(typeName);
    }

    /**
     * Return whether we can identify the typeName as a java.util collection class
     * or interface as specified.
     *
     * @param clazzType Class
     * @param includeInterfaces boolean
     * @return boolean
     */
    public static boolean isCollectionType(Class<?> clazzType, boolean includeInterfaces) {

	   if (COLLECTION_CLASSES_BY_NAMES.contains(clazzType)) {
	      return true;
	   }

	   return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(clazzType);
    }

    /**
     * Returns the items as a populated set.
     *
     * @param items Object[]
     * @return Set
     */
    public static <T> Set<T> asSet(T[] items) {

	   return new HashSet<>(Arrays.asList(items));
    }

    /**
     * Creates and returns a map populated with the keyValuesSets where
     * the value held by the tuples are they key and value in that order.
     *
     * @param keys K[]
     * @param values V[]
     * @return Map
     */
    public static <K, V> Map<K, V> mapFrom(K[] keys, V[] values) {
	   if (keys.length != values.length) {
	     throw new RuntimeException("mapFrom keys and values arrays have different sizes");
	   }
	   Map<K, V> map = new HashMap<>(keys.length);
	   for (int i = 0; i < keys.length; i++) {
	      map.put(keys[i], values[i]);
	      }
	   return map;
    }

    /**
     * Returns a map based on the source but with the key & values swapped.
     *
     * @param source Map
     * @return Map
     */
    public static <K, V> Map<V, K> invertedMapFrom(Map<K, V> source) {
	   Map<V, K> map = new HashMap<>(source.size());
	   for (Map.Entry<K, V> entry : source.entrySet()) {
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
    public static boolean arraysAreEqual(Object value, Object otherValue) {
	   if (value instanceof Object[]) {
	      if (otherValue instanceof Object[]) {
		  return valuesAreTransitivelyEqual((Object[]) value, (Object[]) otherValue);
	      }
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
    public static boolean valuesAreTransitivelyEqual(Object[] thisArray, Object[] thatArray) {
	   if (thisArray == thatArray) {
	     return true;
	   }
	   if (thisArray == null || thatArray == null) {
	     return false;
	   }
	   if (thisArray.length != thatArray.length) {
	     return false;
	   }
	   for (int i = 0; i < thisArray.length; i++) {
	     if (!areEqual(thisArray[i], thatArray[i])) {
	   	 return false; // recurse if req'd
	     }
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
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static boolean areEqual(Object value, Object otherValue) {
    	if (value == otherValue) {
    	    return true;
    	}
    	if (value == null) {
    	    return false;
    	}
    	if (otherValue == null) {
    	    return false;
    	}

    	if (value.getClass().getComponentType() != null) {
    	    return arraysAreEqual(value, otherValue);
    	    }
	    return value.equals(otherValue);
    }

    /**
     * Returns whether the items array is null or has zero length.
     * @param items
     * @return boolean
     */
    public static boolean isEmpty(Object[] items) {
        return items == null || items.length == 0;
    }
    
    /**
     * Returns whether the items array is non-null and has
     * at least one entry.
     * 
     * @param items
     * @return boolean
     */
    public static boolean isNotEmpty(Object[] items) {
        return !isEmpty(items);
    }

    /**
     * Returns true if both arrays are if both are null or have zero-length,
     * otherwise return the false if their respective elements are not
     * equal by position.
     *
     * @param <T>
     * @param a
     * @param b
     * @return boolean
     */
    public static <T> boolean areSemanticEquals(T[] a, T[] b) {

        if (a == null) {
            return isEmpty(b);
        }
        if (b == null) {
            return isEmpty(a);
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (!areEqual(a[i], b[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * If the newValue is already held within the values array then the values array
     * is returned, otherwise a new array is created appending the newValue to the
     * end.
     *
     * @param <T>
     * @param values
     * @param newValue
     * @return an array containing the union of values and newValue
     */
    public static <T> T[] addWithoutDuplicates(T[] values, T newValue) {

        for (T value : values) {
            if (value.equals(newValue)) {
                return values;
            }
        }

        T[] largerOne = (T[])Array.newInstance(values.getClass().getComponentType(), values.length + 1);
        System.arraycopy(values, 0, largerOne, 0, values.length);
        largerOne[values.length] = newValue;
        return largerOne;
    }

    /**
     * Returns an array of values as a union set of the two input arrays.
     *
     * @param <T>
     * @param values
     * @param newValues
     * @return the union of the two arrays
     */
    public static <T> T[] addWithoutDuplicates(T[] values, T[] newValues) {

        Set<T> originals = new HashSet<>(values.length);
        for (T value : values) {
            originals.add(value);
        }
        List<T> newOnes = new ArrayList<>(newValues.length);
        for (T value : newValues) {
            if (originals.contains(value)) {
                continue;
            }
            newOnes.add(value);
        }

        T[] largerOne = (T[]) Array.newInstance(values.getClass().getComponentType(), values.length + newOnes.size());
        System.arraycopy(values, 0, largerOne, 0, values.length);
        for (int i = values.length; i < largerOne.length; i++) {
            largerOne[i] = newOnes.get(i - values.length);
        }
        return largerOne;
    }
}
