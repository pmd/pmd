/*
 * User: tom
 * Date: Jul 1, 2002
 * Time: 1:16:07 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

public class AbstractRuleTest extends TestCase {

    private static class MyRule extends AbstractRule {
        public String getMessage() {
            return "myrule";
        }
    }

    public AbstractRuleTest(String name) {
        super(name);
    }

    public void testCreateRV() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation rv = r.createRuleViolation(ctx, 5);
        assertEquals("Line number mismatch!", 5, rv.getLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "myrule", rv.getDescription());
    }

    public void testCreateRV2() {
        MyRule r = new MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        RuleViolation rv = r.createRuleViolation(ctx, 5, "specificdescription");
        assertEquals("Line number mismatch!", 5, rv.getLine());
        assertEquals("Filename mismatch!", "filename", rv.getFilename());
        assertEquals("Rule object mismatch!", r, rv.getRule());
        assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

}
