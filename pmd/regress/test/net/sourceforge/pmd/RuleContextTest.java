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
    public RuleContextTest(String name) {
        super(name);
    }

    public void testReport() {
        RuleContext ctx = new RuleContext();
        assertNull(ctx.getReport());
        Report r = new Report("xml");
        ctx.setReport(r);
        Report r2 = ctx.getReport();
        assertEquals(r, r2);
    }
}
