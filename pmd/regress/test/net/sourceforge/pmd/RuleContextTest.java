package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;

public class RuleContextTest extends TestCase {

    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertNull("Report should be null", ctx.getReport());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals("report object mismatch", r, r2);
    }

    public void testFilename() {
        RuleContext ctx = new RuleContext();
        assertNull("filename should be null", ctx.getSourceCodeFilename());
        ctx.setSourceCodeFilename("foo");
        assertEquals("filename mismatch", "foo", ctx.getSourceCodeFilename());
    }
}
