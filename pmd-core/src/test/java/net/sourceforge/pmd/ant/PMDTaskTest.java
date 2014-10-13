/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

public class PMDTaskTest extends BuildFileTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/pmdtasktest.xml");
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
    public void testBasic() {
        executeTarget("testBasic");
    }

    @Test
    public void testInvalidLanguageVersion() {
        expectBuildExceptionContaining("testInvalidLanguageVersion", "Fail requested.", "The following language is not supported:<language name=\"java\" version=\"42\" />.");
    }
}
