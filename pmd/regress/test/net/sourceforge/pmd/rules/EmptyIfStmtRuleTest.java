/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:07:25 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyIfStmtRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement/Statement/Block[count(*) = 0]");
    }

    public void testOneEmptyOneNotEmpty() throws Throwable {
        runTest("EmptyIfStmtRule.java", 1, rule);
    }

}
