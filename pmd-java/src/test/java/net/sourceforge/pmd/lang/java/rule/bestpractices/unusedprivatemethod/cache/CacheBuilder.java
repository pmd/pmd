/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedprivatemethod.cache;

public class CacheBuilder<K, V> {
    public static CacheBuilder<Object, Object> newBuilder() {
        return null;
    }

    public <X extends K, Y extends V> LoadingCache<X, Y> build(CacheLoader<? super X, Y> loader) {
        return null;
    }
}
