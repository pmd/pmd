/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * Generic Map interface used by {@link RuleContext} to store the attributes, depending on whether it is used in a
 * multi-threaded environment or single-threaded environment.
 * <p>
 * Note: When PMD starts using Java 8, {@link java.util.Map} interface will already include putIfAbsent method
 * (in Java 7 it only exists in {@link java.util.concurrent.ConcurrentMap}). This means that this interface and its
 * implementations will be removed and {@link RuleContext} will directly instance a new {@link java.util.HashMap} or
 * {@link java.util.concurrent.ConcurrentHashMap}.
 * </p>
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
/* default */ interface RuleContextMap<K, V> {
    V putIfAbsent(K name, V value);

    V get(K name);

    V remove(K name);
}
