/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

class ReportTest {

    // Files are grouped together now.
    @Test
    void testSortedReportFile() {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            FileLocation s = getNode(10, 5, "foo");
            Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(violation(rule1, s));
            FileLocation s1 = getNode(10, 5, "bar");
            Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(violation(rule2, s1));
        });
        assertThat(result, containsString("bar"));
        assertThat(result, containsString("foo"));
        assertTrue(result.indexOf("bar") < result.indexOf("foo"), "sort order wrong");
    }

    @Test
    void testSortedReportLine() {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            FileLocation node1 = getNode(20, 5, "foo1"); // line 20: after rule2 violation
            Rule rule1 = new MockRule("rule1", "rule1", "msg", "rulesetname");
            r.onRuleViolation(violation(rule1, node1));

            FileLocation node2 = getNode(10, 5, "foo1"); // line 10: before rule1 violation
            Rule rule2 = new MockRule("rule2", "rule2", "msg", "rulesetname");
            r.onRuleViolation(violation(rule2, node2)); // same file!!
        });
        assertTrue(result.indexOf("rule2") < result.indexOf("rule1"), "sort order wrong");
    }

    @Test
    void testIterator() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        FileLocation loc1 = getNode(5, 5, "file1");
        FileLocation loc2 = getNode(5, 6, "file1");
        Report r = Report.buildReport(it -> {
            it.onRuleViolation(violation(rule, loc1));
            it.onRuleViolation(violation(rule, loc2));
        });

        assertEquals(2, r.getViolations().size());
    }

    @Test
    void testFilterViolations() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        FileLocation loc1 = getNode(5, 5, "file1");
        FileLocation loc2 = getNode(5, 6, "file1");
        Report r = Report.buildReport(it -> {
            it.onRuleViolation(violation(rule, loc1));
            it.onRuleViolation(violation(rule, loc2, "to be filtered"));
        });

        Report filtered = r.filterViolations(ruleViolation -> !"to be filtered".equals(ruleViolation.getDescription()));

        assertEquals(1, filtered.getViolations().size());
        assertEquals("msg", filtered.getViolations().get(0).getDescription());
    }

    @Test
    void testUnion() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        FileLocation loc1 = getNode(1, 2, "file1");
        Report report1 = Report.buildReport(it -> it.onRuleViolation(violation(rule, loc1)));

        FileLocation loc2 = getNode(2, 1, "file1");
        Report report2 = Report.buildReport(it -> it.onRuleViolation(violation(rule, loc2)));

        Report union = report1.union(report2);
        assertEquals(2, union.getViolations().size());
    }

    public static @NonNull RuleViolation violation(Rule rule, FileLocation loc2) {
        return violation(rule, loc2, rule.getMessage());
    }

    public static @NonNull RuleViolation violation(Rule rule, FileLocation loc1, String rule1) {
        return new ParametricRuleViolation(rule, loc1, rule1);
    }


    private static FileLocation getNode(int line, int column, String filename) {
        return FileLocation.range(filename, TextRange2d.range2d(line, column, line, column));
    }

    public static String render(Renderer renderer, Consumer<? super FileAnalysisListener> listenerEffects) {
        return renderGlobal(renderer, globalListener -> {
            LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();

            TextFile dummyFile = TextFile.forCharSeq("dummyText", "file", dummyVersion);
            try (FileAnalysisListener fal = globalListener.startFileAnalysis(dummyFile)) {
                listenerEffects.accept(fal);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }

    public static String renderGlobal(Renderer renderer, Consumer<? super GlobalAnalysisListener> listenerEffects) {
        StringWriter writer = new StringWriter();
        renderer.setWriter(writer);

        try (GlobalAnalysisListener listener = renderer.newListener()) {
            listenerEffects.accept(listener);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        return writer.toString();
    }

}
