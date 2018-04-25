/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Map used for multi threaded PMD execution
 * @param <K> the type of keys
 * @param <V> the type of values
 */
/* default */ class MultiThreadedRuleContextMap<K, V> implements RuleContextMap<K, V> {
    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

    @Override
    public V putIfAbsent(K name, V value) {
        return map.putIfAbsent(name, value);
    }

    @Override
    public V get(K name) {
        return map.get(name);
    }

    @Override
    public V remove(K name) {
        return map.remove(name);
    }
}
