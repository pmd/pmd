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

    /**
     * Test excludes option.
     */
    @Test
    public void testExcludes() throws Exception {
        runCPD("--minimum-tokens", "34", "--language", "java",
                "--ignore-identifiers",
                "--files", "src/test/resources/net/sourceforge/pmd/cpd/clitest/",
                "--exclude", "src/test/resources/net/sourceforge/pmd/cpd/clitest/File2.java"
                );

        String out = buffer.toString("UTF-8");
        Assert.assertFalse(out.contains("Found a 7 line (34 tokens) duplication"));
    }

    /**
     * #1144 CPD encoding argument has no effect
     */
    @Test
    public void testEncodingOption() throws Exception {
        String origEncoding = System.getProperty("file.encoding");

        // set the default encoding under Windows
        System.setProperty("file.encoding", "Cp1252");

        runCPD("--minimum-tokens", "34", "--language", "java",
                "--files", "src/test/resources/net/sourceforge/pmd/cpd/clitest/",
                "--ignore-identifiers",
                "--format", "xml",
        // request UTF-8 for CPD
                "--encoding", "UTF-8");
        // reset default encoding
        System.setProperty("file.encoding", origEncoding);

        String out = buffer.toString("UTF-8");
        Assert.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    private void runCPD(String... args) {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        CPD.main(args);
    }
}
