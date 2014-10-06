/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import org.junit.Test;

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
}
