/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:03:09 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.reports.Report;
import org.cougaar.util.pmd.SystemOutRule;

public class SystemOutRuleTest extends RuleTst {
    public SystemOutRuleTest(String name) {
        super(name);
    }

    public void testSystemOut() throws Throwable {
        Report report = process("ContainsSystemOut.java", new SystemOutRule());
        assertEquals(1, report.size());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.iterator().next()).getRule());
    }
}
