/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class RuleContextTest {

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
