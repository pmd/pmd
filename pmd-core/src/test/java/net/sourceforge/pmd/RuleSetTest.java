/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.PmdCoreTestUtils.dummyLanguage;
import static net.sourceforge.pmd.PmdCoreTestUtils.dummyLanguage2;
import static net.sourceforge.pmd.PmdCoreTestUtils.dummyVersion;
import static net.sourceforge.pmd.ReportTestUtil.getReportForRuleSetApply;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

class RuleSetTest {

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void testRuleSetRequiresName() {
        assertThrows(NullPointerException.class, () ->
            new RuleSetBuilder(new Random().nextLong())
                .withName(null));
    }

    @Test
    void testRuleSetRequiresDescription() {
        assertThrows(NullPointerException.class, () ->
            new RuleSetBuilder(new Random().nextLong())
                .withName("some name")
                .withDescription(null));
    }

    @Test
    void testRuleSetRequiresName2() {
        assertThrows(NullPointerException.class, () ->
            new RuleSetBuilder(new Random().nextLong()).build());
    }

    @Test
    void testAccessors() {
        RuleSet rs = new RuleSetBuilder(new Random().nextLong())
                .withFileName("baz")
                .withName("foo")
                .withDescription("bar")
                .build();
        assertEquals("baz", rs.getFileName(), "file name mismatch");
        assertEquals("foo", rs.getName(), "name mismatch");
        assertEquals("bar", rs.getDescription(), "description mismatch");
    }

