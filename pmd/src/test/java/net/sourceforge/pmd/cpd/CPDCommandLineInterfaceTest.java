/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link CPDCommandLineInterface}.
 *
 */
public class CPDCommandLineInterfaceTest {
    private ByteArrayOutputStream buffer;
    private PrintStream originalStdout;
    
    
    @Before
    public void setup() {
        originalStdout = System.out;
        buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
    }

    @After
    public void teardown() {
        System.setOut(originalStdout);
    }
    
    /**
     * Test ignore identifiers argument.
     */
    @Test
    public void testIgnoreIdentifiers() throws Exception {
        runCPD("--minimum-tokens", "34", "--language", "java", "--files", "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers");

        String out = buffer.toString("UTF-8");
        Assert.assertTrue(out.contains("Found a 7 line (34 tokens) duplication"));
    }

    private void runCPD(String... args) {
        CPD.dontExitForTests = true;
        CPD.main(args);
    }
}
