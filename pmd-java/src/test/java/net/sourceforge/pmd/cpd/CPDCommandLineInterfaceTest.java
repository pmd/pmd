/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.cli.BaseCLITest.containsPattern;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public void testIgnoreIdentifiers() {
        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers");
        Assert.assertTrue(out.contains("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test ignore identifiers argument with failOnViolation=false
     */
    @Test
    public void testIgnoreIdentifiersFailOnViolationFalse() throws Exception {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--failOnViolation",
                "false");
        Assert.assertTrue(out.contains("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test ignore identifiers argument with failOnViolation=false with changed long options
     */
    @Test
    public void testIgnoreIdentifiersFailOnViolationFalseLongOption() throws Exception {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--fail-on-violation",
                "false");
        Assert.assertTrue(out.contains("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test excludes option.
     */
    @Test
    public void testExcludes() throws Exception {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--ignore-identifiers", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--exclude",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/File2.java");
        Assert.assertFalse(out.contains("Found a 7 line (34 tokens) duplication"));
    }

    /**
     * #1144 CPD encoding argument has no effect
     */
    @Test
    public void testEncodingOption() {
        String origEncoding = System.getProperty("file.encoding");

        // set the default encoding under Windows
        System.setProperty("file.encoding", "Cp1252");

        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--format", "xml",
                // request UTF-8 for CPD
                "--encoding", "UTF-8");
        // reset default encoding
        System.setProperty("file.encoding", origEncoding);

        assertThat(out, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(out, containsPattern("System\\.out\\.println\\([ij] \\+ \"ä\"\\);"));
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1178/
     *
     */
    @Test
    public void testBrokenAndValidFile() throws IOException {
        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/badandgood/", "--format", "text", "--skip-lexical-errors");
        Assert.assertTrue(
                Pattern.compile("Skipping .*?BadFile\\.java\\. Reason: Lexical error in file").matcher(out).find());
        Assert.assertTrue(out.contains("Found a 5 line (13 tokens) duplication"));
    }

    @Test
    public void testFormatXmlWithoutEncoding() throws Exception {
        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--format", "xml");
        Assert.assertTrue(out.contains("<duplication lines=\"3\" tokens=\"10\">"));
    }

    @Test
    public void testCSVFormat() throws Exception {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "100", "--files", "src/test/resources/net/sourceforge/pmd/cpd/badandgood/",
                "--language", "c", "--format", "csv");
        Assert.assertFalse(out.contains("Couldn't instantiate renderer"));
    }
}
