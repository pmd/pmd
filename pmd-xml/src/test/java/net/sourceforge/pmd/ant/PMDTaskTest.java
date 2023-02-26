/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import org.junit.jupiter.api.Test;

class PMDTaskTest extends AbstractAntTestHelper {

    PMDTaskTest() {
        super.antTestScriptFilename = "pmdtasktest.xml";
    }

    @Test
    void testXML() {
        executeTarget("testXML");
        assertOutputContaining("Potentially mistyped CDATA section with extra [ at beginning or ] at the end.");
    }
}
