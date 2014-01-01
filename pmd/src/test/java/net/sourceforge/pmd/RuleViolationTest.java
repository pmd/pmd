/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.rule.MockRule;

import org.junit.Ignore;
import org.junit.Test;

public class RuleViolationTest {

    @Ignore
    @Test
    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyJavaNode s = new DummyJavaNode(1);
        s.testingOnly__setBeginLine(2);
        RuleViolation r = new JavaRuleViolation(rule, ctx, s, rule.getMessage());
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    @Ignore
    @Test
    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyJavaNode s = new DummyJavaNode(1);
        s.testingOnly__setBeginLine(2);
        RuleViolation r = new JavaRuleViolation(rule, ctx, s, "description");
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", 2, r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    @Ignore
    @Test
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename1");
        DummyJavaNode s = new DummyJavaNode(1);
        s.testingOnly__setBeginLine(10);
        RuleViolation r1 = new JavaRuleViolation(rule, ctx, s, "description");
        ctx.setSourceCodeFilename("filename2");
        DummyJavaNode s1 = new DummyJavaNode(1);
        s1.testingOnly__setBeginLine(10);
        RuleViolation r2 = new JavaRuleViolation(rule, ctx, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Ignore
    @Test
    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyJavaNode s = new DummyJavaNode(1);
        s.testingOnly__setBeginLine(10);
        DummyJavaNode s1 = new DummyJavaNode(1);
        s1.testingOnly__setBeginLine(20);
        RuleViolation r1 = new JavaRuleViolation(rule, ctx, s, "description");
        RuleViolation r2 = new JavaRuleViolation(rule, ctx, s1, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Ignore
    @Test
    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = RuleViolationComparator.INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyJavaNode s = new DummyJavaNode(1);
        s.testingOnly__setBeginLine(10);
        DummyJavaNode s1 = new DummyJavaNode(1);
        s1.testingOnly__setBeginLine(10);
        RuleViolation r1 = new JavaRuleViolation(rule, ctx, s, "description");
        RuleViolation r2 = new JavaRuleViolation(rule, ctx, s1, "description");
        assertEquals(1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleViolationTest.class);
    }
}
