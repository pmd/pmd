/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;

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

    @Deprecated
    protected String runTest(String[] args, String testname) {
        return runTest(args);
    }

    @Deprecated
    protected String runTest(String[] args, String testname, int expectedExitCode) {
        String filename = TEST_OUPUT_DIRECTORY + testname + ".txt";
        long start = System.currentTimeMillis();
        createTestOutputFile(filename);
        System.out.println("Start running test " + testname);
        StatusCode statusCode = PMD.runPmd(args);
        assertEquals(expectedExitCode, statusCode.toInt());
        System.out.println("Test finished successfully after " + (System.currentTimeMillis() - start) + "ms.");
        return filename;
    }

    /**
     * Returns the log output.
     */
    protected String runTest(String... args) {
        return runTest(0, args);
    }

    /**
     * Returns the log output.
     */
    protected String runTest(int expectedExitCode, String... args) {
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        try {
            ByteArrayOutputStream console = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(console);
            System.setOut(out);
            System.setErr(out);
            StatusCode statusCode = PMD.runPmd(args);
            assertEquals(expectedExitCode, statusCode.toInt());
            return console.toString();
        } finally {
            System.setOut(oldOut);
            System.setOut(oldErr);
        }
    }

    @Deprecated
    protected void runPMDWith(String[] args) {
        PMD.main(args);
    }

    @Deprecated
    protected void checkStatusCode(int expectedExitCode) {
        int statusCode = getStatusCode();
        if (statusCode != expectedExitCode) {
            fail("PMD failed with status code:" + statusCode);
        }
    }

    @Deprecated
    protected int getStatusCode() {
        return Integer.parseInt(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY));
    }

    public static Matcher<String> containsPattern(final String regex) {
        return new BaseMatcher<String>() {
            final Pattern pattern = Pattern.compile(regex);

            @Override
            public void describeTo(Description description) {
                description.appendText("a string containing the pattern '" + this.pattern + "'");
            }

            @Override
            public boolean matches(Object o) {
                return o instanceof String && pattern.matcher((String) o).find();
            }
        };
    }
}
