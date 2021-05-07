/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.net.URL;

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
}
