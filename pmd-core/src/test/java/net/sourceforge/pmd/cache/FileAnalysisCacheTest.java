package net.sourceforge.pmd.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    public void testStoreSkipsFilesThatFailedProcessing() {
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        cache.isUpToDate(sourceFile);
        cache.analysisFailed(sourceFile);
        cache.persist();

        final FileAnalysisCache reloadedCache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertFalse("Cache believes unmodified file that failed processing is up to date",
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
        setupCacheWithFiles(emptyCacheFile, sourceFile);
        
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertTrue("Cache believes a known, unchanged file is not up to date",
                cache.isUpToDate(sourceFile));
    }
    
    @Test
    public void testFileIsNotUpToDateWhenEdited() throws IOException {
        setupCacheWithFiles(emptyCacheFile, sourceFile);
        
        // Edit the file
        Files.write(Paths.get(sourceFile.getAbsolutePath()), "some text".getBytes());
        
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(emptyCacheFile);
        assertFalse("Cache believes a known, changed file is up to date",
                cache.isUpToDate(sourceFile));
    }

    private void setupCacheWithFiles(final File cacheFile, final File... files) {
        // Setup a cache file with an entry for an empty Source.java with no violations
        final FileAnalysisCache cache = FileAnalysisCache.fromFile(cacheFile);
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
