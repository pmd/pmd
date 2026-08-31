/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.io.TempDirDeletionStrategy;

import net.sourceforge.pmd.cache.internal.FileAnalysisCache;
import net.sourceforge.pmd.cache.internal.NoopAnalysisCache;
import net.sourceforge.pmd.lang.CpdOnlyDummyLanguage;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.CollectionUtil;

class PmdConfigurationTest {
    @TempDir
    private Path tempDir;

    @Test
    void testSuppressMarker() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(PMDConfiguration.DEFAULT_SUPPRESS_MARKER, configuration.getSuppressMarker(), "Default suppress marker");
        configuration.setSuppressMarker("CUSTOM_MARKER");
        assertEquals("CUSTOM_MARKER", configuration.getSuppressMarker(), "Changed suppress marker");
    }

    @Test
    void testThreads() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(Runtime.getRuntime().availableProcessors(), configuration.getThreads(), "Default threads");
        configuration.setThreads(0);
        assertEquals(0, configuration.getThreads(), "Changed threads");
    }

    @Test
    void testClassLoader() throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();
        assertSame(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Default ClassLoader");
        assertNull(configuration.getAuxClasspath(), "Default AuxClasspath should be null");

        String someJar = Files.createFile(tempDir.resolve("some.jar")).toString();
        configuration.prependAuxClasspath(someJar);
        assertEquals(someJar, configuration.getAuxClasspath(), "auxClasspath is missing some.jar");
        // new since 7.27.0 - no classloader is created eagerly anymore
        assertNull(configuration.getClassLoader(), "ClassLoader should be null - not used");

        // reset auxClasspath
        configuration.setAuxClasspath(null);
        assertSame(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Revert to default ClassLoader");
        assertNull(configuration.getAuxClasspath(), "Default AuxClasspath should be null");

        // reset classLoader
        configuration.setClassLoader(null);
        assertSame(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Revert to default ClassLoader");
    }

    /**
     * @deprecated Since 7.27.0. Only tests a deprecated API
     */
    @Test
    @Deprecated
    void testExternallySetClassLoader() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertSame(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Default ClassLoader");
        assertNull(configuration.getAuxClasspath(), "Default AuxClasspath should be null");

        URLClassLoader customClassLoader = new URLClassLoader(new URL[0], PMDConfiguration.class.getClassLoader());
        configuration.setClassLoader(customClassLoader);
        assertSame(customClassLoader, configuration.getClassLoader(), "Not anymore the default ClassLoader");

        // reset
        configuration.setClassLoader(null);
        assertSame(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Revert to default ClassLoader");
    }

    @Test
    void auxClasspathWithAbsoluteFileEmpty() throws IOException {
        Path file = Files.createFile(tempDir.resolve("auxclasspath-empty.cp").toAbsolutePath());
        PMDConfiguration configuration = new PMDConfiguration();
        String auxClasspath = file.toUri().toString();
        configuration.prependAuxClasspath(auxClasspath);
        assertEquals(auxClasspath, configuration.getAuxClasspath());

        // new since 7.27.0 - no classloader is created eagerly anymore
        assertNull(configuration.getClassLoader(), "ClassLoader should be null - not used");
    }

    @Test
    void auxClasspathWithRelativeFileEmpty() {
        String relativeFilePath = "src/test/resources/net/sourceforge/pmd/auxclasspath-empty.cp";
        PMDConfiguration configuration = new PMDConfiguration();
        String auxClasspath = "file:" + relativeFilePath;
        configuration.prependAuxClasspath(auxClasspath);
        assertEquals(auxClasspath, configuration.getAuxClasspath());

        // new since 7.27.0 - no classloader is created eagerly anymore
        assertNull(configuration.getClassLoader(), "ClassLoader should be null - not used");
    }

    @Test
    void auxClasspathWithRelativeFileEmpty2() {
        String relativeFilePath = "./src/test/resources/net/sourceforge/pmd/auxclasspath-empty.cp";
        PMDConfiguration configuration = new PMDConfiguration();
        String auxClasspath = "file:" + relativeFilePath;
        configuration.prependAuxClasspath(auxClasspath);
        assertEquals(auxClasspath, configuration.getAuxClasspath());

        // new since 7.27.0 - no classloader is created eagerly anymore
        assertNull(configuration.getClassLoader(), "ClassLoader should be null - not used");
    }

    @Test
    void auxClasspathWithRelativeFile() throws IOException {
        // when this test runs on a GitHub-hosted Runner under Windows, then the current
        // working directory is on a different drive then the tempDir and we can't
        // create relative paths from working directory to tempDir.
        // That's why we prepare the files for the classpath under the current working directory
        // target/tempdir - which is repo-root/pmd-core/target/tempdir
        Path targetTempDir = Paths.get("target/tempdir").toAbsolutePath();
        Files.createDirectory(targetTempDir);

        try {
            // Prepare auxclasspath.cp file and jar files
            Path currentWorkdirDir = Paths.get(".").toAbsolutePath();
            Path lib1Jar = Files.createFile(targetTempDir.resolve("lib1.jar")).toAbsolutePath();
            Path lib2Jar = Files.createFile(Files.createDirectories(targetTempDir.resolve("other/directory")).resolve("lib2.jar")).toAbsolutePath();
            Path lib3Jar = Files.createFile(targetTempDir.resolve("lib3.jar")).toAbsolutePath();
            Path classes = Files.createDirectory(targetTempDir.resolve("classes")).toAbsolutePath();
            Path classes2 = Files.createDirectory(targetTempDir.resolve("classes2")).toAbsolutePath();
            Path classes3 = Files.createDirectory(targetTempDir.resolve("classes3")).toAbsolutePath();
            Path dirWithSpaces = Files.createDirectories(targetTempDir.resolve("relative source dir/bar")).toAbsolutePath();
            Path auxClasspathFile = targetTempDir.resolve("auxclasspath.cp");
            Files.write(auxClasspathFile, CollectionUtil.listOf(
                    "# relative paths here should be resolved relative to the current working directory - not relative to this file",
                    currentWorkdirDir.relativize(lib1Jar).toString(),
                    currentWorkdirDir.relativize(lib2Jar).toString(),
                    "# absolute paths work as well",
                    lib3Jar.toString(),
                    "# also directories are possible",
                    currentWorkdirDir.relativize(classes).toString(),
                    currentWorkdirDir.relativize(classes2) + File.separator,
                    classes3.toString(),
                    "# relative current directory",
                    ".",
                    "# a test with a space in the uri",
                    currentWorkdirDir.relativize(dirWithSpaces).toString()
            ));

            PMDConfiguration configuration = new PMDConfiguration();
            String auxClasspath = "file:" + currentWorkdirDir.relativize(auxClasspathFile);
            configuration.prependAuxClasspath(auxClasspath);
            assertEquals(auxClasspath, configuration.getAuxClasspath());

            // new since 7.27.0 - no classloader is created eagerly anymore
            assertNull(configuration.getClassLoader(), "ClassLoader should be null - not used");
        } finally {
            TempDirDeletionStrategy.Standard.INSTANCE.delete(targetTempDir, null, null);
        }
    }

    @Test
    void verifyAuxClasspathFiles() throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();

        Path doesNotExistJar = tempDir.resolve("doesNotExist.jar");
        assertFalse(Files.exists(doesNotExistJar), "Test setup failure: the file "
                + doesNotExistJar + " must not exist for the test");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> configuration.prependAuxClasspath(doesNotExistJar.toString()));
        assertThat(exception.getMessage(), containsString(doesNotExistJar.toString()));

        Path doesNotExistJar2 = tempDir.resolve("doesNotExist.JAR");
        assertFalse(Files.exists(doesNotExistJar2), "Test setup failure: the file "
                + doesNotExistJar2 + " must not exist for the test");
        exception = assertThrows(IllegalArgumentException.class, () -> configuration.prependAuxClasspath(doesNotExistJar2.toString()));
        assertThat(exception.getMessage(), containsString(doesNotExistJar2.toString()));

        Path existingJar = Files.createFile(tempDir.resolve("existing.jar"));
        exception = assertThrows(IllegalArgumentException.class, () -> configuration.prependAuxClasspath(existingJar + File.pathSeparator + doesNotExistJar2));
        assertThat(exception.getMessage(), containsString(doesNotExistJar2.toString()));
        assertThat(exception.getMessage(), not(containsString(existingJar.toString())));

        configuration.prependAuxClasspath(existingJar.toString());
        assertEquals(existingJar.toString(), configuration.getAuxClasspath());
    }

    @Test
    void verifyAuxClasspathDirectories() throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();

        Path notExistingDirectory = tempDir.resolve("doesNotExist");
        assertFalse(Files.exists(notExistingDirectory), "Test setup failure: the directory "
            + notExistingDirectory + " must not exist for the test");
        configuration.prependAuxClasspath(notExistingDirectory.toString()); // no exception
        assertEquals(notExistingDirectory.toString(), configuration.getAuxClasspath());

        Path existingJar = Files.createFile(tempDir.resolve("existing.jar"));
        String classpathWithNotExistingDir = existingJar + File.pathSeparator + notExistingDirectory;
        configuration.setAuxClasspath(classpathWithNotExistingDir);
        assertEquals(classpathWithNotExistingDir, configuration.getAuxClasspath());

        Path existingDirectory = Files.createDirectory(tempDir.resolve("existingDirectory"));
        String classpath = existingJar + File.pathSeparator + existingDirectory;
        configuration.setAuxClasspath(classpath);
        assertEquals(classpath, configuration.getAuxClasspath());
    }

    @Test
    void testRuleSets() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertThat(configuration.getRuleSetPaths(), empty());
        configuration.setRuleSets(listOf("/rulesets/basic.xml"));
        assertEquals(listOf("/rulesets/basic.xml"), configuration.getRuleSetPaths());
        configuration.addRuleSet("foo.xml");
        assertEquals(listOf("/rulesets/basic.xml", "foo.xml"), configuration.getRuleSetPaths());
        configuration.setRuleSets(Collections.<String>emptyList());
        assertThat(configuration.getRuleSetPaths(), empty());
        // should be addable even though we set it to an unmodifiable empty list
        configuration.addRuleSet("foo.xml");
        assertEquals(listOf("foo.xml"), configuration.getRuleSetPaths());
    }

    @Test
    void testMinimumPriority() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(RulePriority.LOW, configuration.getMinimumPriority(), "Default minimum priority");
        configuration.setMinimumPriority(RulePriority.HIGH);
        assertEquals(RulePriority.HIGH, configuration.getMinimumPriority(), "Changed minimum priority");
    }

    @Test
    void testSourceEncoding() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(System.getProperty("file.encoding"), configuration.getSourceEncoding().name(), "Default source encoding");
        configuration.setSourceEncoding(StandardCharsets.UTF_16LE);
        assertEquals(StandardCharsets.UTF_16LE, configuration.getSourceEncoding(), "Changed source encoding");
    }

    @Test
    void testReportFormat() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(null, configuration.getReportFormat(), "Default report format");
        configuration.setReportFormat("csv");
        assertEquals("csv", configuration.getReportFormat(), "Changed report format");
    }

    @Test
    void testCreateRenderer() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setReportFormat("csv");
        Renderer renderer = configuration.createRenderer();
        assertEquals(CSVRenderer.class, renderer.getClass(), "Renderer class");
        assertEquals(false, renderer.isShowSuppressedViolations(), "Default renderer show suppressed violations");

        configuration.setShowSuppressedViolations(true);
        renderer = configuration.createRenderer();
        assertEquals(CSVRenderer.class, renderer.getClass(), "Renderer class");
        assertEquals(true, renderer.isShowSuppressedViolations(), "Changed renderer show suppressed violations");
    }

    @Test
    void testShowSuppressedViolations() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(false, configuration.isShowSuppressedViolations(), "Default show suppressed violations");
        configuration.setShowSuppressedViolations(true);
        assertEquals(true, configuration.isShowSuppressedViolations(), "Changed show suppressed violations");
    }

    @Test
    void testReportProperties() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(0, configuration.getReportProperties().size(), "Default report properties size");
        configuration.getReportProperties().put("key", "value");
        assertEquals(1, configuration.getReportProperties().size(), "Changed report properties size");
        assertEquals("value", configuration.getReportProperties().get("key"), "Changed report properties value");
        configuration.setReportProperties(new Properties());
        assertEquals(0, configuration.getReportProperties().size(), "Replaced report properties size");
    }

    @Test
    void testAnalysisCache(@TempDir Path folder) throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();
        assertNotNull(configuration.getAnalysisCache(), "Default cache is null");
        assertTrue(configuration.getAnalysisCache() instanceof NoopAnalysisCache, "Default cache is not a noop");
        configuration.setAnalysisCache(null);
        assertNotNull(configuration.getAnalysisCache(), "Default cache was set to null");

        final File cacheFile = folder.resolve("pmd-cachefile").toFile();
        assertTrue(cacheFile.createNewFile());
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        assertSame(analysisCache, configuration.getAnalysisCache(), "Configured cache not stored");
    }

    @Test
    void testAnalysisCacheLocation() {
        final PMDConfiguration configuration = new PMDConfiguration();

        configuration.setAnalysisCacheLocation(null);
        assertNotNull(configuration.getAnalysisCache(), "Null cache location accepted");
        assertTrue(configuration.getAnalysisCache() instanceof NoopAnalysisCache, "Null cache location accepted");

        configuration.setAnalysisCacheLocation("pmd.cache");
        assertNotNull(configuration.getAnalysisCache(), "Not null cache location produces null cache");
        assertTrue(configuration.getAnalysisCache() instanceof FileAnalysisCache,
                "File cache location doesn't produce a file cache");
    }


    @Test
    void testIgnoreIncrementalAnalysis(@TempDir Path folder) throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();

        // set dummy cache location
        final File cacheFile = folder.resolve("pmd-cachefile").toFile();
        assertTrue(cacheFile.createNewFile());
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        assertNotNull(configuration.getAnalysisCache(), "Null cache location accepted");
        assertFalse(configuration.getAnalysisCache() instanceof NoopAnalysisCache, "Non null cache location, cache should not be noop");

        configuration.setIgnoreIncrementalAnalysis(true);
        assertTrue(configuration.getAnalysisCache() instanceof NoopAnalysisCache, "Ignoring incremental analysis should turn the cache into a noop");
    }

    @Test
    void testCpdOnlyLanguage() {
        final PMDConfiguration configuration = new PMDConfiguration(LanguageRegistry.CPD);

        assertThrows(UnsupportedOperationException.class,
            () -> configuration.setOnlyRecognizeLanguage(CpdOnlyDummyLanguage.getInstance()));
        assertThrows(UnsupportedOperationException.class,
            () -> configuration.setDefaultLanguageVersion(CpdOnlyDummyLanguage.getInstance().getDefaultVersion()));
    }
}
