/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cache.FileAnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.Renderer;

class PmdConfigurationTest {

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
    void testClassLoader() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(), "Default ClassLoader");
        configuration.prependAuxClasspath("some.jar");
        assertEquals(ClasspathClassLoader.class, configuration.getClassLoader().getClass(),
                "Prepended ClassLoader class");
        URL[] urls = ((ClasspathClassLoader) configuration.getClassLoader()).getURLs();
        assertEquals(1, urls.length, "urls length");
        assertTrue(urls[0].toString().endsWith("/some.jar"), "url[0]");
        assertEquals(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader().getParent(),
                "parent classLoader");
        configuration.setClassLoader(null);
        assertEquals(PMDConfiguration.class.getClassLoader(), configuration.getClassLoader(),
                "Revert to default ClassLoader");
    }

    @Test
    void auxClasspathWithRelativeFileEmpty() {
        String relativeFilePath = "src/test/resources/net/sourceforge/pmd/cli/auxclasspath-empty.cp";
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.prependAuxClasspath("file:" + relativeFilePath);
        URL[] urls = ((ClasspathClassLoader) configuration.getClassLoader()).getURLs();
        assertEquals(0, urls.length);
    }

    @Test
    void auxClasspathWithRelativeFileEmpty2() {
        String relativeFilePath = "./src/test/resources/net/sourceforge/pmd/cli/auxclasspath-empty.cp";
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.prependAuxClasspath("file:" + relativeFilePath);
        URL[] urls = ((ClasspathClassLoader) configuration.getClassLoader()).getURLs();
        assertEquals(0, urls.length);
    }

    @Test
    void auxClasspathWithRelativeFile() throws URISyntaxException {
        final String FILE_SCHEME = "file";

        String currentWorkingDirectory = new File("").getAbsoluteFile().toURI().getPath();
        String relativeFilePath = "src/test/resources/net/sourceforge/pmd/cli/auxclasspath.cp";
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.prependAuxClasspath("file:" + relativeFilePath);
        URL[] urls = ((ClasspathClassLoader) configuration.getClassLoader()).getURLs();
        URI[] uris = new URI[urls.length];
        for (int i = 0; i < urls.length; i++) {
            uris[i] = urls[i].toURI();
        }
        URI[] expectedUris = new URI[] {
            new URI(FILE_SCHEME, null, currentWorkingDirectory + "lib1.jar", null),
            new URI(FILE_SCHEME, null, currentWorkingDirectory + "other/directory/lib2.jar", null),
            new URI(FILE_SCHEME, null, new File("/home/jondoe/libs/lib3.jar").getAbsoluteFile().toURI().getPath(), null),
            new URI(FILE_SCHEME, null, currentWorkingDirectory + "classes", null),
            new URI(FILE_SCHEME, null, currentWorkingDirectory + "classes2", null),
            new URI(FILE_SCHEME, null, new File("/home/jondoe/classes").getAbsoluteFile().toURI().getPath(), null),
            new URI(FILE_SCHEME, null, currentWorkingDirectory, null),
            new URI(FILE_SCHEME, null, currentWorkingDirectory + "relative source dir/bar", null),
        };
        assertArrayEquals(expectedUris, uris);
    }

    @Test
    void testRuleSetsLegacy() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertNull(configuration.getRuleSets(), "Default RuleSets");
        configuration.setRuleSets("/rulesets/basic.xml");
        assertEquals("/rulesets/basic.xml", configuration.getRuleSets(), "Changed RuleSets");
        configuration.setRuleSets((String) null);
        assertNull(configuration.getRuleSets());
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
        configuration.setSourceEncoding(StandardCharsets.UTF_16LE.name());
        assertEquals(StandardCharsets.UTF_16LE, configuration.getSourceEncoding(), "Changed source encoding");
    }

    @Test
    void testInputPaths() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertThat(configuration.getInputPathList(), empty());
        configuration.setInputPaths("a,b,c");
        List<Path> expected = listOf(
            Paths.get("a"), Paths.get("b"), Paths.get("c")
        );
        assertEquals(expected, configuration.getInputPathList(), "Changed input paths");
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
    void testReportFile() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(null, configuration.getReportFile(), "Default report file");
        configuration.setReportFile("somefile");
        assertEquals("somefile", configuration.getReportFile(), "Changed report file");
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
    void testDebug() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(false, configuration.isDebug(), "Default debug");
        configuration.setDebug(true);
        assertEquals(true, configuration.isDebug(), "Changed debug");
    }

    @Test
    void testStressTest() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(false, configuration.isStressTest(), "Default stress test");
        configuration.setStressTest(true);
        assertEquals(true, configuration.isStressTest(), "Changed stress test");
    }

    @Test
    void testBenchmark() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals(false, configuration.isBenchmark(), "Default benchmark");
        configuration.setBenchmark(true);
        assertEquals(true, configuration.isBenchmark(), "Changed benchmark");
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
}
