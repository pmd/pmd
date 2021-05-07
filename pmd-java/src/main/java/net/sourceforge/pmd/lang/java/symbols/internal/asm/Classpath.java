/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.net.URL;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Classpath abstraction.
 */
public interface Classpath {

    /**
     * Returns a URL to load the given resource if it exists in this classpath.
     * Otherwise returns null.
     */
    @Nullable URL getURLForResource(String resourcePath);

    /**
     * Returns a classpath instance that uses {@link ClassLoader#getResource(String)}
     * to find resources.
     */
    static Classpath forClassLoader(ClassLoader classLoader) {
        return classLoader::getResource;
    }

    static Classpath contextClasspath() {
        return forClassLoader(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Return a classpath that will ignore the given classpath entries,
     * even if they are present.
     *
     * @param deletedEntries Set of resource paths to exclude
     */
    default Classpath exclude(Set<String> deletedEntries) {
        return resourcePath -> deletedEntries.contains(resourcePath) ? null
                                                                     : getURLForResource(resourcePath);
    }

    default Classpath delegateTo(Classpath c) {
        return path -> {
            URL p = getURLForResource(path);
            if (p != null) {
                return p;
            }
            return c.getURLForResource(path);
        };
    }
}
