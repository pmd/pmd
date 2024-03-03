/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import net.sourceforge.pmd.PmdCoreTestUtils;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextFileContent;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

class FileAnalysisCacheTest {

    @TempDir
    private Path tempFolder;

    private File unexistingCacheFile;
    private File newCacheFile;
    private File emptyCacheFile;

    private TextDocument sourceFile;
    private TextFile sourceFileBackend;

    private final LanguageVersion dummyVersion = PmdCoreTestUtils.dummyVersion();


    @BeforeEach
    public void setUp() throws IOException {
        unexistingCacheFile = tempFolder.resolve("non-existing-file.cache").toFile();
        newCacheFile = tempFolder.resolve("pmd-analysis.cache").toFile();
        emptyCacheFile = Files.createTempFile(tempFolder, null, null).toFile();
        Path sourceFile = tempFolder.resolve("Source.java");
        Files.write(sourceFile, listOf("dummy text"));
        this.sourceFileBackend = TextFile.forPath(sourceFile, Charset.defaultCharset(), dummyVersion);
        this.sourceFile = TextDocument.create(sourceFileBackend);
    }

    @Test
    void testLoadFromNonExistingFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        assertNotNull(cache, "Cache creation from non existing file failed.");
    }

    @Test
    void testLoadFromEmptyFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        assertNotNull(cache, "Cache creation from empty file failed.");
    }

    @Test
    void testLoadFromDirectoryShouldntThrow() throws IOException {
        new FileAnalysisCache(tempFolder.toFile());
    }

    @Test
    void testLoadFromUnreadableFileShouldntThrow() throws IOException {
        emptyCacheFile.setReadable(false);
        new FileAnalysisCache(emptyCacheFile);
    }

    @Test
    void testStoreCreatesFile() throws Exception {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        cache.persist();
        assertTrue(unexistingCacheFile.exists(), "Cache file doesn't exist after store");
    }

    @Test
    void testStoreOnUnwritableFileShouldntThrow() throws IOException {
        emptyCacheFile.setWritable(false);
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        cache.persist();
    }

    @Test
    void testStorePersistsFilesWithViolations() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));
        final FileAnalysisListener cacheListener = cache.startFileAnalysis(sourceFile);
        
        cache.isUpToDate(sourceFile);

        final RuleViolation rv = mock(RuleViolation.class);
        final TextRange2d textLocation = TextRange2d.range2d(1, 2, 3, 4);
        when(rv.getLocation()).thenReturn(FileLocation.range(sourceFile.getFileId(), textLocation));
        final Rule rule = mock(Rule.class, Mockito.RETURNS_SMART_NULLS);
        when(rule.getLanguage()).thenReturn(mock(Language.class));
        when(rv.getRule()).thenReturn(rule);

        cacheListener.onRuleViolation(rv);
        cache.persist();

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));
        assertTrue(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file with violations is not up to date");

        final List<RuleViolation> cachedViolations = reloadedCache.getCachedViolations(sourceFile);
        assertEquals(1, cachedViolations.size(), "Cached rule violations count mismatch");
        final RuleViolation cachedViolation = cachedViolations.get(0);
        assertSame(sourceFile.getFileId(), cachedViolation.getFileId());
        assertEquals(textLocation.getStartLine(), cachedViolation.getBeginLine());
        assertEquals(textLocation.getStartColumn(), cachedViolation.getBeginColumn());
        assertEquals(textLocation.getEndLine(), cachedViolation.getEndLine());
        assertEquals(textLocation.getEndColumn(), cachedViolation.getEndColumn());
    }

    @Test
    void testStorePersistsFilesWithViolationsAndProcessingErrors() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));
        final FileAnalysisListener cacheListener = cache.startFileAnalysis(sourceFile);

        cache.isUpToDate(sourceFile);

        cacheListener.onError(new Report.ProcessingError(new RuntimeException("some rule failed"), sourceFile.getFileId()));

        final RuleViolation rv = mock(RuleViolation.class);
        final TextRange2d textLocation = TextRange2d.range2d(1, 2, 3, 4);
        when(rv.getLocation()).thenReturn(FileLocation.range(sourceFile.getFileId(), textLocation));
        final Rule rule = mock(Rule.class, Mockito.RETURNS_SMART_NULLS);
        when(rule.getLanguage()).thenReturn(mock(Language.class));
        when(rv.getRule()).thenReturn(rule);

        // the next rule wants to report a violation
        cacheListener.onRuleViolation(rv);
        cache.persist();

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));
        assertFalse(reloadedCache.isUpToDate(sourceFile),
                "Cache believes file is up to date although processing errors happened earlier");

        final List<RuleViolation> cachedViolations = reloadedCache.getCachedViolations(sourceFile);
        assertTrue(cachedViolations.isEmpty(), "There should be no cached rule violations");
    }

    @Test
    void testDisplayNameIsRespected() throws Exception {
        // This checks that the display name of the file is respected even if
        // the file is assigned a different display name across runs. The path
        // id is saved into the cache file, and the cache implementation updates the
        // display name of the violations to match their current display name.

        final Rule rule = mock(Rule.class, Mockito.RETURNS_SMART_NULLS);
        when(rule.getLanguage()).thenReturn(mock(Language.class));

        final TextRange2d textLocation = TextRange2d.range2d(1, 2, 3, 4);

        TextFile mockFile = mock(TextFile.class);
        when(mockFile.getFileId()).thenReturn(FileId.fromPathLikeString("a/bc"));
        when(mockFile.getLanguageVersion()).thenReturn(dummyVersion);
        when(mockFile.readContents()).thenReturn(TextFileContent.fromCharSeq("abc"));

        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));

        try (TextDocument doc0 = TextDocument.create(mockFile)) {
            cache.isUpToDate(doc0);
            try (FileAnalysisListener listener = cache.startFileAnalysis(doc0)) {
                listener.onRuleViolation(InternalApiBridge.createRuleViolation(rule,
                        FileLocation.range(doc0.getFileId(), textLocation), "message",
                        Collections.emptyMap()));
            }
        } finally {
            cache.persist();
        }

        reloadWithOneViolation(mockFile);
    }

    private void reloadWithOneViolation(TextFile mockFile) throws IOException {
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(mockFile));
        try (TextDocument doc1 = TextDocument.create(mockFile)) {
            assertTrue(reloadedCache.isUpToDate(doc1),
                       "Cache believes unmodified file with violations is not up to date");
            List<RuleViolation> cachedViolations = reloadedCache.getCachedViolations(doc1);
            assertEquals(1, cachedViolations.size(), "Cached rule violations count mismatch");
            final RuleViolation cachedViolation = cachedViolations.get(0);
            assertEquals(mockFile.getFileId(), cachedViolation.getLocation().getFileId());
        }
    }


    @Test
    void testCacheValidityWithNoChanges() throws IOException {
        final RuleSets rs = mock(RuleSets.class);
        final ClassLoader cl = mock(ClassLoader.class);

        setupCacheWithFiles(newCacheFile, rs, cl);

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl, setOf(sourceFileBackend));
        assertTrue(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is not up to date without ruleset / classpath changes");
    }

    @Test
    void testCacheValidityWithIrrelevantChanges() throws IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] {});

        setupCacheWithFiles(newCacheFile, rs, cl);

        final File classpathFile = Files.createTempFile(tempFolder, null, "foo.xml").toFile();
        when(cl.getURLs()).thenReturn(new URL[] { classpathFile.toURI().toURL(), });

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl, setOf(sourceFileBackend));
        assertTrue(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is not up to date without ruleset / classpath changes");
    }

    @Test
    void testRulesetChangeInvalidatesCache() throws IOException {
        final RuleSets rs = mock(RuleSets.class);
        final ClassLoader cl = mock(ClassLoader.class);

        setupCacheWithFiles(newCacheFile, rs, cl);

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        when(rs.getChecksum()).thenReturn(1L);
        reloadedCache.checkValidity(rs, cl, Collections.emptySet());
        assertFalse(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is up to date after ruleset changed");
    }

    @Test
    void testAuxClasspathNonExistingAuxclasspathEntriesIgnored() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] { tempFolder.resolve("non-existing-dir").toFile().toURI().toURL(), });

        setupCacheWithFiles(newCacheFile, rs, cl);

        final FileAnalysisCache analysisCache = new FileAnalysisCache(newCacheFile);
        when(cl.getURLs()).thenReturn(new URL[] {});
        analysisCache.checkValidity(rs, cl, setOf(sourceFileBackend));
        assertTrue(analysisCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is not up to date after non-existing auxclasspath entry removed");
    }

    @Test
    void testAuxClasspathChangeWithoutDFAorTypeResolutionDoesNotInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] { });

        setupCacheWithFiles(newCacheFile, rs, cl);

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        when(cl.getURLs()).thenReturn(new URL[] { Files.createTempFile(tempFolder, null, null).toFile().toURI().toURL(), });
        reloadedCache.checkValidity(rs, cl, setOf(sourceFileBackend));
        assertTrue(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is not up to date after auxclasspath changed when no rule cares");
    }

    @Test
    void testAuxClasspathChangeInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);
        when(cl.getURLs()).thenReturn(new URL[] { });

        setupCacheWithFiles(newCacheFile, rs, cl);

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        final File classpathFile = Files.createTempFile(tempFolder, null, "foo.class").toFile();
        when(cl.getURLs()).thenReturn(new URL[] { classpathFile.toURI().toURL(), });

        // Make sure the auxclasspath file is not empty
        Files.write(classpathFile.toPath(), "some text".getBytes());

        final Rule r = mock(Rule.class);
        when(r.getLanguage()).thenReturn(mock(Language.class));
        when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        reloadedCache.checkValidity(rs, cl, Collections.emptySet());
        assertFalse(reloadedCache.isUpToDate(sourceFile),
                "Cache believes unmodified file is up to date after auxclasspath changed");
    }

    @Test
    void testAuxClasspathJarContentsChangeInvalidatesCache() throws MalformedURLException, IOException {
        final RuleSets rs = mock(RuleSets.class);
        final URLClassLoader cl = mock(URLClassLoader.class);

        final File classpathFile = Files.createTempFile(tempFolder, null, "foo.class").toFile();
        when(cl.getURLs()).thenReturn(new URL[] { classpathFile.toURI().toURL(), });

        final Rule r = mock(Rule.class);
        when(r.getLanguage()).thenReturn(mock(Language.class));
        when(rs.getAllRules()).thenReturn(Collections.singleton(r));

        setupCacheWithFiles(newCacheFile, rs, cl);

        // Edit the auxclasspath referenced file
        Files.write(classpathFile.toPath(), "some text".getBytes());

        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl, Collections.emptySet());
        assertFalse(reloadedCache.isUpToDate(sourceFile),
                "Cache believes cache is up to date when a auxclasspath file changed");
    }

    @Test
    void testClasspathNonExistingEntryIsIgnored() throws Exception {
        restoreSystemProperties(() -> {
            final RuleSets rs = mock(RuleSets.class);
            final ClassLoader cl = mock(ClassLoader.class);

            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator
                    + tempFolder.toFile().getAbsolutePath() + File.separator + "non-existing-dir");

            final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
            try {
                reloadedCache.checkValidity(rs, cl, Collections.emptySet());
            } catch (final Exception e) {
                fail("Validity check failed when classpath includes non-existing directories");
            }
        });
    }

    @Test
    void testClasspathChangeInvalidatesCache() throws Exception {
        restoreSystemProperties(() -> {
            final RuleSets rs = mock(RuleSets.class);
            final ClassLoader cl = mock(ClassLoader.class);

            final File classpathFile = Files.createTempFile(tempFolder, null, "foo.class").toFile();

            setupCacheWithFiles(newCacheFile, rs, cl);

            // Edit the classpath referenced file
            Files.write(classpathFile.toPath(), "some text".getBytes());
            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + classpathFile.getAbsolutePath());

            final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
            reloadedCache.checkValidity(rs, cl, Collections.emptySet());
            assertFalse(reloadedCache.isUpToDate(sourceFile),
                    "Cache believes cache is up to date when the classpath changed");
        });
    }

    @Test
    void testClasspathContentsChangeInvalidatesCache() throws Exception {
        restoreSystemProperties(() -> {
            final RuleSets rs = mock(RuleSets.class);
            final ClassLoader cl = mock(ClassLoader.class);

            final File classpathFile = Files.createTempFile(tempFolder, null, "foo.class").toFile();

            // Add a file to classpath
            Files.write(classpathFile.toPath(), "some text".getBytes());
            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + classpathFile.getAbsolutePath());

            setupCacheWithFiles(newCacheFile, rs, cl);

            // Change the file's contents
            Files.write(classpathFile.toPath(), "some other text".getBytes());

            final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
            reloadedCache.checkValidity(rs, cl, Collections.emptySet());
            assertFalse(reloadedCache.isUpToDate(sourceFile),
                    "Cache believes cache is up to date when a classpath file changed");
        });
    }

    @Test
    void testWildcardClasspath() throws Exception {
        restoreSystemProperties(() -> {
            final RuleSets rs = mock(RuleSets.class);
            final ClassLoader cl = mock(ClassLoader.class);
            setupCacheWithFiles(newCacheFile, rs, cl);

            // Prepare two class files
            createZipFile("mylib1.jar");
            createZipFile("mylib2.jar");

            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + tempFolder.toFile().getAbsolutePath() + "/*");

            final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
            assertFalse(reloadedCache.isUpToDate(sourceFile),
                    "Cache believes cache is up to date when the classpath changed");
        });
    }

    @Test
    void testWildcardClasspathContentsChangeInvalidatesCache() throws Exception {
        restoreSystemProperties(() -> {
            final RuleSets rs = mock(RuleSets.class);
            final ClassLoader cl = mock(ClassLoader.class);

            // Prepare two jar files
            final File classpathJar1 = createZipFile("mylib1.jar");
            createZipFile("mylib2.jar");

            System.setProperty("java.class.path", System.getProperty("java.class.path") + File.pathSeparator + tempFolder.toFile().getAbsolutePath() + "/*");

            setupCacheWithFiles(newCacheFile, rs, cl);

            // Change one file's contents (ie: adding more entries)
            classpathJar1.delete();
            createZipFile(classpathJar1.getName(), 2);

            final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
            reloadedCache.checkValidity(rs, cl, Collections.emptySet());
            assertFalse(reloadedCache.isUpToDate(sourceFile),
                    "Cache believes cache is up to date when the classpath changed");
        });
    }

    @Test
    void testUnknownFileIsNotUpToDate() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        assertFalse(cache.isUpToDate(sourceFile),
                "Cache believes an unknown file is up to date");
    }

    @Test
    void testFileIsUpToDate() throws IOException {
        setupCacheWithFiles(newCacheFile, mock(RuleSets.class), mock(ClassLoader.class));

        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.checkValidity(mock(RuleSets.class), mock(ClassLoader.class), setOf(sourceFileBackend));
        assertTrue(cache.isUpToDate(sourceFile),
                "Cache believes a known, unchanged file is not up to date");
    }

    @Test
    void testFileIsNotUpToDateWhenEdited() throws IOException {
        setupCacheWithFiles(newCacheFile, mock(RuleSets.class), mock(ClassLoader.class));

        // Edit the file
        TextFileContent text = TextFileContent.fromCharSeq("some text");
        assertEquals(System.lineSeparator(), text.getLineTerminator());
        sourceFileBackend.writeContents(text);
        sourceFile = TextDocument.create(sourceFileBackend);

        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        assertFalse(cache.isUpToDate(sourceFile),
                "Cache believes a known, changed file is up to date");
    }

    private void setupCacheWithFiles(final File cacheFile,
                                     final RuleSets ruleSets,
                                     final ClassLoader classLoader) throws IOException {
        // Setup a cache file with an entry for an empty Source.java with no violations
        final FileAnalysisCache cache = new FileAnalysisCache(cacheFile);
        cache.checkValidity(ruleSets, classLoader, setOf(sourceFileBackend));

        cache.isUpToDate(sourceFile);
        cache.persist();
    }

    private File createZipFile(String fileName) throws IOException {
        return createZipFile(fileName, 1);
    }

    private File createZipFile(String fileName, int numEntries) throws IOException {
        final File zipFile = Files.createTempFile(tempFolder, null, fileName).toFile();
        try (ZipOutputStream zipOS = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            for (int i = 0; i < numEntries; i++) {
                zipOS.putNextEntry(new ZipEntry("lib/foo" + i + ".class"));
                zipOS.write(("content of " + fileName + " entry " + i).getBytes(StandardCharsets.UTF_8));
                zipOS.closeEntry();
            }
        }
        return zipFile;
    }
}
