package test.net.sourceforge.pmd;

import junit.framework.*;
import java.util.*;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

public class RuleViolationTest extends TestCase {
    public RuleViolationTest(String name) {
        super(name);
    }

    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation r = new RuleViolation(rule, 2, "filename");
        assertEquals(rule, r.getRule());
        assertEquals(2, r.getLine());
        assertEquals("filename", r.getFilename());
    }

    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation r = new RuleViolation(rule, 2, "description", "filename");
        assertEquals(rule, r.getRule());
        assertEquals(2, r.getLine());
        assertEquals("filename", r.getFilename());
        assertEquals("description", r.getDescription());
    }
}
