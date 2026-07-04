/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * Utilities to interpret a string-based classpath.
 * Can be used for the analysis classpath or runtime classpath.
 *
 * <p>The general format is a string consisting of entries separated by {@link File#pathSeparator}.
 * The entries are path names, that might point to a single jar file, a directory (which contains
 * class files) or a wildcard entry pointing to a directory.</p>
 *
 * <p>Additionally, PMD supports a classpath file. If the string starts with {@code file:}, then
 * the whole string is interpreted as a simple path name pointing to a text file (platform encoding).
 * Each line is interpreted as a single classpath entry. Lines starting with {@code #} are comments
 * and are ignored.</p>
 *
 * @see PMDConfiguration#prependAuxClasspath(String)
 * @see PMDConfiguration#getClassLoader()
 * @see PMDConfiguration#setClassLoader(ClassLoader)
 *
 * @since 7.27.0
 */
public final class AnalysisClasspathUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisClasspathUtil.class);

    private AnalysisClasspathUtil() {}

    /**
     * Determines the currently used runtime classpath via the system property
     * {@code java.class.path}. Note: This doesn't include the platform classes
     * like "java.lang.Object".
     */
    public static List<Path> getRuntimeClasspath() {
        return expandAnalysisClasspath(System.getProperty("java.class.path"));
    }

    /**
     * Determines the platform classpath of the currently running JVM. This is either
     * the file {@code lib/rt.jar} or {@code lib/jrt-fs.jar}.
     *
     * <p>Note: The file {@code lib/jrt-fs.jar} doesn't contain the actual class files,
     * but provides a way to access the Modular Runtime Image.</p>
     *
     * @see <a href="https://openjdk.org/jeps/220">JEP 220: Modular Run-Time Images</a>
     */
    public static List<Path> getPlatformClasspath() {
        String javaHome = System.getProperty("java.home");
        Path jrtFsJar = Paths.get(javaHome, "lib", "jrt-fs.jar"); // Java 11+
        Path rtJar = Paths.get(javaHome, "lib", "rt.jar"); // Java 8
        if (Files.isRegularFile(jrtFsJar)) {
            LOG.debug("Found current JVM runtime classes at {}", jrtFsJar);
            return Collections.singletonList(jrtFsJar);
        } else if (Files.isRegularFile(rtJar)) {
            LOG.debug("Found current JVM runtime classes at {}", rtJar);
            return Collections.singletonList(rtJar);
        }
        throw new IllegalStateException("Could not determine current jvm classpath");
    }

    /**
     * Uses the given configuration to either return the classpath entries from a classloader
     * (deprecated functionality) or from the analysis classpath.
     *
     * @see #expandAnalysisClasspath(String)
     */
    public static List<Path> analysisClasspathEntries(PMDConfiguration configuration) {
        List<Path> result = new ArrayList<>();

        ClassLoader classLoader = configuration.getClassLoader();
        try {
            if (classLoader instanceof URLClassLoader) {
                @SuppressWarnings("PMD.CloseResource") // we just need to get the URLs, don't close it here. the classloader will be needed later on...
                URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                for (URL url : urlClassLoader.getURLs()) {
                    result.add(Paths.get(url.toURI()));
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Converts the single string into a list of valid path names. The path names are either single jar files
     * or directories.
     *
     * <p>Note: There is no check performed, whether the entries on the classpath actually exist.
     * Wildcard classpath entries are resolved to the actual jar files, so that we have syntactically
     * valid paths ("*" is an invalid character under Windows for path names).</p>
     *
     * <p>The path names are relative to the working directory, if they were relative before. If they
     * were provided as absolute names, then they are absolute.</p>
     *
     * @throws IllegalArgumentException if there is a problem while resolving wildcard classpath entries.
     */
    public static List<Path> expandAnalysisClasspath(String classpath) {
        if (classpath == null) {
            return Collections.emptyList();
        }

        List<String> entries = new ArrayList<>();

        if (classpath.startsWith("file:")) {
            try {
                URI uri = new URI(classpath);
                String uriPath = uri.getPath();
                if (uriPath == null) {
                    // to support relative paths, only the scheme specific part is available
                    uriPath = uri.getSchemeSpecificPart();
                }
                Path path = Paths.get(uriPath);

                try (Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {
                    entries.addAll(lines
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .filter(s -> !s.startsWith("#"))
                            .collect(Collectors.toList()));
                }
            } catch (IOException | URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
            while (toker.hasMoreTokens()) {
                String token = toker.nextToken().trim();
                entries.add(token);
            }
        }

        List<Path> result = new ArrayList<>();
        for (String entry : entries) {
            if (entry.endsWith("/*") || entry.endsWith("\\*")) {
                Path wildcardDirectory = Paths.get(entry.substring(0, entry.length() - 2));
                try (Stream<Path> stream = Files.list(wildcardDirectory)) {
                    result.addAll(stream
                            .filter(p -> "jar".equalsIgnoreCase(IOUtil.getFilenameExtension(p.getFileName().toString())))
                            .sorted() // make the results deterministic
                            .collect(Collectors.toList()));
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error while resolving wildcard entry '" + entry + "'", e);
                }
            } else {
                result.add(Paths.get(entry));
            }
        }

        return result;
    }
}
