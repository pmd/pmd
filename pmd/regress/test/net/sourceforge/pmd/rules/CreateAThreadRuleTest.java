/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:04:24 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;
import org.cougaar.util.pmd.DontCreateThreadsRule;

public class CreateAThreadRuleTest extends RuleTst {
    public CreateAThreadRuleTest(String name) {
        super(name);
    }

    public void testCreateAThread() throws Throwable {
        Report report = process("CreatesAThread.java", new DontCreateThreadsRule());
        assertEquals(1, report.size());
        assertEquals(new DontCreateThreadsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }


}
