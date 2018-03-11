/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cache.FileAnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;

public class ConfigurationTest {

    @Test
    public void testSuppressMarker() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default suppress marker", PMD.SUPPRESS_MARKER, configuration.getSuppressMarker());
        configuration.setSuppressMarker("CUSTOM_MARKER");
        assertEquals("Changed suppress marker", "CUSTOM_MARKER", configuration.getSuppressMarker());
    }

    @Test
    public void testThreads() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default threads", Runtime.getRuntime().availableProcessors(), configuration.getThreads());
        configuration.setThreads(0);
        assertEquals("Changed threads", 0, configuration.getThreads());
    }

    @Test
    public void testClassLoader() throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default ClassLoader", PMDConfiguration.class.getClassLoader(), configuration.getClassLoader());
        configuration.prependClasspath("some.jar");
        assertEquals("Prepended ClassLoader class", ClasspathClassLoader.class,
                configuration.getClassLoader().getClass());
        URL[] urls = ((ClasspathClassLoader) configuration.getClassLoader()).getURLs();
        assertEquals("urls length", 1, urls.length);
        assertTrue("url[0]", urls[0].toString().endsWith("/some.jar"));
        assertEquals("parent classLoader", PMDConfiguration.class.getClassLoader(),
                configuration.getClassLoader().getParent());
        configuration.setClassLoader(null);
        assertEquals("Revert to default ClassLoader", PMDConfiguration.class.getClassLoader(),
                configuration.getClassLoader());
    }

    @Test
    public void testRuleSets() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default RuleSets", null, configuration.getRuleSets());
        configuration.setRuleSets("/rulesets/basic.xml");
        assertEquals("Changed RuleSets", "/rulesets/basic.xml", configuration.getRuleSets());
    }

    @Test
    public void testMinimumPriority() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default minimum priority", RulePriority.LOW, configuration.getMinimumPriority());
        configuration.setMinimumPriority(RulePriority.HIGH);
        assertEquals("Changed minimum priority", RulePriority.HIGH, configuration.getMinimumPriority());
    }

    @Test
    public void testSourceEncoding() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default source encoding", System.getProperty("file.encoding"), configuration.getSourceEncoding().name());
        configuration.setSourceEncoding(StandardCharsets.UTF_16LE.name());
        assertEquals("Changed source encoding", StandardCharsets.UTF_16LE, configuration.getSourceEncoding());
    }

    @Test
    public void testInputPaths() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default input paths", null, configuration.getInputPaths());
        configuration.setInputPaths("a,b,c");
        assertEquals("Changed input paths", "a,b,c", configuration.getInputPaths());
    }

    @Test
    public void testReportShortNames() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default report short names", false, configuration.isReportShortNames());
        configuration.setReportShortNames(true);
        assertEquals("Changed report short names", true, configuration.isReportShortNames());
    }

    @Test
    public void testReportFormat() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default report format", null, configuration.getReportFormat());
        configuration.setReportFormat("csv");
        assertEquals("Changed report format", "csv", configuration.getReportFormat());
    }

    @Test
    public void testCreateRenderer() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setReportFormat("csv");
        Renderer renderer = configuration.createRenderer();
        assertEquals("Renderer class", CSVRenderer.class, renderer.getClass());
        assertEquals("Default renderer show suppressed violations", false, renderer.isShowSuppressedViolations());

        configuration.setShowSuppressedViolations(true);
        renderer = configuration.createRenderer();
        assertEquals("Renderer class", CSVRenderer.class, renderer.getClass());
        assertEquals("Changed renderer show suppressed violations", true, renderer.isShowSuppressedViolations());
    }

    @Test
    public void testReportFile() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default report file", null, configuration.getReportFile());
        configuration.setReportFile("somefile");
        assertEquals("Changed report file", "somefile", configuration.getReportFile());
    }

    @Test
    public void testShowSuppressedViolations() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default show suppressed violations", false, configuration.isShowSuppressedViolations());
        configuration.setShowSuppressedViolations(true);
        assertEquals("Changed show suppressed violations", true, configuration.isShowSuppressedViolations());
    }

    @Test
    public void testReportProperties() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default report properties size", 0, configuration.getReportProperties().size());
        configuration.getReportProperties().put("key", "value");
        assertEquals("Changed report properties size", 1, configuration.getReportProperties().size());
        assertEquals("Changed report properties value", "value", configuration.getReportProperties().get("key"));
        configuration.setReportProperties(new Properties());
        assertEquals("Replaced report properties size", 0, configuration.getReportProperties().size());
    }

    @Test
    public void testDebug() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default debug", false, configuration.isDebug());
        configuration.setDebug(true);
        assertEquals("Changed debug", true, configuration.isDebug());
    }

    @Test
    public void testStressTest() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default stress test", false, configuration.isStressTest());
        configuration.setStressTest(true);
        assertEquals("Changed stress test", true, configuration.isStressTest());
    }

    @Test
    public void testBenchmark() {
        PMDConfiguration configuration = new PMDConfiguration();
        assertEquals("Default benchmark", false, configuration.isBenchmark());
        configuration.setBenchmark(true);
        assertEquals("Changed benchmark", true, configuration.isBenchmark());
    }

    @Test
    public void testAnalysisCache() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();
        assertNotNull("Default cache is null", configuration.getAnalysisCache());
        assertTrue("Default cache is not a noop", configuration.getAnalysisCache() instanceof NoopAnalysisCache);
        configuration.setAnalysisCache(null);
        assertNotNull("Default cache was set to null", configuration.getAnalysisCache());

        final File cacheFile = File.createTempFile("pmd-", ".cache");
        cacheFile.deleteOnExit();
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        assertSame("Confgured cache not stored", analysisCache, configuration.getAnalysisCache());
    }

    @Test
    public void testAnalysisCacheLocation() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();

        configuration.setAnalysisCacheLocation(null);
        assertNotNull("Null cache location accepted", configuration.getAnalysisCache());
        assertTrue("Null cache location accepted", configuration.getAnalysisCache() instanceof NoopAnalysisCache);

        configuration.setAnalysisCacheLocation("pmd.cache");
        assertNotNull("Not null cache location produces null cache", configuration.getAnalysisCache());
        assertTrue("File cache location doesn't produce a file cache",
                configuration.getAnalysisCache() instanceof FileAnalysisCache);
    }


    @Test
    public void testIgnoreIncrementalAnalysis() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();

        // set dummy cache location
        final File cacheFile = File.createTempFile("pmd-", ".cache");
        cacheFile.deleteOnExit();
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        assertNotNull("Null cache location accepted", configuration.getAnalysisCache());
        assertFalse("Non null cache location, cache should not be noop", configuration.getAnalysisCache() instanceof NoopAnalysisCache);

        configuration.setIgnoreIncrementalAnalysis(true);
        assertTrue("Ignoring incremental analysis should turn the cache into a noop", configuration.getAnalysisCache() instanceof NoopAnalysisCache);
    }
}
