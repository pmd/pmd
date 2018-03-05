/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;

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

    // See http://stackoverflow.com/questions/361975/setting-the-default-java-character-encoding and http://stackoverflow.com/a/14987992/1169968
    private static void setDefaultCharset(String charsetName) {
        try {
            System.setProperty("file.encoding", charsetName);
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            Objects.requireNonNull(Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        assertTrue(report.contains("unusedVariableWithÜmlaut"));
    }

    @Test
    public void testFormatterEncodingWithXMLConsole() {
        setDefaultCharset("cp1252");

        executeTarget("testFormatterEncodingWithXMLConsole");
        String report = buildRule.getOutput();
        assertTrue(report.startsWith("<?xml version=\"1.0\" encoding=\"windows-1252\"?>"));
        assertTrue(report.contains("unusedVariableWith&#xdc;mlaut"));
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
