/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A specialized map that stores types by both their full and short (without package prefixes) names.
 * If an incoming type shares the same name (but different package/prefix) with a type already in the
 * map then an IllegalArgumentException will be thrown since any subsequent retrievals by said short
 * name could be in error.
 *
 * @author Brian Remedios
 */
public class TypeMap {

    private Map<String, Class<?>> typesByName;

    /**
     * Constructor for TypeMap.
     *
     * @param initialSize int
     */
    public TypeMap(int initialSize) {
        typesByName = new HashMap<>(initialSize);
    }

    /**
     * Constructor for TypeMap that takes in an initial set of types.
     *
     * @param types Class[]
     */
    public TypeMap(Class<?>... types) {
        this(types.length);
        add(types);
    }

    /**
     * Adds a type to the receiver and stores it keyed by both its full and
     * short names. Throws an exception if the short name of the argument
     * matches an existing one already in the map for a different class.
     *
     * @param type Class
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void add(Class<?> type) {
        final String shortName = ClassUtil.withoutPackageName(type.getName());
        Class<?> existingType = typesByName.get(shortName);
        if (existingType == null) {
            typesByName.put(type.getName(), type);
            typesByName.put(shortName, type);
            return;
        }

        if (existingType != type) {
            throw new IllegalArgumentException("Short name collision between existing " + existingType + " and new "
                    + type);
        }
    }

    /**
     * Returns whether the type is known to the receiver.
     *
     * @param type Class
     * @return boolean
     */
    public boolean contains(Class<?> type) {
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
    public Class<?> typeFor(String typeName) {
        return typesByName.get(typeName);
    }

    /**
     * Adds an array of types to the receiver at once.
     *
     * @param types Class[]
     */
    public void add(Class<?>... types) {
        for (Class<?> element : types) {
            add(element);
        }
    }

    /**
     * Creates and returns a map of short type names (without the package
     * prefixes) keyed by the classes themselves.
     *
     * @return Map
     */
    public Map<Class<?>, String> asInverseWithShortName() {
        Map<Class<?>, String> inverseMap = new HashMap<>(typesByName.size() / 2);

        Iterator<Map.Entry<String,Class<?>>> iter = typesByName.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Class<?>> entry = iter.next();
            storeShortest(inverseMap, entry.getValue(), entry.getKey());
        }

        return inverseMap;
    }

    /**
     * Returns the total number of entries in the receiver. This will be exactly
     * twice the number of types added.
     *
     * @return the total number of entries in the receiver
     */
    public int size() {
        return typesByName.size();
    }

    /**
     * Store the shorter of the incoming value or the existing value in the map
     * at the key specified.
     *
     * @param map
     * @param key
     * @param value
     */
    private void storeShortest(Map<Class<?>, String> map, Class<?> key, String value) {
        String existingValue = map.get(key);

        if (existingValue == null) {
            map.put(key, value);
            return;
        }

        if (existingValue.length() < value.length()) {
            return;
        }

        map.put(key, value);
    }
}
