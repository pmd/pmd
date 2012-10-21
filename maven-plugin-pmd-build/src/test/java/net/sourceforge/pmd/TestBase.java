/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;

/**
 * Base test class which sets up the environment for the tests.
 * @author Andreas Dangel
 *
 */
public abstract class TestBase {

    protected static String TEST_DIR = "target/test-environment/";
    protected static File testDir = new File(TEST_DIR);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	FileUtils.deleteDirectory(testDir);
	FileUtils.copyDirectoryStructure(new File("src/test/resources/sample-pmd"), testDir);
    }
}
