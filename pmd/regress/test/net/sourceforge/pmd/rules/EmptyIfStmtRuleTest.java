/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:07:25 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.rules.EmptyIfStmtRule;

public class EmptyIfStmtRuleTest extends RuleTst {
    public EmptyIfStmtRuleTest(String name) {
        super(name);
    }

    public void testEmptyIf() throws Throwable {
        Report report = process("EmptyIfStmtRule.java", new EmptyIfStmtRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyIfStmtRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

}
