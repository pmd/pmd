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

import org.apache.tools.ant.util.TeeOutputStream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 * @deprecated Only pmd-cli module should use / test the CLI.
 */
@Deprecated
public abstract class BaseCLITest {

    protected static final String TEST_OUPUT_DIRECTORY = "target/cli-tests/";

    // Points toward a folder with not many source files, to avoid actually PMD
    // and slowing down tests
    protected static final String SOURCE_FOLDER = "src/test/resources/net/sourceforge/pmd/cli";

    @Rule
    public SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests();
    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests();

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

    /**
     * @deprecated Use {@link #runTest(String...)}, note that
     *     it returns the log while this returns the name of a file containing the log.
     */
    @Deprecated
    protected String runTest(String[] args, String testname) {
        return runTest(args, testname, 0);
    }

    /**
     * @deprecated Use {@link #runTest(StatusCode, String...)}, note that
     *     it returns the log while this returns the name of a file containing the log.
     */
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
        return runTest(StatusCode.OK, args);
    }

    /**
     * Returns the log output.
     *
     * @deprecated Use {@link #runTest(StatusCode, String...)}
     */
    @Deprecated
    protected String runTest(int expectedExitCode, String... args) {
        switch (expectedExitCode) {
        case 0:
            return runTest(StatusCode.OK, args);
        case 1:
            return runTest(StatusCode.ERROR, args);
        case 4:
            return runTest(StatusCode.VIOLATIONS_FOUND, args);
        default:
            throw AssertionUtil.shouldNotReachHere("unknown status code " + expectedExitCode);
        }
    }

    protected String runTest(StatusCode expectedExitCode, String... args) {
        ByteArrayOutputStream console = new ByteArrayOutputStream();

        PrintStream out = new PrintStream(new TeeOutputStream(console, System.out));
        PrintStream err = new PrintStream(new TeeOutputStream(console, System.err));
        System.setOut(out);
        System.setErr(err);
        StatusCode statusCode = PMD.runPmd(args);
        assertEquals(expectedExitCode, statusCode);
        return console.toString();
    }

    /**
     * @deprecated Use {@link #runTest(StatusCode, String...)}
     */
    @Deprecated
    protected void runPMDWith(String[] args) {
        PMD.main(args);
    }

    /**
     * @deprecated Use {@link #runTest(StatusCode, String...)} instead of checking the return code manually
     */
    @Deprecated
    protected void checkStatusCode(int expectedExitCode) {
        int statusCode = getStatusCode();
        if (statusCode != expectedExitCode) {
            fail("PMD failed with status code: " + statusCode);
        }
    }

    /**
     * @deprecated Use {@link #runTest(StatusCode, String...)} instead
     * of checking the return code manually
     */
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
