/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ant;

import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class PMDTaskTest extends BuildFileTest {

    @Override
    public void setUp() {
        // initialize Ant
        configureProject("regress/test/net/sourceforge/pmd/ant/xml/pmdtasktest.xml");
    }

    @Test
    public void testNoFormattersValidation() {
        // FIXME: no formatter is needed for ant to run
        // see TODO in PMDTask.validate()
        if (TestDescriptor.inRegressionTestMode()) {
            return;
        }
        expectBuildExceptionContaining("testNoFormattersValidation", "Valid Error Message", "<??>");
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
        assertOutputContaining("Position literals first in String comparisons");
        assertOutputContaining("Too many fields");
    }

    @Test
    public void testBasic() {
        executeTarget("testBasic");
    }

    @Test
    public void testInvalidJDK() {
        expectBuildExceptionContaining("testInvalidJDK", "Fail requested.", "The targetjdk attribute, if used, must be set to either ");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDTaskTest.class);
    }
}
