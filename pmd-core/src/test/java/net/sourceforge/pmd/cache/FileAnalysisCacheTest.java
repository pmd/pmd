/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;

public class FileAnalysisCacheTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private File unexistingCacheFile;
    private File newCacheFile;
    private File emptyCacheFile;
    
    private File sourceFile;
    
    @Before
    public void setUp() throws IOException {
        unexistingCacheFile = new File(tempFolder.getRoot(), "non-existing-file.cache");
        newCacheFile = new File(tempFolder.getRoot(), "pmd-analysis.cache");
        emptyCacheFile = tempFolder.newFile();
        sourceFile = tempFolder.newFile("Source.java");
    }

    @Test
    public void testLoadFromNonExistingFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        assertNotNull("Cache creation from non existing file failed.", cache);
    }
    
    @Test
    public void testLoadFromEmptyFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        assertNotNull("Cache creation from empty file failed.", cache);
    }
    
    @Test
    public void testLoadFromDirectoryShouldntThrow() throws IOException {
        new FileAnalysisCache(tempFolder.getRoot());
    }
    
    @Test
    public void testLoadFromUnreadableFileShouldntThrow() throws IOException {
        emptyCacheFile.setReadable(false);
        new FileAnalysisCache(emptyCacheFile);
    }

    @Test
    public void testStoreCreatesFile() {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        cache.persist();
        assertTrue("Cache file doesn't exist after store", unexistingCacheFile.exists());
    }

    @Test
    public void testStoreOnUnwritableFileShouldntThrow() {
        emptyCacheFile.setWritable(false);
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        cache.persist();
    }

    @Test
    public void testStorePersistsFilesWithViolations() {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.isUpToDate(sourceFile);

        final RuleViolation rv = mock(RuleViolation.class, Mockito.RETURNS_SMART_NULLS);
        when(rv.getFilename()).thenReturn(sourceFile.getPath());
        final net.sourceforge.pmd.Rule rule = mock(net.sourceforge.pmd.Rule.class, Mockito.RETURNS_SMART_NULLS);
        when(rv.getRule()).thenReturn(rule);

        cache.ruleViolationAdded(rv);
        cache.persist();

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        assertTrue("Cache believes unmodified file with violations is not up to date",
                reloadedCache.isUpToDate(sourceFile));
        
        final List<RuleViolation> cachedViolations = reloadedCache.getCachedViolations(sourceFile);
        assertEquals("Cached rule violations count mismatch", 1, cachedViolations.size());
    }

    @Test
    public void testCacheValidityWithNoChanges() {
        final RuleSets rs = mock(RuleSets.class);
        final ClassLoader cl = mock(ClassLoader.class);

        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        assertTrue("Cache believes unmodified file is not up to date without ruleset / classpath changes",
                reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testRulesetChangeInvalidatesCache() {
        final RuleSets rs = mock(RuleSets.class);
        final ClassLoader cl = mock(ClassLoader.class);
        
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        when(rs.getChecksum()).thenReturn(1L);
        reloadedCache.checkValidity(rs, cl);
        assertFalse("Cache believes unmodified file is up to date after ruleset changed",
                reloadedCache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testClasspathChangeWithoutDFAorTypeResolutionDoesNotInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] { });
        
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        when(cl.getURLs()).thenReturn(new URL[] { tempFolder.newFile().toURI().toURL(), });
        reloadedCache.checkValidity(rs, cl);
        assertTrue("Cache believes unmodified file is not up to date after classpath changed when no rule cares",
                reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testClasspathChangeInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] { });
        
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        final File classpathFile = tempFolder.newFile();
        when(cl.getURLs()).thenReturn(new URL[] { classpathFile.toURI().toURL(), });
        
        // Make sure the classpath file is not empty
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        
        final net.sourceforge.pmd.Rule r = mock(net.sourceforge.pmd.Rule.class);
        when(r.usesDFA()).thenReturn(true);
        when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        reloadedCache.checkValidity(rs, cl);
        assertFalse("Cache believes unmodified file is up to date after classpath changed",
                reloadedCache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testClasspathJarContentsChangeInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        
        final File classpathFile = tempFolder.newFile();
        when(cl.getURLs()).thenReturn(new URL[] { classpathFile.toURI().toURL(), });
        
        final net.sourceforge.pmd.Rule r = mock(net.sourceforge.pmd.Rule.class);
        when(r.usesDFA()).thenReturn(true);
        when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        
        // Edit the classpath referenced file
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        assertFalse("Cache believes cache is up to date when a classpath file changed",
                reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testUnknownFileIsNotUpToDate() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        assertFalse("Cache believes an unknown file is up to date",
                cache.isUpToDate(sourceFile));
    }

    @Test
    public void testFileIsUpToDate() throws IOException {
        setupCacheWithFiles(newCacheFile, mock(RuleSets.class), mock(ClassLoader.class), sourceFile);
        
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        assertTrue("Cache believes a known, unchanged file is not up to date",
                cache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testFileIsNotUpToDateWhenEdited() throws IOException {
        setupCacheWithFiles(newCacheFile, mock(RuleSets.class), mock(ClassLoader.class), sourceFile);
        
        // Edit the file
        Files.write(Paths.get(sourceFile.getAbsolutePath()), "some text".getBytes());
        
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        assertFalse("Cache believes a known, changed file is up to date",
                cache.isUpToDate(sourceFile));
    }

    private void setupCacheWithFiles(final File cacheFile, final RuleSets ruleSets,
            final ClassLoader classLoader, final File... files) {
        // Setup a cache file with an entry for an empty Source.java with no violations
        final FileAnalysisCache cache = new FileAnalysisCache(cacheFile);
        cache.checkValidity(ruleSets, classLoader);
        
        for (final File f : files) {
            cache.isUpToDate(f);
        }
        cache.persist();
    }
}
