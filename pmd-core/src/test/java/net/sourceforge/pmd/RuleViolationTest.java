/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

import junit.framework.JUnit4TestAdapter;

public class RuleViolationTest {

    @Test
    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyRootNode s = DummyLanguageModule.parse("abcd", "filename");
        RuleViolation r = new ParametricRuleViolation(rule, s, rule.getMessage());
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", s.getBeginLine(), r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
    }

    @Test
    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        DummyRootNode s = DummyLanguageModule.parse("abcd", "filename");
        RuleViolation r = new ParametricRuleViolation(rule, s, "description");
        assertEquals("object mismatch", rule, r.getRule());
        assertEquals("line number is wrong", s.getBeginLine(), r.getBeginLine());
        assertEquals("filename is wrong", "filename", r.getFilename());
        assertEquals("description is wrong", "description", r.getDescription());
    }

    @Test
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyNode s = DummyLanguageModule.parse("(abc)", "filename1").getFirstChild();
        DummyNode s1 = DummyLanguageModule.parse("(abc)", "filename2").getFirstChild();
        RuleViolation r1 = new ParametricRuleViolation(rule, s, "description");
        RuleViolation r2 = new ParametricRuleViolation(rule, s1, "description");
        assertEquals(-1, comp.compare(r1, r2));
        assertEquals(1, comp.compare(r2, r1));
    }

    @Test
    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        DummyRootNode root = DummyLanguageModule.parse("(abc) (def)");
        DummyNode abcChild = root.getChild(0);
        DummyNode defChild = root.getChild(1);
        RuleViolation r1 = new ParametricRuleViolation(rule, abcChild, "description");
        RuleViolation r2 = new ParametricRuleViolation(rule, defChild, "description");
        assertTrue(comp.compare(r1, r2) < 0);
        assertTrue(comp.compare(r2, r1) > 0);
    }

    @Test
    public void testComparatorWithSameFileSameLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Comparator<RuleViolation> comp = RuleViolation.DEFAULT_COMPARATOR;
        String filename = TextFile.UNKNOWN_FILENAME;


        FileLocation loc = FileLocation.range(filename, TextRange2d.range2d(10, 1, 15, 10));
        RuleViolation r1 = new ParametricRuleViolation(rule, loc, "description");
        RuleViolation r2 = new ParametricRuleViolation(rule, loc, "description");

        assertEquals(0, comp.compare(r1, r2));
        assertEquals(0, comp.compare(r2, r1));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleViolationTest.class);
    }
}
