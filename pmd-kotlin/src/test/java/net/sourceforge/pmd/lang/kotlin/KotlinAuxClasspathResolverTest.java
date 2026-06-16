/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class KotlinAuxClasspathResolverTest {

    @TempDir
    Path tempDir;

    // --- filterEntries ---

    @Test
    void filterEntriesKeepsExistingDirectory() throws IOException {
        Path dir = Files.createTempDirectory(tempDir, "classes");
        List<Path> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(dir));
        assertEquals(1, result.size());
        assertEquals(dir, result.get(0));
    }

    @Test
    void filterEntriesKeepsExistingJar() throws IOException {
        Path jar = Files.createTempFile(tempDir, "lib", ".jar");
        List<Path> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(jar));
        assertEquals(1, result.size());
    }

    @Test
    void filterEntriesDropsNonExistentPath() {
        Path missing = Paths.get("/nonexistent/path/missing.jar");
        List<Path> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(missing));
        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesDropsExistingNonJarFile() throws IOException {
        Path txt = Files.createTempFile(tempDir, "readme", ".txt");
        List<Path> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(txt));
        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesHandlesMixedEntries() throws IOException {
        Path dir = Files.createTempDirectory(tempDir, "classes");
        Path jar = Files.createTempFile(tempDir, "lib", ".jar");
        Path missing = Paths.get("/nonexistent/missing.jar");

        List<Path> result = KotlinAuxClasspathResolver.filterEntries(
                Arrays.asList(dir, jar, missing));
        assertEquals(2, result.size());
        assertTrue(result.contains(dir));
        assertTrue(result.contains(jar));
        assertFalse(result.contains(missing));
    }

    // --- resolve() ---

    @Test
    void resolveUsesAuxClasspathStringProperty() throws IOException {
        Path jar = Files.createTempFile(tempDir, "mylib", ".jar");
        Path dir = Files.createTempDirectory(tempDir, "classes");

        String cp = jar.toAbsolutePath() + java.io.File.pathSeparator + dir.toAbsolutePath();
        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance());
        props.setProperty(KotlinLanguageProperties.AUX_CLASSPATH, cp);

        List<Path> result = new KotlinAuxClasspathResolver(props).resolve();
        assertEquals(2, result.size());
        assertTrue(result.contains(jar));
        assertTrue(result.contains(dir));
    }

    @Test
    void resolveStringPropertyFiltersInvalidEntries() throws IOException {
        Path jar = Files.createTempFile(tempDir, "valid", ".jar");
        String cp = jar.toAbsolutePath() + java.io.File.pathSeparator + "/nonexistent/missing.jar";

        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance());
        props.setProperty(KotlinLanguageProperties.AUX_CLASSPATH, cp);

        List<Path> result = new KotlinAuxClasspathResolver(props).resolve();
        assertEquals(1, result.size());
        assertEquals(jar, result.get(0));
    }

    @Test
    void resolveReturnsEmptyWhenPropertyNotSet() {
        KotlinLanguageProperties props = new KotlinLanguageProperties(KotlinLanguageModule.getInstance());
        List<Path> result = new KotlinAuxClasspathResolver(props).resolve();
        assertTrue(result.isEmpty());
    }
}
