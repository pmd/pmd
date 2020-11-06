/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.cli.BaseCPDCLITest;

/**
 * Unit test for {@link CPDCommandLineInterface}.
 *
 */
public class CPDCommandLineInterfaceTest extends BaseCPDCLITest {
    /**
     * Test ignore identifiers argument.
     */
    @Test
    public void testIgnoreIdentifiers() throws Exception {
        runCPD("--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers");

        String out = getOutput();
        Assert.assertTrue(out.contains("Found a 7 line (36 tokens) duplication"));
        Assert.assertEquals(4, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    /**
     * Test ignore identifiers argument with failOnViolation=false
     */
    @Test
    public void testIgnoreIdentifiersFailOnViolationFalse() throws Exception {
        runCPD("--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--failOnViolation",
                "false");

        String out = getOutput();
        Assert.assertTrue(out.contains("Found a 7 line (36 tokens) duplication"));
        Assert.assertEquals(0, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    /**
     * Test excludes option.
     */
    @Test
    public void testExcludes() throws Exception {
        runCPD("--minimum-tokens", "34", "--language", "java", "--ignore-identifiers", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--exclude",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/File2.java");

        String out = getOutput();
        Assert.assertFalse(out.contains("Found a 7 line (34 tokens) duplication"));
        Assert.assertEquals(0, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    /**
     * #1144 CPD encoding argument has no effect
     */
    @Test
    public void testEncodingOption() throws Exception {
        String origEncoding = System.getProperty("file.encoding");

        // set the default encoding under Windows
        System.setProperty("file.encoding", "Cp1252");

        runCPD("--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--format", "xml",
                // request UTF-8 for CPD
                "--encoding", "UTF-8");
        // reset default encoding
        System.setProperty("file.encoding", origEncoding);

        String out = getOutput();
        Assert.assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        Assert.assertTrue(Pattern.compile("System\\.out\\.println\\([ij] \\+ \"ä\"\\);").matcher(out).find());
        Assert.assertEquals(4, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1178/
     *
     * @throws IOException
     *             any error
     */
    @Test
    public void testBrokenAndValidFile() throws IOException {
        runCPD("--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/badandgood/", "--format", "text", "--skip-lexical-errors");
        String out = getOutput();
        Assert.assertTrue(
                Pattern.compile("Skipping .*?BadFile\\.java\\. Reason: Lexical error in file").matcher(out).find());
        Assert.assertTrue(out.contains("Found a 5 line (13 tokens) duplication"));
        Assert.assertEquals(4, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    @Test
    public void testFormatXmlWithoutEncoding() throws Exception {
        runCPD("--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--format", "xml");
        String out = getOutput();
        Assert.assertTrue(out.contains("<duplication lines=\"3\" tokens=\"10\">"));
        Assert.assertEquals(4, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }

    @Test
    public void testCSVFormat() throws Exception {
        runCPD("--minimum-tokens", "100", "--files", "src/test/resources/net/sourceforge/pmd/cpd/badandgood/",
                "--language", "c", "--format", "csv");
        String out = getOutput();
        Assert.assertFalse(out.contains("Couldn't instantiate renderer"));
        Assert.assertEquals(0, Integer.parseInt(System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY)));
    }
}
