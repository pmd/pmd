/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

    @Test
    public void testSourceCodeFilename() {
        RuleContext ctx = new RuleContext();
        assertEquals("filename should be empty", "", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFile(new File("dir/foo.java"));
        assertEquals("filename mismatch", "foo.java", ctx.getSourceCodeFilename());
    }


    @Test
    public void testMessage() throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try (RuleContext ctx = new RuleContext(listener)) {
            ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
            ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\"");
        }

        RuleViolation violation = listener.getReport().getViolations().get(0);
        Assert.assertEquals("message with \"{\"", violation.getDescription());
    }

    @Test
    public void testMessageArgs() throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try (RuleContext ctx = new RuleContext(listener)) {
            ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
            ctx.addViolationWithMessage(new FooRule(), DummyTreeUtil.tree(DummyTreeUtil::root), "message with 1 argument: \"{0}\"", "testarg1");
        }

        RuleViolation violation = listener.getReport().getViolations().get(0);
        Assert.assertEquals("message with 1 argument: \"testarg1\"", violation.getDescription());
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
