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

    //
    // Changed logic of Comparator so that rules in the same file
    // get grouped together in the output report.
    // DDP 7/11/2002
    //
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleViolation r1 = new RuleViolation(rule, 10, "description", "filename1");
        RuleViolation r2 = new RuleViolation(rule, 20, "description", "filename2");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleViolation r1 = new RuleViolation(rule, 10, "description", "filename");
        RuleViolation r2 = new RuleViolation(rule, 20, "description", "filename");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleViolation r1 = new RuleViolation(rule, 10, "description", "filename");
        RuleViolation r2 = new RuleViolation(rule, 10, "description", "filename");
        assertEquals(0, comp.compare(r1, r2));
        assertEquals(0, comp.compare(r2, r1));
    }
}
