/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KotlinAuxClasspathResolverTest {

    @TempDir
    File tempDir;

    // --- filterEntries ---

    @Test
    void filterEntriesKeepsExistingJar() throws IOException {
        File jar = new File(tempDir, "lib.jar");
        Files.createFile(jar.toPath());

        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(jar), "test");

        assertEquals(1, result.size());
        assertTrue(result.contains(jar));
    }

    @Test
    void filterEntriesKeepsExistingDirectory() {
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(tempDir), "test");

        assertEquals(1, result.size());
        assertTrue(result.contains(tempDir));
    }

    @Test
    void filterEntriesDropsNonexistentFile() {
        File missing = new File(tempDir, "missing.jar");

        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(missing), "test");

        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesDropsExistingNonJarFile() throws IOException {
        File xml = new File(tempDir, "config.xml");
        Files.createFile(xml.toPath());

        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.singletonList(xml), "test");

        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesHandlesEmptyList() {
        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Collections.emptyList(), "test");

        assertTrue(result.isEmpty());
    }

    @Test
    void filterEntriesRetainsOnlyValid() throws IOException {
        File jar = new File(tempDir, "lib.jar");
        Files.createFile(jar.toPath());
        File missing = new File(tempDir, "gone.jar");
        File xml = new File(tempDir, "conf.xml");
        Files.createFile(xml.toPath());

        List<File> result = KotlinAuxClasspathResolver.filterEntries(
                Arrays.asList(jar, missing, xml, tempDir), "test");

        assertEquals(2, result.size());
        assertFalse(result.contains(missing));
        assertFalse(result.contains(xml));
    }
}
