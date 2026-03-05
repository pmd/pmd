/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.reporting.ReportTestUtil.getReport;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

class RuleContextTest {


    @Test
    void testMessage() throws Exception {
        DummyRootNode tree = DummyTreeUtil.tree(DummyTreeUtil::root);
        Report report = getReport(new FooRule(), tree, (r, ctx) -> ctx.addViolationWithMessage(tree, "message with \"'{'\""));

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
        DummyRootNode node = DummyTreeUtil.tree(DummyTreeUtil::root);
        Report report = getReport(new FooRule(), node, (r, ctx) -> {
            ctx.at(node).reportWithMessage(unescapedMessage, args);
        });
        return report.getViolations().get(0);
    }

}
