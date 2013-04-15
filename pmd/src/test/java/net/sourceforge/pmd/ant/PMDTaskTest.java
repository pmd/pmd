/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

public class PMDTaskTest extends BuildFileTest {

    @Override
    public void setUp() {
        // initialize Ant    	
        configureProject("target/test-classes/net/sourceforge/pmd/ant/xml/pmdtasktest.xml");
        if (!project.getBaseDir().toString().endsWith("pmd/ant/xml")) {
            // when running from maven, the path needs to be adapted...
            // FIXME: this is more a workaround than a good solution...
            project.setBasedir(project.getBaseDir().toString()
        	    + "/target/test-classes/net/sourceforge/pmd/ant/xml");
        }
    }

    @Test
    public void testNoFormattersValidation() {
        executeTarget("testNoFormattersValidation");
        assertOutputContaining("Fields should be declared at the top of the class");
    }

    @Test
    public void testFormatterWithNoToFileAttribute() {
        expectBuildExceptionContaining("testFormatterWithNoToFileAttribute", "Valid Error Message", "toFile or toConsole needs to be specified in Formatter");
    }

    @Test
    public void testNoRuleSets() {
        expectBuildExceptionContaining("testNoRuleSets", "Valid Error Message", "No rulesets specified");
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
    public void testBasic() {
        executeTarget("testBasic");
    }

    @Test
    public void testInvalidLanguageVersion() {
        expectBuildExceptionContaining("testInvalidLanguageVersion", "Fail requested.", "The following language is not supported:<language name=\"java\" version=\"42\" />.");
    }
    
    @Test
    public void testExplicitRuleInRuleSet() {
        executeTarget("testExplicitRuleInRuleSet");
        assertOutputContaining("Avoid really long methods");
    }
    
    @Test
    public void testEcmascript() {
        executeTarget("testEcmascript");
        assertOutputContaining("A 'return', 'break', 'continue', or 'throw' statement should be the last in a block.");
        assertOutputContaining("Avoid using global variables");
        assertOutputContaining("Use ===/!== to compare with true/false or Numbers");
    }

    @Test
    public void testXML() {
        executeTarget("testXML");
        assertOutputContaining("Potentialy mistyped CDATA section with extra [ at beginning or ] at the end.");
    }

    @Test
    public void testClasspath() {
        executeTarget("testClasspath");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDTaskTest.class);
    }
}
