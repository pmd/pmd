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
    public void testXML() {
        executeTarget("testXML");
        assertOutputContaining("Potentially mistyped CDATA section with extra [ at beginning or ] at the end.");
    }
}
