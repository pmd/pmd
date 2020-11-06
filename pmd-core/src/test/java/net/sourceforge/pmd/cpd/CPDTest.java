/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link CPD}
 */
public class CPDTest {

    private static final String BASE_TEST_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    private static final String TARGET_TEST_RESOURCE_PATH = "target/classes/net/sourceforge/pmd/cpd/files/";

    private CPD cpd;

    // Symlinks are not well supported under Windows - so the tests are
    // simply executed only on linux.
    private boolean canTestSymLinks = SystemUtils.IS_OS_UNIX;

    @Before
    public void setup() throws Exception {
        CPDConfiguration theConfiguration = new CPDConfiguration();
        theConfiguration.setLanguage(new AnyLanguage("any"));
        theConfiguration.setMinimumTileSize(10);
        theConfiguration.postContruct();
        cpd = new CPD(theConfiguration);
    }

    /**
     * As java doesn't support symlinks in zip files, maven does not, too. So,
     * we are creating the symlinks manually here before the test.
     *
     * @throws Exception
     *             any error
     */
    private void prepareSymLinks() throws Exception {
        Assume.assumeTrue("Skipping unit tests with symlinks.", canTestSymLinks);

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
    public void testFileSectionWithBrokenSymlinks() throws Exception {
        prepareSymLinks();

        NoFileAssertListener listener = new NoFileAssertListener(0);
        cpd.setCpdListener(listener);

        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "this-is-a-broken-sym-link-for-test"));
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
    public void testFileAddedAsSymlinkAndReal() throws Exception {
        prepareSymLinks();

        NoFileAssertListener listener = new NoFileAssertListener(1);
        cpd.setCpdListener(listener);

        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "real-file.txt"));
        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "symlink-for-real-file.txt"));
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
    public void testFileAddedWithRelativePath() throws Exception {
        NoFileAssertListener listener = new NoFileAssertListener(1);
        cpd.setCpdListener(listener);

        cpd.add(new File("./" + BASE_TEST_RESOURCE_PATH, "real-file.txt"));
        listener.verify();
    }

    /**
     * The order of the duplicates is dependent on the order the files are added to CPD.
     * See also https://github.com/pmd/pmd/issues/1196
     * @throws Exception
     */
    @Test
    public void testFileOrderRelevance() throws Exception {
        cpd.add(new File("./" + BASE_TEST_RESOURCE_PATH, "dup2.java"));
        cpd.add(new File("./" + BASE_TEST_RESOURCE_PATH, "dup1.java"));
        cpd.go();

        Iterator<Match> matches = cpd.getMatches();
        while (matches.hasNext()) {
            Match match = matches.next();
            // the file added first was dup2.
            Assert.assertTrue(match.getFirstMark().getFilename().endsWith("dup2.java"));
            Assert.assertTrue(match.getSecondMark().getFilename().endsWith("dup1.java"));
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
        public void addedFile(int fileCount, File file) {
            files++;
            if (files > expectedFilesCount) {
                Assert.fail("File was added! - " + file);
            }
        }

        @Override
        public void phaseUpdate(int phase) {
            // not needed for this test
        }

        public void verify() {
            Assert.assertEquals("Expected " + expectedFilesCount + " files, but " + files + " have been added.",
                    expectedFilesCount, files);
        }
    }
}
