/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CPDCommandLineInterfaceTest {
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

    @Test
    public void shouldFindDuplicatesWithDifferentFileExtensions() throws Exception {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files", "src/test/resources/net/sourceforge/pmd/cpd/ts/File1.ts",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/File2.ts");

        String out = bufferStdout.toString("UTF-8");
        Assert.assertTrue(out.contains("Found a 9 line (30 tokens) duplication in the following files"));
    }

    @Test
    public void shouldFindNoDuplicatesWithDifferentFileExtensions() throws Exception {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files", "src/test/resources/net/sourceforge/pmd/cpd/ts/");

        String out = bufferStdout.toString("UTF-8");
        Assert.assertTrue(out.isEmpty());
    }
    
    private void runCPD(String ... args) {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        CPD.main(args);
    }
}
