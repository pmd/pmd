package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.ExternalRuleID;

public class ExternalRuleIDTest extends TestCase {

    public void testSimpleRef() {
        String xrefString = "rulesets/basic.xml/EmptyCatchBlock";
        ExternalRuleID xref = new ExternalRuleID(xrefString);
        assertEquals("Filename mismatch!", "rulesets/basic.xml", xref.getFilename());
        assertEquals("Rule name mismatch!", "EmptyCatchBlock", xref.getRuleName());
    }
}
