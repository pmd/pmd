/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;

/**
 * Resolves the auxiliary classpath entries to pass to kotlin-type-mapper so it
 * can load and resolve external types (e.g. Spring, JPA, Android SDK).
 *
 * <p>Resolution order:
 * <ol>
 *   <li>String property -- set when {@code --aux-classpath} is passed on the command line.</li>
 *   <li>URLClassLoader hierarchy -- how the PMD Designer propagates the auxiliary classpath.</li>
 *   <li>{@code java.class.path} system property -- Maven Surefire puts all test dependencies here.</li>
 * </ol>
 *
 * <p>Each candidate entry is validated: only existing JARs and directories are kept.
 */
final class KotlinAuxClasspathResolver {

    private static final Logger LOG = LoggerFactory.getLogger(KotlinAuxClasspathResolver.class);
    private static final String FILE_PROTOCOL = "file";

    private final JvmLanguagePropertyBundle bundle;

    KotlinAuxClasspathResolver(JvmLanguagePropertyBundle bundle) {
        this.bundle = bundle;
    }

    List<File> resolve() {
        // 1. String property (set via --aux-classpath on the command line)
        String raw = bundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH);
        if (raw != null && !raw.isEmpty()) {
            String sep = System.getProperty("path.separator", ":");
            List<File> entries = new ArrayList<>();
            for (String entry : raw.split(Pattern.quote(sep))) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    entries.add(new File(trimmed));
                }
            }
            LOG.debug("kotlin-type-mapper aux classpath from string property ({} entries)", entries.size());
            return filterEntries(entries, "aux-classpath property");
        }
        // 2. URLClassLoader hierarchy -- PMD Designer (and CLI via ClasspathClassLoader) sets this.
        ClassLoader cl = bundle.getAnalysisClassLoader();
        List<File> urlEntries = new ArrayList<>();
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader) cl).getURLs()) {
                    if (FILE_PROTOCOL.equals(url.getProtocol())) {
                        try {
                            urlEntries.add(new File(url.toURI()));
                        } catch (URISyntaxException e) {
                            LOG.debug("Could not convert classpath URL to File: {}", url);
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
        if (!urlEntries.isEmpty()) {
            LOG.debug("kotlin-type-mapper aux classpath from URLClassLoader hierarchy ({} entries)", urlEntries.size());
            return filterEntries(urlEntries, "analysis classloader");
        }
        // 3. java.class.path system property -- Maven Surefire puts all test dependencies here.
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null && !javaClassPath.isEmpty()) {
            List<File> entries = new ArrayList<>();
            for (String entry : javaClassPath.split(Pattern.quote(File.pathSeparator))) {
                if (!entry.isEmpty()) {
                    entries.add(new File(entry));
                }
            }
            LOG.debug("kotlin-type-mapper aux classpath from java.class.path ({} entries)", entries.size());
            return filterEntries(entries, "java.class.path");
        }
        return new ArrayList<>();
    }

    /**
     * Filters a candidate list down to entries that exist and are either a JAR
     * or a directory. Invalid entries are logged as warnings.
     */
    static List<File> filterEntries(List<File> entries, String source) {
        List<File> filtered = new ArrayList<>(entries.size());
        for (File entry : entries) {
            if (entry.exists() && (entry.isDirectory() || entry.getName().endsWith(".jar"))) {
                filtered.add(entry);
            } else {
                LOG.warn("Skipping invalid Kotlin aux classpath entry from {}: {}", source, entry);
            }
        }
        return filtered;
    }
}
