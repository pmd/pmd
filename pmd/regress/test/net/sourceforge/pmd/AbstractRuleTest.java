/*
 * User: tom
 * Date: Jul 1, 2002
 * Time: 1:16:07 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleContext;

public class AbstractRuleTest extends TestCase {

    private static class MyRule extends AbstractRule {
        public String getMessage() {
            return "myrule";
        }
    }

    public  AbstractRuleTest(String name) {
        super(name);
    }

    public void testCreateRV() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation rv = r.createRuleViolation(ctx, 5);
        assertEquals(5, rv.getLine());
        assertEquals("filename", rv.getFilename());
        assertEquals(r, rv.getRule());
        assertEquals("myrule", rv.getDescription());
    }

    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation rv = r.createRuleViolation(ctx, 5, "specificdescription");
        assertEquals(5, rv.getLine());
        assertEquals("filename", rv.getFilename());
        assertEquals(r, rv.getRule());
        assertEquals("specificdescription", rv.getDescription());
    }
}
