/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 3:31:37 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.EmptyCatchBlockRule;
import net.sourceforge.pmd.rules.EmptyTryBlockRule;

public class EmptyTryBlockRuleTest extends RuleTst {

    public void testEmptyTryBlock1() throws Throwable {
        Report report = process("EmptyTryBlock1.java", new EmptyTryBlockRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyTryBlockRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testEmptyTryBlock2() throws Throwable {
        Report report = process("EmptyTryBlock2.java", new EmptyTryBlockRule());
        assertEquals(1, report.size());
        assertEquals(new EmptyTryBlockRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testEmptyTryBlock3() throws Throwable {
        Report report = process("EmptyTryBlock3.java", new EmptyTryBlockRule());
        assertTrue(report.isEmpty());
    }

}
