/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Unit test for {@link CpdAnalysis}
 */
class CpdAnalysisTest {

    private static final String BASE_TEST_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    private static final String TARGET_TEST_RESOURCE_PATH = "target/classes/net/sourceforge/pmd/cpd/files/";

    @TempDir
    private Path tempDir;

    private CPDConfiguration config = new CPDConfiguration();

    @BeforeEach
    void setup() {
        config.setOnlyRecognizeLanguage(DummyLanguageModule.getInstance());
        config.setMinimumTileSize(10);
    }

    /**
     * As java doesn't support symlinks in zip files, maven does not, too. So,
     * we are creating the symlinks manually here before the test.
     *
     * @throws Exception
     *             any error
     */
    private void prepareSymLinks() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        if (!new File(TARGET_TEST_RESOURCE_PATH, "symlink-for-real-file.txt").exists()) {
            runtime.exec(new String[] { "ln", "-s", BASE_TEST_RESOURCE_PATH + "real-file.txt",
                TARGET_TEST_RESOURCE_PATH + "symlink-for-real-file.txt", }).waitFor();
        }
        if (!new File(BASE_TEST_RESOURCE_PATH, "this-is-a-broken-sym-link-for-test").exists()) {
            runtime.exec(new String[] { "ln", "-s", "broken-sym-link",
                TARGET_TEST_RESOURCE_PATH + "this-is-a-broken-sym-link-for-test", }).waitFor();
        }
    }

    /**
     * A broken symlink (which is basically a not existing file), should be
     * skipped.
     *
     * @throws Exception
     *             any error
     */
    @Test
    @EnabledOnOs(OS.LINUX) // Symlinks are not well supported under Windows
    void testFileSectionWithBrokenSymlinks() throws Exception {
        prepareSymLinks();

        FileCountAssertListener listener = new FileCountAssertListener(0);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            assertFalse(cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "this-is-a-broken-sym-link-for-test")));
            cpd.performAnalysis();
        }

        listener.verify();
    }

    /**
     * A file should be added only once - even if it was found twice, because of
     * a sym link.
     *
     * @throws Exception
     *             any error
     */
    @Test
    @EnabledOnOs(OS.LINUX) // Symlinks are not well supported under Windows
    void testFileAddedAsSymlinkAndReal() throws Exception {
        prepareSymLinks();

        FileCountAssertListener listener = new FileCountAssertListener(1);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            assertTrue(cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "real-file.txt")));
            assertFalse(cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "symlink-for-real-file.txt")));
            cpd.performAnalysis();
        }

        listener.verify();
    }

    /**
     * A file should be not be added via a sym link.
     */
    @Test
    @EnabledOnOs(OS.LINUX) // Symlinks are not well supported under Windows
    void testNoFileAddedAsSymlink() throws Exception {
        prepareSymLinks();

        FileCountAssertListener listener = new FileCountAssertListener(0);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            assertFalse(cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "symlink-for-real-file.txt")));
            cpd.performAnalysis();
        }

        listener.verify();
    }

    /**
     * Add a file with a relative path - should still be added and not be
     * detected as a sym link.
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testFileAddedWithRelativePath() throws Exception {
        FileCountAssertListener listener = new FileCountAssertListener(1);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            assertTrue(cpd.files().addFile(Paths.get("./" + BASE_TEST_RESOURCE_PATH, "real-file.txt")));
            cpd.performAnalysis();
        }

        listener.verify();
    }

    /**
     * The order of the duplicates is dependent on the order the files are added to CPD.
     * See also https://github.com/pmd/pmd/issues/1196
     * @throws Exception
     */
    @Test
    void testFileOrderRelevance() throws Exception {
        Path dup1 = Paths.get("./" + BASE_TEST_RESOURCE_PATH, "dup1.txt");
        Path dup2 = Paths.get("./" + BASE_TEST_RESOURCE_PATH, "dup2.txt");

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            assertTrue(cpd.files().addFile(dup2));
            assertTrue(cpd.files().addFile(dup1));
            cpd.performAnalysis(report -> {
                List<Match> matches = report.getMatches();
                assertFalse(matches.isEmpty());
                for (Match match : matches) {
                    // the file added first was dup2, but we sort now the files alphabetically, so dup1 is the first.
                    assertEquals("dup1.txt", match.getFirstMark().getFileId().getFileName());
                    assertEquals("dup2.txt", match.getSecondMark().getFileId().getFileName());
                }
            });
        }
        // now the other way round
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            assertTrue(cpd.files().addFile(dup1));
            assertTrue(cpd.files().addFile(dup2));
            cpd.performAnalysis(report -> {
                List<Match> matches = report.getMatches();
                assertFalse(matches.isEmpty());
                for (Match match : matches) {
                    // dup1.txt is still the first
                    assertEquals("dup1.txt", match.getFirstMark().getFileId().getFileName());
                    assertEquals("dup2.txt", match.getSecondMark().getFileId().getFileName());
                }
            });
        }
    }

    @Test
    void testNoSkipLexicalErrors() throws IOException {
        PmdReporter reporter = mock(PmdReporter.class);
        config.setReporter(reporter);

        config.setSkipLexicalErrors(false);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo.dummy"), DummyLanguageModule.CPD_THROW_LEX_EXCEPTION));
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo2.dummy"), DummyLanguageModule.CPD_THROW_MALFORMED_SOURCE_EXCEPTION));
            cpd.performAnalysis();
        }
        verify(reporter).errorEx(eq("Error while tokenizing"), any(LexException.class));
        verify(reporter).errorEx(eq("Error while tokenizing"), any(MalformedSourceException.class));
        verify(reporter).errorEx(eq("Exception while running CPD"), any(IllegalStateException.class));
        verifyNoMoreInteractions(reporter);
    }

    @Test
    void reportShouldContainProcessingErrors() throws IOException {
        AtomicReference<CPDReport> report = new AtomicReference<>();
        PmdReporter reporter = mock(PmdReporter.class);
        config.setReporter(reporter);

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo.dummy"), DummyLanguageModule.CPD_THROW_LEX_EXCEPTION));
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo2.dummy"), DummyLanguageModule.CPD_THROW_MALFORMED_SOURCE_EXCEPTION));
            cpd.performAnalysis(report::set);
        }

        assertNotNull(report.get(), "CPD aborted early without producing a report");
        List<Report.ProcessingError> processingErrors = report.get().getProcessingErrors();
        assertEquals(2, processingErrors.size());

        Report.ProcessingError error1 = processingErrors.get(0);
        assertEquals("foo.dummy", error1.getFileId().getFileName());
        assertThat(error1.getDetail(), containsString(LexException.class.getSimpleName()));

        Report.ProcessingError error2 = processingErrors.get(1);
        assertEquals("foo2.dummy", error2.getFileId().getFileName());
        assertThat(error2.getDetail(), containsString(MalformedSourceException.class.getSimpleName()));

        verify(reporter).errorEx(eq("Skipping file"), any(LexException.class));
        verify(reporter).errorEx(eq("Skipping file"), any(MalformedSourceException.class));
        verifyNoMoreInteractions(reporter);
    }

    @Test
    void testSkipLexicalErrors() throws IOException {
        PmdReporter reporter = mock(PmdReporter.class);
        config.setReporter(reporter);

        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo.dummy"), DummyLanguageModule.CPD_THROW_LEX_EXCEPTION));
            assertTrue(cpd.files().addSourceFile(FileId.fromPathLikeString("foo2.dummy"), DummyLanguageModule.CPD_THROW_MALFORMED_SOURCE_EXCEPTION));
            cpd.performAnalysis();
        }
        verify(reporter).errorEx(eq("Skipping file"), any(LexException.class));
        verify(reporter).errorEx(eq("Skipping file"), any(MalformedSourceException.class));
        verifyNoMoreInteractions(reporter);
    }

    @Test
    void duplicatedFilesShouldBeSkipped() throws IOException {
        String filename = "file1.dummy";
        Path aFile1 = Files.createDirectory(tempDir.resolve("a")).resolve(filename).toAbsolutePath();
        Path bFile1 = Files.createDirectory(tempDir.resolve("b")).resolve(filename).toAbsolutePath();

        Files.write(aFile1, "Same content".getBytes(StandardCharsets.UTF_8));
        Files.write(bFile1, "Same content".getBytes(StandardCharsets.UTF_8));

        config.setSkipDuplicates(true);
        config.setInputPathList(Arrays.asList(tempDir));
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            List<TextFile> collectedFiles = cpd.files().getCollectedFiles();
            collectedFiles.stream().map(TextFile::getFileId).map(FileId::getAbsolutePath).forEach(System.out::println);
            assertEquals(1, collectedFiles.size());

            // the order of directory traversal is most likely not defined, so either one
            // of the two files might be added, but not both
            String collectedFile = collectedFiles.get(0).getFileId().getAbsolutePath();
            assertTrue(collectedFile.equals(aFile1.toString()) || collectedFile.equals(bFile1.toString()));
        }
    }

    /**
     * Simple listener that fails, if too many files were added and not skipped.
     */
    private static class FileCountAssertListener implements CPDListener {
        private int expectedFilesCount;
        private int files;

        FileCountAssertListener(int expectedFilesCount) {
            this.expectedFilesCount = expectedFilesCount;
            this.files = 0;
        }

        @Override
        public void addedFile(int fileCount) {
            files++;
            if (files > expectedFilesCount) {
                fail("File was added!");
            }
        }

        @Override
        public void phaseUpdate(int phase) {
            // not needed for this test
        }

        public void verify() {
            assertEquals(expectedFilesCount, files,
                    "Expected " + expectedFilesCount + " files, but " + files + " have been added.");
        }
    }
}
