/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:00:43 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;
import org.cougaar.util.pmd.SystemOutRule;

public class SystemInRuleTest extends RuleTst {
    public SystemInRuleTest(String name) {
        super(name);
    }

    public void testSystemIn() throws Throwable {
        Report report = process("ContainsSystemIn.java", new SystemOutRule());
        assertEquals(2, report.size());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.iterator().next()).getRule());
    }
}

