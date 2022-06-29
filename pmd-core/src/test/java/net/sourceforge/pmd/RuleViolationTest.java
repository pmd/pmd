/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

class RuleViolationTest {

    @Test
    void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyNode s = new DummyRootNode().withFileName("filename");
        s.setCoords(2, 1, 2, 3);
        RuleViolation r = new ParametricRuleViolation<Node>(rule, s, rule.getMessage());
        assertEquals(rule, r.getRule(), "object mismatch");
        assertEquals(2, r.getBeginLine(), "line number is wrong");
        assertEquals("filename", r.getFilename(), "filename is wrong");
    }

    @Test
    void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyNode s = new DummyRootNode().withFileName("filename");
        s.setCoords(2, 1, 2, 3);
        RuleViolation r = new ParametricRuleViolation<Node>(rule, s, "description");
        assertEquals(rule, r.getRule(), "object mismatch");
        assertEquals(2, r.getBeginLine(), "line number is wrong");
        assertEquals("filename", r.getFilename(), "filename is wrong");
        assertEquals("description", r.getDescription(), "description is wrong");
    }

    @Test
    void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyNode s = new DummyRootNode().withFileName("filename1");
        s.setCoords(10, 1, 11, 3);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, s, "description");
        DummyNode s1 = new DummyRootNode().withFileName("filename2");
        s1.setCoords(10, 1, 11, 3);
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Test
    void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyNode s = new DummyRootNode().withFileName("filename1");
        s.setCoords(10, 1, 15, 10);
        DummyNode s1 = new DummyRootNode().withFileName("filename1");
        s1.setCoords(20, 1, 25, 10);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, s, "description");
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, s1, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Disabled
    @Test
    void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyRootNode rootNode = new DummyRootNode();
        rootNode.withFileName("filename1");

        DummyNode s = new DummyNode();
        s.setCoords(10, 1, 15, 10);
        rootNode.addChild(s, 0);
        RuleViolation r1 = new ParametricRuleViolation<Node>(rule, s, "description");

        DummyNode s1 = new DummyNode();
        s1.setCoords(10, 1, 15, 10);
        rootNode.addChild(s1, 1);
        RuleViolation r2 = new ParametricRuleViolation<Node>(rule, s1, "description");

        assertEquals(1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }
}
