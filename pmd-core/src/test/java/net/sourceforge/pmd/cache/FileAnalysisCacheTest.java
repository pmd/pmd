package net.sourceforge.pmd.cache;

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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;

public class FileAnalysisCacheTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private File unexistingCacheFile;
    private File emptyCacheFile;
    
    private File sourceFile;
    
    @Before
    public void setUp() throws IOException {
        unexistingCacheFile = new File(tempFolder.getRoot(), "non-existing-file.cache");
        emptyCacheFile = tempFolder.newFile();
        sourceFile = tempFolder.newFile("Source.java");
    }

    @Test
    public void testLoadFromNonExistingFile() throws IOException {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(unexistingCacheFile);
        assertNotNull("Cache creation from non existing file failed.", cache);
    }
    
    @Test
    public void testLoadFromEmptyFile() throws IOException {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertNotNull("Cache creation from empty file failed.", cache);
    }
    
    @Test
    public void testLoadFromDirectory() throws IOException {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(tempFolder.getRoot());
        // TODO
    }
    
    @Test
    public void testLoadFromUnreadableFile() throws IOException {
        emptyCacheFile.setReadable(false);
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        // TODO
    }

    @Test
    public void testStoreCreatesFile() {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(unexistingCacheFile);
        cache.persist();
        assertTrue("Cache file doesn't exist after store", unexistingCacheFile.exists());
    }
    
    @Test
    public void testStoreOnUnwritableFile() {
        emptyCacheFile.setWritable(false);
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        cache.persist();
        assertTrue("Cache file doesn't exist after store", emptyCacheFile.exists());
    }
    
    @Test
    public void testStoreSkipsFilesWithViolations() {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        cache.isUpToDate(sourceFile);
        cache.ruleViolationAdded(new DummyRuleViolation(sourceFile));
        cache.persist();
        
        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertFalse("Cache believes unmodified file with violations is up to date",
                reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testCacheValidityWithNoChanges() {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        
        setupCacheWithFiles(emptyCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        reloadedCache.checkValidity(rs, cl);
        assertTrue("Cache believes unmodified file is not up to date without ruleset / classpath changes",
                reloadedCache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testRulesetChangeInvalidatesCache() {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        
        setupCacheWithFiles(emptyCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        when(rs.getChecksum()).thenReturn(1L);
        reloadedCache.checkValidity(rs, cl);
        assertFalse("Cache believes unmodified file is up to date after ruleset changed",
                reloadedCache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testClasspathChangeWithoutDFAorTypeResolutionDoesNotInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        
        setupCacheWithFiles(emptyCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        when(cl.getURLs()).thenReturn(new URL[] { tempFolder.newFile().toURI().toURL(), });
        reloadedCache.checkValidity(rs, cl);
        assertTrue("Cache believes unmodified file is not up to date after classpath changed when no rule cares",
                reloadedCache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testClasspathChangeInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        
        setupCacheWithFiles(emptyCacheFile, rs, cl, sourceFile);
        
        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        when(cl.getURLs()).thenReturn(new URL[] { tempFolder.newFile().toURI().toURL(), });
        final net.sourceforge.pmd.Rule r = mock(net.sourceforge.pmd.Rule.class);
        when(r.usesDFA()).thenReturn(true);
        when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        reloadedCache.checkValidity(rs, cl);
        assertFalse("Cache believes unmodified file is up to date after classpath changed",
                reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testUnknownFileIsNotUpToDate() throws IOException {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(unexistingCacheFile);
        assertFalse("Cache believes an unknown file is up to date",
                cache.isUpToDate(sourceFile));
    }

    @Test
    public void testFileIsUpToDate() throws IOException {
        setupCacheWithFiles(emptyCacheFile, mock(RuleSets.class), mock(ClassLoader.class), sourceFile);
        
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertTrue("Cache believes a known, unchanged file is not up to date",
                cache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testFileIsNotUpToDateWhenEdited() throws IOException {
        setupCacheWithFiles(emptyCacheFile, mock(RuleSets.class), mock(ClassLoader.class), sourceFile);
        
        // Edit the file
        Files.write(Paths.get(sourceFile.getAbsolutePath()), "some text".getBytes());
        
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertFalse("Cache believes a known, changed file is up to date",
                cache.isUpToDate(sourceFile));
    }

    private void setupCacheWithFiles(final File cacheFile, final RuleSets ruleSets,
            final ClassLoader classLoader, final File... files) {
        // Setup a cache file with an entry for an empty Source.java with no violations
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(cacheFile);
        cache.checkValidity(ruleSets, classLoader);
        
        for (final File f : files) {
            cache.isUpToDate(f);
        }
        cache.persist();
    }
    
    private static class DummyRuleViolation implements RuleViolation {
        
        private final File f;
        
        public DummyRuleViolation(final File f) {
            this.f = f;
        }

        @Override
        public boolean isSuppressed() {
            return false;
        }
        
        @Override
        public String getVariableName() {
            return null;
        }
        
        @Override
        public net.sourceforge.pmd.Rule getRule() {
            return null;
        }
        
        @Override
        public String getPackageName() {
            return null;
        }
        
        @Override
        public String getMethodName() {
            return null;
        }
        
        @Override
        public String getFilename() {
            return f.getPath();
        }
        
        @Override
        public int getEndLine() {
            return 0;
        }
        
        @Override
        public int getEndColumn() {
            return 0;
        }
        
        @Override
        public String getDescription() {
            return null;
        }
        
        @Override
        public String getClassName() {
            return null;
        }
        
        @Override
        public int getBeginLine() {
            return 0;
        }
        
        @Override
        public int getBeginColumn() {
            return 0;
        }
    }
}
