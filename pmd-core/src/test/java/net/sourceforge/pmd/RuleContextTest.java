/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

    public static Report getReport(Consumer<RuleContext> sideEffects) throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try {
            sideEffects.accept(RuleContext.create(listener));
        } finally {
            listener.close();
        }
        return listener.getResult();
    }

    @Test
    public void testMessage() throws Exception {
        Report report = getReport(ctx -> ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\""));

        Assert.assertEquals("message with \"{\"", report.getViolations().get(0).getDescription());
    }

    @Test
    public void testMessageArgs() throws Exception {
        Report report = getReport(ctx -> ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with 1 argument: \"{0}\"", "testarg1"));

        Assert.assertEquals("message with 1 argument: \"testarg1\"", report.getViolations().get(0).getDescription());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(RuleContextTest.class);
    }
}
