/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;

/**
 * Unit test for {@link CpdAnalysis}
 */
class CpdAnalysisTest {

    private static final String BASE_TEST_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    private static final String TARGET_TEST_RESOURCE_PATH = "target/classes/net/sourceforge/pmd/cpd/files/";


    // Symlinks are not well supported under Windows - so the tests are
    // simply executed only on linux.
    private boolean canTestSymLinks = SystemUtils.IS_OS_UNIX;
    CPDConfiguration config = new CPDConfiguration();

    @BeforeEach
    void setup() throws Exception {
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
        assumeTrue(canTestSymLinks, "Skipping unit tests with symlinks.");

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
    void testFileSectionWithBrokenSymlinks() throws Exception {
        prepareSymLinks();

        NoFileAssertListener listener = new NoFileAssertListener(0);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "this-is-a-broken-sym-link-for-test"));
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
    void testFileAddedAsSymlinkAndReal() throws Exception {
        prepareSymLinks();

        NoFileAssertListener listener = new NoFileAssertListener(1);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "real-file.txt"));
            cpd.files().addFile(Paths.get(BASE_TEST_RESOURCE_PATH, "symlink-for-real-file.txt"));
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
        NoFileAssertListener listener = new NoFileAssertListener(1);
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.setCpdListener(listener);
            cpd.files().addFile(Paths.get("./" + BASE_TEST_RESOURCE_PATH, "real-file.txt"));
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
        try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
            cpd.files().addFile(Paths.get("./" + BASE_TEST_RESOURCE_PATH, "dup2.java"));
            cpd.files().addFile(Paths.get("./" + BASE_TEST_RESOURCE_PATH, "dup1.java"));
            cpd.performAnalysis(report -> {


                List<Match> matches = report.getMatches();
                for (Match match : matches) {
                    // the file added first was dup2.
                    assertEquals("dup2.java", match.getFirstMark().getFileId().getFileName());
                    assertEquals("dup1.java", match.getSecondMark().getFileId().getFileName());
                }
            });
        }

    }

    /**
     * Simple listener that fails, if too many files were added and not skipped.
     */
    private static class NoFileAssertListener implements CPDListener {
        private int expectedFilesCount;
        private int files;

        NoFileAssertListener(int expectedFilesCount) {
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
