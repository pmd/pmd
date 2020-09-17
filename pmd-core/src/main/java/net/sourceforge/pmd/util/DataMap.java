/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * An opaque, strongly typed heterogeneous data container. Data maps can
 * be set to accept only a certain type of key, with the type parameter.
 * The key can itself constrain the type of values, using its own type
 * parameter {@code T}.
 *
 * @param <K> Type of keys in this map.
 */
public final class DataMap<K> {

    private Map<DataKey<? extends K, ?>, Object> map;

    private DataMap() {

    }

    /**
     * Set the mapping to the given data.
     *
     * @param key  Key
     * @param data Data mapped to the key
     * @param <T>  Type of the data
     *
     * @return Previous value associated with the key (nullable)
     */
    @SuppressWarnings("unchecked")
    public <T> T set(DataKey<? extends K, ? super T> key, T data) {
        return (T) getMap().put(key, data);
    }

    /**
     * Retrieves the data currently mapped to the key.
     *
     * @param key Key
     * @param <T> Type of the data
     *
     * @return Value associated with the key (nullable)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(DataKey<? extends K, ? extends T> key) {
        return map == null ? null : (T) map.get(key);
    }

    private Map<DataKey<? extends K, ?>, Object> getMap() {
        // the map is lazily created, it's only needed if set() is called
        // at least once, but get() might be called many more times, as
        // sometimes you cache a key sparsely on some nodes, and default
        // to the first parent for which the key is set. The default expected
        // max size is also 21, which is *way* bigger than what data maps
        // typically contain (1/2 keys)
        if (map == null) {
            map = new IdentityHashMap<>(1);
        }
        return map;
    }

    /**
     * Returns true if the given key has a non-null value in the map.
     *
     * @param key Key
     *
     * @return True if some value is set
     */
    public boolean isSet(DataKey<? extends K, ?> key) {
        return map != null && map.containsKey(key);
    }

    public static <K> DataMap<K> newDataMap() {
        return new DataMap<>();
    }

    public static <T> SimpleDataKey<T> simpleDataKey(final String name) {
        return new SimpleDataKey<>(name);
    }

    /**
     * A key for type-safe access into a {@link DataMap}. Data keys use
     * reference identity and are only compared by reference within
     * {@link DataMap}.
     *
     * @param <K> Type of the family of keys this is a part of
     * @param <T> Type of the addressed data
     */
    public interface DataKey<K extends DataKey<K, T>, T> {

    }

    public static class SimpleDataKey<T> implements DataKey<SimpleDataKey<T>, T> {

        private final String name;

        SimpleDataKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
