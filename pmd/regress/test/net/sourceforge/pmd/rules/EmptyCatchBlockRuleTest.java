/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:56:19 PM
 */
package test.net.sourceforge.pmd.rules;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.EmptyCatchBlockRule;

public class EmptyCatchBlockRuleTest extends RuleTst {

    public EmptyCatchBlockRuleTest(String name) {
        super(name);
    }

    public void testEmptyCatchBlock() throws Throwable {
        Report report = process("EmptyCatchBlock.java", new EmptyCatchBlockRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyCatchBlockRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testEmptyCatchBlock2() throws Throwable {
        Report report = process("EmptyCatchBlock2.java", new EmptyCatchBlockRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testEmptyCatchBlock3() throws Throwable {
        Report report = process("EmptyCatchBlock3.java", new EmptyCatchBlockRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyCatchBlockRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }


}
