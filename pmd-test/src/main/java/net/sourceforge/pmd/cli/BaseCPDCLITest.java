/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDCommandLineInterface;

public abstract class BaseCPDCLITest {
    private ByteArrayOutputStream bufferStdout;
    private PrintStream originalStdout;
    private PrintStream originalStderr;

    @Before
    public void setup() throws UnsupportedEncodingException {
        originalStdout = System.out;
        originalStderr = System.err;
        bufferStdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bufferStdout, false, "UTF-8"));
        System.setErr(System.out);
    }

    @After
    public void teardown() {
        System.setOut(originalStdout);
        System.setErr(originalStderr);
    }

    /**
     * @deprecated Use {@link #runTest(CPD.StatusCode, String...)} which returns the output.
     */
    @Deprecated
    public final String getOutput() {
        try {
            return bufferStdout.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated Use {@link #runTest(CPD.StatusCode, String...)}
     */
    @Deprecated
    protected void runCPD(String... args) {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        CPD.main(args);
    }

    protected String runTest(CPD.StatusCode expectedStatusCode, String... args) {
        CPD.StatusCode statusCode = CPD.runCpd(args);
        Assert.assertEquals("Unexpected status code", expectedStatusCode, statusCode);
        return getOutput();
    }
}
