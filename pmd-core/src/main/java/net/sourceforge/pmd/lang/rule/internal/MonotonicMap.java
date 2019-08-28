/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;


import java.util.Map;
import java.util.function.BiFunction;

public interface MonotonicMap<K, V> extends Map<K, V> {


    @Override
    default boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }


    @Override
    V get(Object key);


    @Override
    default V remove(Object key) {
        throw new UnsupportedOperationException();
    }


    @Override
    default boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }


    @Override
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }


    @Override
    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}
