/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {
    public static Report getReport(Consumer<RuleContext> sideEffects) throws Exception {
        Report report = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("test.dummy"));
        ctx.setReport(report);
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        sideEffects.accept(ctx);
        return report;
    }

    public static Report getReport(Rule rule, BiConsumer<Rule, RuleContext> sideEffects) throws Exception {
        return getReport(ctx -> sideEffects.accept(rule, ctx));
    }

    public static Report getReportForRuleApply(Rule rule, Node node) throws Exception {
        return getReport(rule, (r, ctx) -> r.apply(node, ctx));
    }

    public static Report getReportForRuleSetApply(RuleSet ruleset, RootNode node) throws Exception {
        return getReport(ctx -> new RuleSets(ruleset).apply(Collections.singletonList(node), ctx));
    }

    @Test
    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertEquals(0, ctx.getReport().getViolations().size());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals("report object mismatch", r, r2);
    }

    @Test
    public void testSourceCodeFilename() {
        RuleContext ctx = new RuleContext();
        assertEquals("filename should be empty", "", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFile(new File("dir/foo.java"));
        assertEquals("filename mismatch", "foo.java", ctx.getSourceCodeFilename());
    }

    @Test
    public void testSourceCodeFile() {
        RuleContext ctx = new RuleContext();
        assertNull("file should be null", ctx.getSourceCodeFile());
        ctx.setSourceCodeFile(new File("somefile.java"));
        assertEquals("filename mismatch", new File("somefile.java"), ctx.getSourceCodeFile());
    }


    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleContextTest.class);
    }
}
