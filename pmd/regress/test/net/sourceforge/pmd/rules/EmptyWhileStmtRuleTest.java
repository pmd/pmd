/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:08:40 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.EmptyWhileStmtRule;

public class EmptyWhileStmtRuleTest extends RuleTst {

    public void testEmptyWhileStmtRule() throws Throwable {
        Report report = process("EmptyWhileStmtRule.java", new EmptyWhileStmtRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyWhileStmtRule(), ((RuleViolation)report.iterator().next()).getRule());
    }


}
