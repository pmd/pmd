/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;

import net.sourceforge.pmd.build.PmdBuildException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

/**
 * Base test class which sets up the environment for the tests.
 * @author Andreas Dangel
 *
 */
public abstract class TestBase {

    protected static String TEST_DIR = "target/test-environment/";
    protected static File testDir = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	testDir = new File(TEST_DIR);
	if (! testDir.exists() && ! testDir.mkdir() )
	{
	    throw new PmdBuildException("Can't create " + TEST_DIR);
	}
	else if ( ! testDir.isDirectory() )
	{
	    throw new PmdBuildException("testdir " + TEST_DIR + " exist !");
	}
	
	FileUtils.copyDirectory(new File("src/test/resources/sample-pmd"), testDir);
    }
    
    @After
    public void tearDown() throws Exception {
	if (testDir != null) {
	    FileUtils.deleteDirectory(testDir);
	}
	testDir = null;
    }
}
