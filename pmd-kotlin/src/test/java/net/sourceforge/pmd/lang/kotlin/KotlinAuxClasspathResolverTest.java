/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;

class KotlinAuxClasspathResolverTest {

    @TempDir
    Path tempDir;

    // --- filterEntries ---

    @Test
    void filterEntriesKeepsExistingDirectory() throws IOException {
        Path dir = Files.createTempDirectory(tempDir, "classes");
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(dir.toFile()), "test");
        assertEquals(1, result.size());
        assertEquals(dir.toFile(), result.get(0));
    }

    @Test
    void filterEntriesKeepsExistingJar() throws IOException {
        Path jar = Files.createTempFile(tempDir, "lib", ".jar");
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(jar.toFile()), "test");
        assertEquals(1, result.size());
    }

    @Test
    void filterEntriesDropsNonExistentPath() {
        File missing = new File("/nonexistent/path/missing.jar");
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(missing), "test");
        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesDropsExistingNonJarFile() throws IOException {
        Path txt = Files.createTempFile(tempDir, "readme", ".txt");
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(txt.toFile()), "test");
        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesHandlesMixedEntries() throws IOException {
        Path dir = Files.createTempDirectory(tempDir, "classes");
        Path jar = Files.createTempFile(tempDir, "lib", ".jar");
        File missing = new File("/nonexistent/missing.jar");

        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Arrays.asList(dir.toFile(), jar.toFile(), missing), "test");
        assertEquals(2, result.size());
        assertTrue(result.contains(dir.toFile()));
        assertTrue(result.contains(jar.toFile()));
        assertFalse(result.contains(missing));
    }

    // --- resolve() tier 1: string property ---

    @Test
    void resolveUsesAuxClasspathStringProperty() throws IOException {
        Path jar = Files.createTempFile(tempDir, "mylib", ".jar");
        Path dir = Files.createTempDirectory(tempDir, "classes");

        String cp = jar.toAbsolutePath() + File.pathSeparator + dir.toAbsolutePath();
        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance());
        props.setProperty(KotlinLanguageProperties.AUX_CLASSPATH, cp);

        List<File> result = new KotlinAuxClasspathResolver(props).resolve();
        assertEquals(2, result.size());
        assertTrue(result.contains(jar.toFile()));
        assertTrue(result.contains(dir.toFile()));
    }

    @Test
    void resolveStringPropertyFiltersInvalidEntries() throws IOException {
        Path jar = Files.createTempFile(tempDir, "valid", ".jar");
        String cp = jar.toAbsolutePath() + File.pathSeparator + "/nonexistent/missing.jar";

        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance());
        props.setProperty(KotlinLanguageProperties.AUX_CLASSPATH, cp);

        List<File> result = new KotlinAuxClasspathResolver(props).resolve();
        assertEquals(1, result.size());
        assertEquals(jar.toFile(), result.get(0));
    }

    // --- resolve() tier 2: URLClassLoader ---

    @Test
    void resolveUsesUrlClassLoader() throws IOException {
        Path jar = Files.createTempFile(tempDir, "urllib", ".jar");
        URL[] urls = { jar.toUri().toURL() };
        URLClassLoader ucl = new URLClassLoader(urls, null);

        // Subclass to inject URLClassLoader as analysis classloader
        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance()) {
            @Override
            public ClassLoader getAnalysisClassLoader() {
                return ucl;
            }
        };

        List<File> result = new KotlinAuxClasspathResolver(props).resolve();
        assertEquals(1, result.size());
        assertEquals(jar.toFile(), result.get(0));
        ucl.close();
    }
}
