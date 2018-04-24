/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.HashMap;

/**
 * Map used for single threaded PMD execution
 * @param <K> the type of keys
 * @param <V> the type of values
 */
/* default */ class SingleThreadedRuleContextMap<K, V> implements RuleContextMap<K, V> {
    private final java.util.Map<K, V> map = new HashMap<>();

    @Override
    public V putIfAbsent(K key, V value) {
        if (!map.containsKey(key)) {
            return map.put(key, value);
        }
        return map.get(key);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }
}
