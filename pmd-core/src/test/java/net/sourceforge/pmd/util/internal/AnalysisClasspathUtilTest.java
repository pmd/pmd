/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.util.CollectionUtil;

class AnalysisClasspathUtilTest {

    @TempDir
    private Path tempDir;

    @Test
    void expandAnalysisClasspathEmpty() {
        assertEquals(emptyList(), AnalysisClasspathUtil.expandAnalysisClasspath(null));
        assertEquals(emptyList(), AnalysisClasspathUtil.expandAnalysisClasspath(""));
    }

    @Test
    void expandAnalysisClasspathSimpleEntries() {
        assertEquals(CollectionUtil.listOf(
                Paths.get("a.jar"),
                Paths.get("b/c.jar"),
                Paths.get("b/file with space.jar"),
                Paths.get("/d/e/f.jar"),
                Paths.get("libs"),
                Paths.get("libs2/"),
                Paths.get("/usr/lib")
        ), AnalysisClasspathUtil.expandAnalysisClasspath(
                "a.jar" + File.pathSeparator
                // note: whitespaces are trimmed
                + "  b/c.jar  " + File.pathSeparator
                + "b/file with space.jar" + File.pathSeparator
                + "/d/e/f.jar" + File.pathSeparator
                + "libs" + File.pathSeparator
                + "libs2/" + File.pathSeparator
                + "/usr/lib"));
    }

    @Test
    void expandAnalysisClasspathFile() throws IOException {
        Path wildcardDirectory = prepareDirectory("mylibs", "file1.jar", "file2.JAR", "foo.txt");
        Path classpathFile = tempDir.resolve("classpath.cp");
        Files.write(classpathFile, CollectionUtil.listOf(
                "# comments are possible",
                "a.jar",
                "  b/c.jar  ",
                "b/file with space.jar",
                "/d/e/f.jar",
                "libs",
                "libs2/",
                "/usr/lib",
                wildcardDirectory + "/*"
        ), Charset.defaultCharset());

        assertEquals(CollectionUtil.listOf(
                Paths.get("a.jar"),
                Paths.get("b/c.jar"),
                Paths.get("b/file with space.jar"),
                Paths.get("/d/e/f.jar"),
                Paths.get("libs"),
                Paths.get("libs2/"),
                Paths.get("/usr/lib"),
                wildcardDirectory.resolve("file1.jar"),
                wildcardDirectory.resolve("file2.JAR")
        ), AnalysisClasspathUtil.expandAnalysisClasspath("file:" + classpathFile));
    }

    @Test
    void expandAnalysisClasspathFileEmpty() throws IOException {
        Path classpathFile = tempDir.resolve("classpath.cp");
        Files.write(classpathFile, CollectionUtil.listOf(
                "# empty classpath...",
                "# and empty lines",
                ""
        ), Charset.defaultCharset());

        assertEquals(Collections.emptyList(), AnalysisClasspathUtil.expandAnalysisClasspath("file:" + classpathFile));
    }

    @Test
    void expandAnalysisClasspathWildcard() throws IOException {
        Path wildcardDirectory = prepareDirectory("mylibs", "file1.jar", "file2.JAR", "foo.txt");
        assertEquals(CollectionUtil.listOf(
                wildcardDirectory.resolve("file1.jar"),
                wildcardDirectory.resolve("file2.JAR")
        ), AnalysisClasspathUtil.expandAnalysisClasspath(wildcardDirectory + File.separator + "*"));
    }

    private Path prepareDirectory(String directoryName, String... fileNames) throws IOException {
        Path wildcardDirectory = tempDir.resolve(directoryName);
        Files.createDirectory(wildcardDirectory);
        for (String fileName : fileNames) {
            Files.createFile(wildcardDirectory.resolve(fileName));
        }
        return wildcardDirectory;
    }

    @Test
    void fromConfigurationClassLoader() throws IOException {
        Path aJar = Files.createFile(tempDir.resolve("a.jar"));
        PMDConfiguration configuration = new PMDConfiguration();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {
                aJar.toUri().toURL(),
        });
        configuration.setClassLoader(urlClassLoader);

        assertEquals(CollectionUtil.listOf(
                aJar
        ), AnalysisClasspathUtil.analysisClasspathEntries(configuration));
    }
}
