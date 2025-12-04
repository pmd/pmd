/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.impl.Classpath;

/**
 * Wrapper around a classloader that makes the closing behavior explicit.
 * This is designed to allow PMD to manage its own classloader (or internal
 * resource finding logic), if the user only uses classpath strings. It
 * also allows PMD to use a user-provided classloader, or reuse a classloader
 * between several analyses without closing it.
 */
public final class PmdClasspathWrapper implements AutoCloseable {

    private @Nullable MyClassLoaderWrapper current;
    private final AtomicInteger refCount = new AtomicInteger();

    private PmdClasspathWrapper(ClassLoader loader, boolean close) {
        this.current = new MyClassLoaderWrapper(loader, close, null);
    }

    /** Empty instance. */
    private PmdClasspathWrapper() {
    }

    /**
     * Return the underlying classloader or null if this is {@link #emptyClasspath()}.
     *
     * @deprecated For removal. You should not interact with the classloader directly.
     *     This will be removed in a future major version of PMD.
     */
    @Deprecated
    public @Nullable ClassLoader getClassLoader() {
        return current == null ? null : current.loader;
    }

    /**
     * Return an instance of this class that uses PMD's boot classpath
     * to find resources. If this is used for analysis, it is very likely
     * that PMD will not be able to resolve classes during the analysis.
     * This is most likely a mistake in the configuration of PMD, although
     * it only matters if you are analyzing Java code.
     *
     * @return An instance for the boot classloader
     */
    public static PmdClasspathWrapper bootClasspath() {
        return thisClassLoaderWillNotBeClosedByPmd(PmdClasspathWrapper.class.getClassLoader());
    }

    /**
     * Return an instance of this class that will never find any resources.
     * This is useful as a starting point (alternative to {@link #bootClasspath()}
     * for instance) before calling {@link #prependClasspath(String)}.
     *
     * @return An empty instance
     */
    public static PmdClasspathWrapper emptyClasspath() {
        return new PmdClasspathWrapper();
    }

    /**
     * Return an instance of this class that uses the provided classloader
     * to find resources. The provided classloader will not be closed by
     * PMD, and must instead be managed by the caller.
     *
     * @param classLoader A classloader instance.
     * @return A new instance
     */
    public static PmdClasspathWrapper thisClassLoaderWillNotBeClosedByPmd(ClassLoader classLoader) {
        return new PmdClasspathWrapper(classLoader, false);
    }

    /**
     * Prepend the given classpath to this instance. This causes classes
     * and resources to be fetched first on the given classpath, and then
     * defaulted to the behavior of the current instance if the resource
     * is not found on the classpath.
     *
     * <p>A classpath string (the parameter) must be a list of classpath
     * entries separated by {@link File#pathSeparatorChar} characters.
     *
     * <p>Each entry may be a {@code file:} or {@code jar:} scheme URL,
     * or a path string (without scheme) that will be interpreted by
     * {@link Paths#get(String, String...)}.
     *
     * <p>A {@code file:} scheme URL that ends with {@code /} will be interpreted
     * as a directory. A path or {@code file:} scheme URL that refers
     * to a directory will be assumed to contain class files to be loaded
     * as needed. A path or {@code file:} scheme URL that refers to a file,
     * or a {@code jar:} scheme URL, is assumed to refer to a JAR file.
     * This is consistent with how {@link java.net.URLClassLoader} interprets
     * classpath entries.
     *
     * <p>Note: contrary to {@link net.sourceforge.pmd.PMDConfiguration#prependAuxClasspath(String)},
     * this method does not treat {@code file://} URLs specially (it treats them just like
     * {@link java.net.URLClassLoader} would). That other method instead treats
     * them as the path to a text file containing classpath entries written
     * one by line.
     *
     * @param classpath A list of classpath entries separated by {@link File#pathSeparatorChar}
     */
    public void prependClasspath(String classpath) {
        prependClasspathOrClasspathListFile(classpath, false);
    }

    public void prependClasspathOrClasspathListFile(String classpath) {
        prependClasspathOrClasspathListFile(classpath, true);
    }

    private void prependClasspathOrClasspathListFile(String classpath, boolean allowCpListFile) {
        if (StringUtils.isNotBlank(classpath)) {
            ClassLoader parent = current == null ? null : current.loader;
            ClasspathClassLoader newClassloader = new ClasspathClassLoader(classpath, parent, allowCpListFile);
            this.current = new MyClassLoaderWrapper(newClassloader, true, current);
        }
    }

    public Classpath asClasspath() {
        assert refCount.get() > 0 : "Classpath wrapper used after being closed. Did you call subscribe?";

        if (current == null) {
            return x -> null;
        }
        return current.loader::getResourceAsStream;
    }

    @InternalApi
    public AutoCloseable subscribe() {
        refCount.incrementAndGet();
        return () -> {
            // This will be called when the subscriber unsubscribes
            // (closes itself, most likely)
            if (refCount.decrementAndGet() == 0) {
                close();
            }
        };
    }

    @Override
    public void close() throws Exception {
        if (current != null) {
            current.close();
        }
    }

    /**
     * A single node in the chain of classloaders that we build. Keeps
     * references to parents (closing a ClassLoader does not close its parent).
     */
    private static final class MyClassLoaderWrapper implements AutoCloseable {
        private final @NonNull ClassLoader loader;
        private final AtomicBoolean shouldClose;
        private final @Nullable MyClassLoaderWrapper parent;

        private MyClassLoaderWrapper(@NonNull ClassLoader loader, boolean shouldClose, @Nullable MyClassLoaderWrapper parent) {
            this.loader = loader;
            this.shouldClose = new AtomicBoolean(shouldClose);
            this.parent = parent;
        }

        @Override
        public void close() throws Exception {
            try {
                if (shouldClose.compareAndSet(true, false) && loader instanceof AutoCloseable) {
                    // This will only be called at most once, regardless of the number of threads
                    // that have access to the ClassLoaderWrapper instance
                    ((AutoCloseable) loader).close();
                }
            } finally {
                if (parent != null) {
                    parent.close();
                }
            }
        }
    }

}
