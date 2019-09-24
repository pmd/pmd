/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

import junit.framework.JUnit4TestAdapter;

public class RuleViolationTest {

    @Test
    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(2);
        s.testingOnlySetBeginColumn(1);
        RuleViolation r = new ParametricRuleViolation<Node>(rule, ctx, s, rule.getMessage());
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    @Test
    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(2);
        s.testingOnlySetBeginColumn(1);
        RuleViolation r = new ParametricRuleViolation<Node>(rule, ctx, s, "description");
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    @Test
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("filename1"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(10);
        s.testingOnlySetBeginColumn(1);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, ctx, s, "description");
        ctx.setSourceCodeFile(new File("filename2"));
        DummyNode s1 = new DummyNode(1);
        s1.testingOnlySetBeginLine(10);
        s1.testingOnlySetBeginColumn(1);
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, ctx, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Test
    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(10);
        s.testingOnlySetBeginColumn(1);
        DummyNode s1 = new DummyNode(1);
        s1.testingOnlySetBeginLine(20);
        s1.testingOnlySetBeginColumn(1);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, ctx, s, "description");
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, ctx, s1, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Ignore
    @Test
    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("filename"));
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(10);
        s.testingOnlySetBeginColumn(1);
        DummyNode s1 = new DummyNode(1);
        s1.testingOnlySetBeginLine(10);
        s1.testingOnlySetBeginColumn(1);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, ctx, s, "description");
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, ctx, s1, "description");
        assertEquals(1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleViolationTest.class);
    }
}
