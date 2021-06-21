/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

public class PMDTaskTest extends AbstractAntTestHelper {

    public PMDTaskTest() {
        super.antTestScriptFilename = "pmdtasktest.xml";
    }

    @Test
    public void testNoFormattersValidation() {
        executeTarget("testNoFormattersValidation");
        assertOutputContaining("Fields should be declared at the top of the class");
    }

    @Test
    public void testNestedRuleset() {
        executeTarget("testNestedRuleset");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
    }

    @Test
    public void testFormatterWithProperties() {
        executeTarget("testFormatterWithProperties");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
        assertOutputContaining("link_prefix");
        assertOutputContaining("line_prefix");
    }

    @Test
    public void testAbstractNames() {
        executeTarget("testAbstractNames");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
    }

    @Test
    public void testAbstractNamesInNestedRuleset() {
        executeTarget("testAbstractNamesInNestedRuleset");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
    }

    @Test
    public void testCommaInRulesetfiles() {
        executeTarget("testCommaInRulesetfiles");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
    }

    @Test
    public void testRelativeRulesets() {
        executeTarget("testRelativeRulesets");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at the");
    }

    @Test
    public void testRelativeRulesetsInRulesetfiles() {
        executeTarget("testRelativeRulesetsInRulesetfiles");
        assertOutputContaining("Avoid really long methods");
        assertOutputContaining("Fields should be declared at");
    }

    @Test
    public void testExplicitRuleInRuleSet() {
        executeTarget("testExplicitRuleInRuleSet");
        assertOutputContaining("Avoid really long methods");
    }

    @Test
    public void testClasspath() {
        executeTarget("testClasspath");
    }

    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final TestRule restoreLocale = new ExternalResource() {
        private Locale originalLocale;

        @Override
        protected void before() throws Throwable {
            originalLocale = Locale.getDefault();
        }

        @Override
        protected void after() {
            Locale.setDefault(originalLocale);
        }
    };

    private static void setDefaultCharset(String charsetName) {
        System.setProperty("file.encoding", charsetName);
    }

    @Rule
    public final TestRule restoreDefaultCharset = new ExternalResource() {
        private Charset defaultCharset;

        @Override
        protected void before() throws Throwable {
            defaultCharset = Charset.defaultCharset();
        }

        @Override
        protected void after() {
            setDefaultCharset(defaultCharset.name());
        }
    };

    @Test
    public void testFormatterEncodingWithXML() throws Exception {
        Locale.setDefault(Locale.FRENCH);
        setDefaultCharset("cp1252");

        executeTarget("testFormatterEncodingWithXML");
        String report = FileUtils.readFileToString(currentTempFile(), "UTF-8");
        assertTrue(report.contains("someVariableWithÜmlaut"));
    }

    private static String convert(String report) {
        // reinterpret output as cp1252 - ant BuildFileRule can only unicode
        StringBuilder sb = new StringBuilder(report.length());
        for (int i = 0; i < report.length(); i++) {
            char c = report.charAt(i);
            if (c > 0x7f) {
                sb.append((char) (c & 0xff));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Test
    public void testFormatterEncodingWithXMLConsole() throws UnsupportedEncodingException {
        setDefaultCharset("cp1252");

        executeTarget("testFormatterEncodingWithXMLConsole");
        String report = convert(buildRule.getOutput());
        String expectedStart = "\"<?xml version=\\\"1.0\\\" encoding=\\\"windows-1252\\\"?>\"";
        //assertTrue(String.format("XML start is different: Expected: [%s], Actual:[%s]", expectedStart,
        //   report.substring(0, expectedStart.length())), report.startsWith(expectedStart));
        assertTrue(report.contains("someVariableWithÜmlaut"));
    }

    @Test
    public void testMissingCacheLocation() {
        executeTarget("testMissingCacheLocation");
        assertOutputContaining("Avoid really long methods");
        assertContains(buildRule.getLog(), "This analysis could be faster");
    }

    @Test
    public void testAnalysisCache() {
        executeTarget("testAnalysisCache");
        assertOutputContaining("Avoid really long methods");
        assertDoesntContain(buildRule.getLog(), "This analysis could be faster");

        assertTrue(currentTempFile().exists());
    }


    @Test
    public void testDisableIncrementalAnalysis() {
        executeTarget("testDisableIncrementalAnalysis");
        assertOutputContaining("Avoid really long methods");
        assertDoesntContain(buildRule.getLog(), "This analysis could be faster");

        assertFalse(currentTempFile().exists());
    }
}
