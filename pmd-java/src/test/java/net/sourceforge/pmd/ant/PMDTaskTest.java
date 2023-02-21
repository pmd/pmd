/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

class PMDTaskTest extends AbstractAntTestHelper {

    PMDTaskTest() {
        antTestScriptFilename = "pmdtasktest.xml";
    }

    @Test
    void testNoFormattersValidation() {
        executeTarget("testNoFormattersValidation");
        assertOutputContaining("Violation from test-rset-1.xml");
    }

    @Test
    void testNestedRuleset() {
        executeTarget("testNestedRuleset");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertOutputContaining("Violation from test-rset-2.xml");
    }

    @Test
    void testFormatterWithProperties() {
        executeTarget("testFormatterWithProperties");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertOutputContaining("link_prefix");
        assertOutputContaining("line_prefix");
    }

    @Test
    void testAbstractNames() {
        executeTarget("testAbstractNames");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertOutputContaining("Violation from test-rset-2.xml");
    }

    @Test
    void testAbstractNamesInNestedRuleset() {
        executeTarget("testAbstractNamesInNestedRuleset");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertOutputContaining("Violation from test-rset-2.xml");
    }

    @Test
    void testCommaInRulesetfiles() {
        executeTarget("testCommaInRulesetfiles");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertOutputContaining("Violation from test-rset-2.xml");
    }

    @Test
    void testRelativeRulesets() {
        executeTarget("testRelativeRulesets");
        assertOutputContaining("Violation from test-rset-1.xml");
    }

    @Test
    void testRelativeRulesetsInRulesetfiles() {
        executeTarget("testRelativeRulesetsInRulesetfiles");
        assertOutputContaining("Violation from test-rset-1.xml");
    }

    @Test
    void testExplicitRuleInRuleSet() {
        executeTarget("testExplicitRuleInRuleSet");
        assertOutputContaining("Violation from test-rset-1.xml");
    }

    @Test
    void testClasspath() {
        executeTarget("testClasspath");
    }

    private static void setDefaultCharset(String charsetName) {
        System.setProperty("file.encoding", charsetName);
    }

    @Test
    void testFormatterEncodingWithXML() throws Exception {
        Locale.setDefault(Locale.FRENCH);
        setDefaultCharset("cp1252");

        executeTarget("testFormatterEncodingWithXML");
        String report = IOUtil.readFileToString(currentTempFile(), StandardCharsets.UTF_8);
        assertTrue(report.contains("someVariableWithÜmlaut"));
    }

    @Test
    void testFormatterEncodingWithXMLConsole() throws UnsupportedEncodingException {
        setDefaultCharset("cp1252");

        String report = executeTarget("testFormatterEncodingWithXMLConsole");
        assertTrue(report.startsWith("<?xml version=\"1.0\" encoding=\"windows-1252\"?>"));
        assertTrue(report.contains("someVariableWithÜmlaut"));
    }

    @Test
    void testMissingCacheLocation() {
        executeTarget("testMissingCacheLocation");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertContains(getLog(), "This analysis could be faster");
    }

    @Test
    void testAnalysisCache() {
        executeTarget("testAnalysisCache");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertDoesntContain(getLog(), "This analysis could be faster");

        assertTrue(currentTempFile().exists());
    }


    @Test
    void testDisableIncrementalAnalysis() {
        executeTarget("testDisableIncrementalAnalysis");
        assertOutputContaining("Violation from test-rset-1.xml");
        assertDoesntContain(getLog(), "This analysis could be faster");

        assertFalse(currentTempFile().exists());
    }
}
