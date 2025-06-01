/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.test.AbstractAntTestHelper;
import org.junit.jupiter.api.Test;

class PMDTaskTest extends AbstractAntTestHelper {

    PMDTaskTest() {
        super.pathToTestScript = "target/test-classes/net/sourceforge/pmd/lang/xml";
        super.antTestScriptFilename = "pmdtasktest.xml";
    }

    @Test
    void testXML() {
        executeTarget("testXML");
        assertOutputContaining("Potentially mistyped CDATA section with extra [ at beginning or ] at the end.");
    }
}
