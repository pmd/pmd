/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 3:31:37 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyTryBlockRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement/Block[1][count(*) = 0]");
    }

    public void testEmptyTryBlock1() throws Throwable {
        runTest("EmptyTryBlock1.java", 1, rule);
    }

    public void testEmptyTryBlock2() throws Throwable {
        runTest("EmptyTryBlock2.java", 1, rule);
    }

    public void testEmptyTryBlock3() throws Throwable {
        runTest("EmptyTryBlock3.java", 0, rule);
    }

}
