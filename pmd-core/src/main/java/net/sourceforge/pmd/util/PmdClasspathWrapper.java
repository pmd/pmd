/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.impl.Classpath;

/**
 * Wrapper around a classloader that makes the closing behavior explicit.
 *
 * <ul>
 * <li>An instance may wrap a user-provided classloader, in which case
 * the responsibility for closing it is on the caller. See
 * {@link #thisClassLoaderWillNotBeClosedByPmd(ClassLoader)}.
 * <li>{@link #bootClasspath()} uses the classloader used to load this
 * class (and PMD sources).
 * <li>{@link #emptyClasspath()} does not load any classes.
 * </ul>
 *
 * <p>Any of these instances may be prepended with a string classpath
 * (see {@link #prependClasspath(String)}), which creates a classloader
 * managed internally by PMD. Note that calls to {@link #prependClasspath(String)}
 * do not immediately create this classloader. It is created by the first
 * call to {@link #open()} and destroyed by the call to {@link #close()}.
 * The call to {@link #close()} may be performed manually, else it will
 * be done when the last {@link OpenClasspath} instance obtained from
 * {@link #open()} is closed.
 *
 *
 */
public final class PmdClasspathWrapper implements AutoCloseable {

    private @Nullable MyClassLoaderWrapper current;
    private final AtomicInteger refCount = new AtomicInteger();

    private PmdClasspathWrapper(ClassLoaderMaker loader, boolean close) {
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
        return current == null ? null : current.getClassLoader();
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
    // TODO make this the default and require that people put JDK sources
    //  on the classpath explicitly.
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
        return new PmdClasspathWrapper(x -> classLoader, false);
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
     * <p>Note: contrary to {@link PMDConfiguration#prependAuxClasspath(String)},
     * this method does not treat {@code file://} URLs specially (it treats them just like
     * {@link java.net.URLClassLoader} would). That other method instead treats
     * them as the path to a text file containing classpath entries written
     * one by line. Use {@link #prependClasspathOrClasspathListFile(String)} for this
     * behavior.
     *
     * @param classpath A list of classpath entries separated by {@link File#pathSeparatorChar}
     *
     * @return This instance
     */
    public PmdClasspathWrapper prependClasspath(String classpath) {
        prependClasspathOrClasspathListFile(classpath, false);
        return this;
    }

    /**
     * Prepend the given classpath to this instance. This causes classes
     * and resources to be fetched first on the given classpath, and then
     * defaulted to the behavior of the current instance if a resource
     * is not found on the classpath.
     *
     * <p>This method uses the same format as {@link #prependClasspath(String)},
     * but if the parameter starts with {@code file://}, it will be interpreted
     * as the path to a text file containing classpath entries, one by line.
     * This is the behavior of {@link PMDConfiguration#prependAuxClasspath(String)}.
     *
     * @param classpath A list of classpath entries separated by {@link File#pathSeparatorChar}
     * @return This instance
     */
    public PmdClasspathWrapper prependClasspathOrClasspathListFile(String classpath) {
        prependClasspathOrClasspathListFile(classpath, true);
        return this;
    }

    private void prependClasspathOrClasspathListFile(String classpath, boolean allowCpListFile) {
        if (StringUtils.isNotBlank(classpath)) {
            this.current = new MyClassLoaderWrapper(
                parent -> new ClasspathClassLoader(classpath, parent, allowCpListFile),
                true, current);
        }
    }

    /**
     * Return an object that witnesses that the classpath has been opened.
     * Any call to {@link OpenClasspath#findResource(String)} needs to be
     * done before closing that instance. The instance must be closed by
     * the caller when they are done. It is possible for several threads
     * to run analyses concurrently with the same classpath wrapper object.
     * In this case the classpath will only ever be closed by the last
     * analysis that finishes.
     */
    @InternalApi
    public OpenClasspath open() {
        return new OpenClasspath();
    }

    public final class OpenClasspath implements AutoCloseable, Classpath {
        private OpenClasspath() {
            refCount.incrementAndGet();
        }

        @Override
        public @Nullable InputStream findResource(String resourcePath) {
            assert refCount.get() > 0 : "Classpath wrapper used after being closed";
            return current == null ? null : current.getClassLoader().getResourceAsStream(resourcePath);
        }

        @Override
        public void close() throws Exception {
            if (refCount.decrementAndGet() == 0) {
                PmdClasspathWrapper.this.close();
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (current != null) {
            current.close();
        }
    }

    /**
     * A single node in the chain of classloaders that we build. Keeps
     * references to parents (closing a ClassLoader does not close its parent
     * so if we want to close them all we need to keep all the chain).
     *
     * <p>This class lazily builds the classloader from its parent. If
     * we were to build them eagerly, there is a risk of memory leak.
     * You could write for instance
     * <pre>{@code
     * PMDConfiguration config = new PMDConfiguration();
     * // This internally would create a ClasspathClassLoader
     * // that is the responsibility of PMD to close.
     * config.prependClasspath("...");
     * // This would clear the previous ClasspathClassLoader
     * // without closing it.
     * config.setAnalysisClasspath(PmdClasspathWrapper.bootClasspath());
     * }</pre>
     * It is not possible to specify {@link PMDConfiguration#setAnalysisClasspath(PmdClasspathWrapper)} to
     * close the previous wrapper, because then you could write
     * <pre>{@code
     * PMDConfiguration config = new PMDConfiguration();
     * PmdClasspathWrapper classpath = PmdClasspathWrapper.bootClasspath().prependClasspath("...");
     * config.setAnalysisClasspath(classpath);
     * // This would close the instance, and set it back to the same
     * // (closed) value.
     * config.setAnalysisClasspath(classpath);
     * }</pre>
     * We need {@link PMDConfiguration#setAnalysisClasspath(PmdClasspathWrapper)}
     * because we want to be able to write
     * <pre>{@code
     * config.setAnalysisClasspath(PmdClasspathWrapper.thisClassLoaderWillNotBeClosedByPmd(classLoader));
     * }</pre>
     * to use a custom classloader provided by the user.
     *
     */
    private static final class MyClassLoaderWrapper implements AutoCloseable {
        private final @NonNull ClassLoaderMaker loader;
        private volatile ClassLoader myClassLoader; // NOPMD Field must be volatile as we use it in double-checked locking

        private final AtomicBoolean shouldClose;
        private final @Nullable MyClassLoaderWrapper parent;

        private MyClassLoaderWrapper(@NonNull ClassLoaderMaker makeClassloader, boolean shouldClose, @Nullable MyClassLoaderWrapper parent) {
            this.loader = makeClassloader;
            this.shouldClose = new AtomicBoolean(shouldClose);
            this.parent = parent;
        }

        ClassLoader getClassLoader() {
            if (myClassLoader == null) {
                synchronized (this) {
                    if (myClassLoader == null) {
                        ClassLoader parent = this.parent == null ? null : this.parent.getClassLoader();
                        myClassLoader = loader.build(parent);
                    }
                }
            }
            return myClassLoader;
        }

        @Override
        public void close() throws Exception {
            try {
                if (shouldClose.compareAndSet(true, false) && myClassLoader instanceof AutoCloseable) {
                    // This will only be called at most once, regardless of the number of threads
                    // that have access to the ClassLoaderWrapper instance
                    ((AutoCloseable) myClassLoader).close();
                }
            } finally {
                if (parent != null) {
                    parent.close();
                }
            }
        }
    }

    interface ClassLoaderMaker {
        ClassLoader build(@Nullable ClassLoader parent);
    }
}
