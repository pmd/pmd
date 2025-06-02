/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedprivatemethod.cache;

import java.util.function.Function;

public class CacheLoader<K, V> {
    private CacheLoader() {}

    public static <K, V> CacheLoader<K, V> from(Function<K, V> r) {
        return null;
    }
}
