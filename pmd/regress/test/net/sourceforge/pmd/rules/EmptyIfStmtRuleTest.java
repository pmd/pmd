/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:07:25 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.EmptyIfStmtRule;

public class EmptyIfStmtRuleTest extends RuleTst {

    public void testOneEmptyOneNotEmpty() throws Throwable {
        runTest("EmptyIfStmtRule.java", 1, new EmptyIfStmtRule());
    }

}
