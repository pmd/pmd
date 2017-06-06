/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;

public class RuleSetTest {

    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresName() {
        new RuleSetBuilder(new Random().nextLong())
            .withName(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresDescription() {
        new RuleSetBuilder(new Random().nextLong())
            .withName("some name")
            .withDescription(null);
    }

    @Test
    public void testNoDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        assertFalse(rs.usesDFA(LanguageRegistry.getLanguage(DummyLanguageModule.NAME)));
    }

    @Test
    public void testIncludesRuleWithDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        mock.setUsesDFA();
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        assertTrue(rs.usesDFA(LanguageRegistry.getLanguage(DummyLanguageModule.NAME)));
    }

    @Test
    public void testAccessors() {
        RuleSet rs = new RuleSetBuilder(new Random().nextLong())
                .withFileName("baz")
                .withName("foo")
                .withDescription("bar")
                .build();
        assertEquals("file name mismatch", "baz", rs.getFileName());
        assertEquals("name mismatch", "foo", rs.getName());
        assertEquals("description mismatch", "bar", rs.getDescription());
    }

    @Test
    public void testGetRuleByName() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        assertEquals("unable to fetch rule by name", mock, rs.getRuleByName("name"));
    }

    @Test
    public void testGetRuleByName2() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        assertNull("the rule FooRule must not be found!", rs.getRuleByName("FooRule"));
    }

    @Test
    public void testRuleList() {
        MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet ruleset = new RuleSetFactory().createSingleRuleRuleSet(rule);

        assertEquals("Size of RuleSet isn't one.", 1, ruleset.size());

        Collection<Rule> rules = ruleset.getRules();

        Iterator<Rule> i = rules.iterator();
        assertTrue("Empty Set", i.hasNext());
        assertEquals("Returned set of wrong size.", 1, rules.size());
        assertEquals("Rule isn't in ruleset.", rule, i.next());
    }

    @Test
    public void testAddRuleSet() {
        RuleSet set1 = new RuleSetBuilder(new Random().nextLong())
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set2 = new RuleSetBuilder(new Random().nextLong())
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .addRuleSet(set1)
                .build();
        assertEquals("ruleset size wrong", 2, set2.size());
    }

    @Test(expected = RuntimeException.class)
    public void testAddRuleSetByReferenceBad() {
        RuleSet set1 = new RuleSetBuilder(new Random().nextLong())
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set2 = new RuleSetBuilder(new Random().nextLong())
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .addRuleSetByReference(set1, false)
                .build();
    }

    @Test
    public void testAddRuleSetByReferenceAllRule() {
        RuleSet set2 = new RuleSetBuilder(new Random().nextLong())
                .withFileName("foo")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set1 = new RuleSetBuilder(new Random().nextLong())
                .addRuleSetByReference(set2, true)
                .build();
        assertEquals("wrong rule size", 2, set1.getRules().size());
        for (Rule rule : set1.getRules()) {
            assertTrue("not a rule reference", rule instanceof RuleReference);
            RuleReference ruleReference = (RuleReference) rule;
            assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
            assertTrue("not all rule reference", ruleReference.getRuleSetReference().isAllRules());
        }
    }

    @Test
    public void testAddRuleSetByReferenceSingleRule() {
        RuleSet set2 = new RuleSetBuilder(new Random().nextLong())
                .withFileName("foo")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set1 = new RuleSetBuilder(new Random().nextLong())
                .addRuleSetByReference(set2, false)
                .build();
        assertEquals("wrong rule size", 2, set1.getRules().size());
        for (Rule rule : set1.getRules()) {
            assertTrue("not a rule reference", rule instanceof RuleReference);
            RuleReference ruleReference = (RuleReference) rule;
            assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
            assertFalse("should not be all rule reference", ruleReference.getRuleSetReference().isAllRules());
        }
    }

    @Test
    public void testApply0Rules() throws Exception {
        RuleSet ruleset = new RuleSetBuilder(new Random().nextLong()).build();
        verifyRuleSet(ruleset, 0, new HashSet<RuleViolation>());
    }

    @Test
    public void testEquals1() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong()).build();
        assertFalse("A ruleset cannot be equals to null", s.equals(null));
    }

    @Test
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    public void testEquals2() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong()).build();
        assertTrue("A rulset must be equals to itself", s.equals(s));
    }

    @Test
    public void testEquals3() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong())
                .withName("basic rules")
                .build();
        assertFalse("A ruleset cannot be equals to another kind of object", s.equals("basic rules"));
    }

    @Test
    public void testEquals4() {
        RuleSet s1 = new RuleSetBuilder(new Random().nextLong())
                .withName("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = new RuleSetBuilder(new Random().nextLong())
                .withName("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        assertEquals("2 rulesets with same name and rules must be equals", s1, s2);
        assertEquals("Equals rulesets must have the same hashcode", s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testEquals5() {
        RuleSet s1 = new RuleSetBuilder(new Random().nextLong())
                .withName("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = new RuleSetBuilder(new Random().nextLong())
                .withName("my other ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        assertFalse("2 rulesets with different name but same rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testEquals6() {
        RuleSet s1 = new RuleSetBuilder(new Random().nextLong())
                .withName("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = new RuleSetBuilder(new Random().nextLong())
                .withName("my ruleset")
                .addRule(new MockRule("other rule", "desc", "msg", "rulesetname"))
                .build();

        assertFalse("2 rulesets with same name but different rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testLanguageApplies() {

        Rule rule = new MockRule();

        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        assertFalse("Different languages should not apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME).getDefaultVersion()));

        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        assertTrue("Same language with no min/max should apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));

        rule.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5"));
        assertTrue("Same language with valid min only should apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));

        rule.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.6"));
        assertTrue("Same language with valid min and max should apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));
        assertFalse("Same language with outside range of min/max should not apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.4")));
        assertFalse("Same language with outside range of min/max should not apply",
                RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7")));
    }

    @Test
    public void testAddExcludePattern() {
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("*")
                .build();
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of patterns", 1, ruleSet.getExcludePatterns().size());
        
        RuleSet ruleSet2 = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("*")
                .addExcludePattern("*") // try to create a duplicate
                .build();
        assertEquals("Invalid number of patterns", 1, ruleSet2.getExcludePatterns().size());
        assertEquals("Exclude pattern", "*", ruleSet2.getExcludePatterns().get(0));
        assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
        assertEquals("Invalid number of include patterns", 0, ruleSet2.getIncludePatterns().size());
    }

    @Test
    public void testAddExcludePatterns() {
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("*")
                .addExcludePattern(".*")
                .build();
        RuleSet ruleSet2 = new RuleSetBuilder(new Random().nextLong())
                .addExcludePatterns(ruleSet.getExcludePatterns())
                .build();
        assertNotNull("Exclude patterns", ruleSet2.getExcludePatterns());
        assertEquals("Invalid number of patterns", 2, ruleSet2.getExcludePatterns().size());
        
        RuleSet ruleSet3 = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("*")
                .addExcludePattern(".*")
                .addExcludePattern(".*") // try to create a duplicate
                .build();
        assertEquals("Invalid number of patterns", 2, ruleSet3.getExcludePatterns().size());
        assertEquals("Exclude pattern", "*", ruleSet3.getExcludePatterns().get(0));
        assertEquals("Exclude pattern", ".*", ruleSet3.getExcludePatterns().get(1));
        assertNotNull("Include patterns", ruleSet3.getIncludePatterns());
        assertEquals("Invalid number of include patterns", 0, ruleSet3.getIncludePatterns().size());
    }

    @Test
    public void testSetExcludePatterns() {
        List<String> excludePatterns = new ArrayList<>();
        excludePatterns.add("*");
        excludePatterns.add(".*");
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .setExcludePatterns(excludePatterns)
                .build();
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of exclude patterns", 2, ruleSet.getExcludePatterns().size());
        assertEquals("Exclude pattern", "*", ruleSet.getExcludePatterns().get(0));
        assertEquals("Exclude pattern", ".*", ruleSet.getExcludePatterns().get(1));
        assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        assertEquals("Invalid number of include patterns", 0, ruleSet.getIncludePatterns().size());
    }

    @Test
    public void testAddIncludePattern() {
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addIncludePattern("*")
                .build();
        assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        assertEquals("Invalid number of patterns", 1, ruleSet.getIncludePatterns().size());
        assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testAddIncludePatterns() {
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addIncludePattern("*")
                .addIncludePattern(".*")
                .build();
        RuleSet ruleSet2 = new RuleSetBuilder(new Random().nextLong())
                .addIncludePatterns(ruleSet.getIncludePatterns())
                .build();
        assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
        assertEquals("Invalid number of patterns", 2, ruleSet2.getIncludePatterns().size());
        assertEquals("Include pattern", "*", ruleSet2.getIncludePatterns().get(0));
        assertEquals("Include pattern", ".*", ruleSet2.getIncludePatterns().get(1));
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testSetIncludePatterns() {
        List<String> includePatterns = new ArrayList<>();
        includePatterns.add("*");
        includePatterns.add(".*");
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .setIncludePatterns(includePatterns)
                .build();
        assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        assertEquals("Invalid number of include patterns", 2, ruleSet.getIncludePatterns().size());
        assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
        assertEquals("Include pattern", ".*", ruleSet.getIncludePatterns().get(1));
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testIncludeExcludeApplies() {
        File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");

        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong()).build();
        assertTrue("No patterns", ruleSet.applies(file));

        ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("nomatch")
                .build();
        assertTrue("Non-matching exclude", ruleSet.applies(file));

        ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("nomatch")
                .addExcludePattern(".*/package/.*")
                .build();
        assertFalse("Matching exclude", ruleSet.applies(file));

        ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("nomatch")
                .addExcludePattern(".*/package/.*")
                .addIncludePattern(".*/randomX/.*")
                .build();
        assertFalse("Non-matching include", ruleSet.applies(file));

        ruleSet = new RuleSetBuilder(new Random().nextLong())
                .addExcludePattern("nomatch")
                .addExcludePattern(".*/package/.*")
                .addIncludePattern(".*/randomX/.*")
                .addIncludePattern(".*/random/.*")
                .build();
        assertTrue("Matching include", ruleSet.applies(file));
    }

    @Test
    public void testIncludeExcludeMultipleRuleSetWithRuleChainApplies() throws PMDException {
        File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");

        Rule rule = new FooRule();
        rule.setName("FooRule1");
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.addRuleChainVisit("dummyNode");
        assertTrue("RuleChain rule", rule.usesRuleChain());
        RuleSet ruleSet1 = new RuleSetBuilder(new Random().nextLong())
                .withName("RuleSet1")
                .addRule(rule)
                .build();

        RuleSet ruleSet2 = new RuleSetBuilder(new Random().nextLong())
                .withName("RuleSet2")
                .addRule(rule)
                .build();

        RuleSets ruleSets = new RuleSets();
        ruleSets.addRuleSet(ruleSet1);
        ruleSets.addRuleSet(ruleSet2);

        // Two violations
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report r = new Report();
        ctx.setReport(r);
        ctx.setSourceCodeFilename(file.getName());
        ctx.setSourceCodeFile(file);
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ruleSets.apply(makeCompilationUnits(), ctx, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        assertEquals("Violations", 2, r.size());

        // One violation
        ruleSet1 = new RuleSetBuilder(new Random().nextLong())
                .withName("RuleSet1")
                .addExcludePattern(".*/package/.*")
                .addRule(rule)
                .build();

        ruleSets = new RuleSets();
        ruleSets.addRuleSet(ruleSet1);
        ruleSets.addRuleSet(ruleSet2);

        r = new Report();
        ctx.setReport(r);
        ruleSets.apply(makeCompilationUnits(), ctx, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        assertEquals("Violations", 1, r.size());
    }

    private void verifyRuleSet(RuleSet ruleset, int size, Set<RuleViolation> values) {

        RuleContext context = new RuleContext();
        Set<RuleViolation> reportedValues = new HashSet<>();
        context.setReport(new Report());
        ruleset.apply(makeCompilationUnits(), context);

        assertEquals("Invalid number of Violations Reported", size, context.getReport().size());

        Iterator<RuleViolation> violations = context.getReport().iterator();
        while (violations.hasNext()) {
            RuleViolation violation = violations.next();

            reportedValues.add(violation);
            assertTrue("Unexpected Violation Returned: " + violation, values.contains(violation));
        }

        Iterator<RuleViolation> expected = values.iterator();
        while (expected.hasNext()) {
            RuleViolation violation = expected.next();
            assertTrue("Expected Violation not Returned: " + violation, reportedValues.contains(violation));
        }
    }

    private List<Node> makeCompilationUnits() {
        List<Node> nodes = new ArrayList<>();
        DummyNode node = new DummyNode(1);
        node.testingOnlySetBeginLine(1);
        node.testingOnlySetBeginColumn(1);
        node.setImage("Foo");
        nodes.add(node);
        return nodes;
    }
}
