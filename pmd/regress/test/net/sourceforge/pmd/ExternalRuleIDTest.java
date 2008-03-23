/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.ExternalRuleID;

import org.junit.Test;
public class ExternalRuleIDTest {

    @Test
    public void testSimpleRef() {
        String xrefString = "rulesets/basic.xml/EmptyCatchBlock";
        ExternalRuleID xref = new ExternalRuleID(xrefString);
        assertEquals("Filename mismatch!", "rulesets/basic.xml", xref.getFilename());
        assertEquals("Rule name mismatch!", "EmptyCatchBlock", xref.getRuleName());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ExternalRuleIDTest.class);
    }
}
