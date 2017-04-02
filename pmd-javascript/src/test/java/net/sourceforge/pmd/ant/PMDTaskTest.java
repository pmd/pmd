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
    public void testEcmascript() {
        executeTarget("testEcmascript");
        assertOutputContaining("A 'return', 'break', 'continue', or 'throw' statement should be the last in a block.");
        assertOutputContaining("Avoid using global variables");
        assertOutputContaining("Use ===/!== to compare with true/false or Numbers");
    }
}
