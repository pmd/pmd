/*
 * User: tom
 * Date: Jul 10, 2002
 * Time: 3:22:57 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyFinallyBlockRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Finally='true']/Block[position() = last()][count(*) = 0]");
    }

    public void testEmptyFinallyBlock1() throws Throwable {
        runTest("EmptyFinallyBlock1.java", 1, rule);
    }

    public void testEmptyFinallyBlock2() throws Throwable {
        runTest("EmptyFinallyBlock2.java", 1, rule);
    }

    public void testEmptyFinallyBlock3() throws Throwable {
        runTest("EmptyFinallyBlock3.java", 0, rule);
    }

    public void testMultipleCatchBlocksWithFinally() throws Throwable {
        runTest("EmptyFinallyBlock4.java", 1, rule);
    }
}
