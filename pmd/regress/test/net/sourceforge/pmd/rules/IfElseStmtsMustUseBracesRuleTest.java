/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:55:48 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.IfElseStmtsMustUseBracesRule;
import net.sourceforge.pmd.Report;

public class IfElseStmtsMustUseBracesRuleTest extends RuleTst {

    public void testIfElseStmtsMustUseBraces1() throws Throwable {
        Report report = process("IfElseStmtsNeedBraces1.java", new IfElseStmtsMustUseBracesRule());
        assertEquals(1, report.size());
    }
    public void testIfElseStmtsMustUseBraces2() throws Throwable {
        Report report = process("IfElseStmtsNeedBraces2.java", new IfElseStmtsMustUseBracesRule());
        assertTrue(report.isEmpty());
    }


}
