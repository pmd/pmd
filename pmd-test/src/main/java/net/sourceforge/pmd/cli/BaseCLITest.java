/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import net.sourceforge.pmd.PMD;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 * 
 */
public abstract class BaseCLITest {

    protected static final String TEST_OUPUT_DIRECTORY = "target/cli-tests/";

    // Points toward a folder without any source files, to avoid actually PMD
    // and slowing down tests
    protected static final String SOURCE_FOLDER = "src/main/resources";

    private PrintStream originalOut;
    private PrintStream originalErr;

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
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    protected void createTestOutputFile(String filename) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(filename));
            System.setOut(out);
            System.setErr(out);
        } catch (FileNotFoundException e) {
            fail("Can't create file " + filename + " for test.");
        }
    }

    protected String runTest(String[] args, String testname) {
        String filename = TEST_OUPUT_DIRECTORY + testname + ".txt";
        long start = System.currentTimeMillis();
        createTestOutputFile(filename);
        System.out.println("Start running test " + testname);
        runPMDWith(args);
        checkStatusCode();
        System.out.println("Test finished successfully after " + (System.currentTimeMillis() - start) + "ms.");
        return filename;
    }

    protected void runPMDWith(String[] args) {
        PMD.main(args);
    }

    protected void checkStatusCode() {
        int statusCode = getStatusCode();
        if (statusCode > 0)
            fail("PMD failed with status code:" + statusCode);
    }

    protected int getStatusCode() {
        return Integer.parseInt(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY));
    }
}
