/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;

/**
 * Resolves the auxiliary classpath entries for Kotlin type analysis.
 *
 * <p>Reads the {@link JvmLanguagePropertyBundle#AUX_CLASSPATH auxClasspath} language
 * property, splits it on the platform path separator, and returns only entries that
 * exist and are either a JAR file or a directory.
 *
 * <p>Configure via {@code --aux-classpath} on the command line or via
 * {@link KotlinLanguageProperties#AUX_CLASSPATH} in API usage.
 */
final class KotlinAuxClasspathResolver {

    private static final Logger LOG = LoggerFactory.getLogger(KotlinAuxClasspathResolver.class);

    private final JvmLanguagePropertyBundle bundle;

    KotlinAuxClasspathResolver(JvmLanguagePropertyBundle bundle) {
        this.bundle = bundle;
    }

    List<Path> resolve() {
        String raw = bundle.getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyList();
        }
        String sep = System.getProperty("path.separator", ":");
        List<Path> entries = new ArrayList<>();
        for (String entry : raw.split(sep, -1)) {
            String trimmed = entry.trim();
            if (!trimmed.isEmpty()) {
                entries.add(Paths.get(trimmed));
            }
        }
        LOG.debug("Kotlin aux classpath from auxClasspath property ({} entries)", entries.size());
        return filterEntries(entries);
    }

    static List<Path> filterEntries(List<Path> entries) {
        List<Path> filtered = new ArrayList<>(entries.size());
        for (Path entry : entries) {
            if (Files.exists(entry) && (Files.isDirectory(entry) || entry.getFileName().toString().endsWith(".jar"))) {
                filtered.add(entry);
            } else {
                LOG.warn("Skipping invalid Kotlin aux classpath entry: {}", entry);
            }
        }
        return filtered;
    }
}
