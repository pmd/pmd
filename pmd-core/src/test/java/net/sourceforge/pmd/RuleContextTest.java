/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.ReportTestUtil.getReport;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

class RuleContextTest {


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
