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

    private final Map<DataKey<? extends K, ?>, Object> map = new IdentityHashMap<>();

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
        return (T) map.put(key, data);
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
    public <T> T get(DataKey<? extends K, ? super T> key) {
        return (T) map.get(key);
    }

    /**
     * Returns true if the given key has a non-null value in the map.
     *
     * @param key Key
     *
     * @return True if some value is set
     */
    public boolean isSet(DataKey<? extends K, ?> key) {
        return map.containsKey(key);
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
