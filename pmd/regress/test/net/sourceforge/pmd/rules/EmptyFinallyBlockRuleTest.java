/*
 * User: tom
 * Date: Jul 10, 2002
 * Time: 3:22:57 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.EmptyFinallyBlockRule;

public class EmptyFinallyBlockRuleTest extends RuleTst {

    public void testEmptyFinallyBlock1() throws Throwable {
        Report report = process("EmptyFinallyBlock1.java", new EmptyFinallyBlockRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyFinallyBlockRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testEmptyFinallyBlock2() throws Throwable {
        Report report = process("EmptyFinallyBlock2.java", new EmptyFinallyBlockRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyFinallyBlockRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testEmptyFinallyBlock3() throws Throwable {
        Report report = process("EmptyFinallyBlock3.java", new EmptyFinallyBlockRule());
        assertTrue(report.isEmpty());
    }
}
