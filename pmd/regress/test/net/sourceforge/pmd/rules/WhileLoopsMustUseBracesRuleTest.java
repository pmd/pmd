/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:17:08 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.WhileLoopsMustUseBracesRule;

public class WhileLoopsMustUseBracesRuleTest extends RuleTst {

    public void test1() throws Throwable {
        Report report = process("WhileLoopsNeedBraces1.java", new WhileLoopsMustUseBracesRule());
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("WhileLoopsNeedBraces2.java", new WhileLoopsMustUseBracesRule());
        assertTrue(report.isEmpty());
    }
}
