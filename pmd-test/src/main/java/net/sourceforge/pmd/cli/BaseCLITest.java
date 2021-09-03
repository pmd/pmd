/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import net.sourceforge.pmd.PMD;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public abstract class BaseCLITest {

    protected static final String TEST_OUPUT_DIRECTORY = "target/cli-tests/";

    // Points toward a folder with not many source files, to avoid actually PMD
    // and slowing down tests
    protected static final String SOURCE_FOLDER = "src/test/resources/net/sourceforge/pmd/cli";

    protected PrintStream originalOut;
    protected PrintStream originalErr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        System.setProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        File testOuputDir = new File(TEST_OUPUT_DIRECTORY);
        if (!testOuputDir.exists()) {
            assertTrue("failed to create output directory for test:" + testOuputDir.getAbsolutePath(),
                    testOuputDir.mkdirs());
        }
    }

    @Before
    public void setup() {
        originalOut = System.out;
        originalErr = System.err;
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(System.out);

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    protected void createTestOutputFile(String filename) {
        try {
            @SuppressWarnings("PMD.CloseResource")
            PrintStream out = new PrintStream(Files.newOutputStream(new File(filename).toPath()));
            System.setOut(out);
            System.setErr(out);
        } catch (IOException e) {
            fail("Can't create file " + filename + " for test.");
        }
    }

    protected String runTest(String[] args, String testname) {
        return runTest(args, testname, 0);
    }

    protected String runTest(String[] args, String testname, int expectedExitCode) {
        String filename = TEST_OUPUT_DIRECTORY + testname + ".txt";
        long start = System.currentTimeMillis();
        createTestOutputFile(filename);
        System.out.println("Start running test " + testname);
        runPMDWith(args);
        checkStatusCode(expectedExitCode);
        System.out.println("Test finished successfully after " + (System.currentTimeMillis() - start) + "ms.");
        return filename;
    }

    protected void runPMDWith(String[] args) {
        PMD.main(args);
    }

    protected void checkStatusCode(int expectedExitCode) {
        int statusCode = getStatusCode();
        if (statusCode != expectedExitCode) {
            fail("PMD failed with status code:" + statusCode);
        }
    }

    protected int getStatusCode() {
        return Integer.parseInt(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY));
    }
}
