/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.io.InputStream;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Classpath abstraction. PMD's symbol resolver uses the classpath to
 * find class files.
 */
@FunctionalInterface
public interface Classpath {

    /**
     * Returns a URL to load the given resource if it exists in this classpath.
     * Otherwise returns null. This will typically be used to find Java class files.
     * A typical input would be {@code java/lang/String.class}.
     *
     * @param resourcePath Resource path, as described in {@link ClassLoader#getResource(String)}
     *
     * @return A InputStream if the resource exists, otherwise null
     */
    @Nullable InputStream findResource(String resourcePath);

    // <editor-fold  defaultstate="collapsed" desc="Transformation methods (defaults)">

    /**
     * Return a classpath that will ignore the given classpath entries,
     * even if they are present in this classpath. Every call to {@link #findResource(String)}
     * is otherwise delegated to this one.
     *
     * @param deletedEntries Set of resource paths to exclude
     */
    default Classpath exclude(Set<String> deletedEntries) {
        return resourcePath -> deletedEntries.contains(resourcePath) ? null : findResource(resourcePath);
    }

    default Classpath delegateTo(Classpath c) {
        return path -> {
            InputStream p = findResource(path);
            if (p != null) {
                return p;
            }
            return c.findResource(path);
        };
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Creator methods">


    /**
     * Returns a classpath instance that uses {@link ClassLoader#getResourceAsStream(String)}
     * to find resources.
     */
    static Classpath forClassLoader(ClassLoader classLoader) {
        return classLoader::getResourceAsStream;
    }

    static Classpath contextClasspath() {
        return forClassLoader(Thread.currentThread().getContextClassLoader());
    }

    // </editor-fold>

}
