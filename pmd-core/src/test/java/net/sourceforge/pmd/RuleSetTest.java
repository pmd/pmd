/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.util.CollectionUtil;

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

    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresName2() {
        new RuleSetBuilder(new Random().nextLong()).build();
    }

    @Test
    public void testNoDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        RuleSet rs = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(mock);
        assertFalse(rs.usesDFA(LanguageRegistry.getLanguage(DummyLanguageModule.NAME)));
    }

    @Test
    public void testIncludesRuleWithDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        mock.setDfa(true);
        RuleSet rs = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(mock);
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
        RuleSet rs = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(mock);
        assertEquals("unable to fetch rule by name", mock, rs.getRuleByName("name"));
    }

    @Test
    public void testGetRuleByName2() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(mock);
        assertNull("the rule FooRule must not be found!", rs.getRuleByName("FooRule"));
    }

    @Test
    public void testRuleList() {
        MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet ruleset = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);

        assertEquals("Size of RuleSet isn't one.", 1, ruleset.size());

        Collection<Rule> rules = ruleset.getRules();

        Iterator<Rule> i = rules.iterator();
        assertTrue("Empty Set", i.hasNext());
        assertEquals("Returned set of wrong size.", 1, rules.size());
        assertEquals("Rule isn't in ruleset.", rule, i.next());
    }

    private RuleSetBuilder createRuleSetBuilder(String name) {
        return new RuleSetBuilder(new Random().nextLong())
                .withName(name)
                .withDescription("Description for " + name);
    }

    @Test
    public void testAddRuleSet() {
        RuleSet set1 = createRuleSetBuilder("ruleset1")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set2 = createRuleSetBuilder("ruleset2")
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .addRuleSet(set1)
                .build();
        assertEquals("ruleset size wrong", 2, set2.size());
    }

    @Test(expected = RuntimeException.class)
    public void testAddRuleSetByReferenceBad() {
        RuleSet set1 = createRuleSetBuilder("ruleset1")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        createRuleSetBuilder("ruleset2")
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .addRuleSetByReference(set1, false)
                .build();
    }

    @Test
    public void testAddRuleSetByReferenceAllRule() {
        RuleSet set2 = createRuleSetBuilder("ruleset2")
                .withFileName("foo")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set1 = createRuleSetBuilder("ruleset1")
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
        RuleSet set2 = createRuleSetBuilder("ruleset2")
                .withFileName("foo")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .build();
        RuleSet set1 = createRuleSetBuilder("ruleset1")
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
        RuleSet ruleset = createRuleSetBuilder("ruleset").build();
        verifyRuleSet(ruleset, 0, new HashSet<RuleViolation>());
    }

    @Test
    public void testEquals1() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        assertFalse("A ruleset cannot be equals to null", s.equals(null));
    }

    @Test
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    public void testEquals2() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        assertTrue("A rulset must be equals to itself", s.equals(s));
    }

    @Test
    public void testEquals3() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong())
                .withName("basic rules")
                .withDescription("desc")
                .build();
        assertFalse("A ruleset cannot be equals to another kind of object", s.equals("basic rules"));
    }

    @Test
    public void testEquals4() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = createRuleSetBuilder("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        assertEquals("2 rulesets with same name and rules must be equals", s1, s2);
        assertEquals("Equals rulesets must have the same hashcode", s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testEquals5() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = createRuleSetBuilder("my other ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        assertFalse("2 rulesets with different name but same rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testEquals6() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
                .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
                .build();

        RuleSet s2 = createRuleSetBuilder("my ruleset")
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
        RuleSet ruleSet =
            createRuleSetBuilder("ruleset1")
                .withFileExclusions(Pattern.compile(".*"))
                .build();
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertEquals("Invalid number of patterns", 1, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testExcludePatternAreOrdered() {

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
                .withFileExclusions(Pattern.compile(".*"))
                .withFileExclusions(Pattern.compile(".*ha"))
                .build();
        assertEquals("Exclude pattern", Arrays.asList(".*", ".*ha"), ruleSet2.getExcludePatterns());
    }

    @Test
    public void testIncludePatternsAreOrdered() {

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
                .withFileInclusions(Pattern.compile(".*"))
                .withFileInclusions(Arrays.asList(Pattern.compile(".*ha"), Pattern.compile(".*hb")))
                .build();
        assertEquals("Exclude pattern", Arrays.asList(".*", ".*ha", ".*hb"), ruleSet2.getIncludePatterns());
    }

    @Test
    public void testAddExcludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1")
                .withFileExclusions(Pattern.compile(".*"))
                .build();

        assertNotNull("Exclude patterns", ruleSet.getFileExclusions());
        assertEquals("Invalid number of patterns", 1, ruleSet.getFileExclusions().size());

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
                .withFileExclusions(ruleSet.getFileExclusions())
                .build();
        assertNotNull("Exclude patterns", ruleSet2.getFileExclusions());
        assertEquals("Invalid number of patterns", 1, ruleSet2.getFileExclusions().size());
    }

    @Test
    public void testSetExcludePatterns() {
        List<Pattern> excludePatterns = new ArrayList<>();
        excludePatterns.add(Pattern.compile("ah*"));
        excludePatterns.add(Pattern.compile(".*"));
        RuleSet ruleSet = createRuleSetBuilder("ruleset").replaceFileExclusions(excludePatterns).build();
        assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        assertNotNull("Exclude patterns", ruleSet.getFileExclusions());
        assertEquals("Invalid number of exclude patterns", 2, ruleSet.getFileExclusions().size());
        assertEquals("Exclude pattern", "ah*", ruleSet.getFileExclusions().get(0).pattern());
        assertEquals("Exclude pattern", ".*", ruleSet.getFileExclusions().get(1).pattern());
        assertNotNull("Include patterns", ruleSet.getFileInclusions());
        assertEquals("Invalid number of include patterns", 0, ruleSet.getFileInclusions().size());
    }

    @Test
    public void testAddIncludePattern() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset")
                .withFileInclusions(Pattern.compile(".*"))
                .build();
        assertNotNull("Include patterns", ruleSet.getFileInclusions());
        assertEquals("Invalid number of patterns", 1, ruleSet.getFileInclusions().size());
        assertEquals("Include pattern", ".*", ruleSet.getFileInclusions().get(0).pattern());
        assertNotNull("Exclude patterns", ruleSet.getFileExclusions());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getFileExclusions().size());
    }

    @Test
    public void testAddIncludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1")
                .withFileInclusions(Pattern.compile("ah*"), Pattern.compile(".*"))
                .build();
        RuleSet ruleSet2 = createRuleSetBuilder("ruleset1")
                .withFileInclusions(ruleSet.getFileInclusions())
                .build();
        assertNotNull("Include patterns", ruleSet2.getFileInclusions());
        assertEquals("Invalid number of patterns", 2, ruleSet2.getFileInclusions().size());
        assertEquals("Include pattern", "ah*", ruleSet2.getFileInclusions().get(0).pattern());
        assertEquals("Include pattern", ".*", ruleSet2.getFileInclusions().get(1).pattern());
        assertNotNull("Exclude patterns", ruleSet.getFileExclusions());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getFileExclusions().size());
    }

    @Test
    public void testSetIncludePatterns() {
        List<Pattern> includePatterns = new ArrayList<>();
        includePatterns.add(Pattern.compile("ah*"));
        includePatterns.add(Pattern.compile(".*"));
        RuleSet ruleSet = createRuleSetBuilder("ruleset")
            .replaceFileInclusions(includePatterns)
            .build();

        assertEquals("Include patterns", includePatterns, ruleSet.getFileInclusions());
        assertNotNull("Exclude patterns", ruleSet.getFileInclusions());
        assertEquals("Invalid number of exclude patterns", 0, ruleSet.getFileExclusions().size());
    }

    @Test
    public void testIncludeExcludeApplies() {
        File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");

        RuleSet ruleSet = createRuleSetBuilder("ruleset").build();
        assertTrue("No patterns", ruleSet.applies(file));

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .build();
        assertTrue("Non-matching exclude", ruleSet.applies(file));

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"), Pattern.compile(".*/package/.*"))
                .build();
        assertFalse("Matching exclude", ruleSet.applies(file));

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .withFileExclusions(Pattern.compile(".*/package/.*"))
                .withFileInclusions(Pattern.compile(".*/randomX/.*"))
                .build();
        assertFalse("Non-matching include", ruleSet.applies(file));

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .withFileExclusions(Pattern.compile(".*/package/.*"))
                .withFileInclusions(Pattern.compile(".*/randomX/.*"))
                .withFileInclusions(Pattern.compile(".*/random/.*"))
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
        assertTrue("RuleChain rule", rule.isRuleChain());
        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1")
                .addRule(rule)
                .build();

        RuleSet ruleSet2 = createRuleSetBuilder("RuleSet2")
                .addRule(rule)
                .build();

        RuleSets ruleSets = new RuleSets();
        ruleSets.addRuleSet(ruleSet1);
        ruleSets.addRuleSet(ruleSet2);

        // Two violations
        RuleContext ctx = new RuleContext();
        Report r = new Report();
        ctx.setReport(r);
        ctx.setSourceCodeFile(file);
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ruleSets.apply(makeCompilationUnits(), ctx, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        assertEquals("Violations", 2, r.size());

        // One violation
        ruleSet1 = createRuleSetBuilder("RuleSet1")
                .withFileExclusions(Pattern.compile(".*/package/.*"))
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

    @Test
    public void copyConstructorDeepCopies() {
        Rule rule = new FooRule();
        rule.setName("FooRule1");
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.addRuleChainVisit("dummyNode");
        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1")
                .addRule(rule)
                .build();
        RuleSet ruleSet2 = new RuleSet(ruleSet1);

        assertEquals(ruleSet1, ruleSet2);
        assertNotSame(ruleSet1, ruleSet2);

        assertEquals(rule, ruleSet2.getRuleByName("FooRule1"));
        assertNotSame(rule, ruleSet2.getRuleByName("FooRule1"));
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

    @Test
    public void ruleExceptionShouldBeReported() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported")
                .addRule(new MockRule() {
                    @Override
                    public void apply(List<? extends Node> nodes, RuleContext ctx) {
                        throw new RuntimeException("Test exception while applying rule");
                    }
                })
                .build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFile(new File(RuleSetTest.class.getName() + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true); // the default
        ruleset.apply(makeCompilationUnits(), context);

        assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        assertEquals("Errors expected", 1, errors.size());
        assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        assertTrue("Should be a RuntimeException", errors.get(0).getError() instanceof RuntimeException);
    }

    @Test(expected = RuntimeException.class)
    public void ruleExceptionShouldBeThrownIfNotIgnored() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported")
                .addRule(new MockRule() {
                    @Override
                    public void apply(List<? extends Node> nodes, RuleContext ctx) {
                        throw new RuntimeException("Test exception while applying rule");
                    }
                })
                .build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFile(new File(RuleSetTest.class.getName() + ".ruleExceptionShouldBeThrownIfNotIgnored"));
        context.setIgnoreExceptions(false);
        ruleset.apply(makeCompilationUnits(), context);
    }

    @Test
    public void ruleExceptionShouldNotStopProcessingFile() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                for (Node node : nodes) {
                    addViolationWithMessage(ctx, node, "Test violation of the second rule in the ruleset");
                }
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFile(new File(RuleSetTest.class.getName() + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true); // the default
        ruleset.apply(makeCompilationUnits(), context);

        assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        assertEquals("Errors expected", 1, errors.size());
        assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        assertTrue("Should be a RuntimeException", errors.get(0).getError() instanceof RuntimeException);

        assertEquals("There should be a violation", 1, context.getReport().size());
    }

    @Test
    public void ruleExceptionShouldNotStopProcessingFileWithRuleChain() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            {
                addRuleChainVisit("dummyNode");
            }

            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {
            {
                addRuleChainVisit("dummyNode");
            }

            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                for (Node node : nodes) {
                    addViolationWithMessage(ctx, node, "Test violation of the second rule in the ruleset");
                }
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFile(new File(RuleSetTest.class.getName() + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true); // the default
        RuleSets rulesets = new RuleSets(ruleset);
        rulesets.apply(makeCompilationUnits(), context, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));

        assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        assertEquals("Errors expected", 1, errors.size());
        assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        assertTrue("Should be a RuntimeException", errors.get(0).getError() instanceof RuntimeException);

        assertEquals("There should be a violation", 1, context.getReport().size());
    }

}
