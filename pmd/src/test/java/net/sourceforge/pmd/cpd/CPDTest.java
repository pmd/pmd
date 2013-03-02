/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link CPD}
 */
public class CPDTest {

    private static final String BASE_TEST_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    
    private CPD cpd;

    @Before
    public void setup() {
        CPDConfiguration theConfiguration = new CPDConfiguration();
        theConfiguration.postContruct();
        cpd = new CPD(theConfiguration);
    }

    /**
     * A broken symlink (which is basically a not existing file), should be skipped.
     * @throws Exception any error
     */
    @Test
    public void testFileSectionWithBrokenSymlinks() throws Exception {
        cpd.setCpdListener(new NoFileAssertListener(0));

        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "this-is-a-broken-sym-link-for-test"));
    }

    /**
     * A file should be added only once - even if it was found twice, because of a sym link.
     * @throws Exception any error
     */
    @Test
    public void testFileAddedAsSymlinkAndReal() throws Exception {
        cpd.setCpdListener(new NoFileAssertListener(1));

        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "real-file.txt"));
        cpd.add(new File(BASE_TEST_RESOURCE_PATH, "symlink-for-real-file.txt"));
    }

    /**
     * Simple listener that fails, if to many files were added and not skipped.
     */
    private static class NoFileAssertListener implements CPDListener {
        private int maximumFilesAllowed;
        private int files;
        public NoFileAssertListener(int maximumFilesAllowed) {
            this.maximumFilesAllowed = maximumFilesAllowed;
            this.files = 0;
        }
        public void addedFile(int fileCount, File file) {
            files++;
            if (files > maximumFilesAllowed) {
                Assert.fail("File was added! - " + file);
            }
        }
        public void phaseUpdate(int phase) {
            // not needed for this test
        }
    }
}
