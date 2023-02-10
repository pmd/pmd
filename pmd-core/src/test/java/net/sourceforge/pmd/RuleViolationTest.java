/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.ReportTest.violation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

class RuleViolationTest {

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyRootNode s = helper.parse("abcd", "filename");
        RuleViolation r = new ParametricRuleViolation(rule, s, rule.getMessage());
        assertEquals(rule, r.getRule(), "object mismatch");
        assertEquals(1, r.getBeginLine(), "line number is wrong");
        assertEquals("filename", r.getFilename(), "filename is wrong");
    }

    @Test
    void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyRootNode s = helper.parse("abcd", "filename");
        RuleViolation r = new ParametricRuleViolation(rule, s, "description");
        assertEquals(rule, r.getRule(), "object mismatch");
        assertEquals(1, r.getBeginLine(), "line number is wrong");
        assertEquals("filename", r.getFilename(), "filename is wrong");
        assertEquals("description", r.getDescription(), "description is wrong");
    }

    @Test
    void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyNode s = helper.parse("(abc)", "filename1").getFirstChild();
        DummyNode s1 = helper.parse("(abc)", "filename2").getFirstChild();
        RuleViolation r1 = new ParametricRuleViolation(rule, s, "description");
        RuleViolation r2 = new ParametricRuleViolation(rule, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Test
    void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyRootNode root = helper.parse("(abc) (def)");
        DummyNode abcChild = root.getChild(0);
        DummyNode defChild = root.getChild(1);
        RuleViolation r1 = new ParametricRuleViolation(rule, abcChild, "description");
        RuleViolation r2 = new ParametricRuleViolation(rule, defChild, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Test
    void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        String filename = TextFile.UNKNOWN_FILENAME;


        FileLocation loc = FileLocation.range(filename, TextRange2d.range2d(10, 1, 15, 10));
        RuleViolation r1 = violation(rule, loc, "description");
        RuleViolation r2 = violation(rule, loc, "description");

        assertEquals(0, comp.compare(r1, r2));
        assertEquals(0, comp.compare(r2, r1));
    }
}
