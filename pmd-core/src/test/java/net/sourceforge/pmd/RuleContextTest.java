/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

public class RuleContextTest {

    static Report getReport(Rule rule, BiConsumer<Rule, RuleContext> sideEffects) {
        return Report.buildReport(listener -> sideEffects.accept(rule, RuleContext.create(listener, rule)));
    }

    public static Report getReportForRuleApply(Rule rule, Node node) {
        return getReport(rule, (r, ctx) -> r.apply(node, ctx));
    }

    static Report getReportForRuleSetApply(RuleSet ruleset, RootNode node) {
        return Report.buildReport(listener -> new RuleSets(ruleset).apply(node, listener));
    }

    @Test
    void testMessage() throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> ctx.addViolationWithMessage(DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\""));

        assertEquals("message with \"{\"", report.getViolations().get(0).getDescription());
    }

    @Test
    void testMessageEscaping() throws Exception {
        RuleViolation violation = makeViolation("message with \"'{'\"");

        assertEquals("message with \"{\"", violation.getDescription());
    }

    @Test
    void testMessageEscaping2() throws Exception {
        RuleViolation violation = makeViolation("message with ${ohio}");

        assertEquals("message with ${ohio}", violation.getDescription());
    }

    private RuleViolation makeViolation(String unescapedMessage, Object... args) throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> {
            DummyRootNode node = DummyTreeUtil.tree(DummyTreeUtil::root);
            ctx.addViolationWithMessage(node, unescapedMessage, args);
        });
        return report.getViolations().get(0);
    }

}
