/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Caches some results of subtyping checks.
 */
final class SupertypeCheckCache {

    private final Map<JTypeMirror, Set<JTypeMirror>> cache = new LinkedHashMap<JTypeMirror, Set<JTypeMirror>>() {
        // Even with a relatively small cache size, the hit ratio is
        // very high (around 75% on the tests we have here, discounting
        // the stress tests)
        // TODO refresh numbers using a real codebase
        private static final int MAX_SIZE = 50;

        @Override
        protected boolean removeEldestEntry(Entry<JTypeMirror, Set<JTypeMirror>> eldest) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * Returns true if t is certainly a subtype of s. Otherwise it
     * needs to be recomputed.
     */
    boolean isCertainlyASubtype(JTypeMirror t, JTypeMirror s) {
        Set<JTypeMirror> superTypesOfT = cache.get(t);
        return superTypesOfT != null && superTypesOfT.contains(s);
    }

    void remember(JTypeMirror t, JTypeMirror s) {
        if (shouldCache(t)) {
            cache.computeIfAbsent(t, k -> new HashSet<>()).add(s);
        }
    }


    private boolean shouldCache(JTypeMirror t) {
        // some types are never cached
        return !(t instanceof InferenceVar) && !(t instanceof JPrimitiveType);
    }

}
