/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

    @Test
    public void testMessage() throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try (RuleContext ctx = new RuleContext(listener)) {
            ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\"");
        }

        RuleViolation violation = listener.getReport().getViolations().get(0);
        Assert.assertEquals("message with \"{\"", violation.getDescription());
    }

    @Test
    public void testMessageArgs() throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try (RuleContext ctx = new RuleContext(listener)) {
            ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with 1 argument: \"{0}\"", "testarg1");
        }

        RuleViolation violation = listener.getReport().getViolations().get(0);
        Assert.assertEquals("message with 1 argument: \"testarg1\"", violation.getDescription());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleContextTest.class);
    }
}
