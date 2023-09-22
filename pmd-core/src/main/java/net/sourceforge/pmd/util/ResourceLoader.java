/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public class ResourceLoader {

    public static final int TIMEOUT;

    static {
        int timeoutProperty = 5000;
        try {
            timeoutProperty = Integer.parseInt(System.getProperty("net.sourceforge.pmd.http.timeout", "5000"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        TIMEOUT = timeoutProperty;
    }

    private final ClassLoader classLoader;

    /**
     * Constructor for ResourceLoader.
     */
    public ResourceLoader() {
        this(ResourceLoader.class.getClassLoader());
    }

    /**
     * Constructor for ResourceLoader.
     */
    public ResourceLoader(final ClassLoader cl) {
        this.classLoader = Objects.requireNonNull(cl);
    }

    /**
     * Attempts to load the resource from file, a URL or the claspath
     * <p>
     * Caller is responsible for closing the {@link InputStream}.
     *
     * @param name The resource to attempt and load
     *
     * @return InputStream
     */
    public @NonNull InputStream loadResourceAsStream(final String name) throws IOException {
        // Search file locations first
        final File file = new File(name);
        if (file.exists()) {
            try {
                return Files.newInputStream(file.toPath());
            } catch (final IOException e) {
                // if the file didn't exist, we wouldn't be here
                // somehow the file vanished between checking for existence and opening
                throw new IOException("File was checked to exist", e);
            }
        }

        // Maybe it's a url?
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(name).openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            InputStream is = connection.getInputStream();
            if (is != null) {
                return is;
            }
        } catch (final Exception e) {
            return loadClassPathResourceAsStreamOrThrow(name);
        }

        throw new IOException("Can't find resource " + name + ". Make sure the resource is a valid file or URL or is on the classpath");
    }

    public @Nullable InputStream loadClassPathResourceAsStream(final String name) throws IOException {
        /*
         * Don't use getResourceAsStream to avoid reusing connections between threads
         * See https://github.com/pmd/pmd/issues/234
         */
        final URL resource = classLoader.getResource(name);
        if (resource == null) {
            // Don't throw RuleSetNotFoundException, keep API compatibility
            return null;
        } else {
            final URLConnection connection = resource.openConnection();
            // This avoids reusing the underlying file, if the resource is loaded from a Jar file.
            // The file is closed with the input stream then thus not leaving a leaked resource behind.
            // See https://github.com/pmd/pmd/issues/364 and https://github.com/pmd/pmd/issues/337
            connection.setUseCaches(false);
            return connection.getInputStream();
        }
    }

    public @NonNull InputStream loadClassPathResourceAsStreamOrThrow(final String name) throws IOException {
        InputStream is = null;
        try {
            is = loadClassPathResourceAsStream(name);
        } catch (final IOException ignored) {
            // ignored
        }

        if (is == null) {
            throw new FileNotFoundException("Can't find resource " + name
                    + ". Make sure the resource is on the classpath");
        }

        return is;
    }

    /**
     * Load the rule from the classloader from resource loader, consistent with the ruleset
     */
    public Rule loadRuleFromClassPath(final String clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (Rule) classLoader.loadClass(clazz).newInstance();
    }
}
