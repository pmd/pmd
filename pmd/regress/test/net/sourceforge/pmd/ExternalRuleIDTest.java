/*
 * User: tom
 * Date: Jul 9, 2002
 * Time: 2:52:26 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.ExternalRuleID;

public class ExternalRuleIDTest extends TestCase {
    public ExternalRuleIDTest(String name) {
        super(name);
    }

    public void testParse() {
        String xrefString = "rulesets/basic.xml/EmptyCatchBlock";
        ExternalRuleID xref = new ExternalRuleID(xrefString);
        assertEquals("Filename mismatch!", "rulesets/basic.xml", xref.getFilename());
        assertEquals("Rule name mismatch!", "EmptyCatchBlock", xref.getRuleName());
    }
}
