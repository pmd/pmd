/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 4:30:42 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.Report;

public class RuleContextTest extends TestCase {

    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertNull(ctx.getReport());
        Report r = new Report();
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals(r, r2);
    }

    public void testFilename() {
        RuleContext ctx = new RuleContext();
        assertNull(ctx.getSourceCodeFilename());
        ctx.setSourceCodeFilename("foo");
        assertEquals("foo", ctx.getSourceCodeFilename());
    }
}