    @Test
    void testGetRuleByName() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = RuleSet.forSingleRule(mock);
        assertEquals(mock, rs.getRuleByName("name"), "unable to fetch rule by name");
    }

    @Test
    void testGetRuleByName2() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = RuleSet.forSingleRule(mock);
        assertNull(rs.getRuleByName("FooRule"), "the rule FooRule must not be found!");
    }

    @Test
    void testRuleList() {
        MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet ruleset = RuleSet.forSingleRule(rule);

        assertEquals(1, ruleset.size(), "Size of RuleSet isn't one.");

        Collection<Rule> rules = ruleset.getRules();

        Iterator<Rule> i = rules.iterator();
        assertTrue(i.hasNext(), "Empty Set");
        assertEquals(1, rules.size(), "Returned set of wrong size.");
        assertEquals(rule, i.next(), "Rule isn't in ruleset.");
    }

    private RuleSetBuilder createRuleSetBuilder(String name) {
        return new RuleSetBuilder(new Random().nextLong())
            .withName(name)
            .withDescription("Description for " + name);
    }

    @Test
    void testAddRuleSet() {
        RuleSet set1 = createRuleSetBuilder("ruleset1")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();
        RuleSet set2 = createRuleSetBuilder("ruleset2")
                .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                .addRuleSet(set1)
                .build();
        assertEquals(2, set2.size(), "ruleset size wrong");
    }

    @Test
    void testAddRuleSetByReferenceBad() {
        RuleSet set1 = createRuleSetBuilder("ruleset1")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        assertThrows(RuntimeException.class, () ->
            createRuleSetBuilder("ruleset2")
                    .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
                    .addRuleSetByReference(set1, false)
                    .build());
    }

    @Test
    void testAddRuleSetByReferenceAllRule() {
        RuleSet set2 = createRuleSetBuilder("ruleset2")
            .withFileName("foo")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
            .build();
        RuleSet set1 = createRuleSetBuilder("ruleset1")
                .addRuleSetByReference(set2, true)
                .build();
        assertEquals(2, set1.getRules().size(), "wrong rule size");
        for (Rule rule : set1.getRules()) {
            assertTrue(rule instanceof RuleReference, "not a rule reference");
            RuleReference ruleReference = (RuleReference) rule;
            assertEquals("foo", ruleReference.getRuleSetReference().getRuleSetFileName(), "wrong ruleset file name");
            assertTrue(ruleReference.getRuleSetReference().isAllRules(), "not all rule reference");
        }
    }

    @Test
    void testAddRuleSetByReferenceSingleRule() {
        RuleSet set2 = createRuleSetBuilder("ruleset2")
            .withFileName("foo")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .addRule(new MockRule("name2", "desc", "msg", "rulesetname"))
            .build();
        RuleSet set1 = createRuleSetBuilder("ruleset1")
                .addRuleSetByReference(set2, false)
                .build();
        assertEquals(2, set1.getRules().size(), "wrong rule size");
        for (Rule rule : set1.getRules()) {
            assertTrue(rule instanceof RuleReference, "not a rule reference");
            RuleReference ruleReference = (RuleReference) rule;
            assertEquals("foo", ruleReference.getRuleSetReference().getRuleSetFileName(), "wrong ruleset file name");
            assertFalse(ruleReference.getRuleSetReference().isAllRules(), "should not be all rule reference");
        }
    }

    @Test
    void testApply0Rules() throws Exception {
        RuleSet ruleset = createRuleSetBuilder("ruleset").build();
        verifyRuleSet(ruleset, new HashSet<RuleViolation>());
    }

    @Test
    void testEquals1() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        assertFalse(s.equals(null), "A ruleset cannot be equals to null");
    }

    @Test
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    void testEquals2() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        assertTrue(s.equals(s), "A rulset must be equals to itself");
    }

    @Test
    void testEquals3() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong())
                .withName("basic rules")
                .withDescription("desc")
                .build();
        assertFalse(s.equals("basic rules"), "A ruleset cannot be equals to another kind of object");
    }

    @Test
    void testEquals4() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        RuleSet s2 = createRuleSetBuilder("my ruleset")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        assertEquals(s1, s2, "2 rulesets with same name and rules must be equals");
        assertEquals(s1.hashCode(), s2.hashCode(), "Equals rulesets must have the same hashcode");
    }

    @Test
    void testEquals5() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        RuleSet s2 = createRuleSetBuilder("my other ruleset")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        assertFalse(s1.equals(s2), "2 rulesets with different name but same rules must not be equals");
    }

    @Test
    void testEquals6() {
        RuleSet s1 = createRuleSetBuilder("my ruleset")
            .addRule(new MockRule("name", "desc", "msg", "rulesetname"))
            .build();

        RuleSet s2 = createRuleSetBuilder("my ruleset")
            .addRule(new MockRule("other rule", "desc", "msg", "rulesetname"))
            .build();

        assertFalse(s1.equals(s2), "2 rulesets with same name but different rules must not be equals");
    }

    @Test
    void testLanguageApplies() {

        Rule rule = new MockRule();

        assertFalse(RuleSet.applies(rule, dummyLanguage2().getDefaultVersion()),
                "Different languages should not apply");

        assertTrue(RuleSet.applies(rule, dummyLanguage().getVersion("1.5")),
                "Same language with no min/max should apply");

        rule.setMinimumLanguageVersion(dummyLanguage().getVersion("1.5"));
        assertTrue(RuleSet.applies(rule, dummyLanguage().getVersion("1.5")),
                "Same language with valid min only should apply");

        rule.setMaximumLanguageVersion(dummyLanguage().getVersion("1.6"));
        assertTrue(RuleSet.applies(rule, dummyLanguage().getVersion("1.5")),
                "Same language with valid min and max should apply");
        assertFalse(RuleSet.applies(rule, dummyLanguage().getVersion("1.4")),
                "Same language with outside range of min/max should not apply");
        assertFalse(RuleSet.applies(rule, dummyLanguage().getVersion("1.7")),
                "Same language with outside range of min/max should not apply");
    }

    @Test
    void testAddExcludePattern() {
        RuleSet ruleSet =
            createRuleSetBuilder("ruleset1")
                .withFileExclusions(Pattern.compile(".*"))
                .build();
        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(1, ruleSet.getFileExclusions().size(), "Invalid number of patterns");
    }

    @Test
    void testExcludePatternAreOrdered() {

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
                .withFileExclusions(Pattern.compile(".*"))
                .withFileExclusions(Pattern.compile(".*ha"))
                .build();
        assertEquals(Arrays.asList(".*", ".*ha"), toStrings(ruleSet2.getFileExclusions()), "Exclude pattern");
    }

    @Test
    void testIncludePatternsAreOrdered() {

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
            .withFileInclusions(Pattern.compile(".*"))
            .withFileInclusions(Arrays.asList(Pattern.compile(".*ha"), Pattern.compile(".*hb")))
            .build();
        assertEquals(Arrays.asList(".*", ".*ha", ".*hb"), toStrings(ruleSet2.getFileInclusions()), "Exclude pattern");
    }

    private List<String> toStrings(List<Pattern> strings) {
        return strings.stream().map(Pattern::pattern).collect(Collectors.toList());
    }

    @Test
    void testAddExcludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1")
            .withFileExclusions(Pattern.compile(".*"))
            .build();

        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(1, ruleSet.getFileExclusions().size(), "Invalid number of patterns");

        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2")
                .withFileExclusions(ruleSet.getFileExclusions())
                .build();
        assertNotNull(ruleSet2.getFileExclusions(), "Exclude patterns");
        assertEquals(1, ruleSet2.getFileExclusions().size(), "Invalid number of patterns");
    }

    @Test
    void testSetExcludePatterns() {
        List<Pattern> excludePatterns = new ArrayList<>();
        excludePatterns.add(Pattern.compile("ah*"));
        excludePatterns.add(Pattern.compile(".*"));
        RuleSet ruleSet = createRuleSetBuilder("ruleset").replaceFileExclusions(excludePatterns).build();
        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(2, ruleSet.getFileExclusions().size(), "Invalid number of exclude patterns");
        assertEquals("ah*", ruleSet.getFileExclusions().get(0).pattern(), "Exclude pattern");
        assertEquals(".*", ruleSet.getFileExclusions().get(1).pattern(), "Exclude pattern");
        assertNotNull(ruleSet.getFileInclusions(), "Include patterns");
        assertEquals(0, ruleSet.getFileInclusions().size(), "Invalid number of include patterns");
    }

    @Test
    void testAddIncludePattern() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset")
                .withFileInclusions(Pattern.compile(".*"))
                .build();
        assertNotNull(ruleSet.getFileInclusions(), "Include patterns");
        assertEquals(1, ruleSet.getFileInclusions().size(), "Invalid number of patterns");
        assertEquals(".*", ruleSet.getFileInclusions().get(0).pattern(), "Include pattern");
        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(0, ruleSet.getFileExclusions().size(), "Invalid number of exclude patterns");
    }

    @Test
    void testAddIncludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1")
            .withFileInclusions(Pattern.compile("ah*"), Pattern.compile(".*"))
            .build();
        RuleSet ruleSet2 = createRuleSetBuilder("ruleset1")
                .withFileInclusions(ruleSet.getFileInclusions())
                .build();
        assertNotNull(ruleSet2.getFileInclusions(), "Include patterns");
        assertEquals(2, ruleSet2.getFileInclusions().size(), "Invalid number of patterns");
        assertEquals("ah*", ruleSet2.getFileInclusions().get(0).pattern(), "Include pattern");
        assertEquals(".*", ruleSet2.getFileInclusions().get(1).pattern(), "Include pattern");
        assertNotNull(ruleSet.getFileExclusions(), "Exclude patterns");
        assertEquals(0, ruleSet.getFileExclusions().size(), "Invalid number of exclude patterns");
    }

    @Test
    void testSetIncludePatterns() {
        List<Pattern> includePatterns = new ArrayList<>();
        includePatterns.add(Pattern.compile("ah*"));
        includePatterns.add(Pattern.compile(".*"));
        RuleSet ruleSet = createRuleSetBuilder("ruleset")
            .replaceFileInclusions(includePatterns)
            .build();

        assertEquals(includePatterns, ruleSet.getFileInclusions(), "Include patterns");
        assertNotNull(ruleSet.getFileInclusions(), "Exclude patterns");
        assertEquals(0, ruleSet.getFileExclusions().size(), "Invalid number of exclude patterns");
    }

    @Test
    void testIncludeExcludeApplies() {
        TextFile file = TextFile.forPath(Paths.get("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java"), Charset.defaultCharset(), dummyVersion());

        RuleSet ruleSet = createRuleSetBuilder("ruleset").build();
        assertTrue(ruleSet.applies(file), "No patterns");

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .build();
        assertTrue(ruleSet.applies(file), "Non-matching exclude");

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"), Pattern.compile(".*/package/.*"))
                .build();
        assertFalse(ruleSet.applies(file), "Matching exclude");

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .withFileExclusions(Pattern.compile(".*/package/.*"))
                .withFileInclusions(Pattern.compile(".*/randomX/.*"))
                .build();
        assertFalse(ruleSet.applies(file), "Non-matching include");

        ruleSet = createRuleSetBuilder("ruleset")
                .withFileExclusions(Pattern.compile("nomatch"))
                .withFileExclusions(Pattern.compile(".*/package/.*"))
                .withFileInclusions(Pattern.compile(".*/randomX/.*"))
                .withFileInclusions(Pattern.compile(".*/random/.*"))
                .build();
        assertTrue(ruleSet.applies(file), "Matching include");
    }

    @Test
    void testIncludeExcludeMultipleRuleSetWithRuleChainApplies() throws Exception {
        Rule rule = new FooRule();
        rule.setName("FooRule1");
        rule.setLanguage(dummyLanguage());

        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1").addRule(rule).build();
        RuleSet ruleSet2 = createRuleSetBuilder("RuleSet2").addRule(rule).build();


        RuleSets ruleSets = new RuleSets(listOf(ruleSet1, ruleSet2));

        // Two violations
        Report report = Report.buildReport(ctx1 -> ruleSets.apply(makeCompilationUnits(), ctx1));
        assertEquals(2, report.getViolations().size(), "Violations");

        // One violation
        ruleSet1 = createRuleSetBuilder("RuleSet1")
            .withFileExclusions(Pattern.compile(".*/package/.*"))
            .addRule(rule)
            .build();

        RuleSets ruleSets2 = new RuleSets(listOf(ruleSet1, ruleSet2));

        report = Report.buildReport(ctx -> ruleSets2.apply(makeCompilationUnits("C:\\package\\RandomClass.java"), ctx));
        assertEquals(1, report.getViolations().size(), "Violations");
    }

    @Test
    void copyConstructorDeepCopies() {
        Rule rule = new FooRule();
        rule.setName("FooRule1");
        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1")
            .addRule(rule)
            .build();
        RuleSet ruleSet2 = new RuleSet(ruleSet1);

        assertEquals(ruleSet1, ruleSet2);
        assertNotSame(ruleSet1, ruleSet2);

        assertEquals(rule, ruleSet2.getRuleByName("FooRule1"));
        assertNotSame(rule, ruleSet2.getRuleByName("FooRule1"));
    }

    private void verifyRuleSet(RuleSet ruleset, Set<RuleViolation> expected) throws Exception {

        Report report = getReportForRuleSetApply(ruleset, makeCompilationUnits());

        assertEquals(expected.size(), report.getViolations().size(), "Invalid number of Violations Reported");

        for (RuleViolation violation : report.getViolations()) {
            assertTrue(expected.contains(violation), "Unexpected Violation Returned: " + violation);
        }

        for (RuleViolation violation : expected) {
            assertTrue(report.getViolations().contains(violation), "Expected Violation not Returned: " + violation);
        }
    }

    private RootNode makeCompilationUnits() {
        return makeCompilationUnits("sampleFile.dummy");
    }

    private RootNode makeCompilationUnits(String filename) {
        DummyRootNode node = helper.parse("dummyCode", filename);
        node.setImage("Foo");
        return node;
    }

    @Test
    void ruleExceptionShouldBeReported() throws Exception {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported")
            .addRule(new MockRule() {
                @Override
                public void apply(Node nodes, RuleContext ctx) {
                    throw new IllegalStateException("Test exception while applying rule");
                }
            })
            .build();

        Report report = getReportForRuleSetApply(ruleset, makeCompilationUnits());

        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        ProcessingError error = errors.get(0);
        assertThat(error.getMsg(), containsString("java.lang.IllegalStateException: Test exception while applying rule\n"));
        assertThat(error.getMsg(), containsString("Rule applied on node=dummyRootNode[@Image=Foo]"));
        assertThat(error.getError().getCause(), instanceOf(IllegalStateException.class));
    }


    @Test
    void ruleExceptionShouldNotStopProcessingFile() throws Exception {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            @Override
            public void apply(Node target, RuleContext ctx) {
                throw new IllegalStateException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {
            @Override
            public void apply(Node target, RuleContext ctx) {
                addViolationWithMessage(ctx, target, "Test violation of the second rule in the ruleset");
            }
        }).build();

        Report report = getReportForRuleSetApply(ruleset, makeCompilationUnits("samplefile.dummy"));

        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        ProcessingError error = errors.get(0);
        assertThat(error.getMsg(), containsString("java.lang.IllegalStateException: Test exception while applying rule\n"));
        assertThat(error.getMsg(), containsString("Rule applied on node=dummyRootNode[@Image=Foo]"));
        assertThat(error.getError().getCause(), instanceOf(IllegalStateException.class));
        assertThat(IOUtil.normalizePath(error.getFile()), equalTo("samplefile.dummy"));

        assertThat(report.getViolations(), hasSize(1));
    }

    @Test
    void ruleExceptionShouldNotStopProcessingFileWithRuleChain() throws Exception {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {

            @Override
            protected @NonNull RuleTargetSelector buildTargetSelector() {
                return RuleTargetSelector.forXPathNames(setOf("dummyRootNode"));
            }

            @Override
            public void apply(Node target, RuleContext ctx) {
                throw new UnsupportedOperationException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {

            @Override
            protected @NonNull RuleTargetSelector buildTargetSelector() {
                return RuleTargetSelector.forXPathNames(setOf("dummyRootNode"));
            }

            @Override
            public void apply(Node target, RuleContext ctx) {
                addViolationWithMessage(ctx, target, "Test violation of the second rule in the ruleset");
            }
        }).build();

        Report report = getReportForRuleSetApply(ruleset, makeCompilationUnits());

        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        ProcessingError error = errors.get(0);
        assertThat(error.getMsg(), containsString("java.lang.UnsupportedOperationException: Test exception while applying rule\n"));
        assertThat(error.getMsg(), containsString("Rule applied on node=dummyRootNode[@Image=Foo]"));
        assertThat(error.getError().getCause(), instanceOf(UnsupportedOperationException.class));

        assertThat(report.getViolations(), hasSize(1));
    }


    static class MockRule extends net.sourceforge.pmd.lang.rule.MockRule {

        MockRule() {
            super();
            setLanguage(DummyLanguageModule.getInstance());
        }

        MockRule(String name, String description, String message, String ruleSetName, RulePriority priority) {
            super(name, description, message, ruleSetName, priority);
            setLanguage(DummyLanguageModule.getInstance());
        }

        MockRule(String name, String description, String message, String ruleSetName) {
            super(name, description, message, ruleSetName);
            setLanguage(DummyLanguageModule.getInstance());
        }

    }

}
