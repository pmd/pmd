/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 11:08:40 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyWhileStmtRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement/Statement/Block[count(*) = 0]");
    }

    public void testEmptyWhileStmtRule() throws Throwable {
        runTest("EmptyWhileStmtRule.java", 1, rule);
    }


}
