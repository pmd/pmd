package test.net.sourceforge.pmd;

import junit.framework.*;
import java.util.*;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

public class RuleViolationTest extends TestCase {
    public RuleViolationTest(String name) {
        super(name);
    }

    public void testBasic() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation r = new RuleViolation(rule, 2);
        System.out.println("r.getXML() = " + r.getXML());
        assertTrue(r.getXML().indexOf("desc") != -1);
        assertTrue(r.getXML().indexOf("2") != -1);
        assertTrue(r.getHTML().indexOf("desc") != -1);
        assertTrue(r.getHTML().indexOf("2") != -1);
    }
}
