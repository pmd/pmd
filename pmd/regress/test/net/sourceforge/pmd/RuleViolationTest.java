package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

public class RuleViolationTest extends TestCase {

    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r = new RuleViolation(rule, 2, ctx);
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r = new RuleViolation(rule, 2, "description", ctx);
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename1");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        ctx.setSourceCodeFilename("filename2");
        RuleViolation r2 = new RuleViolation(rule, 20, "description", ctx);
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        RuleViolation r2 = new RuleViolation(rule, 20, "description", ctx);
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg");
        RuleViolation.RuleViolationComparator comp = new RuleViolation.RuleViolationComparator();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation r1 = new RuleViolation(rule, 10, "description", ctx);
        RuleViolation r2 = new RuleViolation(rule, 10, "description", ctx);
        assertEquals(0, comp.compare(r1, r2));
        assertEquals(0, comp.compare(r2, r1));
    }
}
