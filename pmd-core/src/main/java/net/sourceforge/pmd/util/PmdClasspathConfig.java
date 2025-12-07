/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cache.internal.ClasspathFingerprinter;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader.ParsedClassPath;
import net.sourceforge.pmd.lang.impl.Classpath;

import com.google.errorprone.annotations.CheckReturnValue;

/**
 * Wrapper around a classpath used to find resources on the classpath.
 * Instances store construction steps to build a classloader in a managed
 * way. This clarifies ownership of classloader resources, and therefore the
 * responsibility for closing them.
 *
 * <p>These are the main ways to obtain an instance, corresponding to
 * the fallback behavior of the classpath:
 * <ul>
 * <li>Users may create an instance to wrap a user-provided classloader,
 * in which case the responsibility for closing it is on the caller. See
 * {@link #thisClassLoaderWillNotBeClosedByPmd(ClassLoader)}.
 * <li>{@link #bootClasspath()} uses the classloader used to load this
 * class (and PMD sources).
 * </ul>
 *
 * <p>Any instance of this class may be additionally prepended with a
 * string classpath (see {@link #prependClasspath(String)}), which will
 * load classes and resources from a set of JAR files and directories.
 * This will be managed entirely by PMD without leaking resources.
 *
 * <p>Note that this class does not implement {@link java.io.Closeable},
 * because it doesn't own any resources. Calling {@link #open()} builds
 * the effective classloader and may allocate resources that MUST be
 * closed afterward, by the caller of {@link #open()}. This will most
 * typically be called internally by PMD.
 */
public final class PmdClasspathConfig {
    private static final @NonNull ParsedClassPath EMPTY_CP =
        ClasspathClassLoader.parseClasspath("", false);

    private final @NonNull ClassLoader fallback; // for now it is nonnull
    private final @NonNull ParsedClassPath classpath;

    private PmdClasspathConfig(@NonNull ClassLoader fallback, @NonNull ParsedClassPath classpath) {
        this.fallback = fallback;
        this.classpath = classpath;
    }

    // test only
    @InternalApi
    public @NonNull ParsedClassPath getClasspath() {
        return classpath;
    }

    // test only
    @InternalApi
    public @NonNull ClassLoader getFallback() {
        return fallback;
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
    public static PmdClasspathConfig bootClasspath() {
        return thisClassLoaderWillNotBeClosedByPmd(PmdClasspathConfig.class.getClassLoader());
    }

    /**
     * Return an instance of this class that uses the provided classloader
     * to find resources. The provided classloader will not be closed by
     * PMD, and must instead be managed by the caller.
     *
     * @param classLoader A classloader instance.
     * @return A new instance
     */
    public static PmdClasspathConfig thisClassLoaderWillNotBeClosedByPmd(ClassLoader classLoader) {
        return new PmdClasspathConfig(classLoader, EMPTY_CP);
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
     * @throws IllegalArgumentException If some path is incorrect or a malformed URI
     */
    @CheckReturnValue
    public PmdClasspathConfig prependClasspath(String classpath) {
        return prependClasspathOrClasspathListFile(classpath, false);
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
     * @throws IllegalArgumentException If some path is incorrect or a malformed URI
     */
    @CheckReturnValue
    public PmdClasspathConfig prependClasspathOrClasspathListFile(String classpath) {
        return prependClasspathOrClasspathListFile(classpath, true);
    }

    private PmdClasspathConfig prependClasspathOrClasspathListFile(String classpath, boolean allowCpListFile) {
        if (StringUtils.isBlank(classpath)) {
            return this;
        }
        ParsedClassPath parsed = ClasspathClassLoader.parseClasspath(classpath, allowCpListFile);
        return new PmdClasspathConfig(
            this.fallback,
            this.classpath.prepend(parsed)
        );
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
    @CheckReturnValue
    public OpenClasspath open() {
        ClassLoader classLoader = fallback;
        boolean shouldClose = false;
        if (!classpath.isEmpty()) {
            classLoader = new ClasspathClassLoader(classpath, classLoader);
            shouldClose = true;
        }
        return new OpenClasspath(classLoader, shouldClose);
    }


    /**
     * Used internally to fingerprint classpath entries and figure out
     * if they have changed between runs.
     *
     * @param fingerprinter A fingerprinter
     */
    @InternalApi
    public long fingerprint(ClasspathFingerprinter fingerprinter) {
        long fingerprint = 0;
        if (!classpath.isEmpty()) {
            fingerprint = classpath.fingerprint(fingerprinter);
        }
        if (fallback instanceof URLClassLoader) {
            List<URL> urls = Arrays.asList(((URLClassLoader) fallback).getURLs());
            fingerprint = 31 * fingerprint + fingerprinter.fingerprint(urls);
        }
        return fingerprint;
    }

    /**
     * This method should not be used as it may leak resources. It may
     * allocate a new classloader. The caller should check whether the
     * return value is {@link AutoCloseable} and close it if it is.
     *
     */
    @Deprecated
    @InternalApi
    public ClassLoader leakClassLoader() {
        //noinspection resource
        return open().myClassLoader;
    }

    public static final class OpenClasspath implements AutoCloseable, Classpath {
        private final ClassLoader myClassLoader;
        private final AtomicBoolean shouldClose;

        private OpenClasspath(@NonNull ClassLoader myClassLoader, boolean shouldClose) {
            this.myClassLoader = myClassLoader;
            this.shouldClose = new AtomicBoolean(shouldClose);
        }

        // test only
        ClassLoader classLoader() {
            return myClassLoader;
        }

        // test only
        boolean shouldClose() {
            return shouldClose.get();
        }

        @Override
        public @Nullable InputStream findResource(String resourcePath) {
            return myClassLoader.getResourceAsStream(resourcePath);
        }

        @Override
        public void close() throws Exception {
            if (shouldClose.compareAndSet(true, false) && myClassLoader instanceof AutoCloseable) {
                // This will only be called at most once, regardless of the number of threads
                // that have access to the ClassLoaderWrapper instance
                ((AutoCloseable) myClassLoader).close();
            }
        }
    }
}
