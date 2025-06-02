/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.test.AbstractAntTestHelper;

class PMDTaskTest extends AbstractAntTestHelper {

    PMDTaskTest() {
        super.pathToTestScript = "target/test-classes/net/sourceforge/pmd/lang/ecmascript/ant/xml";
        super.antTestScriptFilename = "pmdtasktest.xml";
    }

    @Test
    void testEcmascript() {
        executeTarget("testEcmascript");
        assertOutputContaining("A 'return', 'break', 'continue', or 'throw' statement should be the last in a block.");
        assertOutputContaining("Avoid using global variables");
        assertOutputContaining("Use ===/!== to compare with true/false or Numbers");
    }
}
