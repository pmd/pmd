/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:37:46 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.IfStmtsMustUseBracesRule;

public class IfStmtsMustUseBracesRuleTest extends RuleTst {

    public void testSimpleBad() throws Throwable {
        runTest("IfStmtsMustUseBraces1.java", 1, new IfStmtsMustUseBracesRule());
    }

    public void testSimpleOK() throws Throwable {
        runTest("IfStmtsMustUseBraces2.java", 0, new IfStmtsMustUseBracesRule());
    }

    public void testNexted() throws Throwable {
        runTest("IfStmtsMustUseBraces3.java", 1, new IfStmtsMustUseBracesRule());
    }
}
