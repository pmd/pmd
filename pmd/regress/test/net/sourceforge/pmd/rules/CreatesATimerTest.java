/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:05:42 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.reports.Report;
import org.cougaar.util.pmd.DontCreateTimersRule;

public class CreatesATimerTest extends RuleTst {
    public CreatesATimerTest(String name) {
        super(name);
    }

    public void testCreateATimer() throws Throwable {
        Report report = process("CreatesATimer.java", new DontCreateTimersRule());
        assertEquals(1, report.size());
        assertEquals(new DontCreateTimersRule(), ((RuleViolation)report.iterator().next()).getRule());
    }


}
