/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:55:48 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class IfElseStmtsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) > 2][not(Statement/Block)]");
    }

    public void testIfElseStmtsMustUseBraces1() throws Throwable {
        runTest("IfElseStmtsNeedBraces1.java", 1, rule);
    }

    public void testIfElseStmtsMustUseBraces2() throws Throwable {
        runTest("IfElseStmtsNeedBraces2.java", 0, rule);
    }
}
