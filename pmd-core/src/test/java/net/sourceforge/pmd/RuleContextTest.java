/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

public class RuleContextTest {

    public static Report getReport(Rule rule, BiConsumer<Rule, RuleContext> sideEffects) throws Exception {
        return Report.buildReport(listener -> sideEffects.accept(rule, RuleContext.create(listener, rule)));
    }

    public static Report getReportForRuleApply(Rule rule, Node node) throws Exception {
        return getReport(rule, (r, ctx) -> r.apply(node, ctx));
    }

    public static Report getReportForRuleSetApply(RuleSet ruleset, RootNode node) throws Exception {
        return Report.buildReport(listener -> new RuleSets(ruleset).apply(node, listener));
    }

    @Test
    public void testMessage() throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> ctx.addViolationWithMessage(DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\""));

        Assert.assertEquals("message with \"{\"", report.getViolations().get(0).getDescription());
    }

    @Test
    public void testMessageEscaping() throws Exception {
        RuleViolation violation = makeViolation("message with \"'{'\"");

        Assert.assertEquals("message with \"{\"", violation.getDescription());
    }

    @Test
    public void testMessageEscaping2() throws Exception {
        RuleViolation violation = makeViolation("message with ${ohio}");

        Assert.assertEquals("message with ${ohio}", violation.getDescription());
    }

    private RuleViolation makeViolation(String unescapedMessage, Object... args) throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> ctx.addViolationWithMessage(DummyTreeUtil.tree(DummyTreeUtil::root), unescapedMessage, args));
        return report.getViolations().get(0);
    }

}
