/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.cli.BaseCLITest.containsPattern;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(out, containsString("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test ignore identifiers argument with failOnViolation=false
     */
    @Test
    public void testIgnoreIdentifiersFailOnViolationFalse() {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--failOnViolation",
                "false");
        assertThat(out, containsString("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test ignore identifiers argument with failOnViolation=false with changed long options
     */
    @Test
    public void testIgnoreIdentifiersFailOnViolationFalseLongOption() {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--ignore-identifiers", "--fail-on-violation",
                "false");
        assertThat(out, containsString("Found a 7 line (36 tokens) duplication"));
    }

    /**
     * Test excludes option.
     */
    @Test
    public void testExcludes() {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "34", "--language", "java", "--ignore-identifiers", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--exclude",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/File2.java");
        assertThat(out, not(containsString("Found a 7 line (34 tokens) duplication")));
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
    public void testBrokenAndValidFile() {
        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/badandgood/", "--format", "text", "--skip-lexical-errors");
        String stderr = getStderr();
        assertThat(stderr, containsPattern("Skipping .*?BadFile\\.java\\. Reason: Lexical error in file"));
        assertThat(out, containsString("Found a 5 line (13 tokens) duplication"));
    }

    @Test
    public void testFormatXmlWithoutEncoding() {
        String out = runTest(CPD.StatusCode.DUPLICATE_CODE_FOUND, "--minimum-tokens", "10", "--language", "java", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/clitest/", "--format", "xml");
        assertThat(out, containsString("<duplication lines=\"3\" tokens=\"10\">"));
    }

    @Test
    public void testCSVFormat() {
        String out = runTest(CPD.StatusCode.OK, "--minimum-tokens", "100", "--files", "src/test/resources/net/sourceforge/pmd/cpd/badandgood/",
                "--language", "c", "--format", "csv");
        assertThat(out, not(containsString("Couldn't instantiate renderer")));
    }
}
